package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.services.ProductService;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;

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
    private final ShoppingCartService cartService;
    private ConfigurableFilterDataProvider<Product, String, Category> dataProvider;
    private Grid<Product> grid;
    private ProductFilter productFilter;
    private Page<Product> page;
    private final CategoryService categoryService;

    public ProductView(AuthRepository auth,
                    ProductService productService,
                    ShoppingCartService cartService,
                    CategoryService categoryService) {
        this.cartService = cartService;
        this.productService = productService;
        this.grid = new Grid<>();
        this.productFilter = new ProductFilter();
        this.categoryService = categoryService;
        this.page = productService.findAll(productFilter, Pageable.unpaged());
        this.auth = auth;
        init();
    }

    private void init() {
        dataProvider = getDataProvider(productService);
        grid.setDataProvider(dataProvider);
//        List<ValueProvider<Product, String>> valueProviders = new ArrayList<>();
//        valueProviders.add(Product::getTitle);
//        valueProviders.add(product -> String.valueOf(product.getVendorCode()));
//        valueProviders.add(product -> String.valueOf(product.getPrice()));
//        valueProviders.add(product -> String.valueOf(product.getCategory().getTitle()));
//
//        Iterator<ValueProvider<Product, String>> iterator = valueProviders
//                .iterator();

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

        grid.addComponentColumn(
                product -> {
                    Button button = new Button("Add to cart",
                            e -> cartService.addToCart(VaadinService.getCurrentRequest(), product));
                    button.setIconAfterText(false);
                    button.setSizeFull();
                    return button;
                })
                .setHeader("Actions")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        HeaderRow filterRow = grid.appendHeaderRow();
//        Iterator<ValueProvider<Product, String>> iterator2 = valueProviders
//                .iterator();

        grid.getColumns().forEach(column -> {
            if (!Objects.equals(null, column.getKey())) {
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
        });


        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        add(appLayout);
        setHeight("100vh");
    }

    private ConfigurableFilterDataProvider<Product, String, Category> getDataProvider(ProductService service) {
        DataProvider<Product, ProductFilter> dataProvider =
                DataProvider.fromFilteringCallbacks(query -> {
                    // getFilter returns Optional<String>
                    productFilter = query.getFilter().orElse(new ProductFilter());
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    page = service.findAll(
                            productFilter, PageRequest.of(offset, limit));
                    return page.getContent().stream();
                }, query -> {
                    productFilter = query.getFilter().orElse(new ProductFilter());
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    return service.countByFilter(productFilter, PageRequest.of(offset, limit));
                });

        return dataProvider
                .withConfigurableFilter(
                        ProductFilter::new);
    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

}
