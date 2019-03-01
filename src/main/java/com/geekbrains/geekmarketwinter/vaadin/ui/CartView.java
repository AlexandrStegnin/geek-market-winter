package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.entites.OrderItem;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.ArrayList;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.CART_PAGE;
import static com.geekbrains.geekmarketwinter.config.support.Constants.CONFIRM_ORDER_PAGE;

@Route(CART_PAGE)
@PageTitle("Cart")
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
        if (cartService.getTotalQuantity() > 0L) {
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

            FooterRow footerRow = grid.appendFooterRow();
            Label total = new Label("Total");
            total.getStyle().set("font-size", "18px");
            total.getStyle().set("color", "black");
            footerRow.getCell(titleColumn).setComponent(total);

            Label price = new Label();
            price.getStyle().set("font-size", "18px");
            price.getStyle().set("color", "black");
            footerRow.getCell(priceColumn).setComponent(price);

            Label quantity = new Label();
            quantity.getStyle().set("font-size", "18px");
            quantity.getStyle().set("color", "black");
            updateTotalRow(price, quantity);

            Grid.Column<OrderItem> quantityColumn = grid.addComponentColumn(
                    orderItem -> {
                        Div div = new Div();
                        Button qnty = new Button(orderItem.getQuantity().toString());
                        qnty.setDisableOnClick(true);

                        Button btnMinus = new Button("", VaadinIcon.MINUS.create(),
                                e -> {
                                    int qnt = Integer.parseInt(qnty.getText());
                                    if (qnt > 0 && qnt - 1 > 0) {
                                        cartService.setProductCount(VaadinService.getCurrentRequest(), orderItem.getProduct(), qnt - 1L);
                                        qnty.setText(String.valueOf(qnt - 1));
                                        dataProvider.refreshItem(orderItem);
                                    } else {
                                        cartService.removeFromCart(VaadinService.getCurrentRequest(), orderItem.getProduct());
                                        qnty.setText(String.valueOf(qnt + 1));
                                        dataProvider.getItems().remove(orderItem);
                                        dataProvider.refreshAll();
                                    }
                                    updateTotalRow(price, quantity);
                                    updateBadge(quantity.getText());
                                });

                        Button btnPlus = new Button("", VaadinIcon.PLUS.create(),
                                e -> {
                                    int qnt = Integer.parseInt(qnty.getText());
                                    cartService.setProductCount(VaadinService.getCurrentRequest(), orderItem.getProduct(), qnt + 1L);
                                    dataProvider.refreshAll();
                                    updateTotalRow(price, quantity);
                                    updateBadge(quantity.getText());
                                });

                        div.add(btnMinus, qnty, btnPlus);
                        return div;
                    })
                    .setHeader("Quantity")
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setFlexGrow(1);

            quantityColumn.setEditorComponent(new Div());

            footerRow.getCell(quantityColumn).setComponent(quantity);

            Button confirm = new Button("Confirm", buttonClickEvent -> confirm());

            Button clearCart = new Button("Clear cart", buttonClickEvent -> {
                clearCart(price, quantity);
            });
            clearCart.getStyle().set("margin-right", "50px");
            HorizontalLayout buttons = new HorizontalLayout(confirm, clearCart);

            VerticalLayout box = new VerticalLayout(grid, buttons);
            box.setAlignItems(Alignment.END);
            CustomAppLayout appLayout = new CustomAppLayout(auth, box);

            add(appLayout);
            setHeight("100vh");
        } else {
            Span span = new Span("Your cart is empty");
            span.getStyle()
                    .set("position", "absolute")
                    .set("top", "50%")
                    .set("left", "45%")
                    .set("font-size", "20px");
            Div empty = new Div(span);
            empty.setSizeFull();
            CustomAppLayout appLayout = new CustomAppLayout(auth, empty);
            add(appLayout);
        }
    }

    private void clearCart(Label price, Label quantity) {
        Dialog dialog = VaadinViewUtils.initDialog();
        Button yes = new Button("Yes");

        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(yes, cancel);

        VerticalLayout content = new VerticalLayout();
        Div contentText = new Div();
        contentText.setText("Confirm clear cart?");
        content.add(contentText, actions);
        yes.addClickListener(e -> {
            cartService.resetCart(VaadinRequest.getCurrent());
            dataProvider.getItems().clear();
            dataProvider.refreshAll();
            updateTotalRow(price, quantity);
            updateBadge(quantity.getText());
            dialog.close();
        });

        dialog.add(content);
        dialog.open();
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

    private void updateTotalRow(Label price, Label quantity) {
        price.setText(cartService.getCurrentCart(VaadinService.getCurrentRequest()).getTotalCost().toString());
        quantity.setText(getCartItems().stream().map(OrderItem::getQuantity).reduce(0L, (a, b) -> a + b).toString());
    }

    private void updateBadge(String quantity) {
        getUI().ifPresent(ui -> ui.getElement().getChildren().forEach(element -> {
            if (element.getTag().equalsIgnoreCase("paper-badge")) {
                if (Integer.valueOf(quantity) > 0) {
                    element.setAttribute("label", quantity);
                    element.setVisible(true);
                } else {
                    element.setVisible(false);
                }
            }
        }));
    }

}
