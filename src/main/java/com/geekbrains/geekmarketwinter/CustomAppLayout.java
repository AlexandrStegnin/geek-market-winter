package com.geekbrains.geekmarketwinter;

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

        menu.addMenuItems(
                new AppLayoutMenuItem(VaadinIcon.HOME.create(),"Home", "shop"),
                new AppLayoutMenuItem(VaadinIcon.CART.create(), "Cart",  "cart"),
                new AppLayoutMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout", e -> logout())
        );

        setContent(component);
    }

    private void logout() {
        this.getUI().ifPresent(ui -> ui.navigate(Constants.LOGIN_URL.replace("/", "")));
        auth.logout();
    }

}
