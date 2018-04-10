package myapp.GUIfx.Map;
import myapp.GUIfx.Map.MapsAPI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import myapp.data.*;
import myapp.dbhandler.DBH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;

public class MapController extends Application {
    public static void main(String[] args) {
        Bike[] bikes = new Bike[2];
        //int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status
        bikes[0] = new Bike(1, "Trek", 1000.0, "Bysykkel", 1.0, 0, new Location("Munkholmen", true), 1);
        bikes[1] = new Bike(1, "Trek", 1000.0, "Bysykkel", 1.0, 0, new Location("NTNU Kalvskinnet", true), 1);
        //bikes[2] = new Bike(1, 1000.0, "Trek", "Bysykkel", 1.0, 0, new Location("Munkholmen", true));
        //bikes[3] = new Bike(1, 1000.0, "Trek", "Bysykkel", 1.0, 0, new Location("Nidarosdomen Trondheim", true));
        String positions = "";
        for (int i = 0; i < bikes.length; i++) {
            if(i == bikes.length -1){
                positions += "{lat: " + bikes[i].getLocation().getLatitude() + ", lng: " + bikes[i].getLocation().getLongitude() + "}";
            } else{
                positions += "{lat: " + bikes[i].getLocation().getLatitude() + ", lng: " + bikes[i].getLocation().getLongitude() + "},";
            }
        }
        String locations = "[" + positions + "]";
        String javascript = "var map;\n" +
                "function initMap() {\n" +
                "    var positions = " + locations + ";\n" +
                "    var cetnterPos = {lat: 63.429148, lng: 10.392461};\n" +
                "\n" +
                "    var options = {\n" +
                "        zoom: 13,\n" +
                "        center: cetnterPos\n" +
                "    };\n" +
                "\n" +
                "    map = new google.maps.Map(document.getElementById('map'), options);\n" +
                "    for (var i=0; i<positions.length;i++)\n" +
                "    {addMarker(positions[i])};\n" +
                "\n" +
                "\n" +
                "\n" +
                "    // Add marker function\n" +
                "    function addMarker(coords){\n" +
                "        var marker = new google.maps.Marker({\n" +
                "            position: coords,\n" +
                "            map: map\n" +
                "            //icon: \"Bike.png\",\n" +
                "        });\n" +
                "    }\n" +
                "}\n";
        FileWriter fWriter = null;
        BufferedWriter bWriter = null;
        try{
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();

            fWriter = new FileWriter(s + "/src/myApp/GUIfx/Map/mapFunctions.js");
            bWriter = new BufferedWriter(fWriter);
            System.out.println(javascript);
            bWriter.write(javascript);
            bWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if(bWriter != null){
                try{
                    bWriter.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            /*
            if(fWriter != null){
                try{
                    fWriter.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            */
        }
        launch(args);
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
                WebView browser = new WebView();
                root.getChildren().add(browser);
                URL url = getClass().getResource("map.html");
                browser.getEngine().load(url.toExternalForm());
                primaryStage.show();
            }
        });

    }
}