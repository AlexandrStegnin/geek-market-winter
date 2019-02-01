package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Order;
import org.springframework.stereotype.Service;

@Service
public class MailMessageBuilder {
//    private TemplateEngine templateEngine;
//
//    @Autowired
//    public void setTemplateEngine(TemplateEngine templateEngine) {
//        this.templateEngine = templateEngine;
//    }
//
    public String buildOrderEmail(Order order) {
//        Context context = new Context();
//        context.setVariable("order", order);
//        return templateEngine.process("order-mail", context);
        return "";
    }
}
