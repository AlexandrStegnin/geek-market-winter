package ru.stegnin.orderconsumer.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProducerOrder implements Serializable {

    private Long id;

    private OrderStatus status;

}
