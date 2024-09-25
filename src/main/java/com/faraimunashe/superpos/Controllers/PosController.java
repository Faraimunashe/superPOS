package com.faraimunashe.superpos.Controllers;


import com.faraimunashe.superpos.Bootstrap.ConfigReader;
import com.faraimunashe.superpos.Context.Auth;
import com.faraimunashe.superpos.Context.SharedCart;
import com.faraimunashe.superpos.Http.CurrencyHttpService;
import com.faraimunashe.superpos.Http.ItemHttpService;
import com.faraimunashe.superpos.Models.CartItem;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

public class PosController implements Initializable {

    @FXML
    private HBox headerHbox;

    @FXML
    private ScrollPane scrollProducts;

    @FXML
    private Region spacer;

    @FXML
    private AnchorPane tileAnchor;

    @FXML
    private TilePane tileItem;

    @FXML
    private Label timeLabel;

    @FXML
    private Label lblLocation;

    @FXML
    private Label lblTerminal;

    @FXML
    private Label lblUsername;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableView<CartItem> tblCart;

    @FXML
    private TableColumn<CartItem, Void> colAction;

    @FXML
    private TableColumn<CartItem, String> colItem;

    @FXML
    private TableColumn<CartItem, Double> colPrice;

    @FXML
    private TableColumn<CartItem, Integer> colQty;

    @FXML
    private Label lblAmount;

    private ItemHttpService itemService;

    private JsonNode allItems;

    private CurrencyHttpService conversionService = new CurrencyHttpService();

    ConfigReader config = new ConfigReader();

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");


    private String termId = config.getValue("TERMID");

    private ObservableList<CartItem> cartItems;

    private String selectedCurrency = "USD";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemService = new ItemHttpService();
        fetchItemsAndPopulateUI();

        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (allItems != null) {
                filterItems(newText);
            }
        });

        lblTerminal.setText("TERMINAL: " + termId);
        lblUsername.setText("USERNAME: " + Auth.getUser().getName());
        clock_time();

        /*
         * List Cart Items
         * */

        cartItems = SharedCart.getInstance().getCartItems();
        tblCart.setItems(cartItems);

        //colItem_id.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getItemId()).asObject());
        colItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colQty.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(cellData -> {
            double priceInCurrency = cellData.getValue().getTotalPriceInCurrency(selectedCurrency);
            return new SimpleDoubleProperty(priceInCurrency).asObject();
        });
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQty.setOnEditCommit(event -> {
            CartItem item = event.getRowValue();
            int newQty = event.getNewValue();
            item.setQuantity(newQty);
            updateCartDisplay();
        });

        tblCart.setEditable(true);

        addButtonToTable();
    }

    /**
     * Fetches items asynchronously and populates the TilePane with the fetched items.
     */
    private void fetchItemsAndPopulateUI() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                CompletableFuture<JsonNode> itemsFuture = itemService.fetchItems();

                itemsFuture.thenAccept(itemsArray -> {
                    allItems = itemsArray; // Save all items for filtering
                    Platform.runLater(() -> populateTilePane(itemsArray));
                }).exceptionally(ex -> {
                    System.out.println("Error: " + ex.getMessage());
                    return null;
                });

                return null;
            }
        };

        new Thread(task).start();
    }

    /**
     * Populates the TilePane with the items fetched from the API.
     *
     * @param itemsArray The array of items returned from the API.
     */
    private void populateTilePane(JsonNode itemsArray) {
        tileItem.getChildren().clear();

        for (JsonNode itemNode : itemsArray) {
            // Extracting data from JSON node
            int itemId = itemNode.get("id").asInt();
            String itemName = itemNode.get("name").asText();
            double itemPrice = itemNode.get("price").asDouble();

            // Creating a VBox to represent the item
            VBox itemBox = new VBox();
            itemBox.setAlignment(Pos.CENTER);
            itemBox.setPrefHeight(150);
            itemBox.setPrefWidth(320);
            itemBox.getStyleClass().add("tile");

            // Adding click event handler
            itemBox.setOnMouseClicked(event -> handleItemClick(itemId, itemName, itemPrice));

            // Creating and styling labels
            Label nameLabel = new Label(itemName);
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label priceLabel = new Label(String.format("Price: $%.2f", itemPrice));
            priceLabel.setStyle("-fx-font-size: 12px;");

            // Adding labels to the VBox
            itemBox.getChildren().addAll(nameLabel, priceLabel);

            // Adding VBox to the TilePane
            tileItem.getChildren().add(itemBox);
        }

        // Setting the TilePane as the content of the ScrollPane
        scrollProducts.setContent(tileItem);
    }

    private void filterItems(String query) {
        tileItem.getChildren().clear();

        if (query == null || query.isEmpty()) {
            populateTilePane(allItems);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            Iterable<JsonNode> filteredItems = () -> StreamSupport.stream(allItems.spliterator(), false)
                    .filter(item -> item.get("name").asText().toLowerCase().contains(lowerCaseQuery))
                    .iterator();

            populateTilePane(allItems);
        }
    }

    @FXML
    void handleSearchAction(KeyEvent event) {
        //System.out.println("Searched ...");
        String query = txtSearch.getText();
        filterItems(query);
    }

    void handleItemClick(int itemId, String itemName, Double itemPrice)
    {
        try {

            if (itemId <= 0 || itemName.isEmpty() || itemPrice <= 0) {
                showAlert("Invalid Input", "Cart cannot read item now.");
                return;
            }

            SharedCart sharedCart = SharedCart.getInstance();
            boolean itemExists = false;

            for (CartItem cartItem : sharedCart.getCartItems()) {
                if (cartItem.getItemId().equals(itemId)) {
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                CartItem newItem = new CartItem(itemId, itemName, 1, itemPrice);
                sharedCart.addItem(newItem);
            }

            System.out.println("Item added to the cart");
            //Upadate cart display
        } catch (Exception e) {
            showAlert("Invalid Input", "Something went wrong.");
        }
    }

    void  clock_time(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {

            LocalTime currentTime = LocalTime.now();
            timeLabel.setText(currentTime.format(timeFormatter));
        }),
                new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addButtonToTable() {
        colAction.setCellFactory(new Callback<TableColumn<CartItem, Void>, TableCell<CartItem, Void>>() {
            @Override
            public TableCell<CartItem, Void> call(final TableColumn<CartItem, Void> param) {
                final TableCell<CartItem, Void> cell = new TableCell<CartItem, Void>() {

                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction(event -> {
                            CartItem item = getTableView().getItems().get(getIndex());
                            deleteItemFromCart(item);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void deleteItemFromCart(CartItem item) {
        SharedCart.getInstance().removeItem(item);
        updateCartDisplay();
    }

    public void updateCartDisplay() {
        tblCart.refresh();

        double totalAmount = SharedCart.getInstance().getCartItems().stream()
                .mapToDouble(item -> item.getTotalPriceInCurrency(selectedCurrency)) // Use the selected currency
                .sum();

        lblAmount.setText("Total: " + selectedCurrency + " " + String.format("%.2f", totalAmount));
    }

}
