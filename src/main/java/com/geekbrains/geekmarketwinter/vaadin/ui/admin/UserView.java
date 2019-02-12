package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.RoleService;
import com.geekbrains.geekmarketwinter.services.UserServiceImpl;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;
import java.util.stream.Collectors;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_USERS_PAGE;

@PageTitle("Users")
@Route(ADMIN_USERS_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class UserView extends VerticalLayout {

    private final UserServiceImpl userService;
    private final RoleService roleService;
    private Grid<User> grid;
    private final AuthService auth;
    private final Button addNewBtn;
    private ListDataProvider<User> dataProvider;
    private List<Role> roles;

    public UserView(UserServiceImpl userService, AuthService auth, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.addNewBtn = new Button("New user", VaadinIcon.PLUS.create(), e -> showAddDialog());
        this.roles = getRoles();
        this.auth = auth;
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(User::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(User::getUserName)
                .setHeader("Username")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getFirstName)
                .setHeader("First name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getLastName)
                .setHeader("Last name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getEmail)
                .setHeader("Last name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getPhone)
                .setHeader("Phone")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getRoles().stream().map(Role::getHumanized)
                .collect(Collectors.joining(", ")))
                .setHeader("Roles")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Button save = new Button("Save");
        save.addClassName("save");

        Button cancel = new Button("Cancel");
        cancel.addClassName("cancel");

        Grid.Column<User> editorColumn = grid.addComponentColumn(user -> {
            Div actions = new Div();
            Button edit = new Button("", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> showEditDialog(user));
            Button delete = new Button("", VaadinIcon.TRASH.create());
            delete.addClickListener(e -> showDeleteDialog(user));
            actions.add(edit, delete);
            return actions;
        });

        Div empty = new Div(); // TODO: 12.02.2019 Разобраться с component column, без setEditorComponent не рендерится
        editorColumn.setEditorComponent(empty);
        editorColumn
                .setHeader("Actions")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(Alignment.END);
        CustomAppLayout appLayout = new CustomAppLayout(auth, verticalLayout);
        add(appLayout);
        setHeight("100vh");

    }

    private void showAddDialog() {

        FormLayout formLayout = new FormLayout();

        Dialog dialog = new Dialog();
        TextField userName = new TextField("Username");
        userName.setPlaceholder("Enter username");

        TextField pwdField = new TextField("Password");
        pwdField.setPlaceholder("Enter password");

        TextField firstName = new TextField("First name");
        firstName.setPlaceholder("Enter first name");

        TextField lastName = new TextField("Last name");
        lastName.setPlaceholder("Enter last name");

        TextField email = new TextField("Email");
        email.setPlaceholder("Enter email");

        TextField phone = new TextField("Phone");
        email.setPlaceholder("Enter phone");

        CheckboxGroup<Role> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("Choose roles");
        checkboxGroup.setItemLabelGenerator(Role::getHumanized);
        checkboxGroup.setItems(roles);

        // TODO: 2019-02-12 Переделать SystemUser - валидация

        formLayout.add(userName, pwdField, firstName, lastName, email, phone, checkboxGroup);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            User user = new User(userName.getValue(), pwdField.getValue(), firstName.getValue(),
                    lastName.getValue(), email.getValue(), phone.getValue(), checkboxGroup.getSelectedItems());
            addNewUser(user);
            dialog.close();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();
        content.add(formLayout, actions);
        dialog.add(content);
        dialog.open();
        userName.getElement().callFunction("focus");
    }

    private void showEditDialog(User user) {

        FormLayout formLayout = new FormLayout();

        Dialog dialog = new Dialog();
        TextField userName = new TextField("Username");
        userName.setValue(user.getUserName());

        TextField pwdField = new TextField("Password");
        pwdField.setValue("");

        TextField firstName = new TextField("First name");
        firstName.setValue(user.getFirstName());

        TextField lastName = new TextField("Last name");
        lastName.setValue(user.getLastName());

        TextField email = new TextField("Email");
        email.setValue(user.getEmail());

        TextField phone = new TextField("Phone");
        phone.setValue(user.getPhone());

        Div checkBoxDiv = getUserRolesDiv(user);

        formLayout.add(userName, pwdField, firstName, lastName, email, phone, checkBoxDiv);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            updateUser(user);
            dialog.close();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();
        content.add(formLayout, actions);
        dialog.add(content);
        dialog.open();
        userName.getElement().callFunction("focus");
    }

    private void addNewUser(User user) {
        user = userService.create(user);
        dataProvider.getItems().add(user);
        dataProvider.refreshAll();
    }

    private void updateUser(User user) {
        userService.update(user);
        dataProvider.refreshAll();
    }

    private List<User> getAll() {
        return userService.findAll();
    }

    private void showDeleteDialog(User user) {
        Dialog dialog = new Dialog();
        Div contentText = new Div();
        contentText.setText("Are you sure, you want to delete user: \n" + user.getUserName() + "?");

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button yes = new Button("Yes", e -> {
            deleteUser(user);
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

    private void deleteUser(User user) {
        dataProvider.getItems().remove(user);
        userService.delete(user);
        dataProvider.refreshAll();
    }

    private List<Role> getRoles() {
        return roleService.getAllRoles();
    }

    private Div getUserRolesDiv(User user) {
        Div checkBoxDiv = new Div();
        Div label = new Div();
        label.setText("Choose roles");
        label.getStyle().set("margin", "10px 0");
        checkBoxDiv.add(label);
        Div contentDiv = new Div();
        roles.forEach(role -> {
            Checkbox checkbox = new Checkbox(
                    role.getHumanized(),
                    user.getRoles().contains(role));
            checkbox.addValueChangeListener(e -> {
                String roleName = e.getSource().getElement().getText();
                Role userRole = roles.stream()
                        .filter(r -> r.getHumanized().equalsIgnoreCase(roleName))
                        .findAny().orElse(null);
                if (e.getValue()) {
                    user.getRoles().add(userRole);
                } else {
                    user.getRoles().remove(userRole);
                }
            });
            contentDiv.add(checkbox);
        });
        checkBoxDiv.add(contentDiv);
        return checkBoxDiv;
    }
}
