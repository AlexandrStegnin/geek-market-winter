package com.geekbrains.geekmarketwinter.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    // https://getbootstrap.com/docs/4.1/getting-started/introduction/csrf

    // Домашнее задание:
    // Добавить аутентификацию (пользователь у нас есть 'admin 100')
    // Сделать формирование заказа из корзины, на странице корзины кнопка "подтвердить",
    // после чего необходимо указать адрес доставки и телефон в виде строк, и нажав кнопку
    // купить, заказ должен попасть в БД

    @RequestMapping("/api")
    public String showHomePage() {
        return "index";
    }
}
