package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.services.ProductService;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
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

/**
 * @author stegnin
 */

@PageTitle("Admin products")
@Route(ADMIN_PRODUCTS_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class ProductView extends VerticalLayout {

    private final AuthRepository auth;
    private final ProductService productService;
//    private ConfigurableFilterDataProvider<Product, String, Category> dataProvider;
    private ListDataProvider<Product> dataProvider;
    private Grid<Product> grid;
    private ProductFilter productFilter;
    private Page<Product> page;
    private final Button addNewBtn;
    private final CategoryService categoryService;

    public ProductView(AuthRepository auth,
                       ProductService productService,
                       CategoryService categoryService) {
        this.productService = productService;
        this.grid = new Grid<>();
        this.productFilter = new ProductFilter();
        this.categoryService = categoryService;
        this.page = productService.findAll(productFilter, Pageable.unpaged());
        this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create(), e -> showAddDialog());
        this.auth = auth;
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);
        dataProvider = new ListDataProvider<>(getAllProducts());
        grid.setDataProvider(dataProvider);

        grid.addColumn(Product::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(Product::getTitle)
                .setHeader("Title")
                .setKey("title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Product::getVendorCode)
                .setHeader("Vendor code")
                .setKey("code")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(new NumberRenderer<>(
                Product::getPrice,
                NumberFormat.getCurrencyInstance()))
                .setHeader("Price")
                .setKey("price")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(product -> product.getCategory().getTitle())
                .setHeader("Category")
                .setKey("category")
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
                                FormatStyle.MEDIUM)
                )
        )
                .setHeader("Created at")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

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

    private void showAddDialog() {

        FormLayout formLayout = new FormLayout();

        Dialog dialog = new Dialog();
        TextField vendorCode = new TextField("Vendor code");
        vendorCode.setPlaceholder("Enter vendor code");

        // TODO: 13.02.2019 Доделать загрузку картинок

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(),
//                    buffer.getInputStream(event.getFileName()));
//            showOutput(event.getFileName(), component);
        });

        ComboBox<Category> categoryComboBox = new ComboBox<>("Select category", getAllCategories());

        TextField titleField = new TextField("Title");
        titleField.setPlaceholder("Enter title");

        TextField shortDescr = new TextField("Short description");
        shortDescr.setPlaceholder("Enter short description");

        TextField fullDescr = new TextField("Full description");
        fullDescr.setPlaceholder("Enter full description");

        TextField price = new TextField("Price");
        price.setPlaceholder("Enter price");

        formLayout.add(vendorCode, titleField, shortDescr, fullDescr, price, upload);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            Product product = new Product(categoryComboBox.getValue(), vendorCode.getValue(), titleField.getValue(),
                    shortDescr.getValue(), fullDescr.getValue(), Double.valueOf(price.getValue()));
            addNewProduct(product);
            dialog.close();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();
        content.add(formLayout, actions);
        dialog.add(content);
        dialog.open();
        vendorCode.getElement().callFunction("focus");
    }

    private void addNewProduct(Product product) {
        product = productService.create(product);
        dataProvider.getItems().add(product);
        dataProvider.refreshAll();
    }

    private List<Product> getAllProducts() {
        return productService.findAll(productFilter, Pageable.unpaged()).getContent();
    }

}
