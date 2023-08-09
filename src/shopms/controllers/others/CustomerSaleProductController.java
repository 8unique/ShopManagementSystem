package shopms.controllers.others;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import shopms.model.Model;
import shopms.controllers.products.Product;
import shopms.controllers.sales.Sale;

public class CustomerSaleProductController {
    @FXML
    private GridPane customerSaleProductsGridPane;
    @FXML
    private ListView<SalesPurchasesReturns> productSaleListView;

    public static Sale selectedSale;
    private ListView<Product> productListView = new ListView<>();

    @FXML
    private void initialize() {
        for (SalesPurchasesReturns salesPurchasesReturns : Model.getInstance().getSalesPurchasesReturns()) {
            if (selectedSale.getInvoice_no().equals(salesPurchasesReturns.getReference())) {
                for (Product product : Model.getInstance().getProducts()) {
                    if (product.getCode() == salesPurchasesReturns.getCode()) {
                        if (!productListView.getItems().contains(product)) {
                            productListView.getItems().add(product);
                            productSaleListView.getItems().add(salesPurchasesReturns);
                        }
                    }
                }
            }
        }

        productSaleListView.setCellFactory(param -> new ModifiedCell());
    }

    static class ModifiedCell extends ListCell<SalesPurchasesReturns> {
        HBox hbox = new HBox();
        Label item = new Label();

        public ModifiedCell() {
            super();
            super.setAlignment(Pos.CENTER);

            item.setMaxWidth(Double.MAX_VALUE);
            hbox.setStyle("-fx-spacing: 20.0; -fx-font-size: 20px;");
            HBox.setHgrow(item, Priority.ALWAYS);

            hbox.getChildren().addAll(item);
        }

        @Override
        protected void updateItem(SalesPurchasesReturns productSale, boolean empty) {
            super.updateItem(productSale, empty);
            setText(null);
            setGraphic(null);
            if (productSale != null && !empty) {
                for (Product product : Model.getInstance().getProducts()) {
                    if (productSale.getCode() == product.getCode()) {
                        item.setText(product.getName() + " (" + product.getCode() + ")" + " | Quantity : " + productSale.getQuantity() + " | Cost : Rs." + (productSale.getPrice() * productSale.getQuantity()));
                    }
                }
                setGraphic(hbox);
            }
        }
    }
}
