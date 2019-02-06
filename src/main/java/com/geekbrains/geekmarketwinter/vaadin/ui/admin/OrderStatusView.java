package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.OrderStatusService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_ORDER_STATUSES_PAGE;

@Route(ADMIN_ORDER_STATUSES_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class OrderStatusView extends VerticalLayout {

    private OrderStatusService orderStatusService;
    private Grid<OrderStatus> grid;
    private AuthService auth;
    private ListDataProvider<OrderStatus> dataProvider;

    public OrderStatusView(OrderStatusService orderStatusService, AuthService auth) {
        this.orderStatusService = orderStatusService;
        this.dataProvider = new ListDataProvider<>(getAllStatuses());
        this.grid = new Grid<>();
        this.auth = auth;
        init();
    }

    private void init() {

        grid.setDataProvider(dataProvider);

        grid.addColumn(OrderStatus::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<OrderStatus> titleColumn = grid.addColumn(OrderStatus::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Binder<OrderStatus> binder = new Binder<>(OrderStatus.class);
        Editor<OrderStatus> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField titleField = new TextField();
        binder.bind(titleField, "title");
        titleColumn.setEditorComponent(titleField);
        titleColumn.setTextAlign(ColumnTextAlign.CENTER);
        titleField.setSizeFull();

        Grid.Column<OrderStatus> editorColumn = grid.addComponentColumn(orderStatus -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> editor.editItem(orderStatus));
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
            OrderStatus updatedStatus = event.getItem();
            binder.writeBeanIfValid(updatedStatus);
            grid.getDataProvider().refreshAll();
            orderStatusService.update(updatedStatus);
            message.setText("Status title successful updated: " +
                    "New title = " + updatedStatus.getTitle());
            message.open();
        });
    }

    private List<OrderStatus> getAllStatuses() {
        List<OrderStatus> statuses;
        statuses = orderStatusService.findAll();
        return statuses;
    }

}
