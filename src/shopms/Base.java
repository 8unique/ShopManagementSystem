package shopms;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import shopms.model.Model;

import java.util.Objects;

public class Base extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/shopms/resources/login.fxml")));
        primaryStage.setTitle("Shop Management System");
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        if(!Model.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database.");
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Model.getInstance().close();
    }
}
