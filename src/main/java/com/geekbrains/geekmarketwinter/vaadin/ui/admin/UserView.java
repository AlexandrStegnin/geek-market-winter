package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.UserServiceImpl;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
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

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_USERS_PAGE;

@PageTitle("Users")
@Route(ADMIN_USERS_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class UserView extends VerticalLayout {

    private final UserServiceImpl userService;
    private Grid<User> grid;
    private final AuthService auth;
    private final Button addNewBtn;
    private ListDataProvider<User> dataProvider;

    public UserView(UserServiceImpl userService, AuthService auth) {
        this.userService = userService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.addNewBtn = new Button("New user", VaadinIcon.PLUS.create(), e -> showAddDialog());
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

        Grid.Column<User> nameColumn = grid.addColumn(User::getUserName)
                .setHeader("Username")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<User> firstNameColumn = grid.addColumn(User::getFirstName)
                .setHeader("First name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<User> lastNameColumn = grid.addColumn(User::getLastName)
                .setHeader("Last name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<User> emailColumn = grid.addColumn(User::getEmail)
                .setHeader("Last name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        // TODO: 2019-02-12 Добавить вывод ролей в grid

        Binder<User> binder = new Binder<>(User.class);
        Editor<User> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField nameField = new TextField();
        binder.bind(nameField, "userName");
        nameColumn.setEditorComponent(nameField);
        nameColumn.setTextAlign(ColumnTextAlign.CENTER);
        nameField.setSizeFull();

        TextField firstNameField = new TextField();
        binder.bind(firstNameField, "firstName");
        firstNameColumn.setEditorComponent(firstNameField);
        firstNameColumn.setTextAlign(ColumnTextAlign.CENTER);
        firstNameField.setSizeFull();

        TextField lastNameField = new TextField();
        binder.bind(lastNameField, "lastName");
        lastNameColumn.setEditorComponent(lastNameField);
        lastNameColumn.setTextAlign(ColumnTextAlign.CENTER);
        lastNameField.setSizeFull();

        TextField emailField = new TextField();
        binder.bind(emailField, "email");
        emailColumn.setEditorComponent(emailField);
        emailColumn.setTextAlign(ColumnTextAlign.CENTER);
        emailField.setSizeFull();

        Grid.Column<User> editorColumn = grid.addComponentColumn(user -> {
            Div actions = new Div();
            Button edit = new Button("", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> editor.editItem(user));
            Button delete = new Button("", VaadinIcon.TRASH.create());
            delete.addClickListener(e -> showDeleteDialog(user));
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
            User updatedUser = event.getItem();
            binder.writeBeanIfValid(updatedUser);
            grid.getDataProvider().refreshItem(updatedUser); // для refreshItem необходимо переопределить equal & hashCode
            userService.update(updatedUser);
            message.setText("User successful updated: " +
                    "Name = " + updatedUser.getUserName());
            message.open();
        });

        grid.getColumnByKey("actions").setTextAlign(ColumnTextAlign.CENTER);

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

        // TODO: 2019-02-12 Добавление ролей через list box/combobox
        // TODO: 2019-02-12 Переделать SystemUser - валидация

        formLayout.add(userName, pwdField, firstName, lastName, email);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            User user = new User();
            user.setUserName(userName.getValue());
            user.setPassword(pwdField.getValue());
            user.setFirstName(firstName.getValue());
            user.setLastName(lastName.getValue());
            user.setEmail(email.getValue());

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

    private void addNewUser(User user) {
        user = userService.create(user);
        dataProvider.getItems().add(user);
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

}
