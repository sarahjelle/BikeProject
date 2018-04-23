package myapp.GUIfx.Bike;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.*;
import myapp.data.Bike;

import java.util.List;

/**
 * The BikeCell class is used create a ListCell for the list of bikes.
 * The ListCell is composed of a HBox containing Labels for bikeID, type, make and
 * a progressBar to show the battery percentage.
 *
 * @author Sara Hjelle
 */
public class BikeCell extends ListCell<Bike>{

    private HBox hbox;
    private Label bikeid;
    private Label type;
    private Label make;
    private ProgressBar battery;
    private ProgressIndicator pi;

    /**
     * The constructor creates all the labels and adds them to the HBox.
     * The width is preset to make all the cells the same size.
     */
    public BikeCell(){
        hbox = new HBox(50);
        bikeid = new Label();
        bikeid.setPrefWidth(100);
        type = new Label();
        type.setPrefWidth(100);
        make = new Label();
        make.setPrefWidth(100);
        battery = new ProgressBar();
        battery.setPrefWidth(100);
        //pi = new ProgressIndicator();
        hbox.getChildren().addAll(bikeid, type, make, battery);
    }

    /**
     * This method fills a cell with the bike object, if the boolean is true.
     * This is were all the labels gets a value.
     * @param item the Bike object to be filled in the cell.
     * @param empty this is whether or not the cell represent any domain data.
     *              if the boolean is true, this cell can be filled.
     */
    @Override
    public void updateItem(Bike item, boolean empty){
        super.updateItem(item, empty);
        if(item != null && !empty) {
            bikeid.setText("Bicycle " + Integer.toString(item.getId()));
            type.setText(item.getType());
            make.setText(item.getMake());

            double batteri = item.getBatteryPercentage();
            battery.setProgress(batteri);
            battery.setAccessibleText(Double.toString(item.getBatteryPercentage()));
            if(batteri > 0.6) {
                battery.setStyle("-fx-accent: green");
            }
            else if(batteri <= 0.6 && batteri > 0.3){
                battery.setStyle("-fx-accent: #ffb812");
            }
            else{
                battery.setStyle("-fx-accent: #c40000");
            }
            //  pi.setProgress(item.getBatteryPercentage());

            setGraphic(hbox);
        }
    }
}
