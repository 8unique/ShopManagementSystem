package shopms.controllers.reports;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import shopms.model.Functions;
import shopms.model.Model;
import shopms.controllers.sales.Sale;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class DueReportController {
    @FXML
    private Label totalLabel;
    @FXML
    private TableView<Sale> dueReportTableView;

    @FXML
    private void initialize() {
        double paid = 0;
        double due = 0;
        for (Sale sale : Model.getInstance().getSales()) {
            if (sale.getDue_amount() > 0) {
                dueReportTableView.getItems().add(sale);
                paid += sale.getPaid_amount();
                due += sale.getDue_amount();

            }
        }
        totalLabel.setText("Paid: Rs." + paid + " \t Due: Rs." + due);
    }

    @FXML
    private void handleSave() throws IOException {
        if (dueReportTableView.getItems().size() != 0) {
            Functions.getInstance().saveSheet(dueReportTableView, "dues/due-report.xls");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Saved");
            alert.setHeaderText(null);
            alert.setContentText("Due Report saved successfully.");

            DialogPane dialogPane = alert.getDialogPane();
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/shopms/styles/alert.css")).toExternalForm());
            dialogPane.getStyleClass().add("confirmDialog");
            alert.getButtonTypes().remove(ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\dues\\due-report.xls"));
            }
        }
    }
}
