package myapp.GUIfx.Bike;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static myapp.data.Bike.*;

import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import myapp.GUIfx.DialogWindows;
import  myapp.data.Bike;
import myapp.data.Docking;
import myapp.data.Repair;
import myapp.dbhandler.DBH;

import javax.print.Doc;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;




public class BikeController implements Initializable {
    //DBH
    DBH dbh = new DBH();
    ArrayList<Bike> bikes = dbh.getAllBikes();
    int id = -1;
    private String makeTmp;
    private String typeTmp;
    private double priceTmp;
    private LocalDate dateTmp;
    private double batteryTmp;
    private int distanceTmp;

    private DialogWindows dw = new DialogWindows();

    //listview
    @FXML
    private AnchorPane testbikeremoveme;
    @FXML
    private BorderPane listPane;
    @FXML
    private ListView<Bike> bikeList;
    @FXML
    private TextField searchInput;


    //info, edit, repair
    @FXML
    private BorderPane infoEditRepair;

    //info
    @FXML
    private SplitPane bikeInfo;
    @FXML
    private Label idOutput;
    @FXML
    private Label typeInfo;
    @FXML
    private Label makeInfo;
    @FXML
    private Label priceInfo;
    @FXML
    private Label dateInfo;
    @FXML
    private Label batteryInfo;
    @FXML
    private Label statusInfo;
    @FXML
    private Label distanceInfo;
    @FXML private Label tripInfo;

    //List of repairs
    @FXML
    private ListView<Repair> repairList;
    private Repair[] repairs;

    //info about a repair
    @FXML private BorderPane repairListPane;
    @FXML private BorderPane repairInfoPane;
    @FXML private Text dateSentRepInfo;
    @FXML private Text descBeforeInfo;
    @FXML private Text dateReceivedInfo;
    @FXML private Text priceRepairInfo;
    @FXML private Text descAfterInfo;
    @FXML private Text statusRepairInfo;
    @FXML private Button finishRepair;

    //repair
    @FXML
    private BorderPane repairPaneBefore;
    @FXML
    private BorderPane repairPaneAfter;
    @FXML
    private DatePicker dateSent;
    @FXML
    private TextArea descriptionBefore;
    @FXML
    private DatePicker dateReturn;
    @FXML
    private TextField priceRepair;
    @FXML
    private TextArea descriptionDone;

    //Edit
    @FXML
    private BorderPane editPane;
    @FXML
    private ComboBox<String> typeEdit;
    @FXML
    private TextField makeEdit;
    @FXML
    private TextField priceEdit;
    @FXML
    private DatePicker dateEdit;

    //Register new bike:
    @FXML
    private BorderPane registerPane;
    @FXML
    private ComboBox<String> typeReg;
    @FXML private ComboBox<String> locationReg;
    @FXML private DatePicker dateReg;
    @FXML
    private TextField priceReg;
    @FXML
    private TextField makeReg;

    //show map
    @FXML private WebView browser;
    private BikeUpdater bu;
    private Thread buThread;


    public void initialize(URL url, ResourceBundle rb) {
        bikeList.setCellFactory(e -> new BikeCell());
        repairList.setCellFactory(e -> new RepairCell());
        refresh();
    }

    @FXML
    private void refresh() {
        bikeList.getItems().clear();
        Thread thread = new Thread(() -> {
            bikes = dbh.getAllBikes();

            Platform.runLater(() -> {
                for (int i = 0; i < bikes.size(); i++) {
                    if(bikes.get(i).getStatus() != Bike.DELETE) {
                        bikeList.getItems().add(bikes.get(i));
                    }
                }
            });
        });

        thread.start();
    }


    @FXML
    private void refreshRepair(Bike bike) {
        repairList.getItems().clear();

        Thread thread = new Thread(() -> {
            //repairs = bike.getRepairs();
            repairs = dbh.getAllRepairsForBike(bike.getId());
            Platform.runLater(() -> {
                for (int i = 0; i < repairs.length; i++) {
                    repairList.getItems().add(repairs[i]);
                }
            });
        });

        thread.start();
    }

    public void setId(int id){
        this.id = id;
    }

    //methodes for all panes
    public void openPane() {
        refresh();
        closeAll();
        testbikeremoveme.setVisible(true);
        listPane.setVisible(true);
    }

