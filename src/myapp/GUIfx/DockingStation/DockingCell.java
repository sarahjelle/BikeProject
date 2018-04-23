package myapp.GUIfx.DockingStation;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.Bike;
import myapp.data.Docking;

import java.util.HashMap;

/**
 * DockingCell is a class to create custom ListCells in the list of docking stations.
 * The cell is composed of a HBox with Labels containg information about a docking station.
 *
 * @author Sara Hjelle
 */
public class DockingCell extends ListCell<Docking>{
    private HBox hBox;
    private Label id;
    private Label name;
    private Label capacity;
    private Label openSpaces;
    private Label bikesAtDock;

    /**
     * The constructor creates all the Labels and adds them to the HBox.
     * The width of each Label is set, so the elements of the list is places on the same
     * space in each row.
     */
    public DockingCell(){
        hBox = new HBox(50);
        id = new Label();
        id.setPrefWidth(30);
        name = new Label();
        name.setPrefWidth(300);
        capacity = new Label();
        capacity.setPrefWidth(100);
        //openSpaces = new Label();
        //openSpaces.setPrefWidth(50);
        bikesAtDock = new Label();
        bikesAtDock.setPrefWidth(100);
        hBox.getChildren().addAll(id, name, capacity, bikesAtDock);
    }

    /**
     * This method fills a cell with the Docking object, that is sent in if
     * the boolean is false. If the boolean is true the graphics is set to null.
     * @param item the new Docking object for the cell.
     * @param empty this is whether or not this cell represent any domain data.
     */
    @Override
    public void updateItem(Docking item, boolean empty){
        super.updateItem(item, empty);
        if(item != null && !empty){
            id.setText(Integer.toString(item.getId()));
            name.setText(item.getName());
            capacity.setText(Integer.toString(item.getCapacity()));
            //openSpaces.setText(Integer.toString(item.getFreeSpaces()));
            bikesAtDock.setText(Integer.toString(item.getUsedSpaces()));
            setGraphic(hBox);
        }
        else{
            setGraphic(null);
        }
    }
}
