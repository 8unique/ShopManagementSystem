package shopms.controllers.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import shopms.controllers.ShopManagerController;
import shopms.model.Functions;
import shopms.model.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class CategoryController {
    @FXML
    private GridPane categoryGridPane;
    @FXML
    private TextField searchField;

    private final ObservableList<Category> categories = Model.getInstance().getCategories();

    @FXML
    private void initialize() {
        categories.sort(Comparator.comparing(Category::getCategory));
        categoryFunction(categories);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            categoryGridPane.getChildren().removeIf(node -> node instanceof VBox);
            ObservableList<Category> list = FXCollections.observableArrayList();
            for (Category category : categories) {
                if (category.getCategory().toLowerCase().startsWith(newValue.toLowerCase()) ||
                        category.getCategory().toLowerCase().contains(newValue.toLowerCase()))
                    list.add(category);
            }
            categoryFunction(list);
        });
    }

    private void categoryFunction(ObservableList<Category> categoryObservableList) {
        int index = 0;
        for (int i = 1; i <= categoryObservableList.size() / 3; i++) {
            for (int j = 0; j < 3; j++) {
                Category category = categoryObservableList.get(index);

                VBox vBox = new VBox();
                vBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/styles.css")).toExternalForm());
                vBox.getStyleClass().add("categoryVBox");
                Label label = new Label();
                label.setText(category.getCategory());
                label.getStyleClass().add("dNameLabel");

                final ContextMenu menu = new ContextMenu();
                final MenuItem editCategoryMenu = new MenuItem("Edit...");
                editCategoryMenu.setOnAction(event -> {
                    try {
                        handleEditCategory(category);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                final MenuItem deleteCategoryMenu = new MenuItem("Delete...");
                deleteCategoryMenu.setOnAction(event -> {
                    try {
                        handleRemoveCategory(category);
                    } catch (SQLException throwable) {
                        throwable.printStackTrace();
                    }
                });
                menu.getItems().addAll(editCategoryMenu, deleteCategoryMenu);

                vBox.setOnContextMenuRequested(e ->
                        menu.show(vBox, e.getScreenX(), e.getScreenY()));
                vBox.getChildren().add(label);

                categoryGridPane.add(vBox, j, i);
                index++;
            }
        }
    }

    @FXML
    private void handleAddCategory() throws IOException {
        Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "addCategory.fxml");
    }

    @FXML
    private void handleEditCategory(Category editCategory) throws IOException {
        Functions.getInstance().loadEdit(ShopManagerController.staticBorderPane, "editCategory.fxml", editCategory);
    }

    private void handleRemoveCategory(Category removeCategory) throws SQLException {
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove: " + removeCategory.getCategory());

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Model.getInstance().setQueryRemoveCategory(removeCategory.getCategory_id());
            Model.getInstance().getCategories().remove(removeCategory);
            Functions.getInstance().loadCenter(ShopManagerController.staticBorderPane, "categories.fxml");
        }
    }
}
