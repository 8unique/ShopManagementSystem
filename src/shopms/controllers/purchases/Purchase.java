package shopms.controllers.purchases;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Purchase {
    private SimpleStringProperty date = new SimpleStringProperty();
    private SimpleStringProperty reference_no = new SimpleStringProperty();
    private SimpleStringProperty supplier = new SimpleStringProperty();
    private SimpleIntegerProperty items = new SimpleIntegerProperty();
    private SimpleIntegerProperty quantity = new SimpleIntegerProperty();
    private SimpleDoubleProperty product_cost = new SimpleDoubleProperty();
    private SimpleDoubleProperty shipping_cost = new SimpleDoubleProperty();
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

    public String getReference_no() {
        return reference_no.get();
    }

    public SimpleStringProperty reference_noProperty() {
        return reference_no;
    }

    public void setReference_no(String reference_no) {
        this.reference_no.set(reference_no);
    }

    public String getSupplier() {
        return supplier.get();
    }

    public SimpleStringProperty supplierProperty() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier.set(supplier);
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

    public double getProduct_cost() {
        return product_cost.get();
    }

    public SimpleDoubleProperty product_costProperty() {
        return product_cost;
    }

    public void setProduct_cost(double product_cost) {
        this.product_cost.set(product_cost);
    }

    public double getShipping_cost() {
        return shipping_cost.get();
    }

    public SimpleDoubleProperty shipping_costProperty() {
        return shipping_cost;
    }

    public void setShipping_cost(double shipping_cost) {
        this.shipping_cost.set(shipping_cost);
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
