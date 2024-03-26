package org.healthhaven.gui;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import javafx.fxml.FXML;

public class Main extends Application {
	
	private static Stage firstStage;

    @Override
    public void start(Stage firstStage) throws Exception{
        Main.firstStage = firstStage;
        
        try {
           loadPage("login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        firstStage.setTitle("HealthHaven Test");
        firstStage.show();
    	
        
    }
    
    protected void loadPage(String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 460);
        firstStage.setScene(scene);
        
    }
      
    public static Stage getFirstStage() {
        return firstStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    


}