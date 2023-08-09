package shopms.controllers.others;

import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;
import shopms.model.Model;
import shopms.controllers.products.Product;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.Objects;
import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class PrintBarcodeController {
    @FXML
    private ListView<Product> itemListView;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private Button submitButton;

    @FXML
    private void initialize() {
        FilteredList<Product> productFilteredList = new FilteredList<>(Model.getInstance().getProducts(), p -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                    if (!newValue.isEmpty()) {
                        productFilteredList.setPredicate(p -> p.getName().toLowerCase().contains(searchField.getText().toLowerCase()) || String.valueOf(p.getCode()).contains(searchField.getText().toLowerCase()));
                        productTableView.setItems(productFilteredList);
                    } else {
                        productTableView.setItems(null);
                    }
                }
        );

        productTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        submitButton.disableProperty().bind(Bindings.isEmpty(itemListView.getItems()));

        final ContextMenu menu = new ContextMenu();
        final MenuItem addProductMenu = new MenuItem("Add...");
        addProductMenu.setOnAction(event -> {
            Product addProduct = productTableView.getSelectionModel().getSelectedItem();
            handleAddProduct(addProduct);
        });

        addProductMenu.disableProperty().bind(Bindings.isEmpty(productTableView.getSelectionModel().getSelectedItems()));
        menu.getItems().addAll(addProductMenu);
        productTableView.setContextMenu(menu);
        itemListView.setCellFactory(param -> new XCell());
    }

    private void handleAddProduct(Product addProduct) {
        if (!itemListView.getItems().contains(addProduct))
            itemListView.getItems().add(addProduct);
    }

    @FXML
    private void handleSubmit() throws DocumentException, IOException {
        boolean success = barcodeAlgorithm(itemListView);
        if (success) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Barcodes printed");
            alert.setHeaderText(null);
            alert.setContentText("Barcodes printed successfully.\nPlease check the barcodes folder.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                searchField.setText("");
                productTableView.setItems(null);
                itemListView.setItems(null);
//                submitButton.setDisable(true);
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\barcodes"));
            }
        }
    }

    static class XCell extends ListCell<Product> {
        HBox hbox = new HBox();
        Label itemName = new Label();
        Button deleteButton = new Button("DELETE");

        public XCell() {
            super();
            super.setAlignment(Pos.CENTER);

            itemName.setMaxWidth(Double.MAX_VALUE);
            hbox.setStyle("-fx-spacing: 20.0; -fx-font-size: 20px;");
            HBox.setHgrow(itemName, Priority.ALWAYS);

            hbox.getChildren().addAll(itemName, deleteButton);

            deleteButton.setOnAction(event -> getListView().getItems().remove(getItem()));
            deleteButton.setFocusTraversable(false);
        }

        @Override
        protected void updateItem(Product product, boolean empty) {
            super.updateItem(product, empty);
            setText(null);
            setGraphic(null);

            if (product != null && !empty) {
                itemName.setText(product.getName() + " (" + product.getCode() + ")");
                setGraphic(hbox);
            }
        }
    }

    private boolean barcodeAlgorithm(ListView<Product> list) throws FileNotFoundException, IOException, BadElementException, DocumentException {
        boolean printed = false;
        for (int i = 0; i < list.getItems().size(); i++) {
            String name = list.getItems().get(i).getName();
            int code = list.getItems().get(i).getCode();
            double price = list.getItems().get(i).getPrice();

            Code128Bean code128 = new Code128Bean();
            code128.setHeight(15f);
            code128.setModuleWidth(0.3);
            code128.setQuietZone(10);
            code128.doQuietZone(true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            code128.generateBarcode(canvas, String.valueOf(code));
            canvas.finish();

            //write to png file
            FileOutputStream fos = new FileOutputStream("barcodes/" + name + ".png");
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();

            //write to pdf
            Image png = Image.getInstance(baos.toByteArray());
            png.setAbsolutePosition(0, 705);
            png.scalePercent(25);

            Document document;
            document = new Document();
            PdfPTable table = new PdfPTable(3);
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            for (int aw = 0; aw < 27; aw++) {
                Paragraph p = new Paragraph("        " + name);
                p.add("\n        Rs." + price);
                PdfPTable pdfTable = new PdfPTable(1);
                pdfTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                pdfTable.addCell(p);
                pdfTable.addCell(png);
                pdfTable.getDefaultCell().setBorder(0);
                table.addCell(pdfTable);
            }
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("barcodes/" + name + ".pdf"));
            document.open();
            document.add(table);
            document.close();
            writer.close();
            printed = true;
        }
        return printed;
    }
}