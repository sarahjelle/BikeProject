package myapp.GUIfx.Bike.BikeMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;

import myapp.data.Bike;
import myapp.dbhandler.DBH;

public class BikeMapController extends Application {
    @FXML private WebView browser;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Map");
        StackPane root = new StackPane();
        primaryStage.setScene(new Scene(root, 800, 650));
        primaryStage.show();
        browser = new WebView();
        URL url = getClass().getResource("BikeMap.html");
        browser.getEngine().load(url.toExternalForm());
        browser.getEngine().setJavaScriptEnabled(true);
        root.getChildren().add(browser);
        primaryStage.show();
        browser.getEngine().setJavaScriptEnabled(true);

        DBH handler = new DBH();
        ArrayList<Bike> bikeList = handler.getAllBikes();
        Bike[] bikes = new Bike[bikeList.size()];
        bikes = bikeList.toArray(bikes);
        addBikes(bikes, browser.getEngine());
        centerMap(bikes[95], browser.getEngine());
    }

    public void centerMap(Bike bike, WebEngine engine){
        System.out.println("Centering map");
        try{
            Platform.runLater(() -> {
                engine.getLoadWorker().stateProperty().addListener((e) -> {
                    engine.executeScript("document.centerMap({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                            + ", lng: " + bike.getLocation().getLongitude() + "});");
                });
            });
        } catch (Exception e){

        }
    }

    public void addBikes(Bike[] bikes, WebEngine engine){
        String array = "";
        for (int i = 0; i < bikes.length; i++) {
            if(i == bikes.length - 1){
                array += "{ id: " + bikes[i].getId() +
                        ", lat: " + bikes[i].getLocation().getLatitude() +
                        ", lng: " + bikes[i].getLocation().getLongitude() + "}";
            } else{
                array += "{ id: " + bikes[i].getId() +
                        ", lat: " + bikes[i].getLocation().getLatitude() +
                        ", lng: " + bikes[i].getLocation().getLongitude() + "},";
            }
        }
        final String input = "[" + array + "]";
        System.out.println(input);
        try{
            Platform.runLater(() -> {
                engine.getLoadWorker().stateProperty().addListener((e) -> {
                    engine.executeScript("document.addBikes(" + input + ");");
                });
            });
        } catch (Exception e){

        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
