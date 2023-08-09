package shopms.controllers.purchases;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.products.Product;
import shopms.controllers.others.SalesPurchasesReturns;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class AddPurchaseController {
    @FXML
    private DatePicker purchasedDatePicker;
    @FXML
    private Label referenceLabel;
    @FXML
    private TextField supplierField;
    @FXML
    private TextField shippingCostField;
    @FXML
    private GridPane addPurchaseGridPane;
    @FXML
    private ListView<SalesPurchasesReturns> productPurchaseListView;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private Button submitButton;
    @FXML
    private Label error;

    private Purchase addPurchase = new Purchase();
    private ListView<Product> productListView = new ListView<>();
    private final ObservableList<Product> products = Model.getInstance().getProducts();

    static Boolean quantityIsEmpty = false;
    static Boolean costIsEmpty = true;

    @FXML
    private void initialize() {
        purchasedDatePicker.setValue(LocalDate.now());

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        addPurchase.setDate(formattedDate);

        Random random = new Random();
        int code = random.nextInt(1000000);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

        referenceLabel.setText("pr-" + LocalDate.now().format(dateFormat) + "-" + code);
        addPurchase.setReference_no(referenceLabel.getText());

        shippingCostField.setTextFormatter(Functions.getInstance().doubleOnly());

        productTableView.setItems(products);
        FilteredList<Product> productFilteredList = new FilteredList<>(products, p -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                    if (!newValue.isEmpty()) {
                        productFilteredList.setPredicate(p -> p.getName().toLowerCase().contains(newValue.toLowerCase()) || String.valueOf(p.getCode()).contains(newValue.toLowerCase()));
                        productTableView.setItems(productFilteredList);
                    } else {
                        productTableView.setItems(products);
                    }
                }
        );

        productTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        submitButton.disableProperty().bind(Bindings.isEmpty(productPurchaseListView.getItems()));

        productTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                handleAddProductPurchase(productTableView.getSelectionModel().getSelectedItem());
            }
        });

        productPurchaseListView.setCellFactory(param -> new ModifiedCell());
    }

    private void handleAddProductPurchase(Product product) {
        SalesPurchasesReturns productPurchase = new SalesPurchasesReturns();
        productPurchase.setReference(addPurchase.getReference_no());

        if (!productListView.getItems().contains(product)) {
            productListView.getItems().add(product);
            productPurchase.setCode(product.getCode());
            productPurchaseListView.getItems().add(productPurchase);
        }
    }

    @FXML
    private void handleSubmit() throws SQLException {
        if (supplierField.getText().isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the supplier name.");
        } else if (supplierField.getText().length() < 3) {
            error.setManaged(true);
            error.setText("Please enter a valid supplier name.");
        } else if (quantityIsEmpty) {
            error.setManaged(true);
            error.setText("Please enter valid quantity(s).");
        } else if (costIsEmpty) {
            error.setManaged(true);
            error.setText("Please enter valid cost(s).");
        } else {
            error.setManaged(false);
            error.setText("");

            addPurchase.setSupplier(supplierField.getText());
            addPurchase.setItems(productPurchaseListView.getItems().size());
            addPurchase.setQuantity(quantity());
            addPurchase.setProduct_cost(productCost());
            addPurchase.setShipping_cost(Double.parseDouble(shippingCostField.getText()));
            addPurchase.setGrand_total(addPurchase.getProduct_cost() + addPurchase.getShipping_cost());

            boolean success = Model.getInstance().setQueryInsertPurchase(
                    addPurchase.getDate(),
                    addPurchase.getReference_no(),
                    addPurchase.getSupplier(),
                    addPurchase.getItems(),
                    addPurchase.getQuantity(),
                    addPurchase.getProduct_cost(),
                    addPurchase.getShipping_cost(),
                    addPurchase.getGrand_total()
            );

            if (success) {
                Model.getInstance().getPurchases().add(addPurchase);
                for (SalesPurchasesReturns productPurchase : productPurchaseListView.getItems()) {
                    Model.getInstance().setQueryInsertSalesPurchaseReturn(
                            productPurchase.getReference(),
                            productPurchase.getCode(),
                            productPurchase.getQuantity(),
                            productPurchase.getPrice());
                    Model.getInstance().getSalesPurchasesReturns().add(productPurchase);
                    for (Product product : Model.getInstance().getProducts()) {
                        if (product.getCode() == productPurchase.getCode()) {
                            product.setQuantity(product.getQuantity() + productPurchase.getQuantity());
                            Model.getInstance().setQueryUpdateProductQuantity(product.getQuantity(), product.getCode());
                        }
                    }
                }
                Functions.getInstance().showAlert("Purchase added", addPurchaseGridPane);
            }
        }
    }

    private int quantity() {
        int quantity = 0;
        for (SalesPurchasesReturns productPurchase : productPurchaseListView.getItems()) {
            if (productPurchase.getReference().equals(referenceLabel.getText())) {
                quantity += productPurchase.getQuantity();
            }
        }
        return quantity;
    }

    private double productCost() {
        int cost = 0;
        for (SalesPurchasesReturns productPurchase : productPurchaseListView.getItems()) {
            if (productPurchase.getReference().equals(referenceLabel.getText())) {
                cost += productPurchase.getPrice() * productPurchase.getQuantity();
            }
        }
        return cost;
    }

    public void handleCancel() {
        addPurchaseGridPane.getScene().getWindow().hide();
    }

    public void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleSubmit();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            addPurchaseGridPane.getScene().getWindow().hide();
    }

    class ModifiedCell extends ListCell<SalesPurchasesReturns> {
        HBox hbox = new HBox();
        Label item = new Label();
        TextField quantityField = new TextField();
        TextField productCostField = new TextField();
        Button deleteButton = new Button("DELETE");

        public ModifiedCell() {
            super();
            super.setAlignment(Pos.CENTER);

            item.setMaxWidth(Double.MAX_VALUE);
            hbox.setStyle("-fx-spacing: 20.0; -fx-font-size: 20px;");
            HBox.setHgrow(item, Priority.ALWAYS);
            quantityField.setPromptText("Quantity...");
            productCostField.setPromptText("Product Cost...");

            quantityField.setText(String.valueOf(1));
            if (getItem() != null)
                getItem().setQuantity(1);
            quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\s0-9*")) {
                    quantityField.setText(newValue.replaceAll("[^\\s0-9]", ""));
                }
                if (newValue.length() > 3) {
                    String s = newValue.substring(0, 3);
                    quantityField.setText(s);
                }
                if (!newValue.isEmpty() && Integer.parseInt(newValue) != 0) {
                    getItem().setQuantity(Integer.parseInt(newValue));
                    quantityIsEmpty = false;
                } else {
                    quantityIsEmpty = true;
                }
            });

            productCostField.setTextFormatter(Functions.getInstance().doubleOnly());
            productCostField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    getItem().setPrice(Double.parseDouble(newValue));
                }
                costIsEmpty = getItem().getPrice() == 0;
            });

            deleteButton.setOnAction(event -> {
                productListView.getItems().removeIf(product -> product.getCode() == getItem().getCode());
                productPurchaseListView.getItems().remove(getItem());
            });
            deleteButton.setFocusTraversable(false);

            hbox.getChildren().addAll(item, quantityField, productCostField, deleteButton);
        }

        @Override
        protected void updateItem(SalesPurchasesReturns productPurchase, boolean empty) {
            super.updateItem(productPurchase, empty);
            setText(null);
            setGraphic(null);
            if (productPurchase != null && !empty) {
                for (Product product : Model.getInstance().getProducts()) {
                    if (productPurchase.getCode() == product.getCode()) {
                        item.setText(product.getName() + " (" + product.getCode() + ")");
                        productPurchase.setQuantity(Integer.parseInt(quantityField.getText()));
                        productPurchase.setPrice(Double.parseDouble(productCostField.getText()));
                    }
                }
                setGraphic(hbox);
            }
        }
    }
}
