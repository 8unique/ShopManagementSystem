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

public class EditAccountController {
    @FXML
    private GridPane editAccountGridPane;
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

    public static Account editAccount;

    @FXML
    private void initialize() {
        Functions.getInstance().restrictSpace(usernameField);

        usernameField.setText(editAccount.getUsername());
        if(editAccount.getUser_role().equals("staff")) {
            userRoleCombo.getSelectionModel().select(1);
        } else {
            userRoleCombo.getSelectionModel().select(0);
        }
        passwordField.setText(editAccount.getPassword());
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleEditAccount();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            editAccountGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleEditAccount() throws SQLException {
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
        } else if(usernameExists(username) && !username.equals(editAccount.getUsername())) {
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
        } else if(!confirmPassword.equals(Model.getInstance().loggedAccount.getPassword())) {
            error.setManaged(true);
            error.setText("Your password is incorrect.");
        } else {
            error.setManaged(false);
            error.setText("");

            editAccount.setUsername(username);
            editAccount.setPassword(password);
            editAccount.setUser_role(userRole);

            boolean success = Model.getInstance().setQueryUpdateUser(username, userRole, password, editAccount.getUser_id());
            if(success) {
                Functions.getInstance().showAlert("Account edited", editAccountGridPane);
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
        editAccountGridPane.getScene().getWindow().hide();
    }
}