    public void closePane() {
        testbikeremoveme.setVisible(false);
    }

    public void closeAll() {
        //refresh();
        //closePane();
        searchInput.clear();
        searchInput.setPromptText("Bikeid");
        listPane.setVisible(false);
        infoEditRepair.setVisible(false);
        bikeInfo.setVisible(false);
        editPane.setVisible(false);
        repairPaneBefore.setVisible(false);
        repairPaneAfter.setVisible(false);
        registerPane.setVisible(false);
        browser.setVisible(false);
    }

    @FXML
    private void cancel() {
        //refresh();
        closeAll();
        listPane.setVisible(true);
    }

    //methods for info
    public Bike findBike(int bikeId) {
        /*for(int i = 0; i < bikes.size(); i++){
            if(bikeId == bikes.get(i).getId()){
                return bikes.get(i);
            }
        }
        return null;*/

        ObservableList<Bike> bike = bikeList.getItems();
        for (int i = 0; i < bike.size(); i++) {
            if (bikeId == bike.get(i).getId()) {
                return bike.get(i);
            }
        }

        return null;
    }

    @FXML
    private void selectedRow() {
        Bike bike = (Bike) bikeList.getItems().get(bikeList.getSelectionModel().getSelectedIndex());
        showInfo(bike);
    }

    @FXML
    private void search() {
        String bikeID = searchInput.getText();
        int bikeId = -1;
        try {
            bikeId = Integer.parseInt(bikeID);
        } catch (Exception e) {
            searchInput.setText("Write a number");
            searchInput.setStyle("-fx-text-fill: red");
        }

        if (bikeId > 0) {
            Bike bike = findBike(bikeId);
            if (bike instanceof Bike && bike != null) {
                showInfo(bike);
            }
        }
    }

    @FXML
    private void showInfo(Bike bike) {
        //Update attributes for bike choosen
        id = bike.getId();
        makeTmp = bike.getMake();
        typeTmp = bike.getType();
        priceTmp = bike.getPrice();
        dateTmp = bike.getPurchased();

        closeAll();
        infoEditRepair.setVisible(true);
        bikeInfo.setVisible(true);

        idOutput.setText(Integer.toString(id));
        typeInfo.setText(typeTmp);
        makeInfo.setText(makeTmp);
        priceInfo.setText(Double.toString(priceTmp));
        dateInfo.setText(dateTmp.toString());

        double battery = bike.getBatteryPercentage()*100;
        batteryInfo.setText(Double.toString(battery) + "%");
        distanceInfo.setText(Integer.toString(bike.getDistanceTraveled()));
        //tripInfo.setText(Integer.toString(bike.getTotalTrips()));

        refreshRepair(bike);


        if (!getStatus(bike).equals("")) {
            statusInfo.setText(getStatus(bike));
        }

        URL url = getClass().getResource("../Bike/BikeMap/BikeMap.html");
        browser.getEngine().load(url.toExternalForm());
        browser.getEngine().setJavaScriptEnabled(true);
        browser.setVisible(true);


        if(bu == null){
            bu = new BikeUpdater(bike);
        } else{
            bu.setCenterBike(bike);
        }

        if(buThread == null){
            buThread = new Thread(bu);
            buThread.start();
        }
    }

    private String getStatus(Bike bike) {
        int status = bike.getStatus();
        String res = "";

        switch (status) {
            case 1:
                res = "Available";
                break;
            case 2:
                res = "Rented";
                break;
            case 3:
                res = "On repair";
                break;
            case 4:
                res = "Deleted";
                break;
        }
        return res;
    }

    @FXML
    private void showInfoBack() {
        //refresh();
        Bike bike = findBike(id);
        showInfo(bike);
    }


    @FXML private void showRepair(){
        Repair repair = repairList.getSelectionModel().getSelectedItem();
        dateSentRepInfo.setText(repair.getRequestDate().toString());
        descBeforeInfo.setText(repair.getDesc());
        if(repair.getStatus()){
            priceRepairInfo.setText(Double.toString(repair.getPrice()));
            descAfterInfo.setText(repair.getReturnDesc());
            dateReceivedInfo.setText(repair.getRequestDate().toString());
            statusRepairInfo.setText("Finished");
        }

        else{
            priceRepairInfo.setText("");
            descAfterInfo.setText("");
            dateReceivedInfo.setText("");
            statusRepairInfo.setText("Not finished");
            finishRepair.setVisible(true);
        }

        repairListPane.setVisible(false);
        repairInfoPane.setVisible(true);


    }

