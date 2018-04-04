package myapp.GUIfx;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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


    //klasse for å lage listview
    //klasse for å lage hbox?'
    public BikeCell(){
        hbox = new HBox(100);
        bikeid = new Label();
        totalTrips = new Label();
        totalKm = new Label();
        hbox.getChildren().addAll(bikeid, totalTrips, totalKm);
    }

    @Override
    public void updateItem(Bike item, boolean empty){
        super.updateItem(item, empty);
        if(item != null && !empty) {
            bikeid.setText("Bicycle " + Integer.toString(item.getId()));
            totalTrips.setText(Integer.toString(item.getTotalTrips()));
            totalKm.setText(Integer.toString(item.getDistanceTraveled()));
            setGraphic(hbox);
        }
    }
}
