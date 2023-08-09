package shopms.controllers.reports;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.products.Product;
import shopms.controllers.others.SalesPurchasesReturns;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ProductReportController {
    @FXML
    private Label totalLabel;
    @FXML
    private TableView<ProductReport> productReportTableView;

    @FXML
    private void initialize() {
        int totalSold = 0;
        int totalPurchased = 0;
        double totalProfit = 0;
        int totalStock = 0;

        for(Product product : Model.getInstance().getProducts()) {
            String name = product.getName();
            int sold = 0;
            int purchased = 0;

            double soldAmount = 0;
            double purchasedAmount = 0;

            for(SalesPurchasesReturns salesPurchasesReturns : Model.getInstance().getSalesPurchasesReturns()) {
                if(salesPurchasesReturns.getCode() == product.getCode()) {
                    if(salesPurchasesReturns.getReference().startsWith("sr")) {
                        sold += salesPurchasesReturns.getQuantity();
                        soldAmount += (salesPurchasesReturns.getPrice() * salesPurchasesReturns.getQuantity());
                    }
                    else if(salesPurchasesReturns.getReference().startsWith("pr")) {
                        purchased += salesPurchasesReturns.getQuantity();
                        purchasedAmount += (salesPurchasesReturns.getPrice() * salesPurchasesReturns.getQuantity());
                    }
                }
            }

            int stock = product.getQuantity();

            ProductReport productReport = new ProductReport();
            productReport.setProduct(name);
            productReport.setPurchasedQuantity(purchased);
            productReport.setSoldQuantity(sold);
            productReport.setStock(stock);
            productReport.setProfit(soldAmount - purchasedAmount);

            productReportTableView.getItems().add(productReport);

            totalSold += sold;
            totalPurchased += purchased;
            totalProfit += productReport.getProfit();
            totalStock += stock;
        }

        totalLabel.setText("Purchased : " + totalPurchased + " \tSold: " + totalSold + " \t Profit: Rs." + totalProfit + " \tStock: " + totalStock);
    }

    @FXML
    private void handleSave() throws IOException {
        if (productReportTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(productReportTableView, "products/product-report.xls");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Saved");
            alert.setHeaderText(null);
            alert.setContentText("Product Report saved successfully.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\products\\product-report.xls"));
            }
        }
    }
}
