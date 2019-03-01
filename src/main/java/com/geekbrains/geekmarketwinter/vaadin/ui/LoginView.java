package com.geekbrains.geekmarketwinter.vaadin.ui;


import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.geekbrains.geekmarketwinter.config.support.Constants.*;

@PageTitle("Login page")
@Route(LOGIN_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class LoginView extends VerticalLayout {

    private final AuthRepository authRepository;

    public LoginView(AuthRepository authRepository) {
        this.authRepository = authRepository;
        init();
    }

    private void init() {
        FormLayout loginForm = new FormLayout();

        TextField loginField = new TextField();
        loginField.setLabel("Username");
        loginField.setPlaceholder("Login");
        loginField.setId("user_name_field");

        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.setPlaceholder("*****");
        passwordField.setId("pwd_field");

        Button loginButton = new Button("LOG IN", e -> {
            if (authenticated(loginField.getValue(), passwordField.getValue()))
                this.getUI().ifPresent(ui -> {
                    if (SecurityUtils.isUserInRole(ROLE_ADMIN)) {
                        ui.navigate(ADMIN_PAGE);
                    } else {
                        ui.navigate(SHOP_PAGE);
                    }
                });
        });
        loginButton.setId("login_btn");
        loginForm.add(loginField, passwordField, loginButton);
        add(loginForm);

        setMargin(true);
        setAlignSelf(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
        setHeight("100%");
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private boolean authenticated(String login, String password) {
        return authRepository.authenticate(login, password).isAuthenticated();
    }

}
