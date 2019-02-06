package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.repositories.ProductRepository;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.geekbrains.geekmarketwinter.config.support.Constants.SHOP_PAGE;

@PageTitle("Shop")
@Route(SHOP_PAGE)
@Theme(value = Material.class)
public class ShopView extends VerticalLayout {

    private final AuthRepository auth;
    private final ProductRepository productRepo;
    private final ShoppingCartService cartService;
    private ListDataProvider<Product> dataProvider;
    private Grid<Product> grid;

    public ShopView(AuthRepository auth,
                    ProductRepository productRepo,
                    ShoppingCartService cartService) {
        this.cartService = cartService;
        this.productRepo = productRepo;
        this.dataProvider = new ListDataProvider<>(getAllProducts());
        this.grid = new Grid<>();
        this.auth = auth;
        init();
    }

    private void init() {

        grid.setDataProvider(dataProvider);
        List<ValueProvider<Product, String>> valueProviders = new ArrayList<>();
        valueProviders.add(Product::getTitle);
        valueProviders.add(product -> String.valueOf(product.getVendorCode()));
        valueProviders.add(product -> String.valueOf(product.getPrice()));
        valueProviders.add(product -> String.valueOf(product.getCategory().getTitle()));

        Iterator<ValueProvider<Product, String>> iterator = valueProviders
                .iterator();

        grid.addColumn(Product::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(iterator.next())
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
        Iterator<ValueProvider<Product, String>> iterator2 = valueProviders
                .iterator();

        grid.getColumns().forEach(column -> {
            if (!Objects.equals(null, column.getKey()) && (column.getKey().equalsIgnoreCase("title") ||
                    column.getKey().equalsIgnoreCase("code") ||
                    column.getKey().equalsIgnoreCase("price") ||
                    column.getKey().equalsIgnoreCase("category") )) {
                TextField field = new TextField();
                ValueProvider<Product, String> valueProvider = iterator2.next();

                field.addValueChangeListener(event -> dataProvider
                        .addFilter(product -> StringUtils.containsIgnoreCase(
                                valueProvider.apply(product), field.getValue())));

                field.setValueChangeMode(ValueChangeMode.EAGER);

                filterRow.getCell(column).setComponent(field);
                field.setSizeFull();
                field.setPlaceholder("Filter");
            }
        });

        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        add(appLayout);
        setHeight("100vh");
    }

    private List<Product> getAllProducts() {
        return productRepo.findAll();
    }
}
