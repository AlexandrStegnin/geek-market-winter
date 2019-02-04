package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.entites.OrderItem;
import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.repositories.OrderRepository;
import com.geekbrains.geekmarketwinter.utils.ShoppingCart;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final ShoppingCartService cartService;

    @Autowired
    public OrderService(OrderRepository orderRepo, ShoppingCartService cartService) {
        this.orderRepo = orderRepo;
        this.cartService = cartService;
    }

    public Order findById(Long id) {
        return orderRepo.findById(id).get();
}

    public Order saveOrder(Order order) {
        Order orderOut = orderRepo.save(order);
        orderOut.setConfirmed(true);
        return orderOut;
    }

    public Order makeOrder(Order order) {
        User user = SecurityUtils.getCurrentUser();
        ShoppingCart cart = cartService.getCurrentCart(VaadinService.getCurrentRequest());
        order.getDeliveryAddress().setUser(user);
        order.setId(0L);
        order.setUser(user);
        OrderStatus os = new OrderStatus(); // todo исправить
        os.setId(1L);
        os.setTitle("Сформирован");
        order.setStatus(os);
        order.setPrice(cart.getTotalCost());
        order.setOrderItems(new ArrayList<>(cart.getItems()));
        order.setDeliveryAddress(order.getDeliveryAddress());
        order.setDeliveryPrice(100d);
        order.setDeliveryDate(LocalDateTime.now().plusDays(7));
        order.setPhoneNumber(order.getPhoneNumber());
        order.setDeliveryDate(LocalDateTime.now().plusDays(7));
        order.setDeliveryPrice(100d);
        for (OrderItem o : cart.getItems()) {
            o.setOrder(order);
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }
}