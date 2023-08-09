package shopms.controllers.reports;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import shopms.model.Model;
import shopms.controllers.products.Product;

public class ProductQuantityAlertController {
    @FXML
    private TableView<Product> productQuantityAlertTableView;

    @FXML
    private void initialize() {
        for(Product product : Model.getInstance().getProducts()) {
            if(product.getQuantity() == 0) {
                productQuantityAlertTableView.getItems().add(product);
            }
        }
    }
}
