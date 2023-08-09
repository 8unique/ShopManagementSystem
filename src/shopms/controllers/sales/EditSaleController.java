package shopms.controllers.sales;

import javafx.beans.binding.Bindings;
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
import shopms.controllers.customers.Customer;
import shopms.controllers.products.Product;
import shopms.controllers.others.SalesPurchasesReturns;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditSaleController {
    @FXML
    private GridPane editSaleGridPane;
    @FXML
    private DatePicker saleDatePicker;
    @FXML
    private ComboBox<String> customerCombo;
    @FXML
    private Label invoiceLabel;
    @FXML
    private ComboBox<String> paymentMethodCombo;
    @FXML
    private TextField paidAmountField;
    @FXML
    private HBox chequeHBox;
    @FXML
    private TextField chequeNoField;
    @FXML
    private DatePicker chequeDatePicker;
    @FXML
    private Button submitButton;
    @FXML
    private ListView<SalesPurchasesReturns> productSaleListView;
    @FXML
    private Label error;
    @FXML
    private Label grandTotalLabel;

    private ListView<Product> productListView = new ListView<>();
    static Boolean quantityIsEmpty = false;

    public static Sale editSale;

    @FXML
    private void initialize() {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        saleDatePicker.setValue(LocalDate.parse(editSale.getDate().substring(0, 10), myFormatObj));
        Functions.getInstance().numbersOnlyWithLimit(chequeNoField, 6);
        invoiceLabel.setText(editSale.getInvoice_no());
        paidAmountField.setTextFormatter(Functions.getInstance().doubleOnly());
        paidAmountField.setText(String.valueOf(editSale.getPaid_amount()));
        for (SalesPurchasesReturns productPurchase : Model.getInstance().getSalesPurchasesReturns()) {
            if (productPurchase.getReference().equals(editSale.getInvoice_no())) {
                productSaleListView.getItems().add(productPurchase);
            }
            for (Product product : Model.getInstance().getProducts()) {
                if (product.getCode() == productPurchase.getCode()) {
                    productListView.getItems().add(product);
                }
            }
        }

        submitButton.disableProperty().bind(Bindings.isEmpty(productSaleListView.getItems()));

        productSaleListView.setCellFactory(param -> new ModifiedCell());

        for (Customer customer : Model.getInstance().getCustomers()) {
            customerCombo.getItems().add(customer.getName());
        }
        for (String customer : customerCombo.getItems()) {
            if (customer.equals(editSale.getCustomer())) {
                customerCombo.getSelectionModel().select(customer);
            }
        }

        paymentMethodCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            chequeHBox.setVisible(newValue.equals("Cheque"));
        });

        if (editSale.getChequeNo() != 0) {
            chequeHBox.setVisible(true);
            chequeNoField.setText(String.valueOf(editSale.getChequeNo()));
            chequeDatePicker.setValue(LocalDate.parse(editSale.getChequeDate()));
            paymentMethodCombo.getSelectionModel().select(1);
        }

        grandTotalLabel.setText("Grand Total: Rs." + editSale.getGrand_total());
        productListView.setMouseTransparent(true);
        productListView.setFocusTraversable(false);
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleSubmit();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            editSaleGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleCancel() {
        editSaleGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleSubmit() throws SQLException {
        if (customerCombo.getSelectionModel().getSelectedItem() == null) {
            error.setManaged(true);
            error.setText("Please select the customer.");
        } else if (paidAmountField.getText().isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the paid amount.");
        } else if (Double.parseDouble(paidAmountField.getText()) == 0) {
            error.setManaged(true);
            error.setText("Please enter a valid paid amount.");
        } else if (Double.parseDouble(paidAmountField.getText()) < (totalCost() / 2)) {
            error.setManaged(true);
            error.setText("At least 50% must be paid");
        } else if (paymentMethodCombo.getSelectionModel().getSelectedItem().equals("Cheque")) {
            if (chequeNoField.getText().length() < 6) {
                error.setManaged(true);
                error.setText("Please enter a valid cheque no.");
            } else if (chequeDatePicker.getValue().isBefore(LocalDate.now()) || chequeDatePicker.getValue().isAfter(LocalDate.now().plusMonths(1))) {
                error.setManaged(true);
                error.setText("Please choose a valid cheque date.");
            }
        } else {
            error.setManaged(false);
            error.setText("");

            editSale.setCustomer(customerCombo.getSelectionModel().getSelectedItem());
            editSale.setItems(productSaleListView.getItems().size());
            editSale.setQuantity(quantity());
            editSale.setGrand_total(totalCost());
            editSale.setPaid_amount(Double.parseDouble(paidAmountField.getText()));
            if (Double.parseDouble(paidAmountField.getText()) > totalCost()) {
                editSale.setBalance(Double.parseDouble(paidAmountField.getText()) - totalCost());
                editSale.setDue_amount(0);
            } else {
                editSale.setBalance(0);
                editSale.setDue_amount(editSale.getGrand_total() - editSale.getPaid_amount());
            }

            editSale.setPayment_method(paymentMethodCombo.getSelectionModel().getSelectedItem());
            if (editSale.getPaid_amount() >= totalCost()) {
                editSale.setPayment_status("Paid");
            } else if (editSale.getPaid_amount() < totalCost()) {
                editSale.setPayment_status("Due");
            }

            boolean success = Model.getInstance().setQueryUpdateSale(
                    editSale.getDate(),
                    editSale.getInvoice_no(),
                    editSale.getCustomer(),
                    editSale.getItems(),
                    editSale.getQuantity(),
                    editSale.getGrand_total(),
                    editSale.getPaid_amount(),
                    editSale.getBalance(),
                    editSale.getDue_amount(),
                    editSale.getPayment_method(),
                    editSale.getPayment_status(),
                    editSale.getChequeNo(),
                    editSale.getChequeDate()
            );
            if (success) {
                Functions.getInstance().showAlert("Sale edited", editSaleGridPane);
            }
        }
    }

    @FXML
    private void handleTotalRefresh() {
        for (SalesPurchasesReturns salesPurchasesReturns : productSaleListView.getItems()) {
            grandTotalLabel.setText("Grand Total: Rs." + totalCost());
        }
    }

    private int quantity() {
        int quantity = 0;
        for (SalesPurchasesReturns productPurchase : productSaleListView.getItems()) {
            if (productPurchase.getReference().equals(invoiceLabel.getText())) {
                quantity += productPurchase.getQuantity();
            }
        }
        return quantity;
    }

    private double totalCost() {
        int cost = 0;
        for (SalesPurchasesReturns productPurchase : productSaleListView.getItems()) {
            if (productPurchase.getReference().equals(invoiceLabel.getText())) {
                cost += productPurchase.getPrice() * productPurchase.getQuantity();
            }
        }
        return cost;
    }

    static class ModifiedCell extends ListCell<SalesPurchasesReturns> {
        HBox hbox = new HBox();
        Label item = new Label();
        TextField quantityField = new TextField();

        public ModifiedCell() {
            super();
            super.setAlignment(Pos.CENTER);

            item.setMaxWidth(Double.MAX_VALUE);
            hbox.setStyle("-fx-spacing: 20.0; -fx-font-size: 20px;");
            HBox.setHgrow(item, Priority.ALWAYS);
            quantityField.setPromptText("Quantity...");
            quantityField.setDisable(true);
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
                }
            });

            hbox.getChildren().addAll(item, quantityField);
        }

        @Override
        protected void updateItem(SalesPurchasesReturns productSale, boolean empty) {
            super.updateItem(productSale, empty);
            setText(null);
            setGraphic(null);
            if (productSale != null && !empty) {
                for (Product product : Model.getInstance().getProducts()) {
                    if (productSale.getCode() == product.getCode()) {
                        item.setText(product.getName() + " (" + product.getCode() + ") - Rs." + product.getPrice());

                        quantityField.setText(String.valueOf(productSale.getQuantity()));
                        productSale.setCode(product.getCode());
                        productSale.setPrice(product.getPrice());
                    }
                }
                setGraphic(hbox);
            }
        }
    }
}
