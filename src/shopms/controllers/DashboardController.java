package shopms.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import shopms.controllers.others.SalesPurchasesReturns;
import shopms.controllers.products.Product;
import shopms.controllers.purchases.Purchase;
import shopms.controllers.reports.Report;
import shopms.controllers.returns.Return;
import shopms.controllers.sales.Sale;
import shopms.model.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DashboardController {
    @FXML
    private AreaChart<String, Integer> yearlyReportAreaChart;
    @FXML
    private Label revenueAmount;
    @FXML
    private Label revenueDate;
    @FXML
    private Label purchaseAmount;
    @FXML
    private Label purchaseDate;
    @FXML
    private Label profitAmount;
    @FXML
    private Label profitDate;
    @FXML
    private Label saleAmount;
    @FXML
    private Label saleDate;
    @FXML
    private Label returnAmount;
    @FXML
    private Label returnDate;
    @FXML
    private Label saleQty;
    @FXML
    private Label saleQtyDate;
    @FXML
    private ToggleGroup tableViewToggleGroup;
    @FXML
    private ToggleButton saleToggleButton;
    @FXML
    private ToggleButton purchaseToggleButton;
    @FXML
    private ToggleButton returnToggleButton;
    @FXML
    private TableView<Sale> saleTableView;
    @FXML
    private TableView<Purchase> purchaseTableView;
    @FXML
    private TableView<Return> returnTableView;
    @FXML
    private TableView<Product> bestSellingProductsTableView;

    @FXML
    private void initialize() {
        saleTableView.setVisible(true);
        purchaseTableView.setVisible(false);
        returnTableView.setVisible(false);

        tableViewToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == saleToggleButton) {
                setSaleToggleButtonAction();
            } else if (oldValue == purchaseToggleButton) {
                setPurchaseToggleButtonAction();
            } else if (oldValue == returnToggleButton) {
                setReturnToggleButtonAction();
            }
            if (newValue == saleToggleButton) {
                setSaleToggleButtonAction();
            } else if (newValue == purchaseToggleButton) {
                setPurchaseToggleButtonAction();
            } else if (newValue == returnToggleButton) {
                setReturnToggleButtonAction();
            }
        });

        ObservableList<Report> purchasesReport = FXCollections.observableArrayList();
        ObservableList<Report> salesReport = FXCollections.observableArrayList();

        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        double purchases = 0;
        String pDate = null;
        for (Purchase purchase : Model.getInstance().getPurchases()) {
            purchases += purchase.getGrand_total();
            pDate = purchase.getDate();
        }
        purchaseAmount.setText("Rs." + purchases);
        purchaseDate.setText("Last Purchase: " + pDate);

        double sales = 0;
        double paidAmount = 0;
        int saleQuantity = 0;
        String sDate = null;

        for (Sale sale : Model.getInstance().getSales()) {
            sales += sale.getGrand_total();
            saleQuantity += sale.getQuantity();
            paidAmount += sale.getPaid_amount();
            sDate = sale.getDate();
        }
        saleAmount.setText("Rs." + sales);
        saleDate.setText("Last Sale: " + sDate);
        saleQty.setText(saleQuantity + " Items");
        saleQtyDate.setText("Last Sale: " + sDate);

        double returns = 0;
        for (Return getReturn : Model.getInstance().getReturns()) {
            returns += getReturn.getGrand_total();
        }
        returnAmount.setText("Rs." + returns);
        returnDate.setText("Last Update: " + LocalDateTime.now().format(myFormatObj));

        revenueAmount.setText("Rs." + paidAmount);
        revenueDate.setText("Last Update: " + LocalDateTime.now().format(myFormatObj));

        profitAmount.setText("Rs." + (sales - (purchases + returns)));
        profitDate.setText("Last Update: " + LocalDateTime.now().format(myFormatObj));

        for (Purchase purchase : Model.getInstance().getPurchases()) {
            getReport(purchasesReport, myFormatObj, purchase.getDate(), (int) purchase.getGrand_total());
        }

        for (Sale sale : Model.getInstance().getSales()) {
            getReport(salesReport, myFormatObj, sale.getDate(), (int) sale.getGrand_total());
        }

        XYChart.Series<String, Integer> purchasesSeries = new XYChart.Series<>();
        purchasesSeries.setName("Purchases");
        for (Report report : purchasesReport) {
            purchasesSeries.getData().add(new XYChart.Data(report.getMonth(), report.getAmount()));
        }

        XYChart.Series<String, Integer> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Sales");
        for (Report report : salesReport) {
            salesSeries.getData().add(new XYChart.Data(report.getMonth(), report.getAmount()));
        }

        yearlyReportAreaChart.getData().addAll(purchasesSeries, salesSeries);

        ObservableList<Sale> saleObservableList = FXCollections.observableArrayList((Model.getInstance().getSales()));
        saleTableView.setItems(saleObservableList);
        saleTableView.getItems().sort(Comparator.comparing(o -> LocalDate.parse(o.getDate(), myFormatObj)));
        Collections.reverse(saleTableView.getItems());
        saleTableView.getItems().removeIf(sale -> saleTableView.getItems().indexOf(sale) > 4);

        ObservableList<Purchase> purchaseObservableList = FXCollections.observableArrayList((Model.getInstance().getPurchases()));
        purchaseTableView.setItems(purchaseObservableList);
        purchaseTableView.getItems().sort(Comparator.comparing(o -> LocalDate.parse(o.getDate(), myFormatObj)));
        Collections.reverse(purchaseTableView.getItems());
        purchaseTableView.getItems().removeIf(purchase -> purchaseTableView.getItems().indexOf(purchase) > 4);

        ObservableList<Return> returnObservableList = FXCollections.observableArrayList((Model.getInstance().getReturns()));
        returnTableView.setItems(returnObservableList);
        returnTableView.getItems().sort(Comparator.comparing(o -> LocalDate.parse(o.getDate(), myFormatObj)));
        Collections.reverse(returnTableView.getItems());
        returnTableView.getItems().removeIf(checkReturn -> returnTableView.getItems().indexOf(checkReturn) > 4);

        ArrayList<Product> bestSellingProducts = new ArrayList<>();
        for (Product product : Model.getInstance().getProducts()) {
            Product addProduct = new Product();
            addProduct.setCode(product.getCode());
            addProduct.setName(product.getName());
            for (SalesPurchasesReturns salesPurchasesReturns : Model.getInstance().getSalesPurchasesReturns()) {
                if (product.getCode() == salesPurchasesReturns.getCode() && salesPurchasesReturns.getReference().startsWith("sr")) {
                    addProduct.setQuantity(addProduct.getQuantity() + salesPurchasesReturns.getQuantity());
                    if (!bestSellingProducts.contains(addProduct)) {
                        bestSellingProducts.add(addProduct);
                    }
                }
            }
        }

        bestSellingProducts.sort(Comparator.comparingInt(Product::getQuantity));

        int bestSellingSize = bestSellingProducts.size();
        if (bestSellingSize < 5)
            for (int i = bestSellingSize - 1; i >= 0; i--) {
                bestSellingProductsTableView.getItems().add(bestSellingProducts.get(i));
            }
        else {
            for (int i = 4; i >= 0; i--) {
                bestSellingProductsTableView.getItems().add(bestSellingProducts.get(i));
            }
        }
    }

    private void getReport(ObservableList<Report> reports, DateTimeFormatter myFormatObj, String inputDate, int grand_total) {
        LocalDate date = LocalDate.parse(inputDate, myFormatObj);
        if (date.getMonthValue() == 1) {
            Report report = new Report();
            report.setMonth("January");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 2) {
            Report report = new Report();
            report.setMonth("February");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 3) {
            Report report = new Report();
            report.setMonth("March");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 4) {
            Report report = new Report();
            report.setMonth("April");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 5) {
            Report report = new Report();
            report.setMonth("May");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 6) {
            Report report = new Report();
            report.setMonth("June");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 7) {
            Report report = new Report();
            report.setMonth("July");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 8) {
            Report report = new Report();
            report.setMonth("August");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 9) {
            Report report = new Report();
            report.setMonth("September");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 10) {
            Report report = new Report();
            report.setMonth("October");
            report.setAmount(grand_total);
            reports.add(report);
        } else if (date.getMonthValue() == 11) {
            Report report = new Report();
            report.setMonth("November");
            report.setAmount(grand_total);
            reports.add(report);
        } else {
            Report report = new Report();
            report.setMonth("December");
            report.setAmount(grand_total);
            reports.add(report);
        }
    }

    void setSaleToggleButtonAction() {
        if (saleToggleButton.isSelected()) {
            saleTableView.setVisible(true);
            purchaseTableView.setVisible(false);
            returnTableView.setVisible(false);
        }
    }

    void setPurchaseToggleButtonAction() {
        if (purchaseToggleButton.isSelected()) {
            saleTableView.setVisible(false);
            purchaseTableView.setVisible(true);
            returnTableView.setVisible(false);
        }
    }

    void setReturnToggleButtonAction() {
        if (returnToggleButton.isSelected()) {
            saleTableView.setVisible(false);
            purchaseTableView.setVisible(false);
            returnTableView.setVisible(true);
        }
    }
}
