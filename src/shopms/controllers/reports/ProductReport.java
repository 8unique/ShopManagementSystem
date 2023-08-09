package shopms.controllers.reports;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProductReport {
    private SimpleStringProperty product = new SimpleStringProperty();
    private SimpleIntegerProperty purchasedQuantity = new SimpleIntegerProperty();
    private SimpleIntegerProperty soldQuantity = new SimpleIntegerProperty();
    private SimpleDoubleProperty profit = new SimpleDoubleProperty();
    private SimpleIntegerProperty stock = new SimpleIntegerProperty();

    public String getProduct() {
        return product.get();
    }

    public SimpleStringProperty productProperty() {
        return product;
    }

    public void setProduct(String product) {
        this.product.set(product);
    }

    public int getPurchasedQuantity() {
        return purchasedQuantity.get();
    }

    public SimpleIntegerProperty purchasedQuantityProperty() {
        return purchasedQuantity;
    }

    public void setPurchasedQuantity(int purchasedQuantity) {
        this.purchasedQuantity.set(purchasedQuantity);
    }

    public int getSoldQuantity() {
        return soldQuantity.get();
    }

    public SimpleIntegerProperty soldQuantityProperty() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity.set(soldQuantity);
    }

    public double getProfit() {
        return profit.get();
    }

    public SimpleDoubleProperty profitProperty() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit.set(profit);
    }

    public int getStock() {
        return stock.get();
    }

    public SimpleIntegerProperty stockProperty() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }
}
