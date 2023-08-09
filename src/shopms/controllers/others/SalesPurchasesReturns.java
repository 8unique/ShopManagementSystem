package shopms.controllers.others;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalesPurchasesReturns {
    private String reference;
    private String date;
    private int code;
    private int quantity;
    private double price;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyyMMdd");
        setDate(LocalDate.parse(reference.substring(3, 11), myFormatObj).toString());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
