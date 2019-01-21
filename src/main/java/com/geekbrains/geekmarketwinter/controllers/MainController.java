package com.geekbrains.geekmarketwinter.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    // https://getbootstrap.com/docs/4.1/getting-started/introduction/csrf

    // Домашнее задание:
    // Сделать страницу с корзиной, с отображением информации
    // Допилить пагинацию к странице с товарами

    @RequestMapping("/")
    public String showHomePage() {
        return "index";
    }
}
