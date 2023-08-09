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

public class EditCustomerController {
    @FXML
    private GridPane editCustomerGridPane;
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

    public static Customer editCustomer;

    @FXML
    private void initialize() {
        Functions.getInstance().lettersOnly(nameField);
        Functions.getInstance().numbersOnlyWithLimit(phoneNumberField, 11);

        nameField.setText(editCustomer.getName());
        companyNameField.setText(editCustomer.getCompany_name());
        addressField.setText(editCustomer.getAddress());
        phoneNumberField.setText(editCustomer.getPhone_number());
    }

    @FXML
    private void handleEditCustomer() throws SQLException {
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
        } else if (customerExists(name) && !name.equals(editCustomer.getName())) {
            error.setManaged(true);
            error.setText("Customer already exists.");
        } else if (phoneNumber.length() < 10 && !phoneNumber.isEmpty()) {
            error.setManaged(true);
            error.setText("Invalid phone number.");
        } else if (phoneNumberExists(phoneNumber) && !phoneNumber.equals(editCustomer.getPhone_number())) {
            error.setManaged(true);
            error.setText("Customer with phone number already exists.");
        } else {
            if (companyName.isEmpty()) {
                companyName = "N/A";
            }
            if (phoneNumber.isEmpty()) {
                phoneNumber = "N/A";
            }
            if (address.isEmpty()) {
                address = "N/A";
            }

            editCustomer.setName(name);
            editCustomer.setCompany_name(companyName);
            editCustomer.setAddress(address);
            editCustomer.setPhone_number(phoneNumber);

            boolean success = Model.getInstance().setQueryUpdateCustomer(name, companyName, address, phoneNumber, editCustomer.getId());
            if (success) {
                Functions.getInstance().showAlert("Customer edited", editCustomerGridPane);
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
        editCustomerGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleEditCustomer();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            editCustomerGridPane.getScene().getWindow().hide();
    }
}
