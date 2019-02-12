package com.geekbrains.geekmarketwinter.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    // Домашнее задание:
    // - Проанализировать код и сказать что можно оптимизировать и улучшить
    // - Идеи по работе со статусами, и что делать если пользователь
    // сформировал заказ, но не оплатил, как бы нам случайно его ему не отправить?
    // * - Сделать панель управления заказами

    // Планы на будущее:
    // - Paypal
    // - Фильтры
    // - Профиль пользователя
    // - Починить пагинацию

    // TODO: 31.01.2019 Формирование заказа (Order) из корзины, привязка к пользователю SecurityContextHolder
    // TODO: 31.01.2019 Привязка адреса доставки к пользователю
    // TODO: 31.01.2019 Разобраться с Cascade.Type Order -> OrderItem
    // TODO: 01.02.2019 После "подтвердить заказ" смена фона без перезагрузки страницы (слоями/якорями)
    // TODO: 12.02.2019 Валидация полей при CRU операциях

    @RequestMapping("/api")
    public String showHomePage() {
        return "index";
    }
}
