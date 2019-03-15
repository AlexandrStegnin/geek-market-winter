package com.geekbrains.geekmarketwinter.config.support;

public enum PaymentTypeEnum {

    CASH("Наличные"),
    CREDIT_CARD("Картой онлайн"),
    CREDIT_CARD_COURIER("Картой курьеру");

    private String title;

    PaymentTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
