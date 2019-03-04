package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.entites.OrderItem;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

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

        orders.forEach(order -> {
            Details details = new Details(
                    NumberFormat.getCurrencyInstance(LOCALE_RU).format(order.getPrice()) + " - " + order.getStatus().getTitle(),
                    new Text(order.getOrderItems().stream().map(OrderItem::getProduct).map(Product::getTitle).collect(Collectors.joining(", "))));
            content.add(details);
        });
        appLayout.setContent(content);
        add(appLayout);
    }

    private List<Order> fetchOrders(User user) {
        return orderService.findAllByUser(user);
    }

}
