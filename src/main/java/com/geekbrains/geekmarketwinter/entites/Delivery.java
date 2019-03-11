package com.geekbrains.geekmarketwinter.entites;

import com.geekbrains.geekmarketwinter.config.support.DeliveryStatusEnum;
import com.geekbrains.geekmarketwinter.config.support.DeliveryTypeEnum;
import com.geekbrains.geekmarketwinter.config.support.PaymentTypeEnum;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */

@Data
//@Entity
//@Table(name = "deliveries")
public class Delivery {

    private Long id;

    @Enumerated(EnumType.STRING)
    private DeliveryTypeEnum deliveryType;

    @Enumerated(EnumType.STRING)
    private PaymentTypeEnum paymentType;

    private DeliveryAddress address;

    private double deliveryPrice;

    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private DeliveryStatusEnum deliveryStatus;

}