    @FXML private void showAllRepairs(){
        repairListPane.setVisible(true);
        repairInfoPane.setVisible(false);
    }

    //methods for repair
    @FXML
    private void openRepairPane() {
        closeAll();
        infoEditRepair.setVisible(true);

        Bike bike = findBike(id);

        if (bike.getStatus() == Bike.REPAIR) {
            descriptionDone.clear();
            priceReg.clear();
            dateReturn.setValue(null);

            repairPaneAfter.setVisible(true);
        }
        else if(bike.getStatus() == Bike.AVAILABLE){
            descriptionBefore.clear();
            dateSent.setValue(null);
            repairPaneBefore.setVisible(true);
        }
        else{
            dw.errorWindow("It's only possible to register a repair request when the bike " +
                    "is at a docking station. Check Status", "BikeID: " + bike.getId() + "not available");
            showInfoBack();
        }
    }

    //Register repair before sent
    @FXML
    private void beforeRepair() {
        LocalDate date = dateSent.getValue();
        String description = descriptionBefore.getText();
        Bike bike = findBike(id);

        boolean ok = bike.addRepairRequest(description, date);

        if(ok){
            refresh();
            dw.informationWindow("Repair request were added: " +
                    "\n", "Repair is added");
            showInfo(findBike(id));
        }
        else{
            dw.informationWindow("Could not add repair request to the database", "BikeID: " + id);
        }
    }

    //Register repair on return
    @FXML
    private void afterRepair() {
        Bike bike = findBike(id);
        LocalDate date = dateReturn.getValue();
        String priceInput = priceRepair.getText();
        String description = descriptionDone.getText();
        int price = -1;
        try {
            price = Integer.parseInt(priceInput);
        } catch (Exception e) {
            priceRepair.setText("Write a number");
        }

        if (price >= 0) {
            boolean registered = bike.finishLastRepairRequest(description, date, price);
            if(registered){
                refresh();
                dw.informationWindow("Repair request were sucsesfully added! \n" +
                        "The status is now available, and the bike is ready to be rented", "BikeID: " + id);
                showInfoBack();
            }
            else{
                dw.informationWindow("Could not add repair request to the database", "BikeID: " + id);
            }
        }
    }

    //methodes for edit
    @FXML
    private void openEditPane() {
        Bike bike = findBike(id);

        typeEdit.getItems().clear();
        ArrayList<String> types = getTypes();
        for (int i = 0; i < types.size(); i++) {
            typeEdit.getItems().add(types.get(i));
        }
        typeEdit.setValue(typeTmp);
        makeEdit.setText(makeTmp);
        priceEdit.setText(Double.toString(priceTmp));
        dateEdit.setValue(dateTmp);

        closeAll();
        infoEditRepair.setVisible(true);
        editPane.setVisible(true);
    }

    @FXML private void edit(){
        if(!makeEdit.getText().trim().isEmpty()){
            makeTmp = makeEdit.getText().trim();
        }

        if(typeEdit.getSelectionModel().getSelectedItem() != null){
            typeTmp = typeEdit.getSelectionModel().getSelectedItem();
        }

        if(!priceEdit.getText().trim().isEmpty()) {
            try{
                priceTmp = Double.parseDouble(priceEdit.getText().trim());
            }catch (Exception e){
                priceEdit.setPromptText("Value contains non numeric character");
            }
        }

        if(dateEdit.getValue() != null){
            dateTmp = dateEdit.getValue();
        }

        boolean ok = dw.confirmWindow("Confirm that this is the values you want the bike to have \nBikeid: " + id +
                "\nMake: " + makeTmp + "\nType: " + typeTmp + "\nPrice: " + priceTmp
                + "\nDate: " + dateTmp.toString(),
                "Confirm new information");
        if(ok){
            Bike bike = findBike(id);
            bike.setType(typeTmp);
            bike.setMake(makeTmp);
            bike.setPrice(priceTmp);
            bike.setPurchased(dateTmp);

            boolean updated = dbh.updateBike(bike);

            if(updated){
                refresh();
                dw.informationWindow("Bike is now updated", "Update information");
                showInfoBack();
            }
            else{
                dw.errorWindow("Could not register in the database", "Error");
                refresh();
                showInfoBack();
            }
        }
    }


