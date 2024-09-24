package com.faraimunashe.superpos.Services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.faraimunashe.superpos.Bootstrap.ConfigReader;
import com.faraimunashe.superpos.Context.Auth;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CurrencyConversionService {
    private static CurrencyConversionService instance;
    private Map<String, Double> currencyRates;

    static ConfigReader config = new ConfigReader();

    private static String server = config.getValue("SERVER");
    private static String serverVersion = config.getValue("SERVER_VERSION");

    private static final String API_URL = server+"/"+serverVersion+"/rates";
    private static final String BEARER_TOKEN = Auth.getToken();

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    private CurrencyConversionService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        currencyRates = new HashMap<>();
        // Fetch currency rates asynchronously
        loadRatesFromApi();
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

    private void loadRatesFromApi() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode jsonResponse = objectMapper.readTree(response.body());
                            JsonNode data = jsonResponse.get("data");

                            data.fields().forEachRemaining(entry -> {
                                String currencyCode = entry.getKey();
                                double conversionRate = entry.getValue().asDouble();
                                currencyRates.put(currencyCode, conversionRate);
                            });
                            return data;
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing response: " + e.getMessage(), e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch currency rates. HTTP Status: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
