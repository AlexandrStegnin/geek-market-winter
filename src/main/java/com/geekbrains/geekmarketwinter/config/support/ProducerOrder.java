package com.geekbrains.geekmarketwinter.config.support;

import com.geekbrains.geekmarketwinter.entites.OrderStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProducerOrder implements Serializable {

    private Long id;

    private OrderStatus status;

}
