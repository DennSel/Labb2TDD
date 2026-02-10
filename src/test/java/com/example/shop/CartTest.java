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

    @Test
    void testAddingItemWithQuantity() {
        cart.addItem("item", new BigDecimal("1.0"), 2);

        assertThat(cart.getQuantity("item")).isEqualTo(2);
    }

    @Test
    void removeSpecificItemFromCart() {
        cart.addItem("item", new BigDecimal("1.0"), 1);
        cart.removeItem("item");
        assertThat(cart.isItemPresent("item")).isFalse();
    }

    @Test
    void removeNonExistingItemFromCart() {
        cart.removeItem("item");
        assertThat(cart.getItemCount()).isEqualTo(0);
    }

    @Test
    void calculateTotalPrice() {
        cart.addItem("item", new BigDecimal("1.0"), 2);
        cart.addItem("items2", new BigDecimal("2.0"), 1);
        assertThat(cart.calculateTotal()).isEqualByComparingTo("4.0");
    }


}