    private ArrayList<String> getTypes() {
        //return dbh.getTypes();
        ArrayList<String> types = dbh.getBikeTypes();
        return types;
    }

    @FXML private void newType(){
        String newType = dw.inputDialog("Type name: ", "New type");

        if(newType != null){
            if(dbh.addBikeType(newType)){
                typeEdit.getItems().add(newType);
                typeReg.getItems().add(newType);
                dw.informationWindow("New type added to the list", "New type");
            }
            else{
                dw.errorWindow("Could not add new type", "New type");
            }

        }

    }

    @FXML private void deleteType(){
        ArrayList<String> types = getTypes();
        String typeToDelete = dw.choiceDialog("Which type do you want to delete? ", "Delete type", types);

        if(typeToDelete != null){
            if(dw.confirmWindow("Are you sure you want to delete " + typeToDelete + " from the list? ", "Delete type? ")){
                dbh.deleteBikeType(typeToDelete);
                typeReg.getItems().remove(typeToDelete);
                typeEdit.getItems().remove(typeToDelete);
            }
        }
    }


    //methods for bike registration
    private ArrayList<String> getDockingStationNames(){
        ArrayList<String> stationNames = new ArrayList<>();
        Docking[] stations = dbh.getAllDockingStationsWithBikes();

        for(int i = 0; i < stations.length; i++){
            if(stations[i].getFreeSpaces() > 0) {
                stationNames.add(stations[i].getName());
            }
        }

        return stationNames;
    }
    @FXML
    private void openRegBike() {
        typeReg.getItems().clear();
        locationReg.getItems().clear();

        dateReg.setValue(null);
        makeReg.clear();
        priceReg.clear();

        ArrayList<String> types = getTypes();
        for (int i = 0; i < types.size(); i++) {
            typeReg.getItems().add(types.get(i));
        }

        ArrayList<String> locations = getDockingStationNames();
        for(int i = 0; i < locations.size(); i++){
            locationReg.getItems().add(locations.get(i));
        }

        closeAll();
        registerPane.setVisible(true);
    }

    @FXML
    private boolean regBikeOk() {
        boolean ok = true;

        if (makeReg.getText().trim().isEmpty()) {
            makeReg.setPromptText("Can't be empty");
            ok = false;
        }

        if (typeReg.getSelectionModel().getSelectedItem() == null) {
            typeReg.setValue("Choose a type");

            ok = false;
        }

        if (dateReg.getValue() == null) {
            dateReg.setValue(LocalDate.now());
            ok = false;
        }

        if (priceReg.getText().trim().isEmpty()) {
            priceReg.setPromptText("Can't be empty");
            ok = false;
        } else {
            try {
                double price = Double.parseDouble(priceReg.getText());
            } catch (Exception e) {
                ok = false;
                priceReg.setPromptText("Only numeric characters");
            }
        }

        if(locationReg.getSelectionModel().getSelectedItem() == null){
            locationReg.setValue("Choose a location");
            ok = false;
        }
        return ok;
    }

    @FXML
    private void regBike() {
        if (regBikeOk()) {
            Double price = Double.parseDouble(priceReg.getText());
            LocalDate date = dateReg.getValue();
            String type = typeReg.getSelectionModel().getSelectedItem();
            String make = makeReg.getText();
            Docking dock = dbh.getDockingStationByName(locationReg.getSelectionModel().getSelectedItem());

            Bike bike = new Bike(price, date, type, make);
            int id = dbh.registerBike(bike);
            if (id > 0) {
                bike = dbh.getBikeByID(id);
                if(dock.dockBikeWihtoutRent(bike)) {
                    //refresh();
                    dw.informationWindow("Bike were succsesfully added to the database!", "BikeID: " + id);
                    //showInfo(bike);
                    openPane();
                } else {
                    dbh.deleteBike(id);
                    dw.errorWindow("Could not register bike. Something went wrong in the database", "Registration error");
                    openPane();
                }
            }
        }
    }

