package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.entites.OrderItem;
import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.repositories.OrderRepository;
import com.geekbrains.geekmarketwinter.utils.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrderService {

    private final OrderRepository orderRepo;

    @Autowired
    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Order makeOrder(ShoppingCart cart, User user) {
        Order order = new Order();
        order.setId(0L);
        order.setUser(user);
        OrderStatus os = new OrderStatus(); // todo исправить
        os.setId(1L);
        os.setTitle("Сформирован");
        order.setStatus(os);
        order.setPrice(cart.getTotalCost());
        order.setOrderItems(new ArrayList<>(cart.getItems()));
        for (OrderItem o : cart.getItems()) {
            o.setOrder(order);
        }
        return order;
    }

    public Order findById(Long id) {
        return orderRepo.findById(id).get();
}

    public Order saveOrder(Order order) {
        Order orderOut = orderRepo.save(order);
        orderOut.setConfirmed(true);
        return orderOut;
    }
}