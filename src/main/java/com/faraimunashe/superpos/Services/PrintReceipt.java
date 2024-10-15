package com.faraimunashe.superpos.Services;

import com.faraimunashe.superpos.Context.SharedCart;
import com.faraimunashe.superpos.Models.CartItem;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintReceipt {
    private final String cashierName;
    private final LocalDateTime saleDateTime;
    private final String saleRef;
    private final Double amountPaid;
    private final Double change;
    private final String transaction_source;
    private final Double totalAmount;
    private final String currency;

    public PrintReceipt(String cashierName, LocalDateTime saleDateTime, String saleRef, Double amountPaid, Double change, String transaction_source, Double totalAmount, String currency) {
        this.cashierName = cashierName;
        this.saleDateTime = saleDateTime;
        this.saleRef = saleRef;
        this.amountPaid = amountPaid;
        this.change = change;
        this.transaction_source = transaction_source;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }
    public void printReceipt() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            // Get the default printer
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.getDefaultPageLayout();

            VBox receipt = new VBox(5); // 5 is the spacing between elements
            receipt.setAlignment(Pos.TOP_LEFT);

            // Constrain width to fit within 80mm receipt paper width
            double receiptWidth = 300;

            // header
            Text header = new Text("Midlands State University");
            header.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            receipt.getChildren().add(header);

            // subheader
            Text subheader = new Text("Canteen Shop");
            subheader.setFont(Font.font("Arial", FontWeight.BOLD, 9));
            receipt.getChildren().add(subheader);

            // reference number
            Text saleRefText = new Text("Sale Ref: " + this.saleRef);
            saleRefText.setFont(Font.font("Arial", 9));
            receipt.getChildren().add(saleRefText);

            // cashier name
            Text cashierNameText = new Text("Cashier: " + this.cashierName);
            cashierNameText.setFont(Font.font("Arial", 9));
            receipt.getChildren().add(cashierNameText);

            // transaction source
            Text transationSourceText = new Text("Trans: " + this.transaction_source);
            transationSourceText.setFont(Font.font("Arial", 9));
            receipt.getChildren().add(transationSourceText);

            // sale date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Text dateTimeText = new Text("Date: " + this.saleDateTime.format(formatter));
            dateTimeText.setFont(Font.font("Arial", 9));
            receipt.getChildren().add(dateTimeText);

            // separator line
            Line separator = new Line(0, 0, receiptWidth, 0);
            receipt.getChildren().add(separator);

            // Get items from SharedCart
            SharedCart cart = SharedCart.getInstance();

            // Check if the cart is empty
            if (cart.getCartItems().isEmpty()) {
                showAlert("Error", "Cart is empty!");
                return;
            }

            for (CartItem item : cart.getCartItems()) {
                HBox itemLine = new HBox(10);
                itemLine.setAlignment(Pos.CENTER_LEFT);

                Text itemName = new Text(item.getName());
                itemName.setFont(Font.font("Arial", 7));

                Text itemQuantity = new Text("x" + item.getQuantity());
                itemQuantity.setFont(Font.font("Arial", 7));

                Text itemTotalPrice = new Text(String.format("$%.2f", item.getTotalPriceInCurrency(currency)));
                itemTotalPrice.setFont(Font.font("Arial", 7));
                itemTotalPrice.setTextAlignment(TextAlignment.RIGHT);

                itemLine.getChildren().addAll(itemName, itemQuantity, itemTotalPrice);
                receipt.getChildren().add(itemLine);
            }

            // separator
            Line separator2 = new Line(0, 0, receiptWidth, 0);
            receipt.getChildren().add(separator2);

            // total amount
            HBox totalLine = new HBox(10);
            totalLine.setAlignment(Pos.CENTER_LEFT);

            Text totalLabel = new Text("Total:");
            totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));


            Text totalText = new Text(String.format(currency+"$%.2f", totalAmount));
            totalText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            totalText.setTextAlignment(TextAlignment.RIGHT);

            totalLine.getChildren().addAll(totalLabel, totalText);
            receipt.getChildren().add(totalLine);

            // Amount Paid
            HBox amountPaidLine = new HBox(10);
            amountPaidLine.setAlignment(Pos.CENTER_LEFT);

            Text amountPaidLabel = new Text("Amount Paid:");
            amountPaidLabel.setFont(Font.font("Arial", FontWeight.BOLD, 8));

            double amountPaid = this.amountPaid;
            Text amountPaidText = new Text(String.format(currency+"$%.2f", amountPaid));
            amountPaidText.setFont(Font.font("Arial", FontWeight.BOLD, 8));
            amountPaidText.setTextAlignment(TextAlignment.RIGHT);

            amountPaidLine.getChildren().addAll(amountPaidLabel, amountPaidText);
            receipt.getChildren().add(amountPaidLine);

            // Change
            HBox changeLine = new HBox(10);
            changeLine.setAlignment(Pos.CENTER_LEFT);

            Text changeLabel = new Text("Change:");
            changeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 8));

            double change = this.change;
            Text changeText = new Text(String.format(currency+"$%.2f", change));
            changeText.setFont(Font.font("Arial", FontWeight.BOLD, 8));
            changeText.setTextAlignment(TextAlignment.RIGHT);

            changeLine.getChildren().addAll(changeLabel, changeText);
            receipt.getChildren().add(changeLine);

            // Generate QR Code
            try {
                Image qrCodeImage = QRCodeGenerator.generateQRCodeImage(this.saleRef, 100, 100); // Adjust size as needed
                ImageView qrCodeImageView = new ImageView(qrCodeImage);
                qrCodeImageView.setFitWidth(100);
                qrCodeImageView.setFitHeight(100);
                receipt.getChildren().add(qrCodeImageView);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "QR code generation failed!");
            }

            // Set the receipt width and print the VBox node directly without showing the print dialog
            receipt.setPrefWidth(receiptWidth);
            boolean success = job.printPage(pageLayout, receipt);
            if (success) {
                job.endJob();
            }
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
