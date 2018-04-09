package myapp.GUIfx;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.*;
import myapp.data.Bike;

import java.util.List;

public class BikeCell extends ListCell<Bike>{

    private HBox hbox;
    private Label bikeid;
    private Label totalTrips;
    private Label totalKm;
    private ProgressBar battery;
    private ProgressIndicator pi;


    //klasse for å lage listview
    //klasse for å lage hbox?'
    public BikeCell(){
        hbox = new HBox(100);
        bikeid = new Label();
        totalTrips = new Label();
        totalKm = new Label();
        battery = new ProgressBar();
        pi = new ProgressIndicator();
        hbox.getChildren().addAll(bikeid, totalTrips, totalKm, battery, pi);
    }

    @Override
    public void updateItem(Bike item, boolean empty){
        super.updateItem(item, empty);
        if(item != null && !empty) {
            bikeid.setText("Bicycle " + Integer.toString(item.getId()));
            totalTrips.setText(Integer.toString(item.getTotalTrips()));
            totalKm.setText(Integer.toString(item.getDistanceTraveled()));
            setGraphic(hbox);

            double batteri = item.getBatteryPercentage();
            battery.setProgress(batteri);
            if(batteri > 0.6) {
                battery.setStyle("-fx-accent: green");
            }
            else if(batteri <= 0.6 && batteri > 0.3){
                battery.setStyle("-fx-accent: #ffb812");
            }
            else{
                battery.setStyle("-fx-accent: #c40000");
            }
            pi.setProgress(item.getBatteryPercentage());
        }
    }
}
