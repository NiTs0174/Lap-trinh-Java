package com.demo.phone_shop.model;

import lombok.*;

@Setter
@Getter
public class CartItem {
    //Product info
    private Phone phone;
    private int quantity;

    // Constructors
    public CartItem(){}
    public CartItem(Phone phone, int quantity) {
        this.phone = phone;
        this.quantity = quantity;
    }
}
