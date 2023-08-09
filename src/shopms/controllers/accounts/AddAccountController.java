package shopms.controllers.accounts;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import shopms.model.Functions;
import shopms.model.Model;

import java.sql.SQLException;

public class AddAccountController {
    @FXML
    private GridPane addAccountGridPane;
    @FXML
    private TextField usernameField;
    @FXML
    private ComboBox<String> userRoleCombo;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label error;

    @FXML
    private void initialize() {
        Functions.getInstance().restrictSpace(usernameField);
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleAddAccount();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            addAccountGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleAddAccount() throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String userRole = userRoleCombo.getSelectionModel().getSelectedItem();

        if(username.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter username.");
        } else if(username.length() < 3) {
            error.setManaged(true);
            error.setText("Username must contain at least 3 characters.");
        } else if(usernameExists(username)) {
            error.setManaged(true);
            error.setText("Username already exists.");
        } else if(userRoleCombo.getSelectionModel().getSelectedItem() == null) {
            error.setManaged(true);
            error.setText("Please select user role.");
        } else if(password.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter a password.");
        } else if(password.length() < 5) {
            error.setManaged(true);
            error.setText("Password must contain at least 5 characters.");
        } else if(confirmPassword.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the confirm password.");
        } else if(!confirmPassword.equals(password)) {
            error.setManaged(true);
            error.setText("Confirm password is incorrect.");
        } else {
            error.setManaged(false);
            error.setText("");

            Account account = new Account();
            if(Model.getInstance().getAccounts().size() == 0) {
                account.setUser_id(1);
            } else {
                account.setUser_id(Model.getInstance().getAccounts().get(Model.getInstance().getAccounts().size() - 1).getUser_id() + 1);
            }
            account.setUsername(username);
            account.setPassword(password);
            account.setUser_role(userRole);

            boolean success = Model.getInstance().setQueryInsertUser(username, userRole, password);
            if(success) {
                Model.getInstance().getAccounts().add(account);
                Functions.getInstance().showAlert("Account added", addAccountGridPane);
            }
        }
    }

    private boolean usernameExists(String username) {
        for(Account account : Model.getInstance().getAccounts()) {
            if(account.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void handleCancel() {
        addAccountGridPane.getScene().getWindow().hide();
    }
}
