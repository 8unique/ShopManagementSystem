package shopms.controllers.customers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import shopms.model.Functions;
import shopms.model.Model;

import java.sql.SQLException;

public class AddCustomerController {
    @FXML
    private GridPane addCustomerGridPane;
    @FXML
    private TextField nameField;
    @FXML
    private TextField companyNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Label error;

    @FXML
    private void initialize() {
        Functions.getInstance().lettersOnly(nameField);
        Functions.getInstance().numbersOnlyWithLimit(phoneNumberField, 11);
    }

    @FXML
    private void handleAddCustomer() throws SQLException {
        String name = nameField.getText();
        String companyName = companyNameField.getText();
        String address = addressField.getText();
        String phoneNumber = phoneNumberField.getText();

        if (name.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter customer name.");
        } else if (name.length() < 3) {
            error.setManaged(true);
            error.setText("Please enter a valid customer name.");
        } else if (customerExists(name)) {
            error.setManaged(true);
            error.setText("Customer already exists.");
        } else if (phoneNumber.length() < 10 && !phoneNumber.isEmpty()) {
            error.setManaged(true);
            error.setText("Invalid phone number.");
        } else if (phoneNumberExists(phoneNumber)) {
            error.setManaged(true);
            error.setText("Customer with phone number already exists.");
        } else {
            if (companyName.isEmpty()) {
                companyName = "N/A";
            }
            if (address.isEmpty()) {
                address = "N/A";
            }
            if (phoneNumber.isEmpty()) {
                phoneNumber = "N/A";
            }

            Customer customer = new Customer();
            if (Model.getInstance().getCustomers().size() != 0)
                customer.setId(Model.getInstance().getCustomers().get(Model.getInstance().getCustomers().size() - 1).getId() + 1);
            else
                customer.setId(1);
            customer.setName(name);
            customer.setCompany_name(companyName);
            customer.setAddress(address);
            customer.setPhone_number(phoneNumber);

            boolean success = Model.getInstance().setQueryInsertCustomer(name, companyName, address, phoneNumber);
            if (success) {
                Model.getInstance().getCustomers().add(customer);
                Functions.getInstance().showAlert("Customer added", addCustomerGridPane);
            }
        }
    }

    private boolean customerExists(String name) {
        for (Customer customer : Model.getInstance().getCustomers()) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean phoneNumberExists(String phoneNumber) {
        for (Customer customer : Model.getInstance().getCustomers()) {
            if (customer.getPhone_number().equals(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void handleCancel() {
        addCustomerGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleAddCustomer();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            addCustomerGridPane.getScene().getWindow().hide();
    }
}
