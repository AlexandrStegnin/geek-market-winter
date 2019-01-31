package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.utils.ShoppingCart;
import com.vaadin.flow.server.VaadinRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public ShoppingCart getCurrentCart(VaadinRequest request) {
        ShoppingCart cart = (ShoppingCart) request.getWrappedSession().getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart();
            request.getWrappedSession().setAttribute("cart", cart);
        }
        return cart;
    }

    public void resetCart(VaadinRequest request) {
        request.getWrappedSession().setAttribute("cart", new ShoppingCart());
    }

    public void addToCart(VaadinRequest request, Long productId) {
        Product product = productService.getProductById(productId);
        addToCart(request, product);
    }

    public void addToCart(VaadinRequest request, Product product) {
        ShoppingCart cart = getCurrentCart(request);
        cart.add(product);
    }

    public void removeFromCart(VaadinRequest request, Long productId) {
        Product product = productService.getProductById(productId);
        removeFromCart(request, product);
    }

    public void removeFromCart(VaadinRequest request, Product product) {
        ShoppingCart cart = getCurrentCart(request);
        cart.remove(product);
    }

    public void setProductCount(VaadinRequest request, Long productId, Long quantity) {
        ShoppingCart cart = getCurrentCart(request);
        Product product = productService.getProductById(productId);
        cart.setQuantity(product, quantity);
    }

    public void setProductCount(VaadinRequest request, Product product, Long quantity) {
        ShoppingCart cart = getCurrentCart(request);
        cart.setQuantity(product, quantity);
    }

    public double getTotalCost(VaadinRequest request) {
        return getCurrentCart(request).getTotalCost();
    }
}
