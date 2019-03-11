package com.geekbrains.geekmarketwinter.config.support;

/**
 * @author Alexandr Stegnin
 */
public enum DeliveryStatusEnum {

    FORMING("Формируется"),
    WAILTING_COURIER("Ожидает курьера"),
    TRANSFERRED_TO_COURIER("Передано курьеру"),
    WAITING_CLIENT("Ожидает клиента"),
    TRANSFERRED_TO_CLIENT("Передано клиенту");

    private String title;

    DeliveryStatusEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return  title;
    }

}
