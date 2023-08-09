package shopms.controllers.customers;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import shopms.controllers.ShopManagerController;
import shopms.model.Functions;
import shopms.model.Model;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class CustomersController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Customer> customerTableView;

    private final ObservableList<Customer> customers = Model.getInstance().getCustomers();

    @FXML
    private void initialize() {
        Functions.getInstance().lettersOnly(searchField);
        customerTableView.setItems(customers);

        FilteredList<Customer> customerFilteredList = new FilteredList<>(customers, p -> true);
        customerTableView.setItems(customerFilteredList);
        searchField.textProperty().addListener((obs, oldValue, newValue) ->
                customerFilteredList.setPredicate(p -> p.getName().toLowerCase().contains(newValue.toLowerCase().trim()))
        );
        customerTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final ContextMenu menu = new ContextMenu();
        final MenuItem editCustomerMenu = new MenuItem("Edit...");
        editCustomerMenu.setOnAction(event -> {
            try {
                Customer customer = customerTableView.getSelectionModel().getSelectedItem();
                if (!customer.getName().equals("Guest"))
                    handleEditCustomer(customer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final MenuItem deleteCustomerMenu = new MenuItem("Delete...");
        deleteCustomerMenu.setOnAction(event -> {
            try {
                handleRemoveCustomer();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
        editCustomerMenu.disableProperty().bind(Bindings.isEmpty(customerTableView.getSelectionModel().getSelectedItems()));
        deleteCustomerMenu.disableProperty().bind(Bindings.isEmpty(customerTableView.getSelectionModel().getSelectedItems()).or(
                Bindings.createBooleanBinding(() -> !Model.getInstance().loggedAccount.getUser_role().equals("administrator")
                )));
        menu.getItems().addAll(editCustomerMenu, deleteCustomerMenu);
        customerTableView.setContextMenu(menu);
    }

    @FXML
    private void handleAddCustomer() throws IOException {
        Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "addCustomer.fxml");
    }

    private void handleEditCustomer(Customer editCustomer) throws IOException {
        Functions.getInstance().loadEdit(ShopManagerController.staticBorderPane, "editCustomer.fxml", editCustomer);
    }

    private void handleRemoveCustomer() throws SQLException {
        Customer removeCustomer = customerTableView.getSelectionModel().getSelectedItem();
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove: " + removeCustomer.getName());

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Model.getInstance().setQueryRemoveCustomer(removeCustomer.getId());
            Model.getInstance().getCustomers().remove(removeCustomer);
        }
    }

    @FXML
    private void handleSave(ActionEvent actionEvent) throws IOException {
        if (customerTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(customerTableView, "customers/customers.xls");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Customers Saved");
            alert.setHeaderText(null);
            alert.setContentText("Customers saved successfully.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\customers\\customers.xls"));
            }
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if(keyEvent.getCode().equals(KeyCode.DELETE)) {
            handleRemoveCustomer();
        }
    }
}
