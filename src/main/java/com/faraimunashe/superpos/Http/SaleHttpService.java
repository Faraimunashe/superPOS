package com.faraimunashe.superpos.Http;

import com.faraimunashe.superpos.Bootstrap.ConfigReader;
import com.faraimunashe.superpos.Context.Auth;
import com.faraimunashe.superpos.Context.SharedCart;
import com.faraimunashe.superpos.Models.CartItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaleHttpService {
    static ConfigReader config = new ConfigReader();
    private static final String BEARER_TOKEN = Auth.getToken();

    private static String server = config.getValue("SERVER");
    private static String serverVersion = config.getValue("SERVER_VERSION");

    private static final String API_URL = server + "/" + serverVersion + "/sales";

    public static JsonObject postSale(double amount, String type, String currency, double change, double cash) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Get cart items from SharedCart and convert to the required format
        List<Map<String, Object>> items = SharedCart.getInstance().getCartItemsInCurrency(currency).stream()
                .map(cartItem -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("item_id", cartItem.getItemId());
                    itemMap.put("qty", cartItem.getQuantity());
                    itemMap.put("price", cartItem.getPriceInCurrency(currency));
                    itemMap.put("total_price", cartItem.getTotalPriceInCurrency(currency));
                    return itemMap;
                })
                .collect(Collectors.toList());

        // Create the sale request body
        Map<String, Object> saleData = new HashMap<>();
        saleData.put("amount", amount);
        saleData.put("type", type);
        saleData.put("currency", currency);
        saleData.put("items", items);
        saleData.put("change", change);
        saleData.put("cash", cash);

        String requestBody = new Gson().toJson(saleData);

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Process the response
        if (response.statusCode() == 200) {
            return new Gson().fromJson(response.body(), JsonObject.class);
        } else {
            throw new Exception("Sale posting failed: " + response.body());
        }
    }

}