    //methods for deleting bikes
    @FXML
    private void deleteBike() {
        Bike bike = findBike(id);
        boolean ok = dw.confirmWindow("Are you sure you want to delete bike nr. "
                + id + "?", "Delete bike?");
        if (ok) {
            boolean deleted = dbh.deleteBike(bike);
            if(deleted){
                dw.informationWindow("Bike nr. " + id + " were sucsessfully deleted","Deleted bike" );
                closeAll();
                openPane();
            }
            else{
                dw.errorWindow("Bike is not deleted, error in database", "Error with deleting");
            }
        }
    }

    class BikeUpdater implements Runnable{
        private Boolean stop = false;
        private int UPDATE_INTERVAL = 500;
        private int DB_UPDATE_INTERVAL = 5000;
        private Bike centerBike;

        public BikeUpdater(Bike centerBike){
            this.centerBike = centerBike;
        }

        public void setCenterBike(Bike bike){
            this.centerBike = bike;
        }

        public void run(){
            long StartTime = System.currentTimeMillis();
            Docking[] d = null;
            Bike[] in = null;
            while(!stop){
                if((System.currentTimeMillis() - StartTime) >= DB_UPDATE_INTERVAL || d == null || in == null){
                    DBH handler = new DBH();
                    //Bike[] b = handler.getAllBikesOnTrip();

                    ArrayList<Bike> inList = handler.getLoggedBikes();
                    in = new Bike[inList.size()];
                    in = inList.toArray(in);
                    for (int i = 0; i < in.length; i++) {
                        if(in[i].getId() == centerBike.getId()){
                            centerBike = in[i];
                            break;
                        }
                    }

                    ArrayList<Docking> dList = handler.getAllDockingStations();
                    d = new Docking[dList.size()];
                    d = dList.toArray(d);
                    StartTime = System.currentTimeMillis();
                }

                addDockings(d, browser.getEngine());

                if(in != null){
                    addBikes(in, browser.getEngine());
                }
                //Bike[] subset = {centerBike};
                addBikes(in, browser.getEngine());
                centerMap(centerBike, browser.getEngine());
                try{
                    Thread.sleep(UPDATE_INTERVAL);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        public void stop(){
            this.stop = true;
        }

        private void centerMap(Bike bike, WebEngine engine){
            try{
                Platform.runLater(() -> {
                    engine.executeScript("document.centerMap({id: " + bike.getId() + ", lat: " + bike.getLocation().getLatitude()
                            + ", lng: " + bike.getLocation().getLongitude() + "});");
                    //engine.getLoadWorker().stateProperty().addListener((e) -> {});
                });
            } catch (Exception e){

            }
        }

        private void addBikes(Bike[] bikes, WebEngine engine){
            String array = "";
            for (int i = 0; i < bikes.length; i++) {
                if(i == bikes.length - 1){
                    array += "{ id: " + bikes[i].getId() +
                            ", lat: " + bikes[i].getLocation().getLatitude() +
                            ", lng: " + bikes[i].getLocation().getLongitude() + "}";
                } else{
                    array += "{ id: " + bikes[i].getId() +
                            ", lat: " + bikes[i].getLocation().getLatitude() +
                            ", lng: " + bikes[i].getLocation().getLongitude() + "},";
                }
            }
            final String input = "[" + array + "]";
            try{
                Platform.runLater(() -> {
                    engine.getLoadWorker().stateProperty().addListener((e) -> {
                        engine.executeScript("document.addBikes(" + input + ");");
                    });
                });
            } catch (Exception e){

            }
        }

        private void addDockings(Docking[] dockings, WebEngine engine){
            String array = "";
            for (int i = 0; i < dockings.length; i++) {
                if(i == dockings.length - 1){
                    array += "{ id: " + dockings[i].getId() +
                            ", lat: " + dockings[i].getLocation().getLatitude() +
                            ", lng: " + dockings[i].getLocation().getLongitude() + "}";
                } else{
                    array += "{ id: " + dockings[i].getId() +
                            ", lat: " + dockings[i].getLocation().getLatitude() +
                            ", lng: " + dockings[i].getLocation().getLongitude() + "},";
                }
            }
            final String input = "[" + array + "]";
            try{
                Platform.runLater(() -> {
                    engine.getLoadWorker().stateProperty().addListener((e) -> {
                        engine.executeScript("document.addDocks(" + input + ");");
                    });
                });
            } catch (Exception e){

            }
        }

    }
}
