package com.demo.phone_shop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String shippingAddress;
    private String phoneNumber;
    private String email;
    private String notes;
    private String paymentMethod;
    private LocalDate orderDate;

    private String status;

//    @OneToMany(mappedBy = "order")
//    private List<OrderDetail> orderDetails;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
