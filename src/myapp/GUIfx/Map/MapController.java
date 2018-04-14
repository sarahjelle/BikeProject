package myapp.GUIfx.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import myapp.GUIfx.Map.MapsAPI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import myapp.data.*;
import myapp.dbhandler.DBH;

import java.net.URL;
import java.time.LocalDate;
import javafx.fxml.FXML;

public class MapController extends Application{
    @FXML private WebView browser;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map");
        Button btn = new Button();
        btn.setText("Show map");
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 800, 650));
        primaryStage.show();
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                browser = new WebView();
                URL url = getClass().getResource("map.html");
                browser.getEngine().load(url.toExternalForm());
                browser.getEngine().setJavaScriptEnabled(true);
                root.getChildren().add(browser);
                //int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status
                //Bike myBike = new Bike(1, "DBS", 1000.0, "El", 100, 0, new Location("NTNU Kalvskinnet", true), 1);

                //addBike(myBike);
                primaryStage.show();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}