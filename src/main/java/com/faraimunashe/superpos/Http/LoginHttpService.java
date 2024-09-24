package com.faraimunashe.superpos.Http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.Map;

import com.faraimunashe.superpos.Bootstrap.ConfigReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LoginHttpService {
    static ConfigReader config = new ConfigReader();

    private static String server = config.getValue("SERVER");
    private static String serverVersion = config.getValue("SERVER_VERSION");

    private static final String API_URL = server+"/"+serverVersion+"/login";

    public static JsonObject login(String email, String password) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new Gson().fromJson(response.body(), JsonObject.class);
        } else {
            throw new Exception("Login failed: ." + response.body());
        }
    }
}
