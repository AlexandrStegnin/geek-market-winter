package com.geekbrains.geekmarketwinter;

import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.repositories.ProductRepository;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

@Route("shop")
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

        grid.addColumn(Product::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("10px")
                .setFlexGrow(1);

        grid.addColumn(Product::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(new NumberRenderer<>(
                Product::getPrice,
                NumberFormat.getCurrencyInstance()))
                .setHeader("Price")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(product -> product.getCategory().getTitle())
                .setHeader("Category")
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

        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        appLayout.setContent(grid);
        add(appLayout);
        setHeight("100vh");
//      Setup Binder For Fields
//
//        Binder<Product> binder = new Binder<>(Product.class);
//        Editor<Product> editor = grid.getEditor();
//        editor.setBinder(binder);
//        editor.setBuffered(true);
//
//        Div validationStatus = new Div();
//        validationStatus.setId("validation");
//
//        TextField field = new TextField();
//        binder.forField(field)
//                .withValidator(name -> name.startsWith("Person"),
//                        "Name should start with Person")
//                .withStatusLabel(validationStatus).bind("title");
//        titleColumn.setEditorComponent(field);
//
//        Grid.Column<Product> editorColumn = grid.addComponentColumn(product -> {
//            Button edit = new Button("Edit");
//            edit.addClassName("edit");
//            edit.addClickListener(e -> editor.editItem(product));
//            return edit;
//        });
//
//        Button save = new Button("Save", e -> editor.save());
//        save.addClassName("save");
//
//        Button cancel = new Button("Cancel", e -> editor.cancel());
//        cancel.addClassName("cancel");
//
// Add a keypress listener that listens for an escape key up event.
// Note! some browsers return key as Escape and some as Esc
//        grid.getElement().addEventListener("keyup", event -> editor.cancel())
//                .setFilter("event.key === 'Escape' || even.key === 'Esc'");
//
//        Div buttons = new Div(save, cancel);
//        editorColumn.setEditorComponent(buttons);
//
//        editor.addSaveListener(
//                event -> message.setText(event.getItem().getLogin() + ", "
//                        + event.getItem().isEnabled()));
//
    }

    private List<Product> getAllProducts() {
        return productRepo.findAll();
    }
}
