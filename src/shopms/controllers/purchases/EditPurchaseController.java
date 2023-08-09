package shopms.controllers.purchases;

import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.products.Product;
import shopms.controllers.others.SalesPurchasesReturns;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditPurchaseController {
    @FXML
    private DatePicker purchasedDatePicker;
    @FXML
    private Label referenceLabel;
    @FXML
    private TextField supplierField;
    @FXML
    private TextField shippingCostField;
    @FXML
    private GridPane editPurchaseGridPane;
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

    private ListView<Product> productListView = new ListView<>();

    static Boolean quantityIsEmpty = false;
    static Boolean costIsEmpty = true;

    public static Purchase editPurchase;

    @FXML
    private void initialize() {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        purchasedDatePicker.setValue(LocalDate.parse(editPurchase.getDate().substring(0, 10), myFormatObj));

        referenceLabel.setText(editPurchase.getReference_no());
        supplierField.setText(editPurchase.getSupplier());
        shippingCostField.setTextFormatter(Functions.getInstance().doubleOnly());
        shippingCostField.setText(String.valueOf(editPurchase.getShipping_cost()));

        for (SalesPurchasesReturns productPurchase : Model.getInstance().getSalesPurchasesReturns()) {
            if (productPurchase.getReference().equals(editPurchase.getReference_no())) {
                productPurchaseListView.getItems().add(productPurchase);
            }
            for (Product product : Model.getInstance().getProducts()) {
                if (product.getCode() == productPurchase.getCode()) {
                    productListView.getItems().add(product);
                }
            }
        }

        FilteredList<Product> productFilteredList = new FilteredList<>(Model.getInstance().getProducts(), p -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                    if (!newValue.isEmpty()) {
                        productFilteredList.setPredicate(p -> p.getName().toLowerCase().contains(searchField.getText().toLowerCase()) || String.valueOf(p.getCode()).contains(searchField.getText().toLowerCase()));
                        productTableView.setItems(productFilteredList);
                    } else {
                        productTableView.setItems(null);
                    }
                }
        );

        searchField.setDisable(true);

        productTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        submitButton.disableProperty().bind(Bindings.isEmpty(productPurchaseListView.getItems()));

        final ContextMenu menu = new ContextMenu();
        final MenuItem addProductMenu = new MenuItem("Add...");

        addProductMenu.setOnAction(event -> {
            Product addProduct = productTableView.getSelectionModel().getSelectedItem();
            handleAddProductPurchase(addProduct);
        });

        addProductMenu.disableProperty().bind(Bindings.isEmpty(productTableView.getSelectionModel().getSelectedItems()));
        menu.getItems().addAll(addProductMenu);
        productTableView.setContextMenu(menu);
        productPurchaseListView.setCellFactory(param -> new ModifiedCell());
    }

    private void handleAddProductPurchase(Product product) {
        SalesPurchasesReturns productPurchase = new SalesPurchasesReturns();
        productPurchase.setReference(editPurchase.getReference_no());

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

            Model.getInstance().setQueryRemoveSalesPurchaseReturn(editPurchase.getReference_no());

            editPurchase.setSupplier(supplierField.getText());
            editPurchase.setItems(productPurchaseListView.getItems().size());
            editPurchase.setQuantity(quantity());
            editPurchase.setProduct_cost(productCost());
            editPurchase.setShipping_cost(Double.parseDouble(shippingCostField.getText()));
            editPurchase.setGrand_total(editPurchase.getProduct_cost() + editPurchase.getShipping_cost());

            boolean success = Model.getInstance().setQueryUpdatePurchase(
                    editPurchase.getDate(),
                    editPurchase.getSupplier(),
                    editPurchase.getItems(),
                    editPurchase.getQuantity(),
                    editPurchase.getProduct_cost(),
                    editPurchase.getShipping_cost(),
                    editPurchase.getGrand_total(),
                    editPurchase.getReference_no()
            );

            if (success) {
                Model.getInstance().getSalesPurchasesReturns().removeIf(productPurchase -> productPurchase.getReference().equals(editPurchase.getReference_no()));
                for (SalesPurchasesReturns checkProductPurchase : productPurchaseListView.getItems()) {
                    Model.getInstance().setQueryInsertSalesPurchaseReturn(
                            checkProductPurchase.getReference(),
                            checkProductPurchase.getCode(),
                            checkProductPurchase.getQuantity(),
                            checkProductPurchase.getPrice());
                    Model.getInstance().getSalesPurchasesReturns().add(checkProductPurchase);
                }
                Functions.getInstance().showAlert("Purchase edited", editPurchaseGridPane);
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
        editPurchaseGridPane.getScene().getWindow().hide();
    }

    public void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleSubmit();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            editPurchaseGridPane.getScene().getWindow().hide();
    }

    static class ModifiedCell extends ListCell<SalesPurchasesReturns> {
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

            deleteButton.setOnAction(event -> getListView().getItems().remove(getItem()));
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
                        quantityField.setText(String.valueOf(productPurchase.getQuantity()));
                        deleteButton.setDisable(true);
                        quantityField.setDisable(true);
                        productPurchase.setQuantity(Integer.parseInt(quantityField.getText()));
                        productCostField.setText(String.valueOf(productPurchase.getPrice()));
                        productPurchase.setPrice(Double.parseDouble(productCostField.getText()));
                    } else {
                    }
                }
                setGraphic(hbox);
            }
        }
    }
}
