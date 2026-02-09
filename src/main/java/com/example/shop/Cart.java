package com.example.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Cart {

    private Map<String, Item> items = new HashMap<>();

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
}
