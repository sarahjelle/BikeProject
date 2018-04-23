package myapp.GUIfx.DockingStation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import myapp.GUIfx.DialogWindows;
import myapp.data.Bike;
import myapp.data.Docking;
import myapp.dbhandler.DBH;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class DockStationCenter implements Initializable{
    //DBH
    DBH dbh = new DBH();
    private Docking[] stations;
    int idTmp = -1;
    String name;
    private Bike[] bikes;

    //dialogwindows
    DialogWindows dw = new DialogWindows();

    @FXML private AnchorPane dockPane;

    //Pane with list and search
    @FXML private ListView<Docking> dockingList;
    @FXML private BorderPane listPane;
    @FXML private TextField searchInput;

    //dockInfo
    @FXML private BorderPane dockInfo;
    @FXML private HBox infoButtonBar;
    @FXML private Label idInfo;
    @FXML private Label addressInfo;
    @FXML private Label capacityInfo;
    @FXML private Label openSpacesInfo;
    @FXML private Label usedSpacesInfo;
    @FXML private Label batteryInfo;

    //bikelist
    @FXML private ListView<DockBikeData> bikeList;

    //Register new docking station
    @FXML private TextField addressReg;
    @FXML private TextField capacityReg;
    @FXML private VBox regDock;

    //Edit
    @FXML private TextField capacityEdit;
    @FXML private HBox editButtonBar;


    public void initialize(URL url, ResourceBundle rb){
        dockingList.setCellFactory(e -> new DockingCell());

        refresh();

        //list at the infopage
        bikeList.setCellFactory(e -> new BikeAtDock());
    }

    public void openPane(){
        closeAll();
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
            stations = dbh.getAllDockingStationsWithBikes();

            Platform.runLater(() ->{
                for(int i = 0; i < stations.length; i++){
                    dockingList.getItems().add(stations[i]);
                }
            });
        });

        thread.start();
    }

    private void refreshBikeList(Docking dock){
        bikeList.getItems().clear();

        Thread thread = new Thread(() -> {
            bikes = dock.getBikes();
            System.out.println(Arrays.toString(bikes));
            Platform.runLater(() ->{
                for(int i = 0; i < bikes.length; i++){
                    if(bikes[i] != null && bikes[i] instanceof Bike) {
                        bikeList.getItems().add(new DockBikeData(bikes[i], i+1));
                    }
                }
                bikeList.refresh();
            });
        });


        thread.start();
    }

    private void showInfo(Docking dock){
        idTmp = dock.getId();
        name = dock.getName();
        idInfo.setText(Integer.toString(dock.getId()));
        addressInfo.setText(dock.getName());
        capacityInfo.setText(Integer.toString(dock.getCapacity()));
        openSpacesInfo.setText(Integer.toString(dock.getFreeSpaces()));
        usedSpacesInfo.setText(Integer.toString(dock.getUsedSpaces()));
        batteryInfo.setText(Double.toString(dock.getPowerUsage()));
        capacityInfo.setVisible(true);
        capacityEdit.setVisible(false);
        editButtonBar.setVisible(false);
        infoButtonBar.setVisible(true);
        refreshBikeList(dock);
    }

    @FXML private void selectedRow(){
        Docking dock = (Docking) dockingList.getItems().get(dockingList.getSelectionModel().getSelectedIndex());
        showInfo(dock);
        closeAll();
        dockInfo.setVisible(true);
    }

    @FXML private void search(){
        String dockID = searchInput.getText();
        int id = -1;

        try{
            id = Integer.parseInt(dockID);
        }catch (Exception e){
            searchInput.setPromptText("Write a number");
        }
        if(id > 0) {
            Docking dock = dbh.getDockingByID(id);
            showInfo(dock);
            closeAll();
            dockInfo.setVisible(true);
        }
    }

    @FXML private void showInfoBack(){
        showInfo(dbh.getDockingByID(idTmp));
        closeAll();
        dockInfo.setVisible(true);
    }

    @FXML private void openRegDock(){
        addressReg.clear();
        capacityReg.clear();
        closeAll();
        regDock.setVisible(true);
    }

    private boolean regOK(){
        boolean ok = true;

        if(addressReg.getText().trim().isEmpty()){
            addressReg.setPromptText("Field can not be empty");
            ok = false;
        }

        if(capacityReg.getText().trim().isEmpty()){
            capacityReg.setPromptText("Field can not be empty");
            ok = false;
        }
        else{
            try{
                double capacity = Double.parseDouble(capacityReg.getText().trim());
            }catch(Exception e){
                capacityReg.setPromptText("Value contains a non numeric character");
            }
        }

        return ok;
    }

    @FXML private void regDock(){
        if(regOK()) {
            String address = addressReg.getText();
            int capacity = Integer.parseInt(capacityReg.getText().trim());
            Docking dock = new Docking(address, capacity);
            int dockId = dbh.registerDocking(dock);

            if (dockId > 0) {
                refresh();
                dw.informationWindow("Docking station were successfully added!", "DockingID: " + dockId);
                openPane();
            } else {
                dw.errorWindow("Something went wrong with the database", "Could not register docking station");
            }
        }
    }

    @FXML private void deleteDocking(){
        boolean ok = dw.confirmWindow("Are you sure you want to delete docking station with this information? " +
                "\nID: " + idTmp +"\nAddresse: " + name, "Delete docking station?");

        if(ok){
            Docking dock = dbh.getDockingByID(idTmp);
            boolean deleted = dbh.deleteDocking(dock);

            if(deleted){
                //bikeList.getItems().clear();
                refresh();
                dw.informationWindow("Docking station were successfully deleted", "Docking station: " + idTmp);
                openPane();
            }
            else{
                dw.informationWindow("Something went wrong with the database, could not delete docking " +
                        "station", "DockingID: " + idTmp );
            }
        }
    }

    @FXML private void openEditPane(){
        capacityInfo.setVisible(false);
        infoButtonBar.setVisible(false);
        capacityEdit.setVisible(true);
        editButtonBar.setVisible(true);
        Docking dock = dbh.getDockingByID(idTmp);
        capacityEdit.setPromptText(Integer.toString(dock.getCapacity()));
    }

    @FXML private void editDock(){
        int capacity = -1;
        try{
            capacity = Integer.parseInt(capacityEdit.getText());
        }
        catch (Exception e){
            capacityEdit.setPromptText("Value has to be only numeric characters");
        }

        if(capacity > 0){
            boolean ok = dw.confirmWindow("Do you want to change capacity of docking "
                    + idTmp + "to: " + capacity + "?", "Edit docking station" );

            if(ok){
                Docking dock = dbh.getDockingByID(idTmp);
                dock.setCapacity(capacity);
                boolean edited = dbh.editDocking(dock);

                if(edited){
                    refresh();
                    dw.informationWindow("Capacity is changed to " + capacity, "Docking station: " + idTmp);
                    showInfo(dock);
                }
                else{
                    dw.informationWindow("Could not register changes", "Docking station: " + idTmp);
                }
            }
        }
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
