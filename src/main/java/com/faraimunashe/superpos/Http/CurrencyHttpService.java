package com.faraimunashe.superpos.Http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.faraimunashe.superpos.Bootstrap.ConfigReader;
import com.faraimunashe.superpos.Context.Auth;
import com.faraimunashe.superpos.Context.CurrencySessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CurrencyHttpService {
    static ConfigReader config = new ConfigReader();

    private static String server = config.getValue("SERVER");
    private static String serverVersion = config.getValue("SERVER_VERSION");

    private static final String API_URL = server + "/" + serverVersion + "/rates";
    private static final String BEARER_TOKEN = Auth.getToken();

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public CurrencyHttpService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<Object> loadRatesFromApi() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode jsonResponse = objectMapper.readTree(response.body());
                            JsonNode dataArray = jsonResponse.get("data");

                            // Lists to hold currency codes and rates
                            List<String> currencyCodes = new ArrayList<>();
                            Map<String, Double> currencyRates = new HashMap<>();

                            // Iterate over the data array
                            dataArray.forEach(currencyNode -> {
                                String currencyCode = currencyNode.get("currency_code").asText();
                                double conversionRate = currencyNode.get("conversion_rate").asDouble();
                                int isActive = currencyNode.get("active").asInt();

                                // Only add active currencies
                                if (isActive == 1) {
                                    currencyCodes.add(currencyCode);
                                    currencyRates.put(currencyCode, conversionRate);
                                }
                            });

                            // Store data in the session manager
                            CurrencySessionManager session = CurrencySessionManager.getInstance();
                            session.setCurrencyCodes(currencyCodes);
                            session.setCurrencyRates(currencyRates);

                            return null; // Void return type
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing response: " + e.getMessage(), e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch currency rates. HTTP Status: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null; // Handle exception and return null
                });
    }
}
