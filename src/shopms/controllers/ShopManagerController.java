package shopms.controllers;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;
import shopms.model.Functions;
import shopms.model.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class ShopManagerController {
    @FXML
    private Button dashboardButton;
    @FXML
    private MenuItem addAccountMenu;
    @FXML
    private MenuItem addProductMenu;
    @FXML
    private MenuItem printBarcodeMenu;
    @FXML
    private MenuButton purchaseMenu;
    @FXML
    private MenuButton reportMenu;
    @FXML
    private MenuButton accountMenu;
    @FXML
    private BorderPane borderPane;

    public static BorderPane staticBorderPane;

    @FXML
    private void initialize() throws SQLException {
        if (Model.getInstance().loggedAccount.getUser_role().equals("administrator")) {
            printBarcodeMenu.setDisable(false);
            purchaseMenu.setDisable(false);
            accountMenu.setDisable(false);
            reportMenu.setDisable(false);
            addAccountMenu.setDisable(false);
        }
        staticBorderPane = borderPane;
        handleViewDashboard();
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            Platform.exit();
        }

    }

    @FXML
    private void handleViewDashboard() {
        loadCenter(borderPane, "dashboard.fxml");
    }

    @FXML
    private void handleViewCategories() {
        loadCenter(borderPane, "categories.fxml");
    }

    @FXML
    private void handleViewProducts() {
        loadCenter(borderPane, "products.fxml");
    }

    @FXML
    private void handleViewCustomers() {
        loadCenter(borderPane, "customers.fxml");
    }

    @FXML
    private void handleViewPurchases() {
        loadCenter(borderPane, "purchases.fxml");
    }

    @FXML
    private void handleViewSales() {
        loadCenter(borderPane, "sales.fxml");
    }

    @FXML
    private void handleViewReturns() {
        loadCenter(borderPane, "returns.fxml");
    }

    @FXML
    private void handleViewProfitLoss() {
        loadCenter(borderPane, "profitLoss.fxml");
    }

    @FXML
    private void handleViewProductReport() {
        loadCenter(borderPane, "productReport.fxml");
    }

    @FXML
    private void handleViewProductQuantityAlert() {
        loadCenter(borderPane, "productQuantityAlert.fxml");
    }

    @FXML
    private void handleViewCustomerReport() {
        loadCenter(borderPane, "customerReport.fxml");
    }

    @FXML
    private void handleViewDueReport() {
        loadCenter(borderPane, "dueReport.fxml");
    }

    @FXML
    private void handleViewAccounts() {
        loadCenter(borderPane, "accounts.fxml");
    }

    @FXML
    private void handleAddAccount() throws IOException {
        Functions.getInstance().loadAddNew(borderPane, "addAccount.fxml");
    }

    @FXML
    private void handleAddProduct() throws IOException {
        Functions.getInstance().loadAddNew(borderPane, "addProduct.fxml");
    }

    @FXML
    private void handlePrintBarcode() {
        loadCenter(borderPane, "printProductBarcode.fxml");
    }

    @FXML
    private void handleAddCustomer() throws IOException {
        Functions.getInstance().loadAddNew(borderPane, "addCustomer.fxml");
    }

    @FXML
    private void handleAddPurchase() throws IOException {
        Functions.getInstance().loadAddNew(borderPane, "addPurchase.fxml");
    }

    @FXML
    private void handleAddSale() throws IOException {
        Functions.getInstance().loadAddNew(borderPane, "addSale.fxml");
    }

    @FXML
    private void handleAddReturn() throws IOException {
        Functions.getInstance().loadAddNew(borderPane, "selectReturnSaleOrPurchase.fxml");
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

    @FXML
    private void handleLogout() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            Functions.getInstance().loadScene("login.fxml", dashboardButton);
        }
    }
}
