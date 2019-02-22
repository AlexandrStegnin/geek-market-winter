package ru.stegnin.orderconsumer.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderStatus implements Serializable {
    private Long id;

    private String title;

    public OrderStatus() {
        this.title = "Сформирован";
    }

}
