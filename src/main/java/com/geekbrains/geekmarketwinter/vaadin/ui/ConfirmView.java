package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.config.amqp.OrderMessageSender;
import com.geekbrains.geekmarketwinter.entites.DeliveryAddress;
import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.DeliveryAddressService;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.geekbrains.geekmarketwinter.config.support.Constants.*;

@PageTitle("Confirm")
@Route(CONFIRM_ORDER_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class ConfirmView extends VerticalLayout {

    private final AuthRepository auth;
    private final OrderService orderService;
    private final DeliveryAddressService addressService;
    private final OrderMessageSender orderMessageSender;
    private String delivAddress;

    public ConfirmView(AuthRepository auth,
                       OrderService orderService,
                       DeliveryAddressService addressService,
                       OrderMessageSender orderMessageSender) {
        this.orderMessageSender = orderMessageSender;
        this.addressService = addressService;
        this.orderService = orderService;
        this.auth = auth;
        init();
    }

    private void init() {
        // TODO: 2019-02-25 Сбрасывать данные корзины
        FormLayout formLayout = new FormLayout();
        Binder<Order> binder = new Binder<>();

        ComboBox<DeliveryAddress> deliveryAddress = new ComboBox<>();
        deliveryAddress.setLabel("Выбрать адрес доставки");
        deliveryAddress.setItems(addressService.getUserAddresses(1L)); // TODO 2019-02-02 заменить на пользователя системы
        deliveryAddress.setItemLabelGenerator(DeliveryAddress::getAddress);
        deliveryAddress.setRequired(false);

        TextField phone = new TextField();
        phone.setLabel("Phone");
        phone.setPlaceholder("999999");

        Button btnPay = new Button("Pay order");
        Button btnCancel = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate(CART_PAGE)));

        formLayout.add(deliveryAddress, phone, btnPay, btnCancel);
        formLayout.setHeight("100%");
        formLayout.setSizeUndefined();

        SerializablePredicate<String> phonePredicate = value -> !phone
                .getValue().trim().isEmpty();

        Binder.Binding<Order, String> phoneBinding = binder.forField(phone)
                .withValidator(phonePredicate,
                        "Both phone cannot be empty")
                .bind(Order::getPhoneNumber, Order::setPhoneNumber);

        // Trigger cross-field validation when the other field is changed
        phone.addValueChangeListener(event -> phoneBinding.validate());

        deliveryAddress.setRequiredIndicatorVisible(true);
        deliveryAddress.setAllowCustomValue(true);
        deliveryAddress.setReadOnly(false);
        deliveryAddress.addCustomValueSetListener(event -> delivAddress = event.getDetail());

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
                orderMessageSender.sendOrder(
                        orderService.makeOrderToProduce(finalOrder)
                );
                Notification notification = new Notification("Order have been confirmed", 3000,
                        Notification.Position.TOP_END);
                notification.open();

                getUI().ifPresent(ui -> ui.navigate(SHOP_PAGE));
            } else {
                BinderValidationStatus<Order> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses()
                        .stream().filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));
                Notification.show("There are errors: " + errorText);
            }
        });

        CustomAppLayout appLayout = new CustomAppLayout(auth, formLayout);

        add(appLayout);
        setHeight("100vh");
        setMargin(true);
        setAlignItems(Alignment.CENTER);
        setAlignSelf(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

    }
}
