package com.faraimunashe.superpos.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencySessionManager {

    // Singleton instance
    private static CurrencySessionManager instance;

    // Map to hold currency rates (currency code -> conversion rate)
    private Map<String, Double> currencyRates;

    // List to hold currency codes
    private List<String> currencyCodes;

    // Private constructor to prevent instantiation
    private CurrencySessionManager() {
        currencyRates = new HashMap<>();
    }

    // Method to get the singleton instance
    public static synchronized CurrencySessionManager getInstance() {
        if (instance == null) {
            instance = new CurrencySessionManager();
        }
        return instance;
    }

    // Method to set currency codes
    public void setCurrencyCodes(List<String> codes) {
        this.currencyCodes = codes;
    }

    // Method to get currency codes
    public List<String> getCurrencyCodes() {
        return currencyCodes;
    }

    // Method to set currency rates
    public void setCurrencyRates(Map<String, Double> rates) {
        this.currencyRates = rates;
    }

    // Method to get a specific currency rate
    public Double getCurrencyRate(String currencyCode) {
        return currencyRates.get(currencyCode);
    }

    // Method to get all currency rates
    public Map<String, Double> getCurrencyRates() {
        return currencyRates;
    }
}
