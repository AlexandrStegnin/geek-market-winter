package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.config.security.SecurityUtils;
import com.geekbrains.geekmarketwinter.config.support.ProducerOrder;
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
import java.util.HashSet;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final ShoppingCartService cartService;
    private final OrderStatusService orderStatusService;

    @Autowired
    public OrderService(OrderRepository orderRepo,
                        ShoppingCartService cartService,
                        OrderStatusService orderStatusService) {
        this.orderStatusService = orderStatusService;
        this.cartService = cartService;
        this.orderRepo = orderRepo;
    }

    public Order findById(Long id) {
        return orderRepo.findById(id).get();
}

    public Order saveOrder(Order order) {
        Order orderOut = orderRepo.save(order);
        orderOut.setConfirmed(true);
        return orderOut;
    }

    public ProducerOrder makeOrderToProduce(Order order) {
        ProducerOrder producerOrder = new ProducerOrder();
        producerOrder.setId(order.getId());
        producerOrder.setStatus(order.getStatus());
        return producerOrder;
    }

    public Order makeOrder(Order order) {
        User user = SecurityUtils.getCurrentUser();
        ShoppingCart cart = cartService.getCurrentCart(VaadinService.getCurrentRequest());
        order.getDeliveryAddress().setUser(user);
        order.setId(0L);
        order.setUser(user);
        order.setStatus(orderStatusService.getDefaultStatus());
        order.setPrice(cart.getTotalCost());
        order.setOrderItems(new HashSet<>(cart.getItems()));
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

    public void update(Order updatedOrder) {
        orderRepo.save(updatedOrder);
    }

    public Order changeStatusAndSave(ProducerOrder producerOrder) {
        Order order = findById(producerOrder.getId());
        OrderStatus status = orderStatusService.getOneByTitle(producerOrder.getStatus().getTitle());
        order.setStatus(status);
        return saveOrder(order);
    }

    public List<Order> findAllByUser(User user) {
        return orderRepo.findAllByUser(user);
    }

}