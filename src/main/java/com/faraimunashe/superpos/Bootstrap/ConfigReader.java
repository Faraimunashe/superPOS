package com.faraimunashe.superpos.Bootstrap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {
    private Map<String, String> configMap = new HashMap<>();

    private static String filePath = "configs/config.txt";

    public ConfigReader() {
        loadConfig(filePath);
    }

    private void loadConfig(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] keyValue = line.split("=", 2);
                if (keyValue.length == 2) {
                    configMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e.getMessage());
        }
    }

    public String getValue(String key) {
        return configMap.getOrDefault(key, null);
    }
}
