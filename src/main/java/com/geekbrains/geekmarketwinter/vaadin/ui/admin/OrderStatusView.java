package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.config.support.OperationEnum;
import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import com.geekbrains.geekmarketwinter.entites.OrderStatus_;
import com.geekbrains.geekmarketwinter.services.OrderStatusService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_ORDER_STATUSES_PAGE;

@PageTitle("Orders statuses")
@Route(ADMIN_ORDER_STATUSES_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class OrderStatusView extends CustomAppLayout {

    private OrderStatusService orderStatusService;
    private Grid<OrderStatus> grid;
    private ListDataProvider<OrderStatus> dataProvider;
    private Binder<OrderStatus> binder;
    private final Button addNewBtn;

    public OrderStatusView(OrderStatusService orderStatusService) {
        this.orderStatusService = orderStatusService;
        this.dataProvider = new ListDataProvider<>(getAllStatuses());
        this.grid = new Grid<>();
        this.addNewBtn = new Button("New order status", VaadinIcon.PLUS.create(),
                e -> showDialog(new OrderStatus(), OperationEnum.CREATE));
        this.binder = new BeanValidationBinder<>(OrderStatus.class);
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(OrderStatus::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<OrderStatus> titleColumn = grid.addColumn(OrderStatus::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(role -> VaadinViewUtils.makeEditorColumnActions(
                e -> showDialog(role, OperationEnum.UPDATE),
                e -> showDialog(role, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setFlexGrow(2)
                .setHeader("Actions");

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private List<OrderStatus> getAllStatuses() {
        List<OrderStatus> statuses;
        statuses = orderStatusService.findAll();
        return statuses;
    }

    private void showDialog(OrderStatus orderStatus, OperationEnum operation) {
        FormLayout orderStatusForm = new FormLayout();
        TextField titleField = new TextField("Order title");
        titleField.setValue(orderStatus.getTitle() == null ? "" : orderStatus.getTitle());
        binder.forField(titleField)
                .bind(OrderStatus_.TITLE);

        orderStatusForm.add(titleField);

        Dialog dialog = VaadinViewUtils.initDialog();
        Button save = new Button("Save");
        Button cancel = new Button("Cancel", e -> dialog.close());

        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();

        switch (operation) {
            case UPDATE:
                content.add(orderStatusForm, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(orderStatus)) {
                        saveOrderStatus(orderStatus);
                        dialog.close();
                    }
                });
                break;
            case CREATE:
                content.add(orderStatusForm, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(orderStatus)) {
                        dataProvider.getItems().add(orderStatus);
                        saveOrderStatus(orderStatus);
                        dialog.close();
                    }
                });
                break;
            case DELETE:
                Div contentText = new Div();
                contentText.setText("Confirm delete order status: " + orderStatus.getTitle() + "?");
                content.add(contentText, actions);
                save.setText("Yes");
                save.addClickListener(e -> {
                    deleteOrderStatus(orderStatus);
                    dialog.close();
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        titleField.getElement().callFunction("focus");
    }

    private void saveOrderStatus(OrderStatus orderStatus) {
        orderStatusService.save(orderStatus);
        dataProvider.refreshAll();
    }

    private void deleteOrderStatus(OrderStatus orderStatus) {
        dataProvider.getItems().remove(orderStatus);
        orderStatusService.delete(orderStatus);
        dataProvider.refreshAll();
    }

}
