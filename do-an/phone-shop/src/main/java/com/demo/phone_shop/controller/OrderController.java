package com.demo.phone_shop.controller;

import com.demo.phone_shop.model.*;
import com.demo.phone_shop.service.CartService;
import com.demo.phone_shop.service.OrderService;
import com.demo.phone_shop.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            model.addAttribute("message", "Không có sản phẩm trong giỏ hàng.");
            return "/cart/empty-cart"; // Thay thế bằng trang thông báo khi giỏ hàng trống
        }
        model.addAttribute("cartItems", cartItems);
        // Calculate the total price
        long totalPrice = cartItems.stream()
                .mapToLong(cartItem -> cartItem.getPhone().getPrice() *
                        cartItem.getQuantity())
                .sum();
        model.addAttribute("totalPrice", totalPrice);

        // Calculate cart count
        model.addAttribute("cartCount", cartItems.size());

        // Get the current logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> currentUser = userService.findByUsername(username);

        // Pass the current user to the view
        model.addAttribute("currentUser", currentUser);
        return "/cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(String customerName, String shippingAddress, String phoneNumber, String email, String notes, String paymentMethod) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        orderService.createOrder(customerName, shippingAddress, phoneNumber, email, notes, paymentMethod, cartItems);
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }

    //------------------
    @GetMapping("/search")
    public String searchOrders(Model model, @RequestParam Long key,
                                 @RequestParam(defaultValue = "0") int pageNo,
                                 @RequestParam(defaultValue = "5") int pageSize) {
        Page<Order> products = orderService.GetSearchOrders(key, pageNo, pageSize, "id", "desc");
        int totalPages = products.getTotalPages();
        model.addAttribute("order", products);
        model.addAttribute("totalPages", totalPages);
        return "/cart/order-list";
    }
    @GetMapping
    public String getAllOrders(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size) {
        Page<Order> products = orderService.GetAll(page, size, "id", "desc");
        model.addAttribute("totalPages", products.getTotalPages());

        //List<Order> order = orderService.getAllOrders();
        model.addAttribute("order", products);

        List<OrderDetail> oderDetail = orderService.getAllOrderDetail();
        model.addAttribute("orderDetail", oderDetail);
        return "/cart/order-list"; // View name for order list
    }

    @GetMapping("/details/{orderId}")
    public String showOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        List<OrderDetail> orderDetails = order.getOrderDetails();

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);

        List<CartItem> cartItems = cartService.getCartItems();
        // Calculate the total price
        long totalPrice = cartItems.stream()
                .mapToLong(cartItem -> cartItem.getPhone().getPrice() *
                        cartItem.getQuantity())
                .sum();
        model.addAttribute("totalPrice", totalPrice);

        return "/cart/profile-receipt-details";
    }

    @PostMapping("/details/{orderId}")
    public String updateShippingStatus(@PathVariable Long orderId, @RequestParam String status) {
        orderService.updateShippingStatus(orderId, status);
        return "redirect:/order";
    }
}

