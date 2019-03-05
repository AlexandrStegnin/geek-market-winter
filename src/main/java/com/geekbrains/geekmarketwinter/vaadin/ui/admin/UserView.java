package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.config.support.OperationEnum;
import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.entites.User_;
import com.geekbrains.geekmarketwinter.services.RoleService;
import com.geekbrains.geekmarketwinter.services.UserServiceImpl;
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
import java.util.stream.Collectors;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_USERS_PAGE;

@PageTitle("Users")
@Route(ADMIN_USERS_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class UserView extends CustomAppLayout {

    private final UserServiceImpl userService;
    private final RoleService roleService;
    private Grid<User> grid;
    private final Button addNewBtn;
    private ListDataProvider<User> dataProvider;
    private List<Role> roles;
    private Binder<User> binder;

    public UserView(UserServiceImpl userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.binder = new BeanValidationBinder<>(User.class);
        this.addNewBtn = new Button("New user", VaadinIcon.PLUS.create(), e -> showDialog(new User(), OperationEnum.CREATE));
        this.roles = getRoles();
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

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

        grid.addComponentColumn(user ->
                VaadinViewUtils.makeEditorColumnActions(
                        e -> showDialog(user, OperationEnum.UPDATE),
                        e -> showDialog(user, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setFlexGrow(2);

        // TODO: 12.02.2019 Разобраться с component column, без setEditorComponent не рендерится

        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private void saveUser(User user) {
        userService.save(user);
        dataProvider.refreshAll();
    }

    private List<User> getAll() {
        return userService.findAll();
    }

    private void deleteUser(User user) {
        dataProvider.getItems().remove(user);
        userService.delete(user);
        dataProvider.refreshAll();
    }

    private List<Role> getRoles() {
        return roleService.getAllRoles();
    }

    private void showDialog(User user, OperationEnum operation) {
        FormLayout formLayout = new FormLayout();
        TextField userName = new TextField("Username");
        userName.setValue(user.getUserName() == null ? "" : user.getUserName());
        binder.forField(userName)
                .bind(User_.USER_NAME);

        TextField pwdField = new TextField("Password");
        pwdField.setValue("");
        binder.forField(pwdField)
                .bind(User_.PASSWORD);

        TextField firstName = new TextField("First name");
        firstName.setValue(user.getFirstName() == null ? "" : user.getFirstName());
        binder.forField(firstName)
                .bind(User_.FIRST_NAME);

        TextField lastName = new TextField("Last name");
        lastName.setValue(user.getLastName() == null ? "" : user.getLastName());
        binder.forField(lastName)
                .bind(User_.LAST_NAME);

        TextField email = new TextField("Email");
        email.setValue(user.getEmail() == null ? "" : user.getEmail());
        binder.forField(email)
                .bind(User_.EMAIL);

        TextField phone = new TextField("Phone");
        phone.setValue(user.getPhone() == null ? "" : user.getPhone());
        binder.forField(phone)
                .bind(User_.PHONE);

        Div checkBoxDiv = VaadinViewUtils.makeUserRolesDiv(user, roles);

        formLayout.add(userName, pwdField, firstName, lastName, email, phone, checkBoxDiv);

        Dialog dialog = VaadinViewUtils.initDialog();
        Button save = new Button("Save");

        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();

        switch (operation) {
            case UPDATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(user)) {
                        saveUser(user);
                        dialog.close();
                    }
                });
                break;
            case CREATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(user)) {
                        dataProvider.getItems().add(user);
                        saveUser(user);
                        dialog.close();
                    }
                });
                break;
            case DELETE:
                Div contentText = new Div();
                contentText.setText("Confirm delete user: " + user.getUserName() + "?");
                content.add(contentText, actions);
                save.setText("Yes");
                save.addClickListener(e -> {
                    deleteUser(user);
                    dialog.close();
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        userName.getElement().callFunction("focus");

    }
}
