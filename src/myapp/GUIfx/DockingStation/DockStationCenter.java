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

/**
 * DockingStationCenter is the controller class of DockingCenterPane.fxml.
 * This class contains methods to create a list of all the docking stations,
 * add a new docking station, edit/delete a station and see which bikes is at a station.
 */
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

    /**
     * openPane() is used in the appController to make the docking page visible
     * when a user wants to see it.
     */
    public void openPane(){
        refresh();
        closeAll();
        dockPane.setVisible(true);
        listPane.setVisible(true);
    }

    /**
     * closePane() closes the docking window when the user wants to go
     * to bike, map, statistic or admin.
     */
    public void closePane(){
        closeAll();
        dockPane.setVisible(false);

    }

    /**
     * Help method to close all the panes in the docking pane.
     */
    public void closeAll(){
        regDock.setVisible(false);
        dockInfo.setVisible(false);
        listPane.setVisible(false);
    }

    /**
     * Method used on cancel buttons, the method closes
     * all panes and opens the list of docking stations.
     */
    @FXML private void cancel(){
        refresh();
        closeAll();
        listPane.setVisible(true);
    }

    /**
     * The refresh() method refreshes the list of docking stations.
     * This is used so the list always is up to date with the database.
     */
    @FXML private void refresh(){
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

    /**
     * refreshBikeList() is used to refresh the list of bikes at a certain station.
     * This is used so the list always is up to date with the database.
     * @param dock the docking station where a user wants to see the docked bikes.
     */
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

    /**
     * Method to display all the information about a docking station.
     * @param dock the docking station a user wants to se the information about.
     */
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

    /**
     * selectedRow() is used when a user clicks on a row in the list of docking stations.
     * The method uses showInfo to display information about the selected docking.
     */
    @FXML private void selectedRow(){
        Docking dock  = dockingList.getSelectionModel().getSelectedItem();
        showInfo(dock);
        closeAll();
        dockInfo.setVisible(true);
    }

    /**
     * search() is used when a user wants to search for a docking station using it id.
     * The method uses showInfo to display the info about the docking.
     */
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

    /**
     * Method to go back to the infopage about a bike instead of going
     * all the way back to the list.
     */
    @FXML private void showInfoBack(){
        showInfo(dbh.getDockingByID(idTmp));
        closeAll();
        dockInfo.setVisible(true);
    }

    /**
     * Method to open the page where a user can register a new docking station
     */
    @FXML private void openRegDock(){
        addressReg.clear();
        capacityReg.clear();
        closeAll();
        regDock.setVisible(true);
    }

    /**
     * Method to check if the information the user wants to register
     * about a docking station is valid.
     * @return true if the information is valid, false if the information
     * is invalid.
     */
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

    /**
     * regDock() registers a docking station to the database, if
     * regOK() returns true.
     */
    @FXML private void regDock(){
        if(regOK()) {
            String address = addressReg.getText();
            int capacity = Integer.parseInt(capacityReg.getText().trim());
            Docking dock = new Docking(address, capacity);
            int dockId = dbh.registerDocking(dock);

            if (dockId > 0) {
                refresh();
                dw.informationWindow("Docking station was successfully added!", "DockingID: " + dockId);
                openPane();
            } else {
                dw.errorWindow("Something went wrong with the database", "Could not register docking station");
            }
        }
    }

    /**
     * deleteDocking() deletes a docking station in the user confirms the action.
     * It is only possible to delete a docking station if there is no bikes docked at the station.
     */
    @FXML private void deleteDocking(){
        boolean ok = dw.confirmWindow("Are you sure you want to delete docking station with this information? " +
                "\nID: " + idTmp +"\nAddress: " + name, "Delete docking station?");

        if(ok){
            Docking dock = dbh.getDockingByID(idTmp);

            if(dock.getUsedSpaces() == 0) {
                boolean deleted = dbh.deleteDocking(dock);

                if (deleted) {
                    //bikeList.getItems().clear();
                    refresh();
                    dw.informationWindow("Docking station were successfully deleted", "Docking station: " + idTmp);
                    openPane();
                } else {
                    dw.informationWindow("Something went wrong with the database, could not delete docking " +
                            "station", "DockingID: " + idTmp);
                }
            }
            else{
                dw.errorWindow("It is not possible to delete a docking station with docked bikes. ", "Could not delete docking station");
            }
        }
    }

    /**
     * This method opens the page where a user can edit the information about a
     * docking station.
     */
    @FXML private void openEditPane(){
        capacityInfo.setVisible(false);
        infoButtonBar.setVisible(false);
        capacityEdit.setVisible(true);
        editButtonBar.setVisible(true);
        Docking dock = dbh.getDockingByID(idTmp);
        capacityEdit.setPromptText(Integer.toString(dock.getCapacity()));
    }

    /**
     * This method updates the information about a docking station.
     * It is only possible to update the capacity.
     */
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
                    + idTmp + " to: " + capacity + "?", "Edit docking station" );

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

/**
 * DockBikeData is a class to create an Object with a Bike and an int to represent
 * the slotnumber the bike is at.
 * The class is used to fill the list of bikes at the docking station with correct in
 */
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
