package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;

import java.util.ArrayList;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.*;

/**
 * @author stegnin
 */

@Route(PAYPAL_PAGE)
public class PayPalView {

    private String clientId = "ATFgNr4RYcPqSHjPNYXDRBkMl3eOzeynKRiP-ABUda0pMshhr7aIgUQMiHz-ynBBwmj9eiYpLKMbvAGA";
    private String clientSecret = "EA6afcT4LEbXj3_DDHqdMjl1SE189Vc3wCgQoQuX7t9aY8EFWNDKHBmgzum6EHDNzQbxww1GkZ6qTS1G";
    private String mode = "sandbox";

    private APIContext apiContext = new APIContext(clientId, clientSecret, mode);

    public String buy() {
        Payer payer = new Payer();
        payer.setPaymentMethod(PAYMENT_METHOD);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8189/wintermarket");
        redirectUrls.setReturnUrl("http://localhost:8189/wintermarket/paypal/success");

        Amount amount = new Amount(CURRENCY_RUB, "1.0");

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Покупка в WinterMarket");
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Payment payment = new Payment("sale", payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);

        try {
            Payment doPayment = payment.create(apiContext);
            for (Links link : doPayment.getLinks()) {
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "оплата прошла успешно";
    }

    public String success(VaadinRequest request) {
        String paymentId = request.getParameter("paymentId");
        String payerId = request.getParameter("PayerID");

        if (paymentId == null || paymentId.isEmpty() || payerId == null || payerId.isEmpty()) {
            return "redirect:/"; // TODO: 05.03.2019 На страницу с ошибкой
        }

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if (executedPayment.getState().equals("approved")) {
                // TODO: 05.03.2019 Ответ о том, что всё хорошо
            } else {
                // TODO: 05.03.2019 Не всё хорошо
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return "Всё прошло успешно";

    }

}
