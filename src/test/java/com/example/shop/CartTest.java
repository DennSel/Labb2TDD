package com.example.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

class CartTest {

    private Cart cart;

    // Empty shopping cart before each test
    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    void testAddingItem() {
        cart.addItem("item", new BigDecimal("1.0"), 1);

        assertThat(cart.getItemCount()).isEqualTo(1);
    }

    @Test
    void testAddingMultipleItems() {
        cart.addItem("item2", new BigDecimal("2.0"), 1);
        cart.addItem("item3", new BigDecimal("3.0"), 1);

        assertThat(cart.getItemCount()).isEqualTo(2);
    }


}
