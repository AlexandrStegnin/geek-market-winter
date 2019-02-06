package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.entites.OrderItem;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.ArrayList;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.CART_PAGE;
import static com.geekbrains.geekmarketwinter.config.support.Constants.CONFIRM_ORDER_PAGE;

@Route(CART_PAGE)
@Theme(value = Material.class)
public class CartView extends VerticalLayout {

    private final AuthRepository auth;
    private final ShoppingCartService cartService;
    private ListDataProvider<OrderItem> dataProvider;
    private Grid<OrderItem> grid;

    public CartView(AuthRepository auth,
                    ShoppingCartService cartService) {
        this.cartService = cartService;
        this.dataProvider = new ListDataProvider<>(getCartItems());
        this.grid = new Grid<>();
        this.auth = auth;
        init();
    }

    private void init() {

        grid.setDataProvider(dataProvider);
        grid.setHeightByRows(true);

        Grid.Column<OrderItem> titleColumn = grid
                .addColumn(orderItem -> orderItem.getProduct().getTitle())
                .setHeader(new Label("Title"))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<OrderItem> priceColumn = grid
                .addColumn(orderItem -> orderItem.getProduct().getPrice())
                .setHeader(new Label("Price"))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(orderItem -> orderItem.getProduct().getShortDescription())
                .setHeader("Short description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(2);

        grid.addColumn(orderItem -> orderItem.getProduct().getFullDescription())
                .setHeader("Full description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<OrderItem> quantityColumn = grid.addComponentColumn(
                orderItem -> {
                    Div div = new Div();
                    Button quantity = new Button(orderItem.getQuantity().toString());
                    quantity.setDisableOnClick(true);

                    Button btnMinus = new Button("", VaadinIcon.MINUS.create(),
                            e -> {
                                int qnt = Integer.parseInt(quantity.getText());
                                if (qnt > 0 && qnt - 1 > 0) {
                                    cartService.setProductCount(VaadinService.getCurrentRequest(), orderItem.getProduct(), qnt - 1L);
                                    quantity.setText(String.valueOf(qnt - 1));
                                    dataProvider.refreshItem(orderItem);
                                } else {
                                    cartService.removeFromCart(VaadinService.getCurrentRequest(), orderItem.getProduct());
                                    quantity.setText(String.valueOf(qnt + 1));
                                    dataProvider.getItems().remove(orderItem);
                                    dataProvider.refreshAll();
                                }
                            });

                    Button btnPlus = new Button("", VaadinIcon.PLUS.create(),
                            e -> {
                                int qnt = Integer.parseInt(quantity.getText());
                                cartService.setProductCount(VaadinService.getCurrentRequest(), orderItem.getProduct(), qnt + 1L);
                                dataProvider.refreshItem(orderItem);
                            });

                    div.add(btnMinus, quantity, btnPlus);
                    return div;
                })
                .setHeader("Quantity")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        FooterRow footerRow = grid.appendFooterRow();
        Label total = new Label("Total");
        total.getStyle().set("font-size", "18px");
        total.getStyle().set("color", "black");
        footerRow.getCell(titleColumn).setComponent(total);

        Label price = new Label(cartService.getCurrentCart(VaadinService.getCurrentRequest()).getTotalCost().toString());
        price.getStyle().set("font-size", "18px");
        price.getStyle().set("color", "black");
        footerRow.getCell(priceColumn).setComponent(price);

        Label quantity = new Label();
        quantity.getStyle().set("font-size", "18px");
        quantity.getStyle().set("color", "black");
        quantity.setText(getCartItems().stream().map(OrderItem::getQuantity).reduce(0L, (a, b) -> a + b).toString());
        footerRow.getCell(quantityColumn).setComponent(quantity);

        Button confirm = new Button("Confirm", buttonClickEvent -> confirm());
        confirm.getStyle().set("margin-right", "50px");
        VerticalLayout box = new VerticalLayout(grid, confirm);
        box.setAlignItems(Alignment.END);
        CustomAppLayout appLayout = new CustomAppLayout(auth, box);

        add(appLayout);
        setHeight("100vh");
    }

    private List<OrderItem> getCartItems() {
        return new ArrayList<>(
                cartService.getCurrentCart(
                        VaadinService.getCurrentRequest())
                        .getItems()
        );
    }

    private void confirm() {
        this.getUI().ifPresent(ui -> ui.navigate(CONFIRM_ORDER_PAGE));
    }
}
