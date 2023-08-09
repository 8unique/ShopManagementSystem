package shopms.controllers.sales;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sale {
    private SimpleStringProperty date = new SimpleStringProperty();
    private SimpleStringProperty invoice_no = new SimpleStringProperty();
    private SimpleStringProperty customer = new SimpleStringProperty();
    private SimpleIntegerProperty items = new SimpleIntegerProperty();
    private SimpleIntegerProperty quantity = new SimpleIntegerProperty();
    private SimpleDoubleProperty grand_total = new SimpleDoubleProperty();
    private SimpleDoubleProperty paid_amount = new SimpleDoubleProperty();
    private SimpleDoubleProperty balance = new SimpleDoubleProperty();
    private SimpleDoubleProperty due_amount = new SimpleDoubleProperty();
    private SimpleStringProperty payment_method = new SimpleStringProperty();
    private SimpleStringProperty payment_status = new SimpleStringProperty();
    private SimpleIntegerProperty chequeNo = new SimpleIntegerProperty();
    private SimpleStringProperty chequeDate = new SimpleStringProperty();

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getInvoice_no() {
        return invoice_no.get();
    }

    public SimpleStringProperty invoice_noProperty() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no.set(invoice_no);
    }

    public String getCustomer() {
        return customer.get();
    }

    public SimpleStringProperty customerProperty() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer.set(customer);
    }

    public int getItems() {
        return items.get();
    }

    public SimpleIntegerProperty itemsProperty() {
        return items;
    }

    public void setItems(int items) {
        this.items.set(items);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getGrand_total() {
        return grand_total.get();
    }

    public SimpleDoubleProperty grand_totalProperty() {
        return grand_total;
    }

    public void setGrand_total(double grand_total) {
        this.grand_total.set(grand_total);
    }

    public double getPaid_amount() {
        return paid_amount.get();
    }

    public SimpleDoubleProperty paid_amountProperty() {
        return paid_amount;
    }

    public void setPaid_amount(double paid_amount) {
        this.paid_amount.set(paid_amount);
    }

    public double getBalance() {
        return balance.get();
    }

    public SimpleDoubleProperty balanceProperty() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public double getDue_amount() {
        return due_amount.get();
    }

    public SimpleDoubleProperty due_amountProperty() {
        return due_amount;
    }

    public void setDue_amount(double due_amount) {
        this.due_amount.set(due_amount);
    }

    public String getPayment_method() {
        return payment_method.get();
    }

    public SimpleStringProperty payment_methodProperty() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method.set(payment_method);
    }

    public String getPayment_status() {
        return payment_status.get();
    }

    public SimpleStringProperty payment_statusProperty() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status.set(payment_status);
    }

    public int getChequeNo() {
        return chequeNo.get();
    }

    public SimpleIntegerProperty chequeNoProperty() {
        return chequeNo;
    }

    public void setChequeNo(int chequeNo) {
        this.chequeNo.set(chequeNo);
    }

    public String getChequeDate() {
        return chequeDate.get();
    }

    public SimpleStringProperty chequeDateProperty() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate.set(chequeDate);
    }
}
