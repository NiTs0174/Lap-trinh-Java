package com.demo.phone_shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phones")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private long price;
    private String description;
    private String imageUrl;
    private String color;

    @Min(0)
    private int quantity;

    private String os; // Thêm thuộc tính hệ điều hành

    private String screen;
    private String rear_camera;
    private String front_camera;
    private String cpu;
    private String memory;
    private String connect;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
