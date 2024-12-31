package com.demo.phone_shop.repository;

import com.demo.phone_shop.model.OrderDetail;
import com.demo.phone_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    void deleteByOrderId(Long orderId);

    // Tìm các đơn hàng do một người dùng tạo ra
    //List<OrderDetail> findByUser(User user);
}
