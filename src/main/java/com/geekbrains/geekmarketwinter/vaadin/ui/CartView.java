package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.config.support.DeliveryTypeEnum;
import com.geekbrains.geekmarketwinter.config.support.PaymentTypeEnum;
import com.geekbrains.geekmarketwinter.entites.*;
import com.geekbrains.geekmarketwinter.services.DeliveryAddressService;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.*;

import static com.geekbrains.geekmarketwinter.config.support.Constants.CART_PAGE;
import static com.geekbrains.geekmarketwinter.config.support.Constants.PAYPAL_BUY_URL;

@Route(CART_PAGE)
@PageTitle("Cart")
@Theme(value = Material.class)
public class CartView extends CustomAppLayout {

    private final ShoppingCartService cartService;
    private final DeliveryAddressService deliveryAddressService;
    private final OrderService orderService;
    private Binder<Order> binder;
    private ListDataProvider<OrderItem> dataProvider;
    private VerticalLayout deliveryAddressLayout;
    private Grid<OrderItem> grid;
    private String delivAddress;
    private TextField phone;
    private ComboBox<DeliveryAddress> deliveryAddress;

    public CartView(ShoppingCartService cartService, DeliveryAddressService deliveryAddressService,
                    OrderService orderService) {
        this.deliveryAddressService = deliveryAddressService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.dataProvider = new ListDataProvider<>(getCartItems());
        this.grid = new Grid<>();
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

            VerticalLayout deliveryTypeLayout = createDeliveryTypeLayout();
            deliveryTypeLayout.setVisible(false);
            deliveryAddressLayout = createDeliveryAddressLayout();
            deliveryAddressLayout.setVisible(false);

            Button confirmBtn = new Button("Confirm", buttonClickEvent -> confirm(deliveryTypeLayout));

            Button clearCart = new Button("Clear cart", buttonClickEvent -> clearCart(price, quantity));
            clearCart.getStyle().set("margin-right", "50px");
            HorizontalLayout buttons = new HorizontalLayout(confirmBtn, clearCart);

            VerticalLayout box = new VerticalLayout(grid, buttons, deliveryTypeLayout, deliveryAddressLayout);
            box.setAlignItems(FlexComponent.Alignment.END);
            setContent(box);
        } else {
            clearContent();
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
            clearContent();
            dialog.close();
        });

        dialog.add(content);
        dialog.open();
    }

    private void clearContent() {
        Div content = VaadinViewUtils.createInfoDiv("Your cart is empty");
        setContent(content);
    }

    private List<OrderItem> getCartItems() {
        return new ArrayList<>(
                cartService.getCurrentCart(
                        VaadinService.getCurrentRequest())
                        .getItems()
        );
    }

    private void confirm(VerticalLayout next) {
        next.setVisible(true);
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
                    clearContent();
                }
            }
        }));
    }

    private VerticalLayout createDeliveryTypeLayout() {
        //        Выбор типа доставки (самовывоз/доставка)
        VerticalLayout infoLayout = new VerticalLayout();
        Div infoDiv = new Div();
        ComboBox<DeliveryTypeEnum> deliveryTypeCombobox = new ComboBox<>();
        deliveryTypeCombobox.setLabel("Choose delivery type");
        deliveryTypeCombobox.setItems(DeliveryTypeEnum.values());
        infoDiv.getStyle().set("width", "50%");
        infoDiv.add(deliveryTypeCombobox);
        deliveryTypeCombobox.setWidth("45%");
        deliveryTypeCombobox.getStyle().set("margin-right", "1em");
        deliveryTypeCombobox.setItemLabelGenerator(DeliveryTypeEnum::getTitle);
        deliveryTypeCombobox.setRequired(true);
        deliveryTypeCombobox.addValueChangeListener(event -> {
            if (!Objects.equals(null, event.getValue()) && event.getValue().compareTo(DeliveryTypeEnum.DELIVERY) == 0) {
                deliveryAddressLayout.setVisible(true);
            } else {
                deliveryAddressLayout.setVisible(false);
            }
        });
        infoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        infoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        //        Выбор типа оплаты (наличные/картой онлайн/картой курьеру)

        ComboBox<PaymentTypeEnum> paymentTypeCombobox = new ComboBox<>();
        paymentTypeCombobox.setLabel("Choose payment type");
        paymentTypeCombobox.setItems(PaymentTypeEnum.values());
        paymentTypeCombobox.setItemLabelGenerator(PaymentTypeEnum::getTitle);
        paymentTypeCombobox.setWidth("45%");
        paymentTypeCombobox.setRequired(true);
        paymentTypeCombobox.addValueChangeListener(event -> {
            Optional<DeliveryTypeEnum> deliveryAddress = deliveryTypeCombobox.getOptionalValue();
            if (deliveryAddress.isPresent()) {
                if (!Objects.equals(null, event.getValue()) && event.getValue().compareTo(PaymentTypeEnum.CREDIT_CARD) == 0) {
                    getContent().setChild(4, createButtonsLayout(true).getElement());
                } else if (!Objects.equals(null, event.getValue())){
                    getContent().setChild(4, createButtonsLayout(false).getElement());
                }
            }
        });
        infoDiv.add(paymentTypeCombobox);

        infoLayout.add(infoDiv);

        return infoLayout;
    }

    private VerticalLayout createDeliveryAddressLayout() {
        VerticalLayout infoLayout = new VerticalLayout();
        Div deliveryAddressLayout = new Div();
        binder = new Binder<>();

        deliveryAddress = new ComboBox<>();
        deliveryAddress.setWidth("45%");
        deliveryAddress.getStyle().set("margin-right", "1em");
        deliveryAddress.setLabel("Choose delivery address");
        User currentUser = SecurityUtils.getCurrentUser();
        if (!Objects.equals(null, currentUser)) {
            deliveryAddress.setItems(deliveryAddressService.getUserAddresses(currentUser.getId()));
            deliveryAddress.setItemLabelGenerator(DeliveryAddress::getAddress);
            deliveryAddress.setRequired(true);
        } else {
            deliveryAddress.setItems(Collections.emptyList());
        }

        phone = new TextField();
        phone.setWidth("45%");
        phone.setLabel("Phone");
        phone.setPlaceholder("+78912345678");
        phone.setRequired(true);

        SerializablePredicate<String> phonePredicate = value -> !phone
                .getValue().trim().isEmpty();

        Binder.Binding<Order, String> phoneBinding = binder.forField(phone)
                .withValidator(phonePredicate,
                        "Phone cannot be empty")
                .bind(Order::getPhoneNumber, Order::setPhoneNumber);

        // Trigger cross-field validation when the other field is changed
        phone.addValueChangeListener(event -> phoneBinding.validate());

        deliveryAddress.setRequiredIndicatorVisible(true);
        deliveryAddress.setAllowCustomValue(true);
        deliveryAddress.setReadOnly(false);
        deliveryAddress.addCustomValueSetListener(event -> delivAddress = event.getDetail());

        deliveryAddressLayout.getStyle().set("width", "50%");
        deliveryAddressLayout.add(deliveryAddress, phone);

        infoLayout.add(deliveryAddressLayout);
        infoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        infoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return infoLayout;
    }

    private VerticalLayout createButtonsLayout(boolean pay) {
        VerticalLayout infoLayout = new VerticalLayout();
        Div buttonsLayout = new Div();
        Button btnPay = new Button("Pay with paypal");
        btnPay.setWidth("45%");
        Button btnCancel = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate(CART_PAGE)));
        // TODO: 2019-03-09 btnCancel заменить обработчик на скрытие слоёв
        btnCancel.setWidth("45%");
        Button btnConfirm = new Button("Confirm order");
        btnConfirm.setWidth("45%");
        // TODO: 2019-03-09 Добавить обработчик на кнопку "confirm", менять статус заказа и переадресовывать на shop
        btnPay.addClickListener(event -> {

            Order finalOrder = new Order();
            finalOrder.setPhoneNumber(phone.getValue());

            DeliveryAddress address = deliveryAddress.getValue();
            if (address == null) {
                address = new DeliveryAddress(null, delivAddress);
            }

            finalOrder.setDeliveryAddress(address);
            finalOrder = orderService.makeOrder(finalOrder);

            if (binder.writeBeanIfValid(finalOrder)) {
                finalOrder = orderService.saveOrder(finalOrder);
//                orderMessageSender.sendOrder(
//                        orderService.makeOrderToProduce(finalOrder)
//                );
                String orderId = finalOrder.getId().toString();
                getUI().ifPresent(ui -> ui.navigate(PAYPAL_BUY_URL + orderId));
                cartService.resetCart(VaadinRequest.getCurrent());
            }
        });

        buttonsLayout.getStyle().set("width", "50%");
        if (pay) {
            buttonsLayout.add(btnPay, btnCancel);
        } else {
            buttonsLayout.add(btnConfirm, btnCancel);
        }
        infoLayout.add(buttonsLayout);
        infoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        infoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        return infoLayout;
    }
}
