package myapp.GUIfx.MainApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
    @FXML private BikePaneController bikeController;
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


        try {
            URL url = getClass().getResource("../Map/map.html");
            mapPane.getEngine().load(url.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        private int UPDATE_INTERVAL = 10000;

        private Bike[] bikesTest;

        public Updater(){
            //int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status, LocalDate purchased

            String adrOne = "NTNU Kalvskinnet";
            String adrTwo = "NTNU Gløshaugen";
            String adrThree = "Bautavegen 3, 7056 Ranheim";
            String adrFour = "Solsiden Trondheim";
            bikesTest = new Bike[4];
            bikesTest[0] = new Bike(1, "Trek", 13000.0, "Elsykkel", 1.0, 100, new Location(adrOne, true), 1, LocalDate.now());
            bikesTest[1] = new Bike(2, "Merida", 2000.0, "Tereng", 1.0, 112, new Location(adrTwo, true), 1, LocalDate.now());
            bikesTest[2] = new Bike(3, "DBS", 3400.0, "Bysykkel", 1.0, 100, new Location(adrThree, true), 1, LocalDate.now());
            bikesTest[3] = new Bike(4, "Trek", 100.0, "Landevei", 1.0, 100, new Location(adrFour, true), 1, LocalDate.now());
            bikes = bikesTest;


            DBH handler = new DBH();
            ArrayList<Bike> bikesList = handler.getAllBikes();
            Bike[] b = new Bike[bikesList.size()];
            if(bikesList != null){
                this.bikes = bikesList.toArray(b);
            }

            URL url = getClass().getResource("../Map/map.html");
            mapPane.getEngine().load(url.toExternalForm());
            mapPane.getEngine().setJavaScriptEnabled(true);
            removeAll(mapPane.getEngine());
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
                    ArrayList<Bike> bikesList = handler.getAllBikes();
                    Bike[] b = new Bike[bikesList.size()];
                    if(bikesList != null){
                        this.bikes = bikesList.toArray(b);
                    }

                    WebEngine engine = mapPane.getEngine();
                    int[] array = new int[bikes.length];
                    for (int i = 0; i < bikes.length; i++) {
                        array[i] = bikes[i].getId();
                    }
                    checkID(array, engine);
                    for (int i = 0; i < bikes.length; i++) {
                        updateBike(bikes[i], engine);
                    }
                    StartTime = System.currentTimeMillis();

                }
                /*
                bikes[0].setLocation(new Location("Lade Arena", true));

                Platform.runLater(() ->{
                    updateBike(bikes[0], mapPane.getEngine());
                });


                try{
                    Thread.sleep(3000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                bikes[0].setLocation(new Location("NTNU Kalvskinnet", true));

                Platform.runLater(() ->{
                    updateBike(bikes[0], mapPane.getEngine());
                });

                try{
                    Thread.sleep(3000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                */
            }
        }

        private void stop(){
            removeAll(mapPane.getEngine());
            this.bikes = null;
            this.stop = true;
        }

        public void addBike(Bike bike, WebEngine engine){
            try{
                engine.getLoadWorker().stateProperty().addListener((e) -> {
                    engine.executeScript("document.addBike({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                            + ", lng: " + bike.getLocation().getLongitude() + "});");
                });
            } catch (Exception e){

            }
        }

        public void updateBike(Bike bike, WebEngine engine) {
            try{
                engine.executeScript("document.updateBike({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                        + ", lng: " + bike.getLocation().getLongitude() + "});");
            } catch (Exception e){

            }
        }

        public void checkID(int[] bikeID, WebEngine engine){
            try{
                String array = "{";
                for (int i = 0; i < bikeID.length; i++) {
                    array += bikeID[i] + ", ";
                }
                array += "}";
                engine.executeScript("document.checkID(" + array + ");");
            } catch(Exception e){
                e.printStackTrace();
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
