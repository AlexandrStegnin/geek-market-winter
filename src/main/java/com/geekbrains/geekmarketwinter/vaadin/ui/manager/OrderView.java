package com.geekbrains.geekmarketwinter.vaadin.ui.manager;

import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.services.OrderStatusService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.LOCALE_RU;
import static com.geekbrains.geekmarketwinter.config.support.Constants.MANAGER_ORDERS_PAGE;

@PageTitle("Manage orders")
@Route(MANAGER_ORDERS_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class OrderView extends VerticalLayout {
    private final OrderService orderService;
    private final OrderStatusService orderStatusService;
    private Grid<Order> grid;
    private final AuthService auth;
    private ListDataProvider<Order> dataProvider;
    private Order order;

    public OrderView(OrderService orderService, AuthService auth, OrderStatusService orderStatusService) {
        this.orderService = orderService;
        this.orderStatusService = orderStatusService;
        this.dataProvider = new ListDataProvider<>(getAllOrders());
        this.grid = new Grid<>();
        this.auth = auth;
        this.order = new Order();
        init();
    }

    private void init() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(LOCALE_RU);

        grid.setDataProvider(dataProvider);

        grid.addColumn(Order::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(new NumberRenderer<>(Order::getPrice, currencyFormat))
                .setHeader("Price")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        // NumberRenderer to render numbers in general
        grid.addColumn(new NumberRenderer<>(Order::getDeliveryPrice, currencyFormat))
                .setHeader("Delivery price")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        // You can also set complex objects directly. Internal properties of the
        // bean are accessible in the template.
        grid.addColumn(TemplateRenderer.<Order> of(
                "<div>[[item.deliveryAddress.address]],<br>number <small>[[item.deliveryAddress.id]]</small></div>")
                .withProperty("deliveryAddress", Order::getDeliveryAddress))
                .setHeader("Address")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(TemplateRenderer.<Order> of(
                "<div>[[item.user.lastName]] [[item.user.firstName]], <br><small>[[item.user.email]]</small></div>")
                .withProperty("user", Order::getUser))
                .setHeader("User")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        // LocalDateTimeRenderer for date and time
        grid.addColumn(new LocalDateTimeRenderer<>(Order::getCreateAt,
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT,
                        FormatStyle.MEDIUM)))
                .setHeader("Created at")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(2);

        grid.addColumn(new LocalDateTimeRenderer<>(Order::getDeliveryDate,
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)))
                .setHeader("Delivery date")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<Order> statusColumn = grid.addColumn(order -> order.getStatus().getTitle())
                .setHeader("Status")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Binder<Order> binder = new Binder<>(Order.class);
        Editor<Order> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        ComboBox<OrderStatus> comboBox = new ComboBox<>();
        comboBox.setItemLabelGenerator(OrderStatus::getTitle);
        comboBox.setItems(getStatuses());

        comboBox.addValueChangeListener(event -> {
            OrderStatus status = comboBox.getValue();
            if (status != null) {
                order.setStatus(status);
            }
        });

        binder.bind(comboBox, "status");
        statusColumn.setEditorComponent(comboBox);
        statusColumn.setTextAlign(ColumnTextAlign.CENTER);
        statusColumn.setFlexGrow(1);

        Grid.Column<Order> editorColumn = grid.addComponentColumn(order -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> editor.editItem(order));
            return edit;
        });

        editorColumn
                .setHeader("Actions")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        Notification message = new Notification("", 3000, Notification.Position.TOP_END);

        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        add(appLayout);
        setHeight("100vh");

        editor.addSaveListener(event -> {
            Order updatedOrder = event.getItem();
            binder.writeBeanIfValid(updatedOrder);
            grid.getDataProvider().refreshAll();
            orderService.update(updatedOrder);
            message.setText("Order successful updated: New status = " + updatedOrder.getStatus().getTitle());
            message.open();
        });
    }

    private List<OrderStatus> getStatuses() {
        List<OrderStatus> statuses;
        statuses = orderStatusService.findAll();
        return statuses;
    }

    private List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
