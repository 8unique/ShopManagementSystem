package shopms.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import shopms.controllers.*;
import shopms.controllers.accounts.Account;
import shopms.controllers.accounts.EditAccountController;
import shopms.controllers.categories.Category;
import shopms.controllers.categories.EditCategoryController;
import shopms.controllers.customers.Customer;
import shopms.controllers.customers.EditCustomerController;
import shopms.controllers.products.EditProductController;
import shopms.controllers.products.Product;
import shopms.controllers.purchases.EditPurchaseController;
import shopms.controllers.purchases.Purchase;
import shopms.controllers.returns.EditReturnController;
import shopms.controllers.returns.Return;
import shopms.controllers.sales.EditSaleController;
import shopms.controllers.sales.Sale;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class Functions {

    private static final Functions instance = new Functions();

    public static Functions getInstance() {
        return instance;
    }

    public Functions() {

    }

    public void loadScene(String fxml, Button button) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/shopms/resources/" + fxml)));

        Stage stage = new Stage();
//        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/shopms/media/icon.png")).toExternalForm()));
        stage.setResizable(false);
        stage.initStyle(StageStyle.DECORATED);

        if (fxml.equals("shopManager.fxml")) {
            stage.setTitle("Shop Manager");
            stage.setScene(new Scene(parent, 1305, 715));
        } else if (fxml.equals("login.fxml")) {
            stage.setTitle("Shop Management System");
            stage.setScene(new Scene(parent));
        }
        Stage closeStage = (Stage) button.getScene().getWindow();
        closeStage.close();
        stage.show();
    }

    public void loadAddNew(BorderPane borderPane, String fxml) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/shopms/resources/" + fxml));

        Stage stage = new Stage();
        stage.initOwner(borderPane.getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);
        switch (fxml) {
            case "addCategory.fxml":
                stage.setTitle("Add Category");
            case "addProduct.fxml":
                stage.setTitle("Add Product");
            case "addCustomer.fxml":
                stage.setTitle("Add Customer");
            case "addPurchase.fxml":
                stage.setTitle("Add Purchase");
            case "addSale.fxml":
                stage.setTitle("Add Sale");
            case "selectReturnSaleOrPurchase.fxml":
                stage.setTitle("Add Return");
            case "customerSaleProducts.fxml":
                stage.setTitle("Products Bought");
            case "addAccount.fxml":
                stage.setTitle("Add Account");
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setResizable(false);
        stage.show();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }

    public void loadEdit(BorderPane borderPane, String fxml, Object object) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ShopManagerController.class.getResource("/shopms/resources/" + fxml));

        Stage stage = new Stage();
        stage.initOwner(borderPane.getScene().getWindow());
        switch (fxml) {
            case "editCategory.fxml": {
                stage.setTitle("Edit Category");
                EditCategoryController.editCategory = (Category) object;
                break;
            }
            case "editProduct.fxml": {
                stage.setTitle("Edit Product");
                EditProductController.editProduct = (Product) object;
                break;
            }
            case "editCustomer.fxml": {
                stage.setTitle("Edit Product");
                EditCustomerController.editCustomer = (Customer) object;
                break;
            }
            case "editPurchase.fxml": {
                stage.setTitle("Edit Purchase");
                EditPurchaseController.editPurchase = (Purchase) object;
                break;
            }
            case "editSale.fxml": {
                stage.setTitle("Edit Sale");
                EditSaleController.editSale = (Sale) object;
                break;
            }
            case "editReturn.fxml": {
                stage.setTitle("Edit Return");
                EditReturnController.editReturn = (Return) object;
                break;
            }
            case "editAccount.fxml": {
                stage.setTitle("Edit Account");
                EditAccountController.editAccount = (Account) object;
                break;
            }
        }
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setResizable(false);
        stage.show();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }

    public void showAlert(String text, GridPane gridPane) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(text);
        alert.setHeaderText(null);
        alert.setContentText(text + " successfully.");

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UTILITY);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            gridPane.getScene().getWindow().hide();
            switch (text) {
                case "Category edited":
                case "Category added":
                    loadCenter(ShopManagerController.staticBorderPane, "categories.fxml");
                    break;
                case "Sale edited":
                case "Sale added":
                    loadCenter(ShopManagerController.staticBorderPane, "sales.fxml");
                    break;
                case "Purchase edited":
                case "Purchase added":
                    loadCenter(ShopManagerController.staticBorderPane, "purchases.fxml");
                    break;
                case "Product edited":
                case "Product added":
                    loadCenter(ShopManagerController.staticBorderPane, "products.fxml");
                    break;
                case "Return edited":
                case "Return added":
                    loadCenter(ShopManagerController.staticBorderPane, "returns.fxml");
                    break;
            }
        }
    }

    public void loadCenter(BorderPane borderPane, String fxml) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(ShopManagerController.class.getResource("/shopms/resources/" + fxml)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        borderPane.setCenter(parent);
    }

    public void saveSheet(TableView<?> tableView, String directory) throws IOException {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Sheet");
        HSSFRow firstRow = hssfSheet.createRow(0);

        for (int i = 0; i < tableView.getColumns().size(); i++) {
            firstRow.createCell(i).setCellValue(tableView.getColumns().get(i).getText());
        }
        for (int row = 0; row < tableView.getItems().size(); row++) {
            HSSFRow hssfRow = hssfSheet.createRow(row + 1);
            for (int col = 0; col < tableView.getColumns().size(); col++) {
                Object celValue = tableView.getColumns().get(col).getCellObservableValue(row).getValue();
                try {
                    if (celValue != null && Double.parseDouble(celValue.toString()) != 0.0) {
                        hssfRow.createCell(col).setCellValue(Double.parseDouble(celValue.toString()));
                    }
                } catch (NumberFormatException e) {
                    hssfRow.createCell(col).setCellValue(celValue.toString());
                }
            }
        }
        //save excel file and close the workbook
        try {
            hssfWorkbook.write(new FileOutputStream(directory));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            hssfWorkbook.close();
        }
    }

    public void restrictSpace(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(textField.getText().replace(" ", ""));
        });
    }

    public void lettersOnly(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                textField.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });
    }

    public void numbersOnlyWithLimit(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\s0-9*")) {
                textField.setText(newValue.replaceAll("[^\\s0-9]", ""));
            }
            if (newValue.length() > maxLength) {
                String s = newValue.substring(0, maxLength);
                textField.setText(s);
            }
        });
    }

    public void lettersAndNumbers(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*0-9")) {
                textField.setText(newValue.replaceAll("[^\\sa-zA-Z0-9]", ""));
            }
        });
    }

    public TextFormatter<Double> doubleOnly() {
        Pattern validEditingState = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };
        StringConverter<Double> converter = new StringConverter<Double>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };
        return new TextFormatter<>(converter, 0.0, filter);
    }
}
