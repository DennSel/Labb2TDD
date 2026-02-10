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

    @Test
    void applyDiscountToCart() {
        cart.addItem("item", new BigDecimal("10.0"), 1);
        cart.discount(10);

        assertThat(cart.calculateTotal()).isEqualByComparingTo("9.0");
    }

    @Test
    void shouldNotApplyMultipleDiscounts() {
        cart.addItem("item", new BigDecimal("1.0"), 10);
        cart.discount(10);
        cart.discount(5); // Shouldn't apply multiple discounts

        assertThat(cart.calculateTotal()).isEqualByComparingTo("9.0");
    }

    @Test
    void applyInvalidDiscountThrowsException() {
        cart.addItem("item", new BigDecimal("1.0"), 10);

        // Percentage can't go above 100%
        assertThatThrownBy(() -> cart.discount(101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Discount must be between 0 and 100");

        // Percentage can't go below 100%
        assertThatThrownBy(() -> cart.discount(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQuantityOfItemInCart() {
        cart.addItem("item", new BigDecimal("1.0"), 1);
        cart.updateQuantity("item", 2);

        assertThat(cart.getQuantity("item")).isEqualTo(2);
    }

    @Test
    void updatedQuantityOfItemInCartToZeroRemoveItem() {
        cart.addItem("item", new BigDecimal("1.0"), 1);
        cart.updateQuantity("item", 0);

        assertThat(cart.isItemPresent("item")).isFalse();
    }

    @Test
    void throwExceptionWhenUpdateQuantityIsNegative() {
        cart.addItem("item", new BigDecimal("1.0"), 1);

        assertThatThrownBy(() -> cart.updateQuantity("item", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity should be positive");
    }
}
