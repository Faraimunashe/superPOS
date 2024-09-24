package com.faraimunashe.superpos.Services;


import com.faraimunashe.superpos.Models.CartItem;

import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<Integer, CartItem> cart;

    private CartManager() {
        cart = new HashMap<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(Integer item_id, String name, double price) {
        CartItem item = cart.get(item_id);
        if (item != null) {
            item.setQuantity(item.getQuantity() + 1);
        } else {
            cart.put(item_id, new CartItem(item_id, name, 1, price));
        }
    }

    public void removeItem(Integer item_id) {
        cart.remove(item_id);
    }

    public Map<Integer, CartItem> getCart() {
        return cart;
    }

    public double getTotalAmount() {
        return cart.values().stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public void clearCart() {
        cart.clear();
    }

    public double getTotalAmountInCurrency(String currency) {
        return cart.values().stream()
                .mapToDouble(item -> item.getTotalPriceInCurrency(currency))
                .sum();
    }
}

