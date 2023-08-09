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
import shopms.controllers.others.SalesPurchasesReturns;
import shopms.controllers.products.Product;
import shopms.model.Functions;
import shopms.model.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditReturnController {
    @FXML
    private GridPane editReturnGridPane;
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

    public static Return editReturn;

    static Boolean quantityIsEmpty = false;


    @FXML
    private void initialize() {
        referenceLabel.setText(editReturn.getReturn_no());
        supplierField.setDisable(true);
        customerCombo.setDisable(true);

        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        returnDatePicker.setValue(LocalDate.parse(editReturn.getDate().substring(0, 10), myFormatObj));

        for (SalesPurchasesReturns productPurchase : Model.getInstance().getSalesPurchasesReturns()) {
            if (productPurchase.getReference().equals(editReturn.getReturn_no())) {
                productReturnListView.getItems().add(productPurchase);
            }
        }

        productReturnListView.setCellFactory(param -> new ModifiedCell());

        if (editReturn.getSale_purchase_no().startsWith("pr")) {
            supplierField.setVisible(true);
            supplierField.setManaged(true);
            supplierField.setText(editReturn.getSupplier_customer());
        } else {
            customerCombo.setVisible(true);
            customerCombo.setManaged(true);
            customerCombo.getItems().add(editReturn.getSupplier_customer());
            customerCombo.getSelectionModel().select(0);
        }
        supplierField.setDisable(true);
        customerCombo.setDisable(true);
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleSubmit();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            editReturnGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleSubmit() throws SQLException {
        if (quantityOverLimit()) {
            error.setManaged(true);
            error.setText(quantityOverLimit() + " quantity limit reached.");
        } else {
            error.setManaged(false);
            error.setText("");

            editReturn.setItems(productReturnListView.getItems().size());
            editReturn.setQuantity(quantity());
            editReturn.setGrand_total(productCost());

            boolean success = Model.getInstance().setQueryUpdateReturn(
                    editReturn.getDate(),
                    editReturn.getSupplier_customer(),
                    editReturn.getItems(),
                    editReturn.getQuantity(),
                    editReturn.getGrand_total(),
                    editReturn.getReturn_no()
            );

            if (success) {
                for (Return changeReturn : Model.getInstance().getReturns()) {
                    if (changeReturn.getReturn_no().equals(editReturn.getReturn_no())) {
                        changeReturn = editReturn;
                    }
                }

                Model.getInstance().getSalesPurchasesReturns().removeIf(productPurchase -> productPurchase.getReference().equals(editReturn.getReturn_no()));

                for (SalesPurchasesReturns checkProductPurchase : productReturnListView.getItems()) {
                    Model.getInstance().setQueryInsertSalesPurchaseReturn(
                            checkProductPurchase.getReference(),
                            checkProductPurchase.getCode(),
                            checkProductPurchase.getQuantity(),
                            checkProductPurchase.getPrice());
                    Model.getInstance().getSalesPurchasesReturns().add(checkProductPurchase);
                    for (Product product : Model.getInstance().getProducts()) {
                        if (product.getCode() == checkProductPurchase.getCode()) {
                            if (editReturn.getSale_purchase_no().startsWith("pr"))
                                product.setQuantity(product.getQuantity() - checkProductPurchase.getQuantity());
                            else
                                product.setQuantity(product.getQuantity() + checkProductPurchase.getQuantity());
                            Model.getInstance().setQueryUpdateProductQuantity(product.getQuantity(), product.getCode());
                        }
                    }
                }
                Functions.getInstance().showAlert("Return edited", editReturnGridPane);
            }
        }
    }


    @FXML
    private void handleCancel() {
        editReturnGridPane.getScene().getWindow().hide();
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

        editReturnGridPane.getScene().getWindow().hide();
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
        for (SalesPurchasesReturns salesPurchasesReturns : productReturnListView.getItems()) {
            for (SalesPurchasesReturns checkSalesPurchasesReturns : Model.getInstance().getSalesPurchasesReturns()) {
                if (checkSalesPurchasesReturns.getReference().equals(editReturn.getReturn_no()) && salesPurchasesReturns.getReference().equals(editReturn.getReturn_no())) {
                    if (salesPurchasesReturns.getQuantity() > checkSalesPurchasesReturns.getQuantity()) {
                        return true;
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
