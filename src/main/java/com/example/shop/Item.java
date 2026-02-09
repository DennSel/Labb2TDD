package com.example.shop;

import java.math.BigDecimal;

public class Item {
    String name;
    BigDecimal price;
    int quantity;

    Item(String name, BigDecimal price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
