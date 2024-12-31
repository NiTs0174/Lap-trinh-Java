package com.demo.phone_shop.repository;

import com.demo.phone_shop.model.Order;
import com.demo.phone_shop.model.OrderDetail;
import com.demo.phone_shop.model.Phone;
import com.demo.phone_shop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //List<OrderDetail> findByOrderId(Long orderId);

    // Tìm các đơn hàng do một người dùng tạo ra
    List<Order> findByUser(User user);

    //Tìm kiếm
    @Query("SELECT o FROM Order o WHERE o.id = :key")
    Page<Order> searchOrders(@Param("key") Long key, Pageable pageable);
}

