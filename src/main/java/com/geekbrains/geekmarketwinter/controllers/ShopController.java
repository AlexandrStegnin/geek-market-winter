package com.geekbrains.geekmarketwinter.controllers;

import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.services.ProductService;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.utils.ShoppingCart;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/shop")
public class ShopController {
    private ProductService productService;
    private ShoppingCartService shoppingCartService;
    private ProductFilter filter = new ProductFilter();

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setShoppingCartService(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("")
    public String shopPage(Model model,
                           @PageableDefault(size = 5)
                           @SortDefault Pageable pageable) {
        Page<Product> products = productService.findAll(filter, pageable);
        model.addAttribute("page", products);
        return "shop-page";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductToCart(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
        shoppingCartService.addToCart(httpServletRequest.getSession(), id);
        return "redirect:/shop";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeProductFromCart(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
        shoppingCartService.removeFromCart(httpServletRequest.getSession(), id);
        return "redirect:/shop/cart";
    }

    @GetMapping("/cart")
    public String showCart(Model model, HttpServletRequest request) {
        ShoppingCart cart = shoppingCartService.getCurrentCart(request.getSession());
        model.addAttribute("cart", cart);
        return "cart-page";
    }
}
