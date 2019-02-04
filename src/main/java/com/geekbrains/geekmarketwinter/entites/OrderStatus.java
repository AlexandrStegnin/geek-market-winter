package com.geekbrains.geekmarketwinter.entites;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "orders_statuses")
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

}
