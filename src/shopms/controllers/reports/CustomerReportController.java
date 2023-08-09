package shopms.controllers.reports;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import shopms.controllers.others.CustomerSaleProductController;
import shopms.controllers.ShopManagerController;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.customers.Customer;
import shopms.controllers.sales.Sale;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class CustomerReportController {
    @FXML
    private Label totalLabel;
    @FXML
    private ComboBox<String> customerCombo;
    @FXML
    private TableView<Sale> customerReportTableView;

    @FXML
    private void initialize() {
        for (Customer customer : Model.getInstance().getCustomers()) {
            customerCombo.getItems().add(customer.getName());
        }
        customerCombo.getSelectionModel().select(0);
        calculateTotal(customerCombo.getItems().get(0));

        for (Sale sale : Model.getInstance().getSales()) {
            if (sale.getCustomer().equals(customerCombo.getItems().get(0))) {
                customerReportTableView.getItems().add(sale);
            }
        }

        customerCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            FilteredList<Sale> productFilteredList = new FilteredList<>(Model.getInstance().getSales(), p -> true);
            if (!newValue.isEmpty()) {
                productFilteredList.setPredicate(p -> p.getCustomer().equals(newValue));
                customerReportTableView.setItems(productFilteredList);
                calculateTotal(newValue);
            } else {
                customerReportTableView.setItems(null);
                calculateTotal(null);
            }
        });

        customerReportTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                CustomerSaleProductController.selectedSale = customerReportTableView.getSelectionModel().getSelectedItem();
                try {
                    Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "customerSaleProducts.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleSave() throws IOException {
        if (customerReportTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(customerReportTableView, ("customers/" + customerCombo.getSelectionModel().getSelectedItem() + "-customer-report.xls"));

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Saved");
            alert.setHeaderText(null);
            alert.setContentText("Customer Report saved successfully.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\customers\\" + customerCombo.getSelectionModel().getSelectedItem() + "-customer-report.xls"));
            }
        }
    }

    private void calculateTotal(String customer) {
        double grand_total = 0;
        double paid = 0;
        double due = 0;
        for (Sale sale : Model.getInstance().getSales()) {
            if (customer.equals(sale.getCustomer())) {
                grand_total += sale.getGrand_total();
                paid += sale.getPaid_amount();
                due += sale.getDue_amount();
            }
        }
        if (customer != null)
            totalLabel.setText("Grand Total: Rs." + grand_total + " \tPaid: Rs." + paid + " \t Due: Rs." + due);
        else
            totalLabel.setText("Grand Total: Rs.0 \tPaid: Rs.0 \t Due: Rs.0");
    }
}
