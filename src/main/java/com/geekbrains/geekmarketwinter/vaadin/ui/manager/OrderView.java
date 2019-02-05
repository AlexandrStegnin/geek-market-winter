package com.geekbrains.geekmarketwinter.vaadin.ui.manager;

import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;
import java.util.Locale;

@Route("manager/orders")
@Theme(value = Material.class, variant = Material.DARK)
public class OrderView extends VerticalLayout {
    private OrderService orderService;
    private Grid<Order> grid;
    private AuthService auth;
    private ListDataProvider<Order> dataProvider;

    public OrderView(OrderService orderService, AuthService auth) {
        this.orderService = orderService;
        this.dataProvider = new ListDataProvider<>(getAllOrders());
        this.grid = new Grid<>();
        this.auth = auth;
        init();
    }

    private void init() {

        grid.setDataProvider(dataProvider);

        grid.addColumn(Order::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("10px")
                .setFlexGrow(0);

        Grid.Column<Order> priceColumn = grid.addColumn(Order::getPrice)
                .setHeader("Price")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<Order> deliveryPriceColumn = grid.addColumn(Order::getDeliveryPrice)
                .setHeader("Delivery price")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        // NumberRenderer to render numbers in general
        grid.addColumn(new NumberRenderer<>(Order::getPrice, "%(,.2f руб.",
                Locale.ENGLISH, "0.00 руб.")).setHeader("Delivery price");


        // You can also set complex objects directly. Internal properties of the
        // bean are accessible in the template.
        grid.addColumn(TemplateRenderer.<Order> of(
                "<div>[[item.address.title]], <br><small>[[item.address.user.userName]]</small></div>")
                .withProperty("address", order -> order.getUser().getUserName()))
                .setHeader("Address");

        Grid.Column<Order> statusColumn = grid.addColumn(order -> order.getStatus().getTitle())
                .setHeader("Status")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

//        Binder<Category> binder = new Binder<>(Category.class);
//        Editor<Category> editor = grid.getEditor();
//        editor.setBinder(binder);
//        editor.setBuffered(true);
//
//        TextField titleField = new TextField();
//        binder.bind(titleField, "title");
//        titleColumn.setEditorComponent(titleField);
//        titleColumn.setTextAlign(ColumnTextAlign.CENTER);
//        titleField.setSizeFull();
//
//        TextField descriptionField = new TextField();
//        binder.bind(descriptionField, "description");
//        descriptionColumn.setEditorComponent(descriptionField);
//        descriptionColumn.setTextAlign(ColumnTextAlign.CENTER);
//        descriptionField.setSizeFull();
//
//        Grid.Column<Category> editorColumn = grid.addComponentColumn(category -> {
//            Button edit = new Button("Edit");
//            edit.addClassName("edit");
//            edit.addClickListener(e -> editor.editItem(category));
//            return edit;
//        });
//
//        Button save = new Button("Save", e -> editor.save());
//        save.addClassName("save");
//
//        Button cancel = new Button("Cancel", e -> editor.cancel());
//        cancel.addClassName("cancel");
//
//        grid.getElement().addEventListener("keyup", event -> editor.cancel())
//                .setFilter("event.key === 'Escape' || event.key === 'Esc'");
//
//        Div buttons = new Div(save, cancel);
//        editorColumn.setEditorComponent(buttons);
//
//        Notification message = new Notification("", 3000, Notification.Position.TOP_END);

        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        add(appLayout);
        setHeight("100vh");

//        editor.addSaveListener(event -> {
//            Category updatedCategory = event.getItem();
//            binder.writeBeanIfValid(updatedCategory);
//            grid.getDataProvider().refreshItem(updatedCategory); // для refreshItem необходимо переопределить equal & hashCode
//            categoryService.update(updatedCategory);
//            message.setText("Category successful updated: " +
//                    "Title = " + updatedCategory.getTitle() + ", " +
//                    "Description = " + updatedCategory.getDescription());
//            message.open();
//        });
    }

    private List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
