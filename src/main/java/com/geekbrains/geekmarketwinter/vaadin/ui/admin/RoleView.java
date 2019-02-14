package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.config.support.OperationEnum;
import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.entites.Role_;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.RoleService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
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

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_ROLES_PAGE;

@PageTitle("Roles")
@Route(ADMIN_ROLES_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class RoleView extends VerticalLayout {

    private final RoleService roleService;
    private Grid<Role> grid;
    private final AuthService auth;
    private final Button addNewBtn;
    private ListDataProvider<Role> dataProvider;
    private Binder<Role> binder;

    public RoleView(RoleService roleService, AuthService auth) {
        this.roleService = roleService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.addNewBtn = new Button("New role", VaadinIcon.PLUS.create(),
                e -> showDialog(new Role(), OperationEnum.CREATE));
        this.binder = new BeanValidationBinder<>(Role.class);
        this.auth = auth;
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(Role::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(Role::getName)
                .setHeader("Role name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Role::getHumanized)
                .setHeader("Humanized")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(role -> VaadinViewUtils.makeEditorColumnActions(
                e -> showDialog(role, OperationEnum.UPDATE),
                e -> showDialog(role, OperationEnum.DELETE))
        )
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setKey("actions");

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(Alignment.END);
        CustomAppLayout appLayout = new CustomAppLayout(auth, verticalLayout);
        add(appLayout);
        setHeight("100vh");

    }

    private List<Role> getAll() {
        return roleService.getAllRoles();
    }

    private void showDialog(Role role, OperationEnum operation) {
        FormLayout roleForm = new FormLayout();
        Dialog dialog = new Dialog();
        TextField nameField = new TextField("Role name");
        nameField.setValue(role.getName() == null ? "" : role.getName());
        binder.forField(nameField)
                .bind(Role_.NAME);

        TextField humanized = new TextField("Humanized");
        humanized.setValue(role.getHumanized() == null ? "" : role.getHumanized());
        binder.forField(humanized)
                .bind(Role_.HUMANIZED);

        roleForm.add(nameField, humanized);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save");

        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();

        switch (operation) {
            case UPDATE:
                content.add(roleForm, actions);
                save.addClickListener(e -> {
                    role.setName(nameField.getValue());
                    role.setHumanized(humanized.getValue());
                    updateRole(role);
                    dialog.close();
                });
                break;
            case CREATE:
                content.add(roleForm, actions);
                save.addClickListener(e -> {
                    role.setName(nameField.getValue());
                    role.setHumanized(humanized.getValue());
                    if (binder.writeBeanIfValid(role)) {
                        addNewRole(role);
                        dialog.close();
                    }
                });
                break;
            case DELETE:
                Div contentText = new Div();
                contentText.setText("Confirm delete role: " + role.getName() + "?");
                content.add(contentText, actions);
                save.setText("Yes");
                save.addClickListener(e -> {
                    deleteRole(role);
                    dialog.close();
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        nameField.getElement().callFunction("focus");
    }

    private void addNewRole(Role role) {
        role = roleService.create(role);
        dataProvider.getItems().add(role);
        dataProvider.refreshAll();
    }

    private void updateRole(Role role) {
        roleService.update(role);
        dataProvider.refreshAll();
    }

    private void deleteRole(Role role) {
        dataProvider.getItems().remove(role);
        roleService.delete(role);
        dataProvider.refreshAll();
    }

}
