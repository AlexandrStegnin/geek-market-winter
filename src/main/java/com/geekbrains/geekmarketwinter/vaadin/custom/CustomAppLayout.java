package com.geekbrains.geekmarketwinter.vaadin.custom;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.config.support.Constants;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.PaperBadge;

import static com.geekbrains.geekmarketwinter.config.support.Constants.*;

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
        AppLayoutMenuItem adminCategoryItem = new AppLayoutMenuItem(VaadinIcon.COGS.create(), "Admin", "admin");
        AppLayoutMenuItem managerItem = new AppLayoutMenuItem(VaadinIcon.PACKAGE.create(), "Manage orders", "manager/orders");

        cartItem.setId("cartItem");
        PaperBadge cartBadge = new PaperBadge(cartItem);
        cartBadge.setHeight("20px");
        cartBadge.setWidth("20px");
        cartBadge.setLabel("0"); // TODO: 14.02.2019 Передавть кол-во товаров в корзине
        cartBadge.getStyle().set("--paper-badge-background", "#b794f6");

        menu.addMenuItems(
                homeItem,
                cartItem
        );

        if (SecurityUtils.isUserInRole(ROLE_ADMIN)) menu.addMenuItems(adminCategoryItem);
        if (SecurityUtils.isUserInRole(ROLE_MANAGER)) menu.addMenuItem(managerItem);
        if (SecurityUtils.isUserLoggedIn()) {
            menu.addMenuItem(logoutItem);
        } else {
            menu.addMenuItem(loginItem);
        }

        setContent(component);
    }

    private void logout() {
        Notification.show("You have been Log Out successful!", 3000, Notification.Position.TOP_END);
        this.getUI().ifPresent(ui -> ui.navigate(Constants.LOGIN_URL.replace("/", "")));
        auth.logout();
    }

}
