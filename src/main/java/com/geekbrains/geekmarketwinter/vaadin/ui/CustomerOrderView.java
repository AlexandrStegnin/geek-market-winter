package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.Objects;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ORDER_DETAILS_PAGE;
import static com.geekbrains.geekmarketwinter.config.support.Constants.SHOP_PAGE;

/**
 * @author Alexandr Stegnin
 */

@Route(ORDER_DETAILS_PAGE)
@PageTitle("Order details")
@Theme(value = Material.class)
public class CustomerOrderView extends CustomAppLayout implements HasUrlParameter<String> {

    private OrderService orderService;

    public CustomerOrderView(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String orderHash) {
        VerticalLayout content = new VerticalLayout();
        Order order = orderService.findByHash(orderHash);
        if (!Objects.equals(null, order)) {
            Anchor backToShop = new Anchor("./" + SHOP_PAGE, "Back to shop");
            Details details = VaadinViewUtils.createDetails(order, backToShop);
            details.setOpened(true);
            content.add(details);
            content.setAlignItems(FlexComponent.Alignment.CENTER);
            content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            content.setHeight("100%");
            setContent(content);
        } else {
            setContent(VaadinViewUtils.createInfoDiv("No orders found"));
        }
    }
}
