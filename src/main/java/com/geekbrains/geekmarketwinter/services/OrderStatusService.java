package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import com.geekbrains.geekmarketwinter.repositories.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderStatusService {

    private static final String DEFAULT_STATUS_NAME = "Подготавливается";
    private final OrderStatusRepository orderStatusRepo;

    @Autowired
    public OrderStatusService(OrderStatusRepository orderStatusRepo) {
        this.orderStatusRepo = orderStatusRepo;
    }

    public List<OrderStatus> findAll() {
        return orderStatusRepo.findAll();
    }

    public void update(OrderStatus orderStatus) {
        orderStatusRepo.save(orderStatus);
    }

    public OrderStatus getDefaultStatus() {
        return orderStatusRepo.getByTitle(DEFAULT_STATUS_NAME);
    }
}
