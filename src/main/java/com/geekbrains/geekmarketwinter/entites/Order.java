package com.geekbrains.geekmarketwinter.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@Table(name = "orders")
@JsonIgnoreProperties("orderItems")
@EqualsAndHashCode(exclude = {"orderItems", "user", "deliveryAddress"})
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "order", fetch = FetchType.EAGER)
    private Collection<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private OrderStatus status;

    @Column(name = "price")
    private Double price;

    @Column(name = "delivery_price")
    private Double deliveryPrice;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "create_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(name = "update_at")
    @CreationTimestamp
    private LocalDateTime updateAt;

    @JsonIgnore
    @Transient
    private boolean confirmed;

    @Override
    public String toString() {
        return "Order: {" +
                " id: " + getId() +
                " user: " + getUser().getUserName() +
                " order items: " + getOrderItems() +
                " order status: " + getStatus() +
                " price: " + getPrice() +
                " delivery price: " + getDeliveryPrice() +
                " delivery address: " + getDeliveryAddress() +
                " phone number: " + getPhoneNumber() +
                " delivery date: " + getDeliveryDate();

    }
}