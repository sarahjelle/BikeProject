package myapp.GUIfx.DockingStation;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.Bike;

import java.util.ArrayList;

/**
 * BikeAtDock is a class to fill the rows in the list of bikes at a docking station,
 * with bikeIDs and the slotnumber the bike is at.
 *
 * @author Sara Hjelle
 */
public class BikeAtDock extends ListCell<DockBikeData>{

    private HBox hBox;
    private Label slotnumber;
    private Label bikeId;

    /**
     * The constructor creates all the Labels and adds them to the HBox.
     * The width of each Label is set, so the elements of the list is places on the same
     * space in each row.
     */
    public BikeAtDock(){
        hBox = new HBox(100);
        bikeId = new Label();
        slotnumber = new Label();
        hBox.getChildren().addAll(slotnumber,bikeId);
    }

    /**
     * This method fills a cell with the bike object, that is sent in if
     * the boolean is false. If the boolean is true the graphics is set to null.
     * @param bike the new object for the cell.
     * @param empty this is whether or not this cell represent any domain data.
     */
    @Override
    public void updateItem(DockBikeData bike, boolean empty){
        super.updateItem(bike, empty);
        if(bike != null && !empty){
            slotnumber.setText("Slotnumber: " + Integer.toString(bike.getSlotNumber()));

            if(bike.getBike() != null) {
                bikeId.setText("Bikeid: " + Integer.toString(bike.getBike().getId()));
            }

            setGraphic(hBox);
        }

        else{
            setGraphic(null);
        }
    }
}

