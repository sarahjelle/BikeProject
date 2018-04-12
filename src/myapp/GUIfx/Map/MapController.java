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

public class MapController extends Application implements Runnable{
    private Boolean stop = false;
    @FXML private WebView browser;
    private WebEngine engine;
    private Bike[] bikes;
    private boolean updateFromDB;

    public MapController(){
        DBH handler = new DBH();
        this.bikes = new Bike[1];
        //int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status, LocalDate purchased
        bikes[0] = new Bike(1, "Trek", 100.0, "Sykkel", 100.0, 0, new Location("NTNU Kalvskinnet", true), Bike.AVAILABLE, LocalDate.now());
        //this.bikes = handler.getAllBikes().toArray(bikes);
        this.updateFromDB = true;
        URL url = getClass().getResource("map.html");
        browser.getEngine().load(url.toExternalForm());
        browser.getEngine().setJavaScriptEnabled(true);
        this.engine = browser.getEngine();
        for (int i = 0; i < bikes.length; i++) {
            addBike(bikes[i]);
        }
    }

    public MapController(Bike[] bikes){
        this.bikes = bikes;
        this.browser = new WebView();
        this.updateFromDB = false;
        URL url = getClass().getResource("map.html");
        browser.getEngine().load(url.toExternalForm());
        browser.getEngine().setJavaScriptEnabled(true);
        this.engine = browser.getEngine();
        for (int i = 0; i < bikes.length; i++) {
            addBike(bikes[i]);
        }
    }



    public void run(){
        long startTime = System.currentTimeMillis();
        while(!stop){
            for (int i = 0; i < bikes.length; i++) {
                updateBike(bikes[i]);
            }
            if(updateFromDB){
                if((System.currentTimeMillis() - startTime) <= 60000){
                    DBH handler = new DBH();
                    bikes = handler.getAllBikes().toArray(bikes);
                }
            }

        }
    }

    public void stop(){
        this.stop = true;
    }

    public WebView getWebView(){
        return browser;
    }

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
                engine = browser.getEngine();
                root.getChildren().add(browser);
                //int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status
                //Bike myBike = new Bike(1, "DBS", 1000.0, "El", 100, 0, new Location("NTNU Kalvskinnet", true), 1);

                //addBike(myBike);
                primaryStage.show();
            }
        });
    }

    public void addBike(Bike bike){
        engine.getLoadWorker().stateProperty().addListener((e) -> {
            engine.executeScript("document.addBike({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                    + ", lng: " + bike.getLocation().getLongitude() + "});");
        });
    }

    public void updateBike(Bike bike) {
        engine.executeScript("document.addBike({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                + ", lng: " + bike.getLocation().getLongitude() + "});");
    }

    public static void main(String[] args) {
        launch(args);
    }
}