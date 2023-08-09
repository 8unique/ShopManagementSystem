package shopms.controllers.categories;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import shopms.model.Functions;
import shopms.model.Model;

import java.sql.SQLException;

public class AddCategoryController {
    @FXML
    private GridPane addCategoryGridPane;
    @FXML
    private TextField categoryField;
    @FXML
    private Label error;

    @FXML
    private void handleAddCategory() throws SQLException {
        String category = categoryField.getText();
        if (category.isEmpty()) {
            error.setManaged(true);
            error.setText("Please enter the category name");
        } else if (category.length() < 3) {
            error.setManaged(true);
            error.setText("Please enter a valid name.");
        } else if (categoryExists(category)) {
            error.setManaged(true);
            error.setText("Category already exists.");
        } else {
            error.setManaged(false);
            error.setText("");
            boolean success = Model.getInstance().setQueryInsertCategory(category);

            Category addCategory = new Category();
            if (Model.getInstance().getCategories().size() != 0)
                addCategory.setCategory_id(Model.getInstance().getCategories().get(Model.getInstance().getCategories().size() - 1).getCategory_id() + 1);
            else
                addCategory.setCategory_id(1);
            addCategory.setCategory(category);
            Model.getInstance().getCategories().add(addCategory);

            if (success) {
                Functions.getInstance().showAlert("Category added", addCategoryGridPane);
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
        addCategoryGridPane.getScene().getWindow().hide();
    }


    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleAddCategory();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            addCategoryGridPane.getScene().getWindow().hide();
    }
}
