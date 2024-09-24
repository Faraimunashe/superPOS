package com.faraimunashe.superpos.Http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.faraimunashe.superpos.Context.Auth;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CurrencyHttpService {
    private static final String API_URL = "http://127.0.0.1:8000/api/v1/currencies";
    private static final String BEARER_TOKEN = Auth.getToken();

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public CurrencyHttpService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<Double> getConversionRate(String currencyCode) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode data = objectMapper.readTree(response.body()).get("data");
                            for (JsonNode currency : data) {
                                if (currency.get("currency_code").asText().equals(currencyCode)) {
                                    return currency.get("rate").asDouble();
                                }
                            }
                            throw new RuntimeException("Currency not found: " + currencyCode);
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing response: " + e.getMessage(), e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch currencies. HTTP Status: " + response.statusCode());
                    }
                });
    }
}
