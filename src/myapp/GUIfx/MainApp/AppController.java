package myapp.GUIfx.MainApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import myapp.GUIfx.Bike.BikeController;
import myapp.GUIfx.Bike.BikePaneController;
import myapp.GUIfx.DockingStation.DockStationCenter;
import myapp.GUIfx.Map.MapController;
import myapp.GUIfx.Statistic.StatController2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.BufferedReader;
import myapp.data.*;
import myapp.dbhandler.*;


import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

public class AppController {
    @FXML private BikeController bikeController;
    @FXML private DockStationCenter dockController;
    @FXML private MapController map;
    @FXML private StatController2 statController;

    private Updater up;
    private Thread upThread;

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
        //URL url = getClass().getResource("../Map/map.html");
        //mapPane.getEngine().load(url.toExternalForm());

        if(up == null){
            up = new Updater();
        }
        if(upThread == null){
            upThread = new Thread(up);
            upThread.start();
        }
    }


    @FXML private void statistic(){
        System.out.println("Statistic");
        closeAll();
        statController.openPane();
    }


    private void closeAll(){
        bikeController.closePane();
        dockController.closePane();
        statController.closePane();
        mapPane.setVisible(false);
    }

    class Updater implements Runnable{
        private Boolean stop = false;
        private Bike[] bikes;
        private int UPDATE_INTERVAL = 5000;

        private Bike[] bikesTest;

        public Updater(){
            //Comment
            DBH handler = new DBH();
            ArrayList<Bike> bikesList = handler.getLoggedBikes();
            if(bikesList != null){
                Bike[] b = new Bike[bikesList.size()];
                this.bikes = bikesList.toArray(b);
            }

            Docking[] dockings = handler.getAllDockingStationsWithBikes();
            for (int i = 0; i < dockings.length; i++) {
                addDock(dockings[i], mapPane.getEngine());
            }
            URL url = getClass().getResource("../Map/map.html");
            mapPane.getEngine().load(url.toExternalForm());
            mapPane.getEngine().setJavaScriptEnabled(true);
            //removeAll(mapPane.getEngine());
            WebEngine engine = mapPane.getEngine();
            for (int i = 0; i < bikes.length; i++) {
                addBike(bikes[i], engine);
            }
        }

        public void run(){
            long StartTime = System.currentTimeMillis();
            while(!stop){
                try{
                    Thread.sleep(UPDATE_INTERVAL);
                } catch(Exception e){
                    e.printStackTrace();
                }
                if((System.currentTimeMillis() - StartTime) >= UPDATE_INTERVAL){
                    DBH handler = new DBH();
                    ArrayList<Bike> bikesList = handler.getLoggedBikes();//handler.getAllBikes();
                    //Docking[] dockings = handler.getAllDockingStationsWithBikes();
                    Bike[] b = new Bike[bikesList.size()];
                    if(bikesList != null){
                        this.bikes = bikesList.toArray(b);
                    }

                    WebEngine engine = mapPane.getEngine();
                    //initMap(engine);
                    //removeAll(engine);//removeBike(bikes[i], engine);
                    updateBikes(bikes, engine);
                    StartTime = System.currentTimeMillis();
                }
            }
        }

        private void stop(){
            removeAll(mapPane.getEngine());
            this.bikes = null;
            this.stop = true;
        }

        public void addDock(Docking dock, WebEngine engine){
            try{
                Platform.runLater(() -> {
                    engine.getLoadWorker().stateProperty().addListener((e) -> {
                        engine.executeScript("document.addDock({id: " + dock.getId() + ", lat: " + dock.getLocation().getLatitude()
                                + ", lng: " + dock.getLocation().getLongitude() + "});");
                    });
                });
            } catch (Exception e){

            }
        }

        public void addBike(Bike bike, WebEngine engine){
            try{
                Platform.runLater(() ->{
                    engine.getLoadWorker().stateProperty().addListener((e) -> {
                        engine.executeScript("document.addBike({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                                + ", lng: " + bike.getLocation().getLongitude() + "});");
                    });
                });
            } catch (Exception e){

            }
        }

        public void updateBikes(Bike[] bikesArr, WebEngine engine){
            try{
                String arr = "";
                for (int i = 0; i < bikesArr.length; i++) {
                    if(i == bikesArr.length -1){
                        arr += "{id: " + bikesArr[i].getId() + ", lat: " +
                                bikesArr[i].getLocation().getLatitude() + ", lng: " + bikesArr[i].getLocation().getLongitude() + "}";
                    } else{
                        arr += "{id: " + bikesArr[i].getId() + ", lat: " +
                                bikesArr[i].getLocation().getLatitude() + ", lng: " + bikesArr[i].getLocation().getLongitude() + "}, ";
                    }

                }
                final String totalArr = "[" + arr + "]";
                Platform.runLater(() ->{
                    engine.executeScript("document.allBikes(" + totalArr + ");");
                });
            } catch (Exception e){

            }
        }

        public void removeAll(WebEngine engine){
            try{
                Platform.runLater(() ->{
                    engine.executeScript("document.removeAll();");
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
