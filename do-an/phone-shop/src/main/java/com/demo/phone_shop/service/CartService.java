package com.demo.phone_shop.service;

import com.demo.phone_shop.model.CartItem;
import com.demo.phone_shop.model.Phone;
import com.demo.phone_shop.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {

    @Autowired
    private PhoneRepository phoneRepository;

    public void addToCart(Long phoneId, int quantity) {
        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getPhone().getId().equals(phoneId)) {
                // Nếu đã tồn tại, cập nhật số lượng
                int newQuantity = item.getQuantity() + quantity;
                // Kiểm tra số lượng sản phẩm có sẵn trong kho
                Phone phone = item.getPhone();
                int availableStock = phone.getQuantity();
                if (newQuantity > availableStock) {
                    newQuantity = availableStock;
                }
                item.setQuantity(newQuantity);
                found = true;
                break;
            }
        }// Nếu chưa tồn tại, thêm sản phẩm mới vào giỏ hàng
        if (!found) {
            Phone phone = phoneRepository.findById(phoneId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + phoneId));

            // Kiểm tra số lượng sản phẩm hiện có
            int availableStock = phone.getQuantity();
            if (quantity > availableStock) {
                quantity = availableStock;
            }

            // Cập nhật số lượng sản phẩm
            if (availableStock > 0) {
                phone.setQuantity(availableStock - quantity);
//            phoneRepository.save(phone);
                CartItem cartItem = new CartItem(phone, quantity);
                cartItems.add(cartItem);
            }
        }
//        cartItems.add(new CartItem(phone, quantity));
    }

    private List<CartItem> cartItems = new ArrayList<>();

    public List<CartItem> getCartItems() {
        return cartItems;
    }


    public void removeFromCart(Long phoneId) {
        cartItems.removeIf(item -> item.getPhone().getId().equals(phoneId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    public void updateCartItem(Long phoneId, int quantity) {
//        CartItem existingCartItem = cartItems.stream()
//                .filter(item -> item.getPhone().getId().equals(phoneId))
//                .findFirst()
//                .orElse(null);
//
//        if (existingCartItem != null) {
//            existingCartItem.setQuantity(quantity);
//        }
        for (CartItem item : cartItems) {
            if (item.getPhone().getId().equals(phoneId)) {
                Phone phone = item.getPhone();
                int availableStock = phone.getQuantity() + item.getQuantity();
                if (quantity > availableStock) {
                    quantity = availableStock;
                }
                phone.setQuantity(availableStock - quantity);
//                phoneRepository.save(phone);
                item.setQuantity(quantity);
                return;
            }
        }
    }
}
