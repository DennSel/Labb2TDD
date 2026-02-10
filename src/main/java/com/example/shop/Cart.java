package com.example.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Cart {

    private Map<String, Item> items = new HashMap<>();
    private BigDecimal discountPercent = BigDecimal.ZERO;

    public void addItem(String itemName, BigDecimal price, int quantity) {
        items.put(itemName, new Item(itemName, price, quantity));
    }

    public int getItemCount() {
        return items.size();
    }

    public int getQuantity(String item) {
        return items.get(item).quantity;
    }

    public boolean isItemPresent(String item) {
        return items.containsKey(item);
    }

    public void removeItem(String item){
        items.remove(item);
    }

    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Item item : items.values()) {
            BigDecimal itemTotal = item.price.multiply(new BigDecimal(item.quantity));
            total = total.add(itemTotal);
        }

        if (discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = total.multiply(discountPercent);
            total = total.subtract(discount);
        }

        return total;
    }

    public void discount (int percent) {
        BigDecimal percentAmount = new BigDecimal(percent);
        BigDecimal hundred = new BigDecimal("100");

        // Throw exception if invalid percentage
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        // Return if discount already applied
        if (discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            return;
        }

        // Divide percent / 100
        this.discountPercent = percentAmount.divide(hundred, 2, RoundingMode.HALF_UP);
    }


    public void updateQuantity(String name, int updatedQuantity) {
        Item item = items.get(name);
        
        if (item != null) {
            item.quantity = updatedQuantity;
        }
    }
}
