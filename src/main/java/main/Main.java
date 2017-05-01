package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Contacts Manager");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) throws SQLException {
        DbHelper dbHelper = DbHelper.getInstance();
        dbHelper.init();
        launch(args);

        dbHelper.close();
    }
}
