package myapp.GUIfx;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.Bike;
import myapp.data.Docking;

import java.util.HashMap;

public class DockingCell extends ListCell<Docking>{
    private HBox hBox;
    private Label name;
    //private Label location;
    private Label capacity;
    private Label numberOfBikes;

    public DockingCell(){
        hBox = new HBox(100);
        name = new Label();
        name.setPrefWidth(100);
        //location = new Label();
        capacity = new Label();
        //numberOfBikes = new Label();
        hBox.getChildren().addAll(name, capacity);
    }

    @Override
    public void updateItem(Docking item, boolean empty){
        super.updateItem(item, empty);
        if(item != null && !empty){
            name.setText(item.getName());
            capacity.setText(Integer.toString(item.getCapacity()));

            /*int bikes = 0;
            HashMap<Integer, Bike> bikesAtStation = new HashMap<>(item.getCapacity());
            for(int i = 0; i < bikesAtStation.size(); i++){
                bikes = bikes + 1;
            }
            numberOfBikes.setText(Integer.toString(bikes));*/
            setGraphic(hBox);
        }
    }
}
