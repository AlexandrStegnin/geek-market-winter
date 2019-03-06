package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.services.OrderService;
import com.geekbrains.geekmarketwinter.services.OrderStatusService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.ArrayList;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.*;
import static com.geekbrains.geekmarketwinter.config.security.SecurityConstants.PAYPAL_CLIENT_ID;
import static com.geekbrains.geekmarketwinter.config.security.SecurityConstants.PAYPAL_SECRET;

/**
 * @author stegnin
 */

@Route(PAYPAL_PAGE)
@PageTitle("Pay order")
@Theme(value = Material.class)
public class PayPalView extends CustomAppLayout implements HasUrlParameter<String> {

    private APIContext apiContext = new APIContext(PAYPAL_CLIENT_ID, PAYPAL_SECRET, PAYPAL_MODE_SANDBOX);

    private OrderService orderService;
    private OrderStatusService orderStatusService;

    public PayPalView(OrderService orderService, OrderStatusService orderStatusService) {
        this.orderStatusService = orderStatusService;
        this.orderService = orderService;
        init();
    }

    private void init() {
        Div div = VaadinViewUtils.createInfoDiv("Pay order in progress, please wait...");
        setContent(div);
    }

    private void buy(String orderId) {
        Payer payer = new Payer();
        payer.setPaymentMethod(PAYMENT_METHOD);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8189/wintermarket");
        redirectUrls.setReturnUrl("http://localhost:8189/wintermarket/paypal/success/" + orderId);

        Amount amount = new Amount(CURRENCY_RUB, "1.0");

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Покупка в WinterMarket");
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Payment payment = new Payment(PAYPAL_INTENT_SALE, payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);

        try {
            Payment doPayment = payment.create(apiContext);
            for (Links link : doPayment.getLinks()) {
                if (link.getRel().equalsIgnoreCase(PAYPAL_APPROVAL_URL)) {
                    String scr = "window.open(\"" + link.getHref() + "\", \"_self\", \"\");";
                    UI.getCurrent().getPage().executeJavaScript(scr);
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    private void success(VaadinRequest request, Long orderId) {
        String paymentId = request.getParameter(PAYMENT_ID);
        String payerId = request.getParameter(PAYER_ID);

        if (paymentId == null || paymentId.isEmpty() || payerId == null || payerId.isEmpty()) {
            Div div = VaadinViewUtils.createInfoDiv("Something went wrong... Please try again later.");
            setContent(div);
            return;
        }

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if (executedPayment.getState().equals(PAYPAL_APPROVED)) {
                VerticalLayout content = new VerticalLayout();
                Order order = orderService.findById(orderId);
                order.setStatus(orderStatusService.getStatusPayed());
                order = orderService.saveOrder(order);
                Anchor backToShop = new Anchor("./" + SHOP_PAGE, "Back to shop");
                Details details = VaadinViewUtils.createDetails(order, backToShop);
                details.setOpened(true);
                content.add(details);
                content.setAlignItems(FlexComponent.Alignment.CENTER);
                content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                content.setHeight("100%");
                setContent(content);
            } else {
                Div div = VaadinViewUtils.createInfoDiv("Something went wrong... Please try again later.");
                setContent(div);
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String parameter) {
        Location location = beforeEvent.getLocation();
        String orderId = location.getSegments().get(2);
        if (location.getSegments().get(1).contains(PAYPAL_BUY_PAGE)) {
            buy(orderId);
        } else if (location.getSegments().get(1).contains(PAYPAL_BUY_SUCCESS_PAGE)) {
            success(VaadinRequest.getCurrent(), Long.parseLong(orderId));
        }
    }
}
