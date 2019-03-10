package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.PROFILE_PAGE;

/**
 * @author stegnin
 */

@Route(PROFILE_PAGE)
@PageTitle("Profile")
@Theme(value = Material.class)
public class ProfileView extends CustomAppLayout {

    private OrderService orderService;

    public ProfileView(OrderService orderService) {
        this.orderService = orderService;
        init();
    }

    private void init() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Order> orders = fetchOrders(currentUser);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Div content = new Div();
        content.getStyle()
                .set("display", "flex")
                .set("flex-flow", "row wrap")
                .set("justify-content", "center")
                .set("margin", "1em");
        orders.forEach(order -> {
            Details details = VaadinViewUtils.createDetails(order, null);
            content.add(details);
        });
        horizontalLayout.add(content);
        setContent(horizontalLayout);
    }

    private List<Order> fetchOrders(User user) {
        return orderService.findAllByUser(user);
    }

}
