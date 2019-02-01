package com.geekbrains.geekmarketwinter;

import com.geekbrains.geekmarketwinter.entites.DeliveryAddress;
import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.entites.OrderItem;
import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.repositories.UserRepository;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.utils.ShoppingCart;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Theme(Material.class)
@Route("confirm-order")
public class ConfirmView extends VerticalLayout {

    private final AuthRepository auth;
    private final ShoppingCartService cartService;
    private final UserRepository userRepo;
    private final OrderService orderService;

    public ConfirmView(AuthRepository auth,
                       ShoppingCartService cartService,
                       UserRepository userRepo,
                       OrderService orderService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userRepo = userRepo;
        this.auth = auth;
        init();
    }

    private void init() {

        FormLayout formLayout = new FormLayout();
        Binder<Order> binder = new Binder<>();
        Order orderToBeCreated = new Order();

        TextField address = new TextField();
        TextField phone = new TextField();

        formLayout.addFormItem(address, "Delivery address");
        formLayout.addFormItem(phone, "Contact phone");

        Button btnPay = new Button("Pay order", e -> payOrder());
        Button btnCancel = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate("cart")));

        HorizontalLayout actions = new HorizontalLayout();
        actions.add(btnPay, btnCancel);

        formLayout.addFormItem(actions, "");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("21em", 2),
                new FormLayout.ResponsiveStep("22em", 3));

        SerializablePredicate<String> phonePredicate = value -> !phone
                .getValue().trim().isEmpty();

        Binder.Binding<Order, String> phoneBinding = binder.forField(phone)
                .withValidator(phonePredicate,
                        "Both phone cannot be empty")
                .bind(Order::getPhoneNumber, Order::setPhoneNumber);

        // Trigger cross-field validation when the other field is changed
        phone.addValueChangeListener(event -> phoneBinding.validate());

        address.setRequiredIndicatorVisible(true);

        binder.forField(address)
                .withValidator(new StringLengthValidator(
                        "Please add the delivery address", 10, null))
                .bind(order -> order.getDeliveryAddress().getAddress(),
                        (order, s) -> order.getDeliveryAddress().setAddress(s));

        btnPay.addClickListener(event -> {
            ShoppingCart cart = cartService.getCurrentCart(VaadinService.getCurrentRequest());
            List<OrderItem> items = cart.getItems();
            orderToBeCreated.setUser(userRepo.findOneByUserName("admin"));
            orderToBeCreated.setOrderItems(items);
            orderToBeCreated.setPrice(cart.getTotalCost());
            orderToBeCreated.setDeliveryPrice(100D);
            orderToBeCreated.setDeliveryAddress(
                    new DeliveryAddress(orderToBeCreated.getUser(), address.getValue())
            );
            orderToBeCreated.setPhoneNumber(phone.getValue());
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setId(1L);
            orderStatus.setTitle("Сформирован");
            orderToBeCreated.setStatus(orderStatus);

            if (binder.writeBeanIfValid(orderToBeCreated)) {
                orderService.createOrder(orderToBeCreated);
                Notification.show("Saved bean values!");
                getUI().ifPresent(ui -> ui.navigate("shop"));
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
    }

    private void payOrder() {
    }
}
