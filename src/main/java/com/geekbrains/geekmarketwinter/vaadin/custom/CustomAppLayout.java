package com.geekbrains.geekmarketwinter.vaadin.custom;

import com.geekbrains.geekmarketwinter.config.support.Constants;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;

public class CustomAppLayout extends AppLayout {

    private final AuthRepository auth;

    public CustomAppLayout(AuthRepository auth, Component component) {
        this.auth = auth;
        AppLayoutMenu menu = createMenu();
        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        img.setHeight("44px");
        setBranding(img);

        AppLayoutMenuItem homeItem = new AppLayoutMenuItem(VaadinIcon.HOME.create(), "Home", "shop");
        AppLayoutMenuItem cartItem = new AppLayoutMenuItem(VaadinIcon.CART.create(), "Cart", "cart");
        AppLayoutMenuItem logoutItem = new AppLayoutMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout", e -> logout());
        AppLayoutMenuItem loginItem = new AppLayoutMenuItem(VaadinIcon.SIGN_IN.create(), "Login", "login");
        AppLayoutMenuItem adminItem = new AppLayoutMenuItem(VaadinIcon.COGS.create(), "Admin", "admin/categories");

        menu.addMenuItems(
                homeItem,
                cartItem
        );

        if (auth.authenticated()) {
            menu.addMenuItem(adminItem);
            menu.addMenuItem(logoutItem);
        } else {
            menu.addMenuItem(loginItem);
        }

        setContent(component);
    }

    private void logout() {
        this.getUI().ifPresent(ui -> ui.navigate(Constants.LOGIN_URL.replace("/", "")));
        auth.logout();
    }

}
