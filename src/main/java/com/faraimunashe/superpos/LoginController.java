package com.faraimunashe.superpos;

import com.faraimunashe.superpos.Context.Auth;
import com.faraimunashe.superpos.Http.LoginHttpService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label lblErrorMessage;

    @FXML
    public void initialize() {

    }

    @FXML
    void handleLoginAction(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        // Disable login form and show progress indicator
        disableLoginForm(true);
        progressIndicator.setVisible(true);

        new Thread(() -> {
            try {
                JsonObject response = LoginHttpService.login(email, password);

//                String token = response.get("token").getAsString();
//                JsonObject userJson = response.getAsJsonObject("user");
//
//                Auth.User user = new Auth.User();
//                user.setId(userJson.get("id").getAsInt());
//                user.setName(userJson.get("name").getAsString());
//                user.setEmail(userJson.get("email").getAsString());
//
//                Auth.setToken(token);
//                Auth.setUser(user);

                handleLoginResponse(response);

                //System.out.println("Username : "+userJson.get("name").getAsString());

                Platform.runLater(() -> {
                    showMainApp();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Login failed: " + e.getMessage());
                    alert.showAndWait();
                });
            } finally {
                Platform.runLater(() -> {
                    disableLoginForm(false);
                    progressIndicator.setVisible(false);
                });
            }
        }).start();
    }

    private void disableLoginForm(boolean disable) {
        txtEmail.setDisable(disable);
        txtPassword.setDisable(disable);
        btnLogin.setDisable(disable);
    }

    private void showMainApp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pos-view.fxml"));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(newScene);
            newStage.show();

            Stage currentStage = (Stage) btnLogin.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handleLoginResponse(JsonObject response) {
        String token = response.get("token").getAsString();
        JsonObject userJson = response.getAsJsonObject("user");

        Auth.User user = new Auth.User();
        user.setId(userJson.get("id").getAsInt());
        user.setName(userJson.get("name").getAsString());
        user.setEmail(userJson.get("email").getAsString());

        // Store user and token
        Auth.setToken(token);
        Auth.setUser(user);

        // Get rates and store them in Auth
        JsonArray ratesJson = response.getAsJsonArray("rates");
        List<Auth.Rate> rates = new ArrayList<>();

        for (int i = 0; i < ratesJson.size(); i++) {
            JsonObject rateJson = ratesJson.get(i).getAsJsonObject();
            Auth.Rate rate = new Auth.Rate();
            rate.setId(rateJson.get("id").getAsInt());
            rate.setCurrencyCode(rateJson.get("currency_code").getAsString());
            rate.setConversionRate(rateJson.get("conversion_rate").getAsDouble());
            rate.setActive(rateJson.get("active").getAsInt() == 1); // Convert to boolean

            rates.add(rate);
        }

        Auth.setRates(rates); // Store rates in Auth

        //Platform.runLater(this::showMainApp);
    }
}