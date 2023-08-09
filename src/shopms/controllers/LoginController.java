package shopms.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import shopms.model.Model;
import shopms.model.Functions;
import shopms.controllers.accounts.Account;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class LoginController {
    @FXML
    private GridPane loginGridPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;
    @FXML
    private Label error;

    @FXML
    private void initialize() throws SQLException {
        Model.getInstance().load();
    }

    @FXML
    private void handleLogin() throws IOException, SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            error.setManaged(true);
            error.setText("Please specify the username.");
        } else if (!Model.getInstance().setQueryUsername(username)) {
            error.setManaged(true);
            error.setText("Incorrect username.");
        } else if (password.isEmpty()) {
            error.setManaged(true);
            error.setText("Please specify the password.");
        } else if (!Model.getInstance().setQueryLogin(username, password)) {
            error.setManaged(true);
            error.setText("Incorrect password.");
            passwordField.setText("");
        } else {
            for (Account account : Model.getInstance().getAccounts()) {
                if (username.equals(account.getUsername()) && password.equals(account.getPassword())) {
                    Model.getInstance().loggedAccount = account;
                }
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + username + "!");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Functions.getInstance().loadScene("shopManager.fxml", loginBtn);
            }
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    public void handleKeyPressEvent(KeyEvent keyEvent) throws SQLException, IOException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleLogin();
    }
}
