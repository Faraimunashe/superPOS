package com.faraimunashe.superpos.Context;

import java.util.List;

public class Auth {
    private static String token;
    private static User user;
    private static List<Rate> rates; // List to store currency rates

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Auth.token = token;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Auth.user = user;
    }

    public static List<Rate> getRates() {
        return rates;
    }

    public static void setRates(List<Rate> rates) {
        Auth.rates = rates;
    }

    public static class User {
        private int id;
        private String name;
        private String email;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class Rate {
        private int id;
        private String currencyCode;
        private double conversionRate;
        private boolean active;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

        public double getConversionRate() { return conversionRate; }
        public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }
}
