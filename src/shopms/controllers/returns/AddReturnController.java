package shopms.controllers.returns;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import shopms.controllers.ShopManagerController;
import shopms.controllers.customers.Customer;
import shopms.controllers.others.SalesPurchasesReturns;
import shopms.controllers.products.Product;
import shopms.controllers.purchases.Purchase;
import shopms.controllers.sales.Sale;
import shopms.model.Functions;
import shopms.model.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddReturnController {
    @FXML
    private GridPane addReturnGridPane;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private Label referenceLabel;
    @FXML
    private TextField supplierField;
    @FXML
    private ComboBox<String> customerCombo;
    @FXML
    private ListView<SalesPurchasesReturns> productReturnListView;
    @FXML
    private Label error;

    public static String reference;

    final DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyyMMdd");
    final String return_no = "rr-" + LocalDate.now().format(myFormatObj) + "-" + reference.substring(12);

    private static Sale selectedSale;
    private static Purchase selectedPurchase;
    static Boolean quantityIsEmpty = false;

    @FXML
    private void initialize() {
        for (Sale sale : Model.getInstance().getSales()) {
            if (sale.getInvoice_no().equals(reference)) {
                selectedSale = sale;
                referenceLabel.setText(sale.getInvoice_no());
                customerCombo.setVisible(true);
                customerCombo.setManaged(true);
                for (Customer customer : Model.getInstance().getCustomers()) {
                    customerCombo.getItems().add(customer.getName());
                    if (customer.getName().equals(sale.getCustomer())) {
                        customerCombo.getSelectionModel().select(customer.getName());
                    }
                }
            }
        }
        for (Purchase purchase : Model.getInstance().getPurchases()) {
            if (purchase.getReference_no().equals(reference)) {
                selectedPurchase = purchase;
                referenceLabel.setText(purchase.getReference_no());
                supplierField.setVisible(true);
                supplierField.setManaged(true);
                supplierField.setText(purchase.getSupplier());
            }
        }
        supplierField.setDisable(true);
        customerCombo.setDisable(true);
        returnDatePicker.setValue(LocalDate.now());

        for (SalesPurchasesReturns productPurchase : Model.getInstance().getSalesPurchasesReturns()) {
            if (productPurchase.getReference().equals(reference)) {
                SalesPurchasesReturns salesPurchasesReturns = productPurchase;
                salesPurchasesReturns.setReference(return_no);
                productReturnListView.getItems().add(salesPurchasesReturns);
            }
        }

        productReturnListView.setCellFactory(param -> new ModifiedCell());
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleSubmit();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            addReturnGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleSubmit() throws SQLException {
        if (quantityOverLimit()) {
            error.setManaged(true);
            error.setText("Quantity over purchased/sale quantity.");
        } else {
            error.setManaged(false);
            error.setText("");

            Return addReturn = new Return();

            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            addReturn.setDate(LocalDateTime.now().format(myFormatObj));

            addReturn.setSale_purchase_no(referenceLabel.getText());
            addReturn.setReturn_no(return_no);

            if (selectedSale != null && selectedPurchase == null) {
                addReturn.setSupplier_customer(selectedSale.getCustomer());
            } else if (selectedPurchase != null && selectedSale == null) {
                addReturn.setSupplier_customer(selectedPurchase.getSupplier());
            }

            addReturn.setItems(productReturnListView.getItems().size());
            addReturn.setQuantity(quantity());
            addReturn.setGrand_total(productCost());

            boolean success = Model.getInstance().setQueryInsertReturn(
                    addReturn.getDate(),
                    addReturn.getSale_purchase_no(),
                    addReturn.getReturn_no(),
                    addReturn.getSupplier_customer(),
                    addReturn.getItems(),
                    addReturn.getQuantity(),
                    addReturn.getGrand_total()
            );

            if (success) {
                Model.getInstance().getReturns().add(addReturn);
                for (SalesPurchasesReturns productPurchase : productReturnListView.getItems()) {
                    Model.getInstance().setQueryInsertSalesPurchaseReturn(
                            productPurchase.getReference(),
                            productPurchase.getCode(),
                            productPurchase.getQuantity(),
                            productPurchase.getPrice());
                    Model.getInstance().getSalesPurchasesReturns().add(productPurchase);
                    for (Product product : Model.getInstance().getProducts()) {
                        if (product.getCode() == productPurchase.getCode()) {
                            if (selectedSale == null && selectedPurchase != null)
                                product.setQuantity(product.getQuantity() - productPurchase.getQuantity());
                            else
                                product.setQuantity(product.getQuantity() + productPurchase.getQuantity());
                            Model.getInstance().setQueryUpdateProductQuantity(product.getQuantity(), product.getCode());
                        }
                    }
                }
                Functions.getInstance().showAlert("Return added", addReturnGridPane);
            }
        }
    }


    @FXML
    private void handleCancel() {
        addReturnGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleBack() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/shopms/resources/selectReturnSaleOrPurchase.fxml"));

        Stage stage = new Stage();
        stage.initOwner(ShopManagerController.staticBorderPane.getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Add Return");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setResizable(false);

        addReturnGridPane.getScene().getWindow().hide();
        stage.show();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }

    private int quantity() {
        int quantity = 0;
        for (SalesPurchasesReturns productPurchase : productReturnListView.getItems()) {
            quantity += productPurchase.getQuantity();
        }
        return quantity;
    }

    private double productCost() {
        int cost = 0;
        for (SalesPurchasesReturns productPurchase : productReturnListView.getItems()) {
            cost += productPurchase.getPrice() * productPurchase.getQuantity();
        }
        return cost;
    }

    private boolean quantityOverLimit() {
        for (SalesPurchasesReturns checkSalesPurchasesReturns : Model.getInstance().getSalesPurchasesReturns()) {
            for (SalesPurchasesReturns salesPurchasesReturns : productReturnListView.getItems()) {
                if (salesPurchasesReturns.getReference().equals(checkSalesPurchasesReturns.getReference())) {
                    if (reference.startsWith("pr")) {
                        for (Product product : Model.getInstance().getProducts()) {
                            if (product.getCode() == salesPurchasesReturns.getCode()) {
                                if (salesPurchasesReturns.getQuantity() > product.getQuantity()) {
                                    return true;
                                }
                            }
                        }
                    } else if (reference.startsWith("sr")) {
                        if (salesPurchasesReturns.getCode() == checkSalesPurchasesReturns.getCode()) {
                            if (salesPurchasesReturns.getQuantity() > checkSalesPurchasesReturns.getQuantity()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    static class ModifiedCell extends ListCell<SalesPurchasesReturns> {
        HBox hbox = new HBox();
        Label item = new Label();
        TextField quantityField = new TextField();
        Button deleteButton = new Button("DELETE");

        public ModifiedCell() {
            super();
            super.setAlignment(Pos.CENTER);

            item.setMaxWidth(Double.MAX_VALUE);
            hbox.setStyle("-fx-spacing: 20.0; -fx-font-size: 20px;");
            HBox.setHgrow(item, Priority.ALWAYS);
            quantityField.setPromptText("Quantity...");

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

            deleteButton.setOnAction(event -> getListView().getItems().remove(getItem()));
            deleteButton.setFocusTraversable(false);

            hbox.getChildren().addAll(item, quantityField, deleteButton);
        }

        @Override
        protected void updateItem(SalesPurchasesReturns productSale, boolean empty) {
            super.updateItem(productSale, empty);
            setText(null);
            setGraphic(null);
            if (productSale != null && !empty) {
                for (Product product : Model.getInstance().getProducts()) {
                    if (productSale.getCode() == product.getCode()) {
                        item.setText(product.getName() + " (" + product.getCode() + ")" + " - Rs." + product.getPrice());
                        quantityField.setText(String.valueOf(productSale.getQuantity()));
                        productSale.setQuantity(Integer.parseInt(quantityField.getText()));
                        productSale.setCode(product.getCode());
                        productSale.setPrice(product.getPrice());
                    }
                }
                setGraphic(hbox);
            }
        }
    }
}
