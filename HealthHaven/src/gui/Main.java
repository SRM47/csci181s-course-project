package gui;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage.setTitle("HealthHaven Test");
        stage.setScene(new Scene(root, 600, 460));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}