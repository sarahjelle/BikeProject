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
    @FXML private Label dockIdOutput;
    @FXML private Label nameOutput;
    @FXML private Label openSpaces;

    //bikelist
    @FXML private ListView<DockBikeData> bikeList;

    public void initialize(URL url, ResourceBundle rb){
        dockingList.setCellFactory(e -> new DockingCell());

        System.out.println("Initialize start");
        refresh();

        /*Docking a = new Docking(1, "Kalvskinnet", null, 100);
        Docking b = new Docking(2, "Solsiden", null, 50);
        Docking c = new Docking(3, "Moholt", null, 20);
        Docking d = new Docking(4, "Lerkendal", null, 30);

        for(int i= 0; i < 25; i++ ) {
            dockingList.getItems().addAll(a, b, c, d);
        }*/

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
