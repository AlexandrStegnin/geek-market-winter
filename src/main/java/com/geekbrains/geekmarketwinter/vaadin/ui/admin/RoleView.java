package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.RoleService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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

    public RoleView(RoleService roleService, AuthService auth) {
        this.roleService = roleService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.addNewBtn = new Button("New role", VaadinIcon.PLUS.create(), e -> showAddDialog());
        this.auth = auth;
        init();
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog();
        TextField nameField = new TextField("Name");
        nameField.setPlaceholder("Enter name");
        TextField humanized = new TextField("Humanized");
        humanized.setPlaceholder("Enter humanized");
        HorizontalLayout fields = new HorizontalLayout();
        fields.add(nameField, humanized);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            Role role = new Role();
            role.setName(nameField.getValue());
            role.setHumanized(humanized.getValue());
            addNewRole(role);
            dialog.close();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();
        content.add(fields, actions);
        dialog.add(content);
        dialog.open();
        nameField.getElement().callFunction("focus");
    }

    private void addNewRole(Role role) {
        role = roleService.create(role);
        dataProvider.getItems().add(role);
        dataProvider.refreshAll();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(Role::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        Grid.Column<Role> nameColumn = grid.addColumn(Role::getName)
                .setHeader("Role name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<Role> humanizedColumn = grid.addColumn(Role::getHumanized)
                .setHeader("Humanized")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Binder<Role> binder = new Binder<>(Role.class);
        Editor<Role> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField nameField = new TextField();
        binder.bind(nameField, "name");
        nameColumn.setEditorComponent(nameField);
        nameColumn.setTextAlign(ColumnTextAlign.CENTER);
        nameField.setSizeFull();

        TextField humanizedField = new TextField();
        binder.bind(humanizedField, "humanized");
        humanizedColumn.setEditorComponent(humanizedField);
        humanizedColumn.setTextAlign(ColumnTextAlign.CENTER);
        humanizedField.setSizeFull();

        Grid.Column<Role> editorColumn = grid.addComponentColumn(role -> {
            Div actions = new Div();
            Button edit = new Button("", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> editor.editItem(role));
            Button delete = new Button("", VaadinIcon.TRASH.create());
            delete.addClickListener(e -> showDeleteDialog(role));
            actions.add(edit, delete);
            return actions;
        });
        editorColumn.setHeader("Actions");
        editorColumn.setKey("actions");

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        Notification message = new Notification("", 3000, Notification.Position.TOP_END);

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(Alignment.END);
        CustomAppLayout appLayout = new CustomAppLayout(auth, verticalLayout);
        add(appLayout);
        setHeight("100vh");

        editor.addSaveListener(event -> {
            Role updatedRole = event.getItem();
            binder.writeBeanIfValid(updatedRole);
            grid.getDataProvider().refreshItem(updatedRole); // для refreshItem необходимо переопределить equal & hashCode
            roleService.update(updatedRole);
            message.setText("Role successful updated: " +
                    "Name = " + updatedRole.getName() + ", " +
                    "Humanized = " + updatedRole.getHumanized());
            message.open();
        });

        grid.getColumnByKey("actions").setTextAlign(ColumnTextAlign.CENTER);

    }

    private void showDeleteDialog(Role role) {
        Dialog dialog = new Dialog();
        Div contentText = new Div();
        contentText.setText("Are you sure, you want to delete role: \n" + role.getName() + " (" + role.getHumanized() + ")?");

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button yes = new Button("Yes", e -> {
            deleteRole(role);
            dialog.close();
        });
        Button no = new Button("No", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(yes, no);

        VerticalLayout content = new VerticalLayout();
        content.add(contentText, actions);
        dialog.add(content);
        dialog.open();
    }

    private void deleteRole(Role role) {
        dataProvider.getItems().remove(role);
        roleService.delete(role);
        dataProvider.refreshAll();
    }

    private List<Role> getAll() {
        return roleService.getAllRoles();
    }

}
