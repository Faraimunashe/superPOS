package com.faraimunashe.superpos.Controllers;

import com.faraimunashe.superpos.Context.AppContext;
import com.faraimunashe.superpos.Context.Auth;
import com.faraimunashe.superpos.Context.SharedCart;
import com.faraimunashe.superpos.Services.PrintReceipt;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.faraimunashe.superpos.Http.SaleHttpService.postSale;

public class CashPaymentController {
    @FXML
    private Button btnExit;

    @FXML
    private Button btnProcess;

    @FXML
    private Label lblErrorMessage;

    @FXML
    private AnchorPane paneTitle;

    @FXML
    private TextField txtCashAmount;

    @FXML
    private TextField txtChangeAmount;

    @FXML
    private ProgressIndicator postProgress;

    private String currency;

    private SharedCart sharedCart = SharedCart.getInstance();


    public void initialize() {
        txtCashAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            updateChangeAmount();
        });
    }


    public void setCurrency(String currency) {
        this.currency = Objects.requireNonNullElse(currency, "USD");
    }

    @FXML
    void handleExit(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        Stage stage = (Stage) sourceButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleProcess(ActionEvent event) {
        // Show the progress indicator and hide buttons/text fields
        postProgress.setVisible(true);
        btnProcess.setDisable(true);
        btnExit.setDisable(true);
        txtCashAmount.setDisable(true);
        txtChangeAmount.setDisable(true);

        // Run the processing in a separate thread
        new Thread(() -> {
            try {
                double amount = SharedCart.getInstance().getCartItems().stream()
                        .mapToDouble(item -> item.getTotalPriceInCurrency(this.currency))
                        .sum();
                String type = "CASH";
                String currency = this.currency;
                double cash = Double.parseDouble(txtCashAmount.getText());
                double change = Double.parseDouble(txtChangeAmount.getText());

                if (cash < amount) {
                    Platform.runLater(() -> {
                        showAlert("Error", "Cash amount cannot be less than cart total.");
                        // Hide progress indicator and enable buttons/text fields
                        postProgress.setVisible(false);
                        btnProcess.setDisable(false);
                        btnExit.setDisable(false);
                        txtCashAmount.setDisable(false);
                        txtChangeAmount.setDisable(false);
                    });
                    return;
                }

                // Post sale
                JsonObject response = postSale(amount, type, currency, change, cash);
                System.out.println(response);

                if (response != null) {
                    // Extract the sale reference safely
                    String saleRef = response.has("sale") && !response.get("sale").isJsonNull()
                            ? response.getAsJsonObject("sale").get("reference").getAsString()
                            : "N/A";

                    // Create the receipt with extracted data
                    PrintReceipt printReceipt = new PrintReceipt(
                            Auth.getUser().getName(),
                            LocalDateTime.now(),
                            saleRef,
                            cash,
                            change,
                            type,
                            amount,
                            currency
                    );

                    printReceipt.printReceipt();

                    SharedCart.getInstance().getCartItems().clear();

                    Button sourceButton = (Button) event.getSource();
                    Stage stage = (Stage) sourceButton.getScene().getWindow();
                    Platform.runLater(stage::close);
                } else {
                    Platform.runLater(() -> {
                        showAlert("Error", "Invalid response from server.");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Error", "Could not process payment, try again later.");
                });
            } finally {
                Platform.runLater(() -> {
                    PosController posController = AppContext.getInstance().getPosController();
                    if (posController != null) {
                        posController.updateCartDisplay();
                    }
                    postProgress.setVisible(false);
                    btnProcess.setDisable(false);
                    btnExit.setDisable(false);
                    txtCashAmount.setDisable(false);
                    txtChangeAmount.setDisable(false);
                });
            }
        }).start();

    }

    private void updateChangeAmount() {
        try {
            Double cashAmount = Double.parseDouble(txtCashAmount.getText());

            // Get the total amount
            double totalAmount = SharedCart.getInstance().getCartItems().stream()
                    .mapToDouble(item -> item.getTotalPriceInCurrency(this.currency))
                    .sum();

            // Calculate the change
            Double change = cashAmount - totalAmount;

            // Ensure change is not negative
            if (change < 0.0) {
                change = 0.0;
            }

            txtChangeAmount.setText(String.format("%.2f", change));
        } catch (NumberFormatException e) {
            txtChangeAmount.setText("0.00");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
