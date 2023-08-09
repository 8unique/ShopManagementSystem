package shopms.controllers.returns;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Return {
    private SimpleStringProperty date = new SimpleStringProperty();
    private SimpleStringProperty sale_purchase_no = new SimpleStringProperty();
    private SimpleStringProperty return_no = new SimpleStringProperty();
    private SimpleStringProperty supplier_customer = new SimpleStringProperty();
    private SimpleIntegerProperty items = new SimpleIntegerProperty();
    private SimpleIntegerProperty quantity = new SimpleIntegerProperty();
    private SimpleDoubleProperty grand_total = new SimpleDoubleProperty();

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getSale_purchase_no() {
        return sale_purchase_no.get();
    }

    public SimpleStringProperty sale_purchase_noProperty() {
        return sale_purchase_no;
    }

    public void setSale_purchase_no(String sale_purchase_no) {
        this.sale_purchase_no.set(sale_purchase_no);
    }

    public String getReturn_no() {
        return return_no.get();
    }

    public SimpleStringProperty return_noProperty() {
        return return_no;
    }

    public void setReturn_no(String return_no) {
        this.return_no.set(return_no);
    }

    public String getSupplier_customer() {
        return supplier_customer.get();
    }

    public SimpleStringProperty supplier_customerProperty() {
        return supplier_customer;
    }

    public void setSupplier_customer(String supplier_customer) {
        this.supplier_customer.set(supplier_customer);
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
}
