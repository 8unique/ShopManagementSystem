package shopms.controllers.categories;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import shopms.model.Functions;
import shopms.model.Model;

import java.sql.SQLException;

public class EditCategoryController {
    @FXML
    private GridPane editCategoryGridPane;
    @FXML
    private TextField categoryField;
    @FXML
    private Button editButton;
    @FXML
    private Label error;

    public static Category editCategory;

    @FXML
    private void initialize() {
        categoryField.setText(editCategory.getCategory());
        if(categoryField.getText().equals(editCategory.getCategory()))
            editButton.setDisable(true);
        categoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.equals(editCategory.getCategory()));
        });
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleEditCategory();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            editCategoryGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleEditCategory() throws SQLException {
        String category = categoryField.getText();
        if (category.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the category name");
        } else if (category.length() < 3) {
            error.setManaged(true);
            error.setText("Please enter a valid name.");
        } else if (categoryExists(category) && !category.equals(editCategory.getCategory())) {
            error.setManaged(true);
            error.setText("Category already exists.");
        } else {
            error.setManaged(false);
            error.setText("");
            editCategory.setCategory(category);
            boolean success = Model.getInstance().setQueryUpdateCategory(editCategory.getCategory(), editCategory.getCategory_id());
            if (success) {
                Functions.getInstance().showAlert("Category edited", editCategoryGridPane);
            }
        }
    }

    private boolean categoryExists(String checkCategory) {
        for (Category category : Model.getInstance().getCategories()) {
            if (category.getCategory().equalsIgnoreCase(checkCategory))
                return true;
        }
        return false;
    }

    @FXML
    private void handleCancel() {
        editCategoryGridPane.getScene().getWindow().hide();
    }
}
