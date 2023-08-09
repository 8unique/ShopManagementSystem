package shopms.controllers.sales;

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
import shopms.controllers.customers.Customer;
import shopms.controllers.products.Product;
import shopms.controllers.others.SalesPurchasesReturns;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class AddSaleController {
    @FXML
    private GridPane addSaleGridPane;
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
    private TextField searchField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private Button submitButton;
    @FXML
    private ListView<SalesPurchasesReturns> productSaleListView;
    @FXML
    private Label error;
    @FXML
    private Label grandTotalLabel;

    private Sale addSale = new Sale();
    private ListView<Product> productListView = new ListView<>();
    private final ObservableList<Product> products = Model.getInstance().getProducts();
    static Boolean quantityIsEmpty = false;

    @FXML
    private void initialize() {
        saleDatePicker.setValue(LocalDate.now());
        chequeDatePicker.setValue(LocalDate.now().plusWeeks(1));
        Functions.getInstance().numbersOnlyWithLimit(chequeNoField, 6);

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        addSale.setDate(formattedDate);

        Random random = new Random();
        int code = random.nextInt(1000000);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

        invoiceLabel.setText("sr-" + LocalDate.now().format(dateFormat) + "-" + code);
        addSale.setInvoice_no(invoiceLabel.getText());

        paidAmountField.setTextFormatter(Functions.getInstance().doubleOnly());

        productTableView.setItems(products);
        FilteredList<Product> productFilteredList = new FilteredList<>(Model.getInstance().getProducts(), p -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                    if (!newValue.isEmpty()) {
                        productFilteredList.setPredicate(p -> p.getName().toLowerCase().contains(newValue.toLowerCase()) || String.valueOf(p.getCode()).contains(newValue.toLowerCase()));
                        productTableView.setItems(productFilteredList);
                    } else {
                        productTableView.setItems(Model.getInstance().getProducts());
                    }
                }
        );

        productTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        submitButton.disableProperty().bind(Bindings.isEmpty(productSaleListView.getItems()));

        productTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                handleAddProductSale(productTableView.getSelectionModel().getSelectedItem());
            }
        });

        productSaleListView.setCellFactory(param -> new ModifiedCell());

        for (Customer customer : Model.getInstance().getCustomers()) {
            customerCombo.getItems().add(customer.getName());
        }

        paymentMethodCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            chequeHBox.setVisible(newValue.equals("Cheque"));
        });

        grandTotalLabel.setText("Grand Total: Rs.0");
    }

    private void handleAddProductSale(Product product) {
        SalesPurchasesReturns productSale = new SalesPurchasesReturns();
        productSale.setReference(addSale.getInvoice_no());

        if (!productListView.getItems().contains(product)) {
            productListView.getItems().add(product);

            productSale.setCode(product.getCode());
            productSaleListView.getItems().add(productSale);
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleSubmit();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            addSaleGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleCancel() {
        addSaleGridPane.getScene().getWindow().hide();
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
        } else if (quantityIsEmpty) {
            error.setManaged(true);
            error.setText("Please enter valid quantity(s).");
        } else if (quantityOverLimit() != null) {
            error.setManaged(true);
            error.setText(quantityOverLimit() + " quantity limit reached.");
        } else {
            error.setManaged(false);
            error.setText("");

            if (paymentMethodCombo.getSelectionModel().getSelectedItem().equals("Cheque")) {
                if (chequeNoField.getText().length() < 6) {
                    error.setManaged(true);
                    error.setText("Please enter a valid cheque no.");
                } else if (chequeDatePicker.getValue().isBefore(LocalDate.now()) || chequeDatePicker.getValue().isAfter(LocalDate.now().plusMonths(1))) {
                    error.setManaged(true);
                    error.setText("Please choose a valid cheque date.");
                } else {
                    error.setManaged(false);
                    error.setText("");
                    resumeAddSale();
                }
            } else {
                resumeAddSale();
            }
        }
    }

    private void resumeAddSale() throws SQLException {
        addSale.setCustomer(customerCombo.getSelectionModel().getSelectedItem());
        addSale.setItems(productSaleListView.getItems().size());
        addSale.setQuantity(quantity());
        addSale.setGrand_total(totalCost());
        addSale.setPaid_amount(Double.parseDouble(paidAmountField.getText()));
        if(Double.parseDouble(paidAmountField.getText()) > totalCost()) {
            addSale.setBalance(Double.parseDouble(paidAmountField.getText()) - totalCost());
            addSale.setDue_amount(0);
        } else {
            addSale.setBalance(0);
            addSale.setDue_amount(addSale.getGrand_total() - addSale.getPaid_amount());
        }
        addSale.setPayment_method(paymentMethodCombo.getSelectionModel().getSelectedItem());
        if (!chequeNoField.getText().isEmpty()) {
            addSale.setChequeNo(Integer.parseInt(chequeNoField.getText()));
            addSale.setChequeDate(chequeDatePicker.getValue().toString());
        } else {
            addSale.setChequeNo(0);
            addSale.setChequeDate("");
        }
        if (addSale.getPaid_amount() >= totalCost()) {
            addSale.setPayment_status("Paid");
        } else if (addSale.getPaid_amount() < totalCost()) {
            addSale.setPayment_status("Due");
        }
        for (SalesPurchasesReturns salesPurchasesReturns : productSaleListView.getItems()) {
            for (Product product : Model.getInstance().getProducts()) {
                if (salesPurchasesReturns.getReference().equals(addSale.getInvoice_no())) {
                    if (salesPurchasesReturns.getCode() == product.getCode()) {
                        product.setQuantity(product.getQuantity() - salesPurchasesReturns.getQuantity());
                        Model.getInstance().setQueryUpdateProductQuantity(product.getQuantity(), product.getCode());
                    }
                }
            }
            Model.getInstance().getSalesPurchasesReturns().add(salesPurchasesReturns);
            Model.getInstance().setQueryInsertSalesPurchaseReturn(
                    salesPurchasesReturns.getReference(),
                    salesPurchasesReturns.getCode(),
                    salesPurchasesReturns.getQuantity(),
                    salesPurchasesReturns.getPrice()
            );
        }

        boolean success = Model.getInstance().setQueryInsertSale(
                addSale.getDate(),
                addSale.getInvoice_no(),
                addSale.getCustomer(),
                addSale.getItems(),
                addSale.getQuantity(),
                addSale.getGrand_total(),
                addSale.getPaid_amount(),
                addSale.getBalance(),
                addSale.getDue_amount(),
                addSale.getPayment_method(),
                addSale.getPayment_status(),
                addSale.getChequeNo(),
                addSale.getChequeDate()
        );
        if (success) {
            Model.getInstance().getSales().add(addSale);
            Functions.getInstance().showAlert("Sale added", addSaleGridPane);
        }
    }

    private String quantityOverLimit() {
        for (SalesPurchasesReturns salesPurchasesReturns : productSaleListView.getItems()) {
            for (Product product : Model.getInstance().getProducts()) {
                if (salesPurchasesReturns.getReference().equals(addSale.getInvoice_no())) {
                    if (product.getCode() == salesPurchasesReturns.getCode()) {
                        if (salesPurchasesReturns.getQuantity() > product.getQuantity()) {
                            return product.getName();
                        }
                    }
                }
            }
        }
        return null;
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

    class ModifiedCell extends ListCell<SalesPurchasesReturns> {
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
                }
            });

            deleteButton.setOnAction(event -> {
                productListView.getItems().removeIf(product -> product.getCode() == getItem().getCode());
                productSaleListView.getItems().remove(getItem());
            });
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
