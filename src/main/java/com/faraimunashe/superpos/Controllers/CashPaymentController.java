package com.faraimunashe.superpos.Controllers;

import com.faraimunashe.superpos.Context.SharedCart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Objects;

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

    private String currency;


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
}
