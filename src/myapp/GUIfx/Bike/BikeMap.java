package myapp.GUIfx.Bike;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

public class BikeMap extends Application{
    @FXML private WebView bikeMap;

    /*public BikeMap(){

    }
    */

    public void loadApp(Stage primaryStage){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("BikeMap.fxml"));
            Scene scene = new Scene(root, 700,500);
            primaryStage.setTitle("FXML Welcome");
            primaryStage.setScene(scene);
            primaryStage.show();
            URL url = getClass().getResource("https://www.google.com/");
            WebEngine engine = bikeMap.getEngine();
            engine.load(url.toExternalForm());
            engine.setJavaScriptEnabled(true);
            bikeMap.setVisible(true);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadApp(primaryStage);
    }


    public static void main(String[]args){
        launch(args);
    }
}
