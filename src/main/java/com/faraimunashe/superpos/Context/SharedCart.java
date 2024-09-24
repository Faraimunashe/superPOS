package com.faraimunashe.superpos.Context;


import com.faraimunashe.superpos.Models.CartItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SharedCart {
    private static SharedCart instance;
    private ObservableList<CartItem> cartItems;

    private SharedCart() {
        cartItems = FXCollections.observableArrayList();
    }

    public static SharedCart getInstance() {
        if (instance == null) {
            instance = new SharedCart();
        }
        return instance;
    }

    public ObservableList<CartItem> getCartItems() {
        return cartItems;
    }

    public void addItem(CartItem item) {
        cartItems.add(item);
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
    }

    public ObservableList<CartItem> getCartItemsInCurrency(String currency) {
        return FXCollections.observableArrayList(
                cartItems.stream()
                        .map(item -> new CartItem(
                                item.getItemId(),
                                item.getName(),
                                item.getQuantity(),
                                item.getPriceInCurrency(currency)
                        ))
                        .toList()
        );
    }
}