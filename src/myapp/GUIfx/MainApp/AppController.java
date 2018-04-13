package myapp.GUIfx.MainApp;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import myapp.GUIfx.Bike.BikeController;
import myapp.GUIfx.Bike.BikePaneController;
import myapp.GUIfx.DockingStation.DockStationCenter;
import myapp.GUIfx.Map.MapController;
import myapp.GUIfx.Statistic.StatController2;

import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.BufferedReader;


import java.net.URL;

public class AppController {
    @FXML private BikeController bikeController;
    @FXML private DockStationCenter dockController;
    @FXML private MapController map;
    @FXML private StatController2 statController;

    @FXML private void bike(){
        System.out.println(bikeController);
        closeAll();
        bikeController.openPane();
    }

    @FXML private void docking(){
        closeAll();
        dockController.openPane();
    }

    @FXML private WebView mapPane;

    @FXML private void map() {
        closeAll();
        mapPane.setVisible(true);
        try {
            URL url = getClass().getResource("../Map/map.html");
            mapPane.getEngine().load(url.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML private void statistic(){
        System.out.println("Statistic");
        closeAll();
        statController.openPane();
    }


    private void closeAll(){
        bikeController.closeAll();
        dockController.closePane();
        statController.closePane();
        mapPane.setVisible(false);
    }
}
