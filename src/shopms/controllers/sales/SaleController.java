package shopms.controllers.sales;

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

public class SaleController {
    @FXML
    private GridPane saleGridPane;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Sale> salesTableView;

    private ObservableList<Sale> sales = Model.getInstance().getSales();

    @FXML
    private void initialize() {
        salesTableView.getItems().setAll(Model.getInstance().getSales());

        FilteredList<Sale> salesFilteredList = new FilteredList<>(sales, p -> true);
        salesTableView.setItems(salesFilteredList);
        searchField.textProperty().addListener((obs, oldValue, newValue) ->
                salesFilteredList.setPredicate(p -> p.getInvoice_no().toLowerCase().contains(newValue.toLowerCase().trim()))
        );
        salesTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final ContextMenu menu = new ContextMenu();

        final MenuItem editSaleMenu = new MenuItem("Edit...");
        editSaleMenu.setOnAction(event -> {
            Sale editSale = salesTableView.getSelectionModel().getSelectedItem();
            try {
                handleEditSale(editSale);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final MenuItem printSaleMenu = new MenuItem("Print...");
        printSaleMenu.setOnAction(event -> {
            Sale printSale = salesTableView.getSelectionModel().getSelectedItem();
            try {
                handlePrintSale(printSale);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        });

        final MenuItem deleteSaleMenu = new MenuItem("Delete...");
        deleteSaleMenu.setOnAction(event -> {
            try {
                handleRemoveSale();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
        editSaleMenu.disableProperty().bind(Bindings.isEmpty(salesTableView.getSelectionModel().getSelectedItems()));
        deleteSaleMenu.disableProperty().bind(Bindings.isEmpty(salesTableView.getSelectionModel().getSelectedItems()));
        printSaleMenu.disableProperty().bind(Bindings.isEmpty(salesTableView.getSelectionModel().getSelectedItems()));

        menu.getItems().addAll(editSaleMenu, printSaleMenu, deleteSaleMenu);
        salesTableView.setContextMenu(menu);

        salesTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Sale sale, boolean empty) {
                super.updateItem(sale, empty);
                if (sale == null)
                    setStyle("");
                else if (sale.getPayment_status().equals("Due"))
                    setStyle("-fx-background-color: #e9661e;");
                else
                    setStyle("");
            }
        });
    }

    private void handlePrintSale(Sale printSale) throws IOException, DocumentException {
        String date = printSale.getDate();
        String invoice_no = printSale.getInvoice_no();
        String customer = printSale.getCustomer();
        int items = printSale.getItems();
        int quantity = printSale.getQuantity();
        double grand_total = printSale.getGrand_total();
        double paid_amount = printSale.getPaid_amount();
        double balance = printSale.getBalance();
        double due_amount = printSale.getDue_amount();
        String payment_method = printSale.getPayment_method();
        String payment_status = printSale.getPayment_status();
        int cheque_no = printSale.getChequeNo();
        String cheque_date = printSale.getChequeDate();

        Code128Bean code128 = new Code128Bean();
        code128.setHeight(15f);
        code128.setModuleWidth(0.3);
        code128.setQuietZone(10);
        code128.doQuietZone(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        code128.generateBarcode(canvas, invoice_no);
        canvas.finish();

        //write to pdf
        Image png = Image.getInstance(baos.toByteArray());

        Document document = new Document();
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Paragraph p = new Paragraph("Date : " + date);
        p.add("\n\nInvoice No : " + invoice_no);
        p.add("\n\nCustomer : " + customer);
        p.add("\n\nItems : " + items);
        p.add("\n\nQuantity : " + quantity);
        p.add("\n\nGrand Total : " + grand_total);
        p.add("\n\nPaid Amount : " + paid_amount);
        p.add("\n\nBalance : " + balance);
        p.add("\n\nDue Amount : " + due_amount);
        p.add("\n\nPayment Method : " + payment_method);
        p.add("\n\nPayment Status : " + payment_status);
        if (cheque_no != 0) {
            p.add("\n\nCheque No : " + cheque_no);
            p.add("\n\nCheque Date : " + cheque_date);
        }
        p.add("\n----------------------------------------");
        p.add("\nSale Details");
        p.add("\n----------------------------------------");

        for (SalesPurchasesReturns productPurchase : Model.getInstance().getSalesPurchasesReturns()) {
            for (Product product : Model.getInstance().getProducts()) {
                if (productPurchase.getCode() == product.getCode() && productPurchase.getReference().equals(printSale.getInvoice_no())) {
                    p.add("\n" + product.getName() + " [" + product.getCode() + "] X " + productPurchase.getQuantity() + "\nRs." + (productPurchase.getPrice() * productPurchase.getQuantity()));
                    p.add("\n----------------------------------------");
                }
            }
        }

        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setBorder(0);
        table.addCell(p);
        table.addCell(png);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("sales/" + invoice_no + "-details.pdf"));
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
            Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\sales\\" + invoice_no + "-details.pdf"));
        }
    }

    private void handleRemoveSale() throws SQLException {
        Sale removeSale = salesTableView.getSelectionModel().getSelectedItem();
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Sale");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove: " + removeSale.getInvoice_no());

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Model.getInstance().setQueryRemoveSale(removeSale.getInvoice_no());
            Model.getInstance().getSales().remove(removeSale);
        }
    }

    private void handleEditSale(Sale editSale) throws IOException {
        Functions.getInstance().loadEdit(ShopManagerController.staticBorderPane, "editSale.fxml", editSale);
    }

    @FXML
    private void handleAddSale() throws IOException {
        Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "addSale.fxml");
    }

    @FXML
    private void handleSave() throws IOException {
        if (salesTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(salesTableView, "sales/sales.xls");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Saved");
            alert.setHeaderText(null);
            alert.setContentText("Sales saved successfully.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\sales\\sales.xls"));
            }
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            handleRemoveSale();
        }
    }
}
