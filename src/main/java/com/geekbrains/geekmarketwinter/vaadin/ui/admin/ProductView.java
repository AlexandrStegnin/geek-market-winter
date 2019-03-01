package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.config.support.OperationEnum;
import com.geekbrains.geekmarketwinter.entites.*;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.services.FileAssetService;
import com.geekbrains.geekmarketwinter.services.ProductService;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.geekbrains.geekmarketwinter.config.support.Constants.*;

/**
 * @author stegnin
 */

@PageTitle("Admin products")
@Route(ADMIN_PRODUCTS_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class ProductView extends VerticalLayout {

    @Value("${spring.config.file-upload-directory}")
    private String fileUploadDirectory;

    private final ProductService productService;
    private ListDataProvider<Product> dataProvider;
    private Grid<Product> grid;
    private ProductFilter productFilter;
    private Page<Product> page;
    private final Button addNewBtn;
    private final CategoryService categoryService;
    private Binder<Product> binder;
    private MultiFileMemoryBuffer buffer;
    private FileAssetService fileAssetService;
    private CustomAppLayout appLayout;

    public ProductView(AuthRepository auth,
                       ProductService productService,
                       CategoryService categoryService,
                       FileAssetService fileAssetService) {
        this.fileAssetService = fileAssetService;
        this.buffer = new MultiFileMemoryBuffer();
        this.productService = productService;
        this.grid = new Grid<>();
        this.productFilter = new ProductFilter();
        this.categoryService = categoryService;
        this.page = productService.findAll(productFilter, Pageable.unpaged());
        this.addNewBtn = new Button("Add new product", e -> showDialog(new Product(),
                OperationEnum.CREATE));
        this.dataProvider = new ListDataProvider<>(getAllProducts(productFilter));
        this.binder = new BeanValidationBinder<>(Product.class);
        this.appLayout = new CustomAppLayout(auth);
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);
        addNewBtn.setId("add_product");
        grid.setDataProvider(dataProvider);

        grid.addColumn(Product::getTitle)
                .setHeader("Title")
                .setKey(Product_.TITLE)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Product::getVendorCode)
                .setHeader("Vendor code")
                .setKey(Product_.VENDOR_CODE)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(new NumberRenderer<>(
                Product::getPrice,
                NumberFormat.getCurrencyInstance(LOCALE_RU)))
                .setHeader("Price")
                .setKey(Product_.PRICE)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(product -> product.getCategory().getTitle())
                .setHeader("Category")
                .setKey(Product_.CATEGORY)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Product::getShortDescription)
                .setHeader("Short description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(2);

        grid.addColumn(Product::getFullDescription)
                .setHeader("Full description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(new LocalDateTimeRenderer<>(
                Product::getCreateAt,
                DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.SHORT,
                        FormatStyle.MEDIUM)))
                .setHeader("Created at")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(product -> {
            if (!Objects.equals(null, product.getImages())) {
                return product.getImages().stream().map(ProductImage::getPath)
                        .collect(Collectors.joining(", "));
            }
            return "";
        })
                .setHeader("Images")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(product ->
                VaadinViewUtils.makeEditorColumnActions(
                        e -> showDialog(product, OperationEnum.UPDATE),
                        e -> showDialog(product, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setFlexGrow(2);

        VerticalLayout horizontalLayout = new VerticalLayout();
        HorizontalLayout filterForm = VaadinViewUtils.getProductFilterForm(dataProvider, getAllCategories());
        addNewBtn.getStyle()
                .set("position", "absolute")
                .set("right", "5%");
        filterForm.add(addNewBtn);
        filterForm.setWidth("100%");
        filterForm.setAlignSelf(Alignment.END, addNewBtn);
        VerticalLayout verticalLayout = new VerticalLayout(/*addNewBtn, */grid);
        horizontalLayout.add(filterForm, verticalLayout);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setPadding(true);
        horizontalLayout.setSizeFull();
        horizontalLayout.setAlignItems(Alignment.START);
        verticalLayout.setAlignItems(Alignment.END);
        appLayout.setContent(horizontalLayout);
        add(appLayout);
        setHeight("100vh");
    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    private List<Product> getAllProducts(ProductFilter filter) {
        return productService.fetchAll(filter);
    }

    private void deleteProduct(Product product) {
        dataProvider.getItems().remove(product);
        productService.delete(product);
        dataProvider.refreshAll();
    }

    private void showDialog(Product product, OperationEnum operation) {
        FormLayout formLayout = new FormLayout();

        ComboBox<Category> category = new ComboBox<>("Select category", getAllCategories());
        category.setItemLabelGenerator(Category::getTitle);
        binder.forField(category)
                .bind(Product_.CATEGORY);
        if (product.getCategory() != null) {
            category.setValue(product.getCategory());
        }
        category.setId("category_combo");

        TextField vendorCode = new TextField("Vendor code");
        vendorCode.setValue(product.getVendorCode() == null ? "" : product.getVendorCode());
        vendorCode.setId("vendor_code_fld");

        TextField title = new TextField("Title");
        title.setValue(product.getTitle() == null ? "" : product.getTitle());
        binder.forField(title)
                .bind(Product_.TITLE);
        title.setId("title_fld");

        TextField shortDescription = new TextField("Short description");
        shortDescription.setValue(product.getShortDescription() == null ? "" : product.getShortDescription());
        binder.forField(shortDescription)
                .bind(Product_.SHORT_DESCRIPTION);
        shortDescription.setId("short_descr_fld");


        TextField fullDescription = new TextField("Full description");
        fullDescription.setValue(product.getFullDescription() == null ? "" : product.getFullDescription());
        binder.forField(fullDescription)
                .bind(Product_.FULL_DESCRIPTION);
        fullDescription.setId("full_descr_fld");

        TextField price = new TextField("Price");
        price.setValue(product.getPrice() == 0 ? "" : String.valueOf(product.getPrice()));
        price.setId("price_fld");
        price.setClearButtonVisible(true);

        binder.forField(price)
                // input should not be null or empty
                .withValidator(string -> string != null && !string.isEmpty(),
                        "Input values should not be empty")
                // convert String to Double, throw ValidationException if String is in incorrect format
                .withConverter(Double::parseDouble,
                        doubleToString -> doubleToString.toString().replace(".", ","),
                        "Input value should be an double")
                // validate converted double: it should be positive
                .withValidator(dbl -> dbl > 0, "Input value should be a positive double")
                .bind(Product_.PRICE);

        Upload images = new Upload(buffer);

        formLayout.add(category, vendorCode, title, shortDescription, fullDescription, price, images);

        Dialog dialog = VaadinViewUtils.initDialog();
        dialog.setId("crud_dialog");
        Button save = new Button("Save");
        save.setId("save_btn");

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.setId("cancel_btn");
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();

        switch (operation) {
            case UPDATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    binder.forField(vendorCode).bind(Product_.VENDOR_CODE);
                    if (binder.writeBeanIfValid(product)) {
                        saveProduct(product);
                        dialog.close();
                        showMessage("UPDATE", product);
                    }
                });
                break;
            case CREATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    binder.forField(vendorCode)
                            .withValidator(string -> {
                                Product p = dataProvider.getItems()
                                        .stream()
                                        .filter(item -> item.getVendorCode().equalsIgnoreCase(string))
                                        .findFirst().orElse(null);
                                return p == null;
                            }, "Product with vendor code already exists")
                            .bind(Product_.VENDOR_CODE);
                    if (binder.writeBeanIfValid(product)) {
                        dataProvider.getItems().add(product);
                        saveProduct(product);
                        dialog.close();
                        showMessage("CREATE", product);
                    }
                });
                break;
            case DELETE:
                Div contentText = new Div();
                contentText.setText("Confirm delete product: " + product.getTitle() + "?");
                content.add(contentText, actions);
                save.setText("Yes");
                save.addClickListener(e -> {
                    deleteProduct(product);
                    dropProductDir(product);
                    dialog.close();
                    showMessage("DELETE", product);
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        category.getElement().callFunction("focus");

    }

    private void saveProduct(Product product) {
        saveProductImages(product);
        productService.save(product);
        dataProvider.refreshAll();
    }

    private void saveProductImages(Product product) {
        final File[] targetFile = {null};
        createProductDir(product);
        buffer.getFiles().forEach(fileName -> {
            targetFile[0] = new File(fileUploadDirectory + product.getVendorCode() + PATH_SEPARATOR + fileName);
            try {
                Files.copy(buffer.getInputStream(fileName), targetFile[0].toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка копирования файла", e);
            }
            FileAsset fileAsset = new FileAsset(fileName);
            fileAssetService.createFileAsset(fileAsset, targetFile[0]);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setPath(fileName);
            product.addImage(productImage);
        });
    }

    private void createProductDir(Product product) {
        Path productDir = Paths.get(fileUploadDirectory + product.getVendorCode());
        if (!Files.exists(productDir)) {
            try {
                Files.createDirectories(productDir);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при создании директории: " + productDir.getFileName().toString(), e);
            }
        }
    }

    private void dropProductDir(Product product) {
        Path productDir = Paths.get(fileUploadDirectory + product.getVendorCode());
        if (Files.exists(productDir)) {
            try {
                Files.walk(productDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при удалении папки с изображениями продукта", e);
            }
        }
    }

    private void showMessage(String action, Product product) {
        Notification notification = new Notification("", 3000, Notification.Position.TOP_END);
        notification.setId("action_status");
        switch (action) {
            case "CREATE":
                notification.setText("Product: " + product.getTitle() + " successful created");
                break;

            case "UPDATE":
                notification.setText("Product: " + product.getTitle() + " successful updated");
                break;

            case "DELETE":
                notification.setText("Product: " + product.getTitle() + " successful deleted");
                break;
        }
        notification.open();
    }

}
