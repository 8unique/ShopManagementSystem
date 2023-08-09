package shopms.controllers.products;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import shopms.controllers.ShopManagerController;
import shopms.model.Functions;
import shopms.model.Model;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class ProductController {
    @FXML
    private GridPane productGridPane;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTableView;

    private ObservableList<Product> products = Model.getInstance().getProducts();

    @FXML
    private void initialize() {
        productTableView.getItems().setAll(Model.getInstance().getProducts());

        FilteredList<Product> productFilteredList = new FilteredList<>(products, p -> true);
        productTableView.setItems(productFilteredList);
        searchField.textProperty().addListener((obs, oldValue, newValue) ->
                productFilteredList.setPredicate(p -> p.getName().toLowerCase().contains(newValue.toLowerCase().trim()))
        );
        productTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final ContextMenu menu = new ContextMenu();

        final MenuItem editProductMenu = new MenuItem("Edit...");
        editProductMenu.setOnAction(event -> {
            try {
                Product editProduct = productTableView.getSelectionModel().getSelectedItem();
                handleEditProduct(editProduct);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        productTableView.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (product == null)
                    setStyle("");
                else if (product.getQuantity() > 0 && product.getQuantity() < 3)
                    setStyle("-fx-background-color: #b28b76;");
                else if (product.getQuantity() == 0)
                    setStyle("-fx-background-color: crimson;");
                else
                    setStyle("");
            }
        });

        final MenuItem printProductMenu = new MenuItem("Print...");
        printProductMenu.setOnAction(event -> {
            try {
                Product printProduct = productTableView.getSelectionModel().getSelectedItem();
                handlePrintProduct(printProduct);
            } catch (IOException | DocumentException throwable) {
                throwable.printStackTrace();
            }
        });

        final MenuItem deleteProductMenu = new MenuItem("Delete...");
        deleteProductMenu.setOnAction(event -> {
            try {
                handleRemoveProduct();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });

        editProductMenu.disableProperty().bind(Bindings.isEmpty(productTableView.getSelectionModel().getSelectedItems()).or(
                Bindings.createBooleanBinding(() -> !Model.getInstance().loggedAccount.getUser_role().equals("administrator")
                )));
        deleteProductMenu.setDisable(true);
        printProductMenu.disableProperty().bind(Bindings.isEmpty(productTableView.getSelectionModel().getSelectedItems()));

        menu.getItems().addAll(editProductMenu, printProductMenu);
        productTableView.setContextMenu(menu);
    }

    private void handlePrintProduct(Product printProduct) throws IOException, DocumentException {
        String name = printProduct.getName();
        int code = printProduct.getCode();
        String brand = printProduct.getBrand();
        String category = printProduct.getCategory();
        double price = printProduct.getPrice();
        int quantity = printProduct.getQuantity();

        Code128Bean code128 = new Code128Bean();
        code128.setHeight(15f);
        code128.setModuleWidth(0.3);
        code128.setQuietZone(10);
        code128.doQuietZone(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        code128.generateBarcode(canvas, String.valueOf(code));
        canvas.finish();

        //write to pdf
        com.itextpdf.text.Image png = Image.getInstance(baos.toByteArray());

        Document document = new Document();
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Paragraph p = new Paragraph("Name : " + name);
        p.add("\n\nCode : " + code);
        p.add("\n\nBrand : " + brand);
        p.add("\n\nCategory : " + category);
        p.add("\n\nPrice : Rs." + price);
        p.add("\n\nQuantity : " + quantity);

        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setBorder(0);
        table.addCell(p);
        table.addCell(png);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("products/" + name + "-details.pdf"));
        document.open();
        document.add(table);
        document.close();
        writer.close();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Details printed");
        alert.setHeaderText(null);
        alert.setContentText("Details printed successfully.");

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\products\\" + name + "-details.pdf"));
        }
    }

    private void handleRemoveProduct() throws SQLException {
        Product removeProduct = productTableView.getSelectionModel().getSelectedItem();
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove: " + removeProduct.getName());

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Model.getInstance().setQueryRemoveProduct(removeProduct.getCode());
            Model.getInstance().getProducts().remove(removeProduct);
        }
    }

    @FXML
    private void handleEditProduct(Product editProduct) throws IOException {
        Functions.getInstance().loadEdit(ShopManagerController.staticBorderPane, "editProduct.fxml", editProduct);
    }

    @FXML
    private void handleAddProduct() throws IOException {
        Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "addProduct.fxml");
    }

    @FXML
    private void handleSave() throws IOException {
        if (productTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(productTableView, "products/products.xls");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Products Saved");
            alert.setHeaderText(null);
            alert.setContentText("Products saved successfully.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\products\\products.xls"));
            }
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            handleRemoveProduct();
        }
    }
}
