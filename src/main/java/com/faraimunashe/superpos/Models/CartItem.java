package com.faraimunashe.superpos.Models;

import com.faraimunashe.superpos.Services.CurrencyConversionService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class CartItem {
    private final SimpleIntegerProperty item_id;
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;
    private final SimpleDoubleProperty totalPrice;

    public CartItem(Integer item_id, String name, int quantity, double price) {
        this.item_id = new SimpleIntegerProperty(item_id);
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.totalPrice = new SimpleDoubleProperty(quantity * price);
    }

    public Integer getItemId() {
        return item_id.get();
    }

    public SimpleIntegerProperty itemIdProperty() {
        return item_id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public SimpleDoubleProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
        this.totalPrice.set(quantity * price.get());
    }

    public double getPriceInCurrency(String currency) {
        double conversionRate = CurrencyConversionService.getInstance().getConversionRate(currency);
        return price.get() * conversionRate;
    }

    public double getTotalPriceInCurrency(String currency) {
        double conversionRate = CurrencyConversionService.getInstance().getConversionRate(currency);
        return totalPrice.get() * conversionRate;
    }
}
