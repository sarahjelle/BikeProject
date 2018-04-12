package myapp.GUIfx.DockingStation;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.Bike;

import java.util.ArrayList;

public class BikeAtDock extends ListCell<DockBikeData>{

    private HBox hBox;
    private Label slotnumber;
    private Label bikeId;

    public BikeAtDock(){
        hBox = new HBox(100);
        bikeId = new Label();
        slotnumber = new Label();
        hBox.getChildren().addAll(slotnumber,bikeId);
    }

    @Override
    public void updateItem(DockBikeData bike, boolean empty){
        super.updateItem(bike, empty);
        if(bike != null && !empty){
            slotnumber.setText("Slotnumber: " + Integer.toString(bike.getSlotNumber()));
            bikeId.setText("Bikeid: " + Integer.toString(bike.getBike().getId()));
            setGraphic(hBox);
        }
    }
}

