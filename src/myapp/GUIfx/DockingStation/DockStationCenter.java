package myapp.GUIfx.DockingStation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import myapp.GUIfx.Bike.BikeData;
import myapp.data.Bike;
import myapp.data.Docking;
import myapp.dbhandler.DBH;

import java.awt.image.AreaAveragingScaleFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DockStationCenter implements Initializable{
    //DBH
    DBH dbh = new DBH();
    private ArrayList<Docking> stations;
    int idTmp = -1;
    private Bike[] bikes;

    @FXML private AnchorPane dockPane;

    //Pane with list and search
    @FXML private ListView dockingList;
    @FXML private BorderPane listPane;

    //dockInfo
    @FXML private BorderPane dockInfo;
    @FXML private Label idInfo;
    @FXML private Label addressInfo;
    @FXML private Label capacityInfo;
    @FXML private Label openSpacesInfo;
    @FXML private Label usedSpacesInfo;
    @FXML private Label batteryInfo;

    //bikelist
    @FXML private ListView<DockBikeData> bikeList;

    public void initialize(URL url, ResourceBundle rb){
        dockingList.setCellFactory(e -> new DockingCell());

        System.out.println("Initialize start");
        refresh();

        //list at the infopage
        bikeList.setCellFactory(e -> new BikeAtDock());
    }

    public void openPane(){
        dockPane.setVisible(true);
        listPane.setVisible(true);
    }

    public void closePane(){
        closeAll();
        dockPane.setVisible(false);

    }

    public void closeAll(){
        regDock.setVisible(false);
        dockInfo.setVisible(false);
        listPane.setVisible(false);

    }
    @FXML private void cancel(){
        closeAll();
        listPane.setVisible(true);
    }

    //refresh information from database
    private void refresh(){
        dockingList.getItems().clear();
        Thread thread = new Thread(() -> {
            stations = dbh.getAllDockingStations();

            Platform.runLater(() ->{
                for(int i = 0; i < stations.size(); i++){
                    dockingList.getItems().add(stations.get(i));
                    refreshBikeList(stations.get(i));
                }
            });
        });

        thread.start();
    }

    private void refreshBikeList(Docking dock){
        bikeList.getItems().clear();
        Thread thread = new Thread(() -> {
            bikes = dock.getBikes();

            Platform.runLater(() ->{
                for(int i = 0; i < bikes.length; i++){
                    if(bikes[i] != null && bikes[i] instanceof Bike) {
                        bikeList.getItems().add(new DockBikeData(bikes[i], i));
                    }
                }
            });
        });

        thread.start();
    }


    //Register new docking station
    @FXML private VBox regDock;

    private void showInfo(Docking dock){
        idInfo.setText(Integer.toString(dock.getId()));
        addressInfo.setText(dock.getName());
        capacityInfo.setText(Integer.toString(dock.getCapacity()));
        openSpacesInfo.setText(Integer.toString(dock.getFreeSpaces()));
        usedSpacesInfo.setText(Integer.toString(dock.getUsedSpaces()));
        batteryInfo.setText(Double.toString(dock.getPowerUsage()));
    }

    @FXML private void selectedRow() {
        Docking dock = (Docking) dockingList.getItems().get(dockingList.getSelectionModel().getSelectedIndex());
        showInfo(dock);
        closeAll();
        dockInfo.setVisible(true);
    }

    @FXML private void openRegDock(){
        closeAll();
        regDock.setVisible(true);
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
