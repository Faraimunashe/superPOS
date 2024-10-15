package com.faraimunashe.superpos.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.faraimunashe.superpos.Context.Auth;

public class CurrencyConversionService {
    private static CurrencyConversionService instance;
    private Map<String, Double> currencyRates;

    private CurrencyConversionService() {
        currencyRates = new HashMap<>();
        loadRatesFromAuth();
    }

    public static synchronized CurrencyConversionService getInstance() {
        if (instance == null) {
            instance = new CurrencyConversionService();
        }
        return instance;
    }

    public double getConversionRate(String currency) {
        return currencyRates.getOrDefault(currency, 1.0);
    }

    public void updateConversionRate(String currency, double rate) {
        currencyRates.put(currency, rate);
    }

    private void loadRatesFromAuth() {
        List<Auth.Rate> rates = Auth.getRates(); // Get rates from Auth

        for (Auth.Rate rate : rates) {
            currencyRates.put(rate.getCurrencyCode(), rate.getConversionRate());
        }
    }
}
