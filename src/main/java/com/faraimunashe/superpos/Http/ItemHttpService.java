package com.faraimunashe.superpos.Http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.faraimunashe.superpos.Bootstrap.ConfigReader;
import com.faraimunashe.superpos.Context.Auth;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemHttpService {
    static ConfigReader config = new ConfigReader();

    private static String server = config.getValue("SERVER");
    private static String serverVersion = config.getValue("SERVER_VERSION");

    private static final String API_URL = server + "/" + serverVersion + "/items";
    private static final String BEARER_TOKEN = Auth.getToken();


    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public ItemHttpService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches items from the API asynchronously.
     *
     * @return A CompletableFuture with the items in JSON format.
     */
    public CompletableFuture<JsonNode> fetchItems() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        //System.out.println(BEARER_TOKEN);

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readTree(response.body()).get("data");
                        } catch (Exception e) {
                            throw new RuntimeException("Error parsing response: " + e.getMessage(), e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch items. HTTP Status: " + response.statusCode());
                    }
                });
    }

}
