package com.demo.phone_shop.controller;

import com.demo.phone_shop.model.CartItem;
import com.demo.phone_shop.model.Phone;
import com.demo.phone_shop.service.CartService;
import com.demo.phone_shop.service.PhoneService;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private PhoneService phoneService;

    @GetMapping
    public String showCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        // Calculate the total price
        long totalPrice = cartItems.stream()
                .mapToLong(cartItem -> cartItem.getPhone().getPrice() *
                        cartItem.getQuantity())
                .sum();
        model.addAttribute("totalPrice", totalPrice);

        // Calculate cart count
        model.addAttribute("cartCount", cartItems.size());

        //model.addAttribute("cartItems", cartService.getCartItems());
        return "/cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long phoneId, @RequestParam int quantity) {
        cartService.addToCart(phoneId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{phoneId}")
    public String removeFromCart(@PathVariable Long phoneId) {
        cartService.removeFromCart(phoneId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long phoneId, @RequestParam int quantity) {
        cartService.updateCartItem(phoneId, quantity);
        return "redirect:/cart";
    }
}
