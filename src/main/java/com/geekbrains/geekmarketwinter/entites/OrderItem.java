package com.geekbrains.geekmarketwinter.entites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "orders_item")
@EqualsAndHashCode(exclude = {"product", "order"})
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "item_price")
    private Double itemPrice;

    @Column(name = "total_price")
    private Double totalPrice;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "order_id")
    private Order order;

    @Override
    public String toString() {
        return "Item order { " +
                "id: " + getId() +
                " product title: " + getProduct().getTitle() +
                " quantity: " + getQuantity() +
                " item price: " + getItemPrice() +
                " total price: " + getTotalPrice();
    }
}
