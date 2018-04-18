package myapp.GUIfx.DockingStation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import myapp.data.Bike;
import myapp.data.Docking;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DockStationCenter implements Initializable{
    @FXML private ListView dockingList;
    @FXML private BorderPane dockPane;

    //dockInfo
    @FXML private BorderPane dockInfo;
    @FXML private Label dockIdOutput;
    @FXML private Label nameOutput;
    @FXML private Label openSpaces;
    //bikelist
    @FXML private ListView<DockBikeData> bikeList;

    public void initialize(URL url, ResourceBundle rb){
        dockingList.setCellFactory(e -> new DockingCell());

        System.out.println("Initialize start");

        Docking a = new Docking(1, "Kalvskinnet", null, 100);
        Docking b = new Docking(2, "Solsiden", null, 50);
        Docking c = new Docking(3, "Moholt", null, 20);
        Docking d = new Docking(4, "Lerkendal", null, 30);

        for(int i= 0; i < 25; i++ ) {
            dockingList.getItems().addAll(a, b, c, d);
        }

        //list at the infopage
        bikeList.setCellFactory(e -> new BikeAtDock());
        //Bike bike1 = new Bike(1, 500, "DBS", "Electric", 0.2, 100);
        //Bike bike2 = new Bike(1, 500, "DBS", "Electric", 0.2, 100);

        //DockBikeData en = new DockBikeData(bike1, 1);
        //DockBikeData to = new DockBikeData(bike2, 2);

        //bikeList.getItems().addAll(en, to);
        System.out.println(bikeList.getItems().size());
    }

    public void openPane(){
        dockPane.setVisible(true);
        dockingList.setVisible(true);
    }

    public void closePane(){
        closeAll();
        dockPane.setVisible(false);

    }


    //Register new docking station
    @FXML private VBox regDock;

    @FXML private void selectedRow() {
        Docking dock = (Docking) dockingList.getItems().get(dockingList.getSelectionModel().getSelectedIndex());
        dockIdOutput.setText(Integer.toString(dock.getId()));
        nameOutput.setText(dock.getName());
        //openSpaces.setText(Integer.toString(dock.getOpenSpaces()));
        closeAll();
        dockInfo.setVisible(true);
    }

    @FXML private void openRegDock(){
        closeAll();
        regDock.setVisible(true);
    }

    public void closeAll(){
        regDock.setVisible(false);
        dockInfo.setVisible(false);
        dockingList.setVisible(false);

    }
    @FXML private void cancel(){
        closeAll();
        dockingList.setVisible(true);
    }
    @FXML private void regDock(){
        //code to register docking station
    }


}

class DockBikeData{
    private Bike bike;
    private int slotNumber;

    public DockBikeData(Bike bike, int slotNumber) {
        this.bike = bike;
        this.slotNumber = slotNumber;
    }

    public Bike getBike() {
        return bike;
    }

    public int getSlotNumber() {
        return slotNumber;
    }
}
