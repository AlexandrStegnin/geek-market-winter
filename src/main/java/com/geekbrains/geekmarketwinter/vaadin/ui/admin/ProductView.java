package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.config.support.OperationEnum;
import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.entites.Product_;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.CategoryService;
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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_PRODUCTS_PAGE;
import static com.geekbrains.geekmarketwinter.config.support.Constants.LOCALE_RU;

/**
 * @author stegnin
 */

@PageTitle("Admin products")
@Route(ADMIN_PRODUCTS_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class ProductView extends VerticalLayout {

    private final AuthRepository auth;
    private final ProductService productService;
    private ListDataProvider<Product> dataProvider;
    private Grid<Product> grid;
    private ProductFilter productFilter;
    private Page<Product> page;
    private final Button addNewBtn;
    private final CategoryService categoryService;
    private Binder<Product> binder;


    public ProductView(AuthRepository auth,
                       ProductService productService,
                       CategoryService categoryService) {
        this.productService = productService;
        this.grid = new Grid<>();
        this.productFilter = new ProductFilter();
        this.categoryService = categoryService;
        this.page = productService.findAll(productFilter, Pageable.unpaged());
        this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create(), e -> showDialog(new Product(),
                OperationEnum.CREATE));
        this.dataProvider = new ListDataProvider<>(getAllProducts(productFilter));
        this.binder = new BeanValidationBinder<>(Product.class);
        this.auth = auth;
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);
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

        grid.addComponentColumn(product ->
                VaadinViewUtils.makeEditorColumnActions(
                        e -> showDialog(product, OperationEnum.UPDATE),
                        e -> showDialog(product, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setFlexGrow(2);

        /*HeaderRow filterRow = grid.appendHeaderRow();

        grid.getColumns().forEach(column -> {

            if (!Objects.equals(null, column.getKey())) {
                // TODO: 13.02.2019 Фильтрация
                if (column.getKey().equalsIgnoreCase("title")) {
                    TextField field = new TextField();
                    field.setValueChangeMode(ValueChangeMode.EAGER);
                    field.setSizeFull();
                    field.setPlaceholder("Filter");
                    field.addValueChangeListener(event -> {
                        if (Objects.equals(null, productFilter)) productFilter = new ProductFilter();
                        productFilter.setTitle(event.getValue());
                        dataProvider.refreshAll();
                    });
                    filterRow.getCell(column).setComponent(field);
                } else if (column.getKey().equalsIgnoreCase("category")) {
                    ComboBox<Category> comboBox = new ComboBox<>();
                    comboBox.setItemLabelGenerator(Category::getTitle);
                    comboBox.setItems(getAllCategories());
                    comboBox.addValueChangeListener(event -> {
                        if (Objects.equals(null, productFilter)) productFilter = new ProductFilter();
                        productFilter.setCategory(event.getValue());
                        dataProvider.setFilter(event.getValue());
                        dataProvider.refreshAll();
                    });
                    filterRow.getCell(column).setComponent(comboBox);
                }
            }
        });*/
        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(Alignment.END);
        CustomAppLayout appLayout = new CustomAppLayout(auth, verticalLayout);
        add(appLayout);
        setHeight("100vh");
    }

//    private ConfigurableFilterDataProvider<Product, String, Category> getDataProvider(ProductService service) {
//        DataProvider<Product, ProductFilter> dataProvider =
//                DataProvider.fromFilteringCallbacks(query -> {
//                    // getFilter returns Optional<String>
//                    productFilter = query.getFilter().orElse(new ProductFilter());
//                    int offset = query.getOffset();
//                    int limit = query.getLimit();
//                    page = service.findAll(
//                            productFilter, PageRequest.of(offset, limit));
//                    return page.getContent().stream();
//                }, query -> {
//                    productFilter = query.getFilter().orElse(new ProductFilter());
//                    int offset = query.getOffset();
//                    int limit = query.getLimit();
//                    return service.countByFilter(productFilter, PageRequest.of(offset, limit));
//                });
//
//        return dataProvider
//                .withConfigurableFilter(
//                        ProductFilter::new);
//    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    private void saveProduct(Product product) {
        productService.save(product);
        dataProvider.refreshAll();
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
        TextField vendorCode = new TextField("Vendor code");
        vendorCode.setValue(product.getVendorCode() == null ? "" : product.getVendorCode());
        binder.forField(vendorCode)
                .bind(Product_.VENDOR_CODE);

        TextField title = new TextField("Title");
        title.setValue(product.getTitle() == null ? "" : product.getTitle());
        binder.forField(title)
                .bind(Product_.TITLE);

        TextField shortDescription = new TextField("Short description");
        shortDescription.setValue(product.getShortDescription() == null ? "" : product.getShortDescription());
        binder.forField(shortDescription)
                .bind(Product_.SHORT_DESCRIPTION);

        TextField fullDescription = new TextField("Full description");
        fullDescription.setValue(product.getFullDescription() == null ? "" : product.getFullDescription());
        binder.forField(fullDescription)
                .bind(Product_.FULL_DESCRIPTION);

        TextField price = new TextField("Price");
        price.setValue(product.getPrice() == 0 ? String.valueOf(0d) : String.valueOf(product.getPrice()));
        binder.forField(price)
                // input should not be null or empty
                .withValidator(string -> string != null && !string.isEmpty(), "Input values should not be empty")
                // convert String to Integer, throw ValidationException if String is in incorrect format
                .withConverter(new StringToDoubleConverter("Input value should be an integer"))
                // validate converted integer: it should be positive
                .withValidator(dbl -> dbl > 0, "Input value should be a positive double")
                .bind(Product_.PRICE);

        Upload images = new Upload();

        // TODO: 13.02.2019 Доделать загрузку картинок

        formLayout.add(category, vendorCode, title, shortDescription, fullDescription, price, images);

        Dialog dialog = VaadinViewUtils.initDialog();
        Button save = new Button("Save");

        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();

        switch (operation) {
            case UPDATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(product)) {
                        saveProduct(product);
                        dialog.close();
                    }
                });
                break;
            case CREATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(product)) {
                        dataProvider.getItems().add(product);
                        saveProduct(product);
                        dialog.close();
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
                    dialog.close();
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        vendorCode.getElement().callFunction("focus");

    }

}
