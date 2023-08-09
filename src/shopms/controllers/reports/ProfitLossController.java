package shopms.controllers.reports;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import shopms.model.Model;
import shopms.controllers.purchases.Purchase;
import shopms.controllers.returns.Return;
import shopms.controllers.sales.Sale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfitLossController {
    @FXML
    private ComboBox<String> dateRangeCombo;
    @FXML
    private Label purchaseCost;
    @FXML
    private Label purchaseAmount;
    @FXML
    private Label saleCost;
    @FXML
    private Label saleAmount;
    @FXML
    private Label salePaidAmount;
    @FXML
    private Label returnRefundCost;
    @FXML
    private Label returnAmount;
    @FXML
    private Label paymentReceivedAmount;
    @FXML
    private Label paymentsReceived;
    @FXML
    private Label paymentCash;
    @FXML
    private Label paymentCheque;
    @FXML
    private Label profitLossAmount;
    @FXML
    private Label profitLossDetails;

    @FXML
    private void initialize() {
        dateRangeCombo.getSelectionModel().select(0);
        profitLossFunction("Last Week");
        dateRangeCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            profitLossFunction(newValue);
        });
    }

    private void profitLossFunction(String range) {

        switch (range) {
            case "Last Week" : {
                calculations(7);
            }
            case "Last 30 Days" : {
                calculations(30);
            }
            case "Last 90 Days" : {
                calculations(90);
            }
            case "Last Year" : {
                calculations(365);
            }
            case "All Time" : {
                calculations(0);
            }
            default : {
                purchaseCost.setText("Rs. 0");
                purchaseAmount.setText("0 Items");
            }
        }
    }

    private void calculations(int days) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        double pCost = 0;
        int pAmount = 0;

        double sCost = 0;
        int sAmount = 0;

        double rCost = 0;
        int rAmount = 0;

        double receivedPayments = 0;
        int receivedPaymentAmount = 0;
        double cashPayments = 0;
        double chequePayments = 0;

        LocalDate today = LocalDate.now();
        for (Purchase purchase : Model.getInstance().getPurchases()) {
            if (days <= 0) {
                pCost += purchase.getGrand_total();
                pAmount += purchase.getQuantity();
                purchaseCost.setText("Rs." + pCost);
                purchaseAmount.setText(pAmount + " Items");
            } else {
                LocalDate purchaseDate = LocalDate.parse(purchase.getDate().substring(0, 10), myFormatObj);
                if (purchaseDate.isAfter(today.minusDays(days)) && today.isAfter(purchaseDate)) {
                    pCost += purchase.getGrand_total();
                    pAmount += purchase.getQuantity();
                    purchaseCost.setText("Rs." + pCost);
                    purchaseAmount.setText(pAmount + " Items");
                } else {
                    purchaseCost.setText("Rs.0");
                    purchaseAmount.setText("0 Items");
                }
            }
        }

        for (Sale sale : Model.getInstance().getSales()) {
            if (days == 0) {
                sCost += sale.getGrand_total();
                sAmount += sale.getQuantity();

                if (sale.getPayment_status().equals("Paid")) {
                    receivedPayments += sale.getPaid_amount();
                    receivedPaymentAmount++;
                    if (sale.getPayment_method().equals("Cash")) {
                        cashPayments += sale.getPaid_amount();
                    } else {
                        chequePayments += sale.getPaid_amount();
                    }
                }

            } else {
                LocalDate saleDate = LocalDate.parse(sale.getDate().substring(0, 10), myFormatObj);
                if (saleDate.isAfter(today.minusDays(days)) && today.isAfter(saleDate)) {
                    sCost += sale.getGrand_total();
                    sAmount += sale.getQuantity();

                    if (sale.getPayment_status().equals("Paid")) {
                        receivedPayments += sale.getPaid_amount();
                        receivedPaymentAmount++;
                        if (sale.getPayment_method().equals("Cash")) {
                            cashPayments += sale.getPaid_amount();
                        } else {
                            chequePayments += sale.getPaid_amount();
                        }
                    }
                } else {
                    saleCost.setText("Rs.0");
                    saleAmount.setText("0 Items");
                }
            }
            saleCost.setText("Rs." + sCost);
            saleAmount.setText(sAmount + " Items");

            paymentReceivedAmount.setText("Rs." + receivedPayments);
            paymentsReceived.setText(receivedPaymentAmount + " Received");
            paymentCash.setText("Cash : Rs." + cashPayments);
            paymentCheque.setText("Cheque :  Rs." + chequePayments);
        }

        for (Return checkReturn : Model.getInstance().getReturns()) {
            if (days <= 0) {
                rCost += checkReturn.getGrand_total();
                rAmount += checkReturn.getQuantity();
                returnRefundCost.setText("Rs." + rCost);
                returnAmount.setText(rAmount + " Items");
            } else {
                LocalDate returnDate = LocalDate.parse(checkReturn.getDate().substring(0, 10), myFormatObj);
                if (returnDate.isAfter(today.minusDays(days)) && today.isAfter(returnDate)) {
                    rCost += checkReturn.getGrand_total();
                    rAmount += checkReturn.getQuantity();
                    returnRefundCost.setText("Rs." + rCost);
                    returnAmount.setText(rAmount + " Items");
                } else {
                    returnRefundCost.setText("Rs.0");
                    returnAmount.setText("0 Items");
                }
            }
        }

        profitLossAmount.setText("Rs." + (sCost - (pCost + rCost)));
        profitLossDetails.setText("Rs." + sCost + " Sales - (Rs." + pCost + " Purchases + Rs." + rCost + " Returns)");

    }
}
