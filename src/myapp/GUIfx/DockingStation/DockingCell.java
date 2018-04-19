package myapp.GUIfx.DockingStation;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.Bike;
import myapp.data.Docking;

import java.util.HashMap;

public class DockingCell extends ListCell<Docking>{
    private HBox hBox;
    private Label id;
    private Label name;
    private Label capacity;
    private Label openSpaces;
    private Label bikesAtDock;

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
    }
}
