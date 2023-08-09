package shopms.controllers.accounts;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.StageStyle;
import shopms.controllers.ShopManagerController;
import shopms.model.Functions;
import shopms.model.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class AccountController {
    @FXML
    private Button addAccountButton;
    @FXML
    private TableView<Account> accountTableView;

    @FXML
    private void initialize() {
        if (Model.getInstance().loggedAccount.getUser_role().equals("administrator")) {
            addAccountButton.setDisable(false);
        }
        accountTableView.setItems(Model.getInstance().getAccounts());
        accountTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final ContextMenu menu = new ContextMenu();

        final MenuItem editAccountMenu = new MenuItem("Edit...");
        editAccountMenu.setOnAction(event -> {
            try {
                Account editAccount = accountTableView.getSelectionModel().getSelectedItem();
                handleEditAccount(editAccount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final MenuItem deleteAccountMenu = new MenuItem("Delete...");
        deleteAccountMenu.setOnAction(event -> {
            try {
                handleRemoveAccount();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });

        editAccountMenu.disableProperty().bind(Bindings.isEmpty(accountTableView.getSelectionModel().getSelectedItems()));
        deleteAccountMenu.disableProperty().bind(Bindings.isEmpty(accountTableView.getSelectionModel().getSelectedItems()));

        menu.getItems().addAll(editAccountMenu, deleteAccountMenu);
        accountTableView.setContextMenu(menu);

    }

    private void handleEditAccount(Account editAccount) throws IOException {
        Functions.getInstance().loadEdit(ShopManagerController.staticBorderPane, "editAccount.fxml", editAccount);
    }

    @FXML
    private void handleAddAccount() throws IOException {
        Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "addAccount.fxml");
    }

    private void handleRemoveAccount() throws SQLException {
        Account removeAccount = accountTableView.getSelectionModel().getSelectedItem();
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove: " + removeAccount.getUsername());

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Model.getInstance().setQueryRemoveUser(removeAccount.getUser_id());
            Model.getInstance().getAccounts().remove(removeAccount);
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if(keyEvent.getCode().equals(KeyCode.DELETE)) {
            handleRemoveAccount();
        }
    }
}
