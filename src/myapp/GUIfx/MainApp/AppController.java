package myapp.GUIfx.MainApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import myapp.GUIfx.Admin.AdminController;
import myapp.GUIfx.Bike.BikeController;
import myapp.GUIfx.DockingStation.DockStationCenter;
import myapp.GUIfx.Map.MapController;
import myapp.GUIfx.Statistic.StatController2;

import myapp.data.*;
import myapp.dbhandler.*;


import java.net.URL;
import java.util.ArrayList;

/**
 * AppController is the controller of App.fxml.
 * This class contains methods to open the different panes
 * (bike, dicking station, map, statistic, admin).
 */
public class AppController {
    @FXML private BikeController bikeController;
    @FXML private DockStationCenter dockController;
    @FXML private MapController map;
    @FXML private StatController2 statController;
    @FXML private AdminController adminController;
    @FXML private WebView mapPane;

    private Updater up;
    private Thread upThread;
    private User user;

    /**
     * Set the User to the user that logged in to the application
     * @param loggedInUser the user that logged in.
     */
    public void setUser(User loggedInUser){
        user = loggedInUser;
    }

    /**
     * Opens the bike pane.
     */
    @FXML private void bike(){
        System.out.println(bikeController);
        closeAll();
        bikeController.openPane();
    }

    /**
     * Opens the docking pane
     */
    @FXML private void docking(){
        closeAll();
        dockController.openPane();
    }

    /**
     * Method to show map with bikes
     */
    @FXML private void map() {

        closeAll();
        mapPane.setVisible(true);

        if(up == null){
            up = new Updater();
        }
        if(upThread == null){
            upThread = new Thread(up);
            upThread.start();
        }
    }

    /**
     * Open the pane with different statistic
     */
    @FXML private void statistic(){
        System.out.println("Statistic");
        closeAll();
        statController.openPane();
    }

    /**
     * Opens the admin pane.
     */
    @FXML private void admin(){
        closeAll();
        adminController.openPane(user);
    }

    /**
     * This is a help method to set all the panes visibility to false,
     * to make sure another pane does not lay on top when a new is opened.
     */
    private void closeAll(){
        bikeController.closePane();
        dockController.closePane();
        statController.closePane();
        mapPane.setVisible(false);
        adminController.closePane();
    }

    class Updater implements Runnable{
        private Boolean stop = false;
        private Bike[] bikes;
        private int UPDATE_INTERVAL = 5000;
        private Docking[] lastUpdate;

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
            lastUpdate = handler.getAllDockingStationsWithBikes();
            addDocksInit(dockings, mapPane.getEngine());
            /*
            for (int i = 0; i < dockings.length; i++) {
                addDock(dockings[i], mapPane.getEngine());
            }
            */
            URL url = this.getClass().getResource("/myapp/GUIfx/Map/map.html");
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
                    Docking[] dockings = handler.getAllDockingStationsWithBikes();
                    boolean allEqualUsedSpace = true;
                    boolean noNewDocks = true;
                    if(dockings.length != lastUpdate.length){
                        noNewDocks = false;
                    }
                    for (int i = 0; i < dockings.length; i++) {
                        for (int j = 0; j < lastUpdate.length; j++) {
                            if(dockings[i].getId() == lastUpdate[j].getId()){
                                if(dockings[i].getUsedSpaces() != lastUpdate[j].getUsedSpaces()){
                                    allEqualUsedSpace = false;
                                }
                            }
                        }
                    }

                    Bike[] b = new Bike[bikesList.size()];
                    if(bikesList != null){
                        this.bikes = bikesList.toArray(b);
                    }

                    WebEngine engine = mapPane.getEngine();
                    //initMap(engine);
                    //removeAll(engine);//removeBike(bikes[i], engine);
                    updateBikes(bikes, engine);
                    if(!allEqualUsedSpace || !noNewDocks){
                        addDocks(dockings, engine);
                    }
                    StartTime = System.currentTimeMillis();
                    lastUpdate = dockings;
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
                    //id: , lat: , lng: , address: , docked:
                    engine.getLoadWorker().stateProperty().addListener((e) -> {
                        engine.executeScript("document.addDock({id: " + dock.getId() + ", lat: " + dock.getLocation().getLatitude()
                                + ", lng: " + dock.getLocation().getLongitude() + ", address: \"" + dock.getName() + "\", docked: " + dock.getUsedSpaces() + ", capasity: " + dock.getCapacity() + "});");
                    });
                });
            } catch (Exception e){

            }
        }

        public void addDocks(Docking[] docks, WebEngine engine){
            String arr = "";
            for (int i = 0; i < docks.length; i++) {
                if(i == docks.length - 1){
                    arr += "{id: " + docks[i].getId() + ", lat: " + docks[i].getLocation().getLatitude()
                            + ", lng: " + docks[i].getLocation().getLongitude() + ", address: \"" + docks[i].getName() + "\", docked: " + docks[i].getUsedSpaces() + ", capasity: " + docks[i].getCapacity() + "}";
                } else{
                    arr += "{id: " + docks[i].getId() + ", lat: " + docks[i].getLocation().getLatitude()
                            + ", lng: " + docks[i].getLocation().getLongitude() + ", address: \"" + docks[i].getName() + "\", docked: " + docks[i].getUsedSpaces() + ", capasity: " + docks[i].getCapacity() + "}, ";
                }

            }
            final String input = "[" + arr + "]";
            try{

                Platform.runLater(() -> {
                    engine.executeScript("document.addDocks(" + input + ");");
                });
                //engine.getLoadWorker().stateProperty().addListener((e) -> {});
            } catch (Exception e){

            }
        }

        public void addDocksInit(Docking[] docks, WebEngine engine){
            String arr = "";
            for (int i = 0; i < docks.length; i++) {
                if(i == docks.length - 1){
                    arr += "{id: " + docks[i].getId() + ", lat: " + docks[i].getLocation().getLatitude()
                            + ", lng: " + docks[i].getLocation().getLongitude() + ", address: \"" + docks[i].getName() + "\", docked: " + docks[i].getUsedSpaces() + ", capasity: " + docks[i].getCapacity() + "}";
                } else{
                    arr += "{id: " + docks[i].getId() + ", lat: " + docks[i].getLocation().getLatitude()
                            + ", lng: " + docks[i].getLocation().getLongitude() + ", address: \"" + docks[i].getName() + "\", docked: " + docks[i].getUsedSpaces() + ", capasity: " + docks[i].getCapacity() + "},";
                }

            }
            final String input = "[" + arr + "]";
            try{
                Platform.runLater(() -> {
                    engine.getLoadWorker().stateProperty().addListener((e) -> {
                        engine.executeScript("document.addDocks(" + input + ");");
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
