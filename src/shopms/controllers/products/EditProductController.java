package shopms.controllers.products;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.categories.Category;

import java.sql.SQLException;
import java.util.Random;

public class EditProductController {
    @FXML
    private TextField unitField;
    @FXML
    private GridPane editProductGridPane;
    @FXML
    private Button editButton;
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

    public static Product editProduct;

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

        nameField.setText(editProduct.getName());
        brandField.setText(editProduct.getBrand());
        categoryCombo.getSelectionModel().select(editProduct.getCategory());
        codeField.setText(String.valueOf(editProduct.getCode()));
        unitField.setText(String.valueOf(editProduct.getUnit()));
        priceField.setText(String.valueOf(editProduct.getPrice()));
        quantityField.setText(String.valueOf(editProduct.getQuantity()));

        if (nameField.getText().equals(editProduct.getName()) &&
                brandField.getText().equals(editProduct.getBrand()) &&
                categoryCombo.getSelectionModel().getSelectedItem().equals(editProduct.getCategory()) &&
                codeField.getText().equals(String.valueOf(editProduct.getCode())) &&
                unitField.getText().equals(String.valueOf(editProduct.getUnit())) &&
                priceField.getText().equals(String.valueOf(editProduct.getPrice())) &&
                quantityField.getText().equals(String.valueOf(editProduct.getQuantity()))) {
            editButton.setDisable(true);
        }

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.equals(editProduct.getName()));
        });

        brandField.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.equals(editProduct.getBrand()));
        });

        categoryCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    editButton.setDisable(newValue.equals(editProduct.getCategory()));
                }
        );

        codeField.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.equals(String.valueOf(editProduct.getCode())));
        });

        unitField.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.equals(String.valueOf(editProduct.getUnit())));
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.equals(String.valueOf(editProduct.getPrice())));
        });

        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.equals(String.valueOf(editProduct.getQuantity())));
        });
    }

    @FXML
    private void handleEditProduct() throws SQLException {
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
        } else if (Model.getInstance().setQueryProduct(name, brand, code) &&
                !name.equals(editProduct.getName()) &&
                code != editProduct.getCode() &&
                !brand.equals(editProduct.getBrand())) {
            error.setManaged(true);
            error.setText("Product already exists.");
        } else if (codeField.getText().isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter/generate a product code.");
        } else if (codeField.getText().length() < 6) {
            error.setManaged(true);
            error.setText("Please enter/generate a valid product code.");
        } else if (codeExists(code) && code != editProduct.getCode()) {
            error.setManaged(true);
            error.setText("Code already in use.");
        } else if (!brand.isEmpty() && brand.length() < 3) {
            error.setManaged(true);
            error.setText("Please enter a valid brand.");
        } else if (brandUsed(brand, name) && !brand.equals(editProduct.getBrand())) {
            error.setManaged(true);
            error.setText(name + " in brand " + brand + " already exists.");
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
        } else if (quantityField.getText().isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the quantity.");
        } else {
            error.setManaged(false);
            error.setText("");

            if (brand.isEmpty()) {
                brand = "Unbranded";
            }

            editProduct.setName(name);
            editProduct.setBrand(brand);
            editProduct.setCategory(category);
            editProduct.setUnit(unit);
            editProduct.setPrice(price);
            editProduct.setQuantity(quantity);
            editProduct.setCode(code);

            boolean success = Model.getInstance().setQueryUpdateProduct(name, brand, category, code, unit, price, quantity, editProduct.getCode());
            if (success) {
                Functions.getInstance().showAlert("Product edited", editProductGridPane);
            }
        }
    }

    private boolean brandUsed(String brand, String name) {
        for (Product product : Model.getInstance().getProducts()) {
            if (product.getName().equalsIgnoreCase(name) && product.getBrand().equalsIgnoreCase(brand))
                return true;
        }
        return false;
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
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !editButton.isDisabled())
            handleEditProduct();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            editProductGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleGenerateCode() {
        Random random = new Random();
        int code = random.nextInt(10000000);
        codeField.setText(String.valueOf(code));
    }

    @FXML
    private void handleCancel() {
        editProductGridPane.getScene().getWindow().hide();
    }
}
