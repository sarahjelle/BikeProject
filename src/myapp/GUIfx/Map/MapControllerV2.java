package myapp.GUIfx.Map;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import myapp.data.*;
import myapp.dbhandler.DBH;

import java.net.URL;

public class MapControllerV2 implements Runnable{
    @FXML private WebView browser;
    @FXML private AnchorPane mapPane;

    private Boolean stop = false;
    private int UPDATE_INTERVAL = 60000;

    private Bike[] bikes;

    public MapControllerV2(){
        DBH handler = new DBH();
        this.bikes = null;
        this.bikes = handler.getAllBikes().toArray(bikes);
        URL url = getClass().getResource("map.html");
        WebEngine engine = browser.getEngine();
        engine.load(url.toExternalForm());
        engine.setJavaScriptEnabled(true);
        for (int i = 0; i < bikes.length; i++) {
            addBike(bikes[i], engine);
        }
        openPane();
    }

    private void addBike(Bike bike, WebEngine engine){
        engine.getLoadWorker().stateProperty().addListener((e) -> {
            engine.executeScript("document.addBike({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                    + ", lng: " + bike.getLocation().getLongitude() + "});");
        });
    }

    private void updateBike(Bike bike, WebEngine engine){
        engine.executeScript("document.addBike({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                + ", lng: " + bike.getLocation().getLongitude() + "});");
    }

    private void startWebView(){

    }

    private void openPane(){
        mapPane.setVisible(true);
        browser.setVisible(true);
    }

    private void closePane(){
        mapPane.setVisible(false);
        browser.setVisible(false);
    }

    public void run(){
        long StartTime = System.currentTimeMillis();
        while(!stop){
            if((System.currentTimeMillis() - StartTime) >= UPDATE_INTERVAL){
                DBH handler = new DBH();
                bikes = handler.getAllBikes().toArray(bikes);
                WebEngine engine = browser.getEngine();
                for (int i = 0; i < bikes.length; i++) {
                    updateBike(bikes[i], engine);
                }
            }
        }
        closePane();
    }

    public void stop(){
        this.stop = true;
    }
}
