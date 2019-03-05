package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.text.NumberFormat;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.LOCALE_RU;
import static com.geekbrains.geekmarketwinter.config.support.Constants.PROFILE_PAGE;

/**
 * @author stegnin
 */

@Route(PROFILE_PAGE)
@PageTitle("Profile")
@Theme(value = Material.class)
public class ProfileView extends VerticalLayout {

    private OrderService orderService;
    private CustomAppLayout appLayout;

    public ProfileView(AuthRepository auth, OrderService orderService) {
        this.orderService = orderService;
        this.appLayout = new CustomAppLayout(auth);
        init();
    }

    private void init() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Order> orders = fetchOrders(currentUser);
        HorizontalLayout content = new HorizontalLayout();

        orders.forEach(order -> order.getOrderItems().forEach(orderItem -> {
            Details details = new Details();
            details.getElement().getStyle().set("margin", "1em 0em 1em 2em");
            Div div = new Div();
            Image image = VaadinViewUtils.getProductImage(orderItem.getProduct(), false);
            image.getStyle().set("float", "left");
            image.getStyle().set("margin-right", "1em");
            div.add(image);
            Span span = new Span(orderItem.getProduct().getShortDescription());
            div.add(span);
            div.getStyle().set("display","flex");
            div.getStyle().set("align-items", "center");
            details.setSummaryText(NumberFormat.getCurrencyInstance(LOCALE_RU).format(order.getPrice()) + " - " + order.getStatus().getTitle());
            details.setContent(div);
            content.add(details);
        }));
        appLayout.setContent(content);
        add(appLayout);
    }

    private List<Order> fetchOrders(User user) {
        return orderService.findAllByUser(user);
    }

}
