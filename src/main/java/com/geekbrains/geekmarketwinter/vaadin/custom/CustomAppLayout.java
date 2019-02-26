package com.geekbrains.geekmarketwinter.vaadin.custom;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.config.support.Constants;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.vaadin.ui.CartView;
import com.geekbrains.geekmarketwinter.vaadin.ui.LoginView;
import com.geekbrains.geekmarketwinter.vaadin.ui.ShopView;
import com.geekbrains.geekmarketwinter.vaadin.ui.admin.AdminView;
import com.geekbrains.geekmarketwinter.vaadin.ui.manager.OrderView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.PaperBadge;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ROLE_ADMIN;
import static com.geekbrains.geekmarketwinter.config.support.Constants.ROLE_MANAGER;

public class CustomAppLayout extends AppLayout {

    private final AuthRepository auth;

    public CustomAppLayout(AuthRepository auth, Component component) {
        this.auth = auth;
        AppLayoutMenu menu = createMenu();
        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        img.setHeight("44px");
        setBranding(img);

        AppLayoutMenuItem homeItem = new AppLayoutMenuItem(VaadinIcon.HOME.create(), "Home", e -> goToPage(ShopView.class));
        AppLayoutMenuItem cartItem = new AppLayoutMenuItem(VaadinIcon.CART.create(), "Cart", e -> goToPage(CartView.class));
        AppLayoutMenuItem logoutItem = new AppLayoutMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout", e -> logout());
        AppLayoutMenuItem loginItem = new AppLayoutMenuItem(VaadinIcon.SIGN_IN.create(), "Login", e -> goToPage(LoginView.class));
        AppLayoutMenuItem adminItem = new AppLayoutMenuItem(VaadinIcon.COGS.create(), "Admin", e -> goToPage(AdminView.class));
        AppLayoutMenuItem managerItem = new AppLayoutMenuItem(VaadinIcon.PACKAGE.create(), "Manage orders", e -> goToPage(OrderView.class));

        cartItem.setId("cartItem");
        PaperBadge cartBadge = new PaperBadge(cartItem);
        cartBadge.setHeight("20px");
        cartBadge.setWidth("20px");
        ShoppingCartService cartService = new ShoppingCartService();
        cartBadge.setLabel(cartService.getTotalQuantity().toString());
        cartBadge.getStyle().set("--paper-badge-background", "#b794f6");

        menu.addMenuItems(
                homeItem,
                cartItem
        );

        if (SecurityUtils.isUserInRole(ROLE_ADMIN)) menu.addMenuItems(adminItem);
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

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }
}
