package shopms.controllers.others;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import shopms.controllers.ShopManagerController;
import shopms.controllers.returns.AddReturnController;
import shopms.model.Model;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SelectReturnSaleOrPurchaseController {
    @FXML
    private Button continueButton;
    @FXML
    private GridPane selectSalePurchaseGridPane;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<SalesPurchasesReturns> saleOrPurchaseTableView;

    @FXML
    private void initialize() {
        FilteredList<SalesPurchasesReturns> filteredList = new FilteredList<>(Model.getInstance().getSalesPurchasesReturns(), p -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                    if (!newValue.isEmpty() && !newValue.startsWith("rr")) {
                        filteredList.setPredicate(p -> p.getReference().toLowerCase().contains(searchField.getText().toLowerCase()));

                        Set<SalesPurchasesReturns> distinctList = filteredList.stream()
                                .collect(Collectors.toCollection(() ->
                                        new TreeSet<>(Comparator.comparing(SalesPurchasesReturns::getReference))));

                        saleOrPurchaseTableView.setItems(FXCollections.observableArrayList(distinctList));
                    } else {
                        saleOrPurchaseTableView.setItems(null);
                    }
                }
        );
        saleOrPurchaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        continueButton.disableProperty().bind(Bindings.isEmpty(saleOrPurchaseTableView.getSelectionModel().getSelectedItems()));
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode().equals(KeyCode.ENTER))
            handleContinueAddReturn();
        else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            selectSalePurchaseGridPane.getScene().getWindow().hide();
    }

    @FXML
    private void handleContinueAddReturn() throws IOException {
        AddReturnController.reference = saleOrPurchaseTableView.getSelectionModel().getSelectedItem().getReference();

        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/shopms/resources/continueAddReturn.fxml"));

        Stage stage = new Stage();
        stage.initOwner(ShopManagerController.staticBorderPane.getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Add Return");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setResizable(false);

        selectSalePurchaseGridPane.getScene().getWindow().hide();
        stage.show();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }

    @FXML
    private void handleCancel() {
        selectSalePurchaseGridPane.getScene().getWindow().hide();
    }
}
