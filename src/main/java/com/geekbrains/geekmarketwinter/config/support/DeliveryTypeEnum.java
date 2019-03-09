package com.geekbrains.geekmarketwinter.config.support;

public enum DeliveryTypeEnum {

    DELIVERY("Доставка"),
    PICKUP("Самовывоз");

    private String title;

    DeliveryTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
