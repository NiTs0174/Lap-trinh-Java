package com.demo.phone_shop.service;

import com.demo.phone_shop.model.*;
import com.demo.phone_shop.repository.OrderDetailRepository;
import com.demo.phone_shop.repository.OrderRepository;
import com.demo.phone_shop.repository.PhoneRepository;
import com.demo.phone_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public List<OrderDetail> getAllOrderDetail() {
        return orderDetailRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
    }

    @Transactional
    public Order createOrder(String customerName, String shippingAddress, String phoneNumber, String email, String notes, String paymentMethod, List<CartItem> cartItems) {
        // Get the current logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User currentUser = userRepository.findByUsername(username);

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setOrderDate(LocalDate.now());
        order.setUser(currentUser);
        order.setStatus("Chờ xác nhận");
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();

            detail.setOrder(order);
            detail.setPhone(item.getPhone());
            detail.setQuantity(item.getQuantity());
            //detail.setUser(currentUser);
            orderDetailRepository.save(detail);

            // Update product stock after placing order successfully
            if (item.getQuantity() <= item.getPhone().getQuantity()) {
                item.getPhone().setQuantity(item.getPhone().getQuantity());
                phoneRepository.save(item.getPhone());
            }
        }
        // Optionally clear the cart after order placement
        cartService.clearCart();
        return order;
    }

    @Transactional
    public Order updateOrder(Long id, String customerName, String shippingAddress, String phoneNumber, String email, String notes, String paymentMethod) {
        Order order = getOrderById(id);
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderDetailRepository.deleteByOrderId(id);
        orderRepository.delete(order);
    }

    // Lấy danh sách các đơn hàng của user
    public List<Order> getCoursesByUser(User user) {
        return orderRepository.findByUser(user);
    }
    // Lấy danh sách chi tiết các đơn hàng của user
//    public List<OrderDetail> getCoursesByUser(User user) {
//        return orderDetailRepository.findByUser(user);
//    }

    public Order findById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    public void updateShippingStatus(Long orderId, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with id " + orderId);
        }
    }

    //Phân trang + Sort
    public Page<Order> GetAll(int pageNo, int pageSize,
                              String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        return orderRepository.findAll(pageRequest);
    }
    //Phân trang + Search + Sort
    public Page<Order> GetSearchOrders(Long key, int pageNo, int pageSize,
                                         String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return orderRepository.searchOrders(key, pageable);
    }
}

