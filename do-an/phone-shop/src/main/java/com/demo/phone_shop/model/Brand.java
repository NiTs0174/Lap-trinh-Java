package com.demo.phone_shop.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String headquarters;
    private Integer founded_year;

    @OneToMany(mappedBy = "brand")
    private Set<Phone> phones;
}
