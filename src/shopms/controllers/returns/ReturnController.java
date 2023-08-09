package shopms.controllers.returns;

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

public class ReturnController {
    @FXML
    private GridPane returnGridPane;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Return> returnTableView;

    private ObservableList<Return> returns = Model.getInstance().getReturns();

    @FXML
    private void initialize() {
        returnTableView.getItems().setAll(Model.getInstance().getReturns());

        FilteredList<Return> purchasesFilteredList = new FilteredList<>(returns, p -> true);
        returnTableView.setItems(purchasesFilteredList);
        searchField.textProperty().addListener((obs, oldValue, newValue) ->
                purchasesFilteredList.setPredicate(p -> p.getReturn_no().toLowerCase().contains(newValue.toLowerCase().trim()))
        );
        returnTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final ContextMenu menu = new ContextMenu();

        final MenuItem editReturnMenu = new MenuItem("Edit...");
        editReturnMenu.setOnAction(event -> {
            Return editReturn = returnTableView.getSelectionModel().getSelectedItem();
            try {
                handleEditReturn(editReturn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final MenuItem printReturnMenu = new MenuItem("Print...");
        printReturnMenu.setOnAction(event -> {
            Return printReturn = returnTableView.getSelectionModel().getSelectedItem();
            try {
                handlePrintReturn(printReturn);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        });

        final MenuItem deleteReturnMenu = new MenuItem("Delete...");
        deleteReturnMenu.setOnAction(event -> {
            try {
                handleRemoveReturn();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
        editReturnMenu.disableProperty().bind(Bindings.isEmpty(returnTableView.getSelectionModel().getSelectedItems()));
        deleteReturnMenu.disableProperty().bind(Bindings.isEmpty(returnTableView.getSelectionModel().getSelectedItems()));
        printReturnMenu.disableProperty().bind(Bindings.isEmpty(returnTableView.getSelectionModel().getSelectedItems()));

        menu.getItems().addAll(editReturnMenu, printReturnMenu, deleteReturnMenu);
        returnTableView.setContextMenu(menu);
    }

    private void handlePrintReturn(Return printReturn) throws IOException, DocumentException {
        String date = printReturn.getDate();
        String return_no = printReturn.getReturn_no();
        String supplier_customer = printReturn.getSupplier_customer();
        int items = printReturn.getItems();
        int quantity = printReturn.getQuantity();
        double grand_total = printReturn.getGrand_total();

        Code128Bean code128 = new Code128Bean();
        code128.setHeight(15f);
        code128.setModuleWidth(0.3);
        code128.setQuietZone(10);
        code128.doQuietZone(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        code128.generateBarcode(canvas, return_no);
        canvas.finish();

        //write to pdf
        Image png = Image.getInstance(baos.toByteArray());

        Document document = new Document();
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        Paragraph p = new Paragraph("Date : " + date);
        p.add("\n\nReturn No : " + return_no);
        p.add("\n\nSupplier/Customer : " + supplier_customer);
        p.add("\n\nItems : " + items);
        p.add("\n\nQuantity : " + quantity);
        p.add("\n\nGrand Total : " + grand_total);
        p.add("\n----------------------------------------");
        p.add("\nReturn Details");
        p.add("\n----------------------------------------");

        for(SalesPurchasesReturns productPurchase : Model.getInstance().getSalesPurchasesReturns()) {
            for(Product product : Model.getInstance().getProducts()) {
                if(productPurchase.getCode() == product.getCode() && productPurchase.getReference().equals(printReturn.getReturn_no())) {
                    p.add("\n" + product.getName() + " [" + product.getCode() + "] X " + productPurchase.getQuantity() + "\nRs." + (productPurchase.getPrice() * productPurchase.getQuantity()));
                    p.add("\n----------------------------------------");
                }
            }
        }

        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setBorder(0);
        table.addCell(p);
        table.addCell(png);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("returns/" + return_no + "-details.pdf"));
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
            Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\returns\\" + return_no + "-details.pdf"));
        }
    }

    private void handleRemoveReturn() throws SQLException {
        Return removeReturn = returnTableView.getSelectionModel().getSelectedItem();
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Return");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to remove: " + removeReturn.getReturn_no());

        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
        dialogPane.getStyleClass().add("confirmDialog");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Model.getInstance().setQueryRemoveReturn(removeReturn.getReturn_no());
            Model.getInstance().setQueryRemoveSalesPurchaseReturn(removeReturn.getReturn_no());
            Model.getInstance().getReturns().remove(removeReturn);
            Model.getInstance().getSalesPurchasesReturns().removeIf(productPurchase -> productPurchase.getReference().equals(removeReturn.getReturn_no()));
        }
    }

    private void handleEditReturn(Return editReturn) throws IOException {
        Functions.getInstance().loadEdit(ShopManagerController.staticBorderPane, "editReturn.fxml", editReturn);
    }

    @FXML
    private void handleAddReturn() throws IOException {
        Functions.getInstance().loadAddNew(ShopManagerController.staticBorderPane, "selectReturnSaleOrPurchase.fxml");
    }

    @FXML
    private void handleSave() throws IOException {
        if (returnTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(returnTableView, "returns/returns.xls");

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
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\returns\\returns.xls"));
            }
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            handleRemoveReturn();
        }
    }
}
