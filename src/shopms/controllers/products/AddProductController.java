package shopms.controllers.products;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import shopms.model.EditableComboBox;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.categories.Category;

import java.sql.SQLException;
import java.util.Random;

public class AddProductController {
    @FXML
    private GridPane addProductGridPane;
    @FXML
    private TextField unitField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;
    @FXML
    private TextField brandField;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private TextField priceField;
    @FXML
    private TextField quantityField;
    @FXML
    private Label error;

    @FXML
    private void initialize() {
        Functions.getInstance().lettersAndNumbers(nameField);
        Functions.getInstance().numbersOnlyWithLimit(codeField, 6);
        Functions.getInstance().lettersAndNumbers(brandField);
        priceField.setTextFormatter(Functions.getInstance().doubleOnly());
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 9) {
                String s = newValue.substring(0, 9);
                priceField.setText(s);
            }
        });
        Functions.getInstance().numbersOnlyWithLimit(quantityField, 6);

        for (Category category : Model.getInstance().getCategories()) {
            categoryCombo.getItems().add(category.getCategory());
        }

        new EditableComboBox<>(categoryCombo);
    }

    @FXML
    private void handleAddProduct() throws SQLException {
        String name = nameField.getText();
        int code = 0;
        if (!codeField.getText().isEmpty())
            code = Integer.parseInt(codeField.getText());
        String brand = brandField.getText();
        String category = null;
        if (categoryCombo.getSelectionModel().getSelectedItem() != null) {
            category = categoryCombo.getSelectionModel().getSelectedItem();
        }
        String unit = unitField.getText();
        double price = 0;
        if (!priceField.getText().isEmpty())
            price = Double.parseDouble(priceField.getText());
        int quantity = 0;
        if (!quantityField.getText().isEmpty())
            quantity = Integer.parseInt(quantityField.getText());

        if (name.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the product name.");
        } else if (name.length() < 3) {
            error.setManaged(true);
            error.setText("Please enter a valid product name.");
        } else if (Model.getInstance().setQueryProduct(name, brand, code)) {
            error.setManaged(true);
            error.setText("Product already exists.");
        } else if (codeField.getText().isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter/generate a product code.");
        } else if (codeField.getText().length() < 6) {
            error.setManaged(true);
            error.setText("Please enter/generate a valid product code.");
        } else if (codeExists(code)) {
            error.setManaged(true);
            error.setText("Code already in use.");
        } else if (!brand.isEmpty() && brand.length() < 3) {
            error.setManaged(true);
            error.setText("Please enter a valid brand.");
        } else if (category == null || categoryCombo.getSelectionModel().getSelectedItem() == null) {
            error.setManaged(true);
            error.setText("Please select a category.");
        } else if (unit.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the unit.");
        } else if (priceField.getText().isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter price.");
        } else if (price < 1) {
            error.setManaged(true);
            error.setText("Please enter a valid price");
        } else {
            error.setManaged(false);
            error.setText("");

            Product product = new Product();
            product.setName(name);
            if (brand.isEmpty()) {
                brand = "Unbranded";
            }
            product.setUnit(unit);
            product.setBrand(brand);
            product.setCategory(category);
            product.setCode(code);
            product.setPrice(price);
            product.setQuantity(quantity);

            boolean success = Model.getInstance().setQueryInsertProduct(name, brand, category, code, unit, price, quantity);
            if (success) {
                Model.getInstance().getProducts().add(product);
                Functions.getInstance().showAlert("Product added", addProductGridPane);
            }
        }
    }

    private boolean codeExists(int code) {
        for (Product product : Model.getInstance().getProducts()) {
            if (product.getCode() == code) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleAddProduct();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            addProductGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleGenerateCode() {
        Random random = new Random();
        int code = random.nextInt(10000000);
        codeField.setText(String.valueOf(code));
    }

    @FXML
    private void handleCancel() {
        addProductGridPane.getScene().getWindow().hide();
    }
}

