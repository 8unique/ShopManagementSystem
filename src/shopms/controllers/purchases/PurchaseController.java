package shopms.controllers.purchases;

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import shopms.controllers.ShopManagerController;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.products.Product;
import shopms.controllers.others.SalesPurchasesReturns;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class PurchaseController {
    @FXML
    private GridPane purchaseGridPane;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Purchase> purchaseTableView;

    private ObservableList<Purchase> purchases = Model.getInstance().getPurchases();

    @FXML
    private void initialize() {
        purchaseTableView.getItems().setAll(Model.getInstance().getPurchases());

        FilteredList<Purchase> purchasesFilteredList = new FilteredList<>(purchases, p -> true);
        purchaseTableView.setItems(purchasesFilteredList);
        searchField.textProperty().addListener((obs, oldValue, newValue) ->
                purchasesFilteredList.setPredicate(p -> p.getReference_no().toLowerCase().contains(newValue.toLowerCase().trim()))
        );
        purchaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final ContextMenu menu = new ContextMenu();

        final MenuItem editPurchaseMenu = new MenuItem("Edit...");
        editPurchaseMenu.setOnAction(event -> {
            Purchase editPurchase = purchaseTableView.getSelectionModel().getSelectedItem();
            try {
                handleEditPurchase(editPurchase);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final MenuItem printPurchaseMenu = new MenuItem("Print...");
        printPurchaseMenu.setOnAction(event -> {
            Purchase printPurchase = purchaseTableView.getSelectionModel().getSelectedItem();
            try {
                handlePrintPurchase(printPurchase);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        });

        final MenuItem deletePurchaseMenu = new MenuItem("Delete...");
        deletePurchaseMenu.setOnAction(event -> {
            try {
                handleRemovePurchase();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
        editPurchaseMenu.disableProperty().bind(Bindings.isEmpty(purchaseTableView.getSelectionModel().getSelectedItems()));
        deletePurchaseMenu.disableProperty().bind(Bindings.isEmpty(purchaseTableView.getSelectionModel().getSelectedItems()));
        printPurchaseMenu.disableProperty().bind(Bindings.isEmpty(purchaseTableView.getSelectionModel().getSelectedItems()));

        menu.getItems().addAll(editPurchaseMenu, printPurchaseMenu, deletePurchaseMenu);
        purchaseTableView.setContextMenu(menu);
    }

    private void handleRemovePurchase() throws SQLException {
        Purchase removePurchase = purchaseTableView.getSelectionModel().getSelectedItem();
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Purchase");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove: " + removePurchase.getReference_no());

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Model.getInstance().setQueryRemovePurchase(removePurchase.getReference_no());
            Model.getInstance().setQueryRemoveSalesPurchaseReturn(removePurchase.getReference_no());
            Model.getInstance().getPurchases().remove(removePurchase);
            Model.getInstance().getSalesPurchasesReturns().removeIf(productPurchase -> productPurchase.getReference().equals(removePurchase.getReference_no()));
        }
    }

    private void handlePrintPurchase(Purchase printPurchase) throws IOException, DocumentException {
        String date = printPurchase.getDate();
        String reference_no = printPurchase.getReference_no();
        String supplier = printPurchase.getSupplier();
        int items = printPurchase.getItems();
        int quantity = printPurchase.getQuantity();
        double product_cost = printPurchase.getProduct_cost();
        double shipping_cost = printPurchase.getShipping_cost();
        double grand_total = printPurchase.getGrand_total();

        Code128Bean code128 = new Code128Bean();
        code128.setHeight(15f);
        code128.setModuleWidth(0.3);
        code128.setQuietZone(10);
        code128.doQuietZone(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        code128.generateBarcode(canvas, reference_no);
        canvas.finish();

        //write to pdf
        Image png = Image.getInstance(baos.toByteArray());

        Document document = new Document();
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Paragraph p = new Paragraph("Date : " + date);
        p.add("\n\nReference No : " + reference_no);
        p.add("\n\nSupplier : " + supplier);
        p.add("\n\nItems : " + items);
        p.add("\n\nQuantity : " + quantity);
        p.add("\n\nProduct Cost : " + product_cost);
        p.add("\n\nShipping Cost : " + shipping_cost);
        p.add("\n\nGrand Total : " + grand_total);
        p.add("\n----------------------------------------");
        p.add("\nProduct Details");
        p.add("\n----------------------------------------");

        for (SalesPurchasesReturns productPurchase : Model.getInstance().getSalesPurchasesReturns()) {
            for (Product product : Model.getInstance().getProducts()) {
                if (productPurchase.getCode() == product.getCode() && productPurchase.getReference().equals(printPurchase.getReference_no())) {
                    p.add("\n" + product.getName() + " [" + product.getCode() + "] X " + productPurchase.getQuantity() + "\nRs." + (productPurchase.getPrice() * productPurchase.getQuantity()));
                    p.add("\n----------------------------------------");
                }
            }
        }

        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setBorder(0);
        table.addCell(p);
        table.addCell(png);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("purchases/" + reference_no + "-details.pdf"));
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
            Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\purchases\\" + reference_no + "-details.pdf"));
        }
    }

    private void handleEditPurchase(Purchase editPurchase) throws IOException {
        Functions.getInstance().loadEdit(ShopManagerController.staticBorderPane, "editPurchase.fxml", editPurchase);
    }

    @FXML
    private void handleAddPurchase() throws IOException {
        Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "addPurchase.fxml");
    }

    @FXML
    private void handleSave() throws IOException {
        if (purchaseTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(purchaseTableView, "purchases/purchases.xls");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Purchases Saved");
            alert.setHeaderText(null);
            alert.setContentText("Purchases saved successfully.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\purchases\\purchases.xls"));
            }
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            handleRemovePurchase();
        }
    }
}
