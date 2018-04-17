package myapp.GUIfx.Bike;

//listview

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import myapp.data.Bike;
import myapp.dbhandler.DBH;

import javax.xml.soap.Text;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class BikeController implements Initializable {
    //DBH
    //DBH dbh = new DBH();
    //ArrayList<Bike> bikes = dbh.getBikes();

    //listview
    @FXML
    private BorderPane listPane;
    @FXML
    private ListView bikeList;
    @FXML private TextField searchInput;


    //info, edit, repair
    @FXML
    private BorderPane infoEditRepair;

    //info
    @FXML private SplitPane bikeInfo;
    @FXML private Label idOutput;
    @FXML private Label typeInfo;
    @FXML private Label makeInfo;
    @FXML private Label priceInfo;
    @FXML private Label dateInfo;
    @FXML private Label batteryInfo;
    @FXML private Label statusInfo;
    @FXML private Label distanceInfo;

    //repair
    @FXML private SplitPane repairPane;
    @FXML private DatePicker dateSent;
    @FXML private TextField descriptionBefore;
    @FXML private DatePicker dateReturn;
    @FXML private TextField priceRepair;
    @FXML private TextField descriptionDone;

    //Edit
    @FXML private BorderPane editPane;
    @FXML private TextField typeEdit;
    @FXML private TextField makeEdit;
    @FXML private TextField priceEdit;
    @FXML private DatePicker dateEdit;
    @FXML private TextField batteryEdit;
    @FXML private ComboBox statusEdit;
    @FXML private TextField distanceEdit;

    //Register new bike:
    @FXML private VBox registerPane;
    @FXML private ComboBox<String> typeReg;
    @FXML private DatePicker dateReg;
    @FXML private TextField priceReg;
    @FXML private TextField makeReg;



    public void initialize(URL url, ResourceBundle rb) {
        bikeList.setCellFactory(e -> new BikeCell());

        Bike bike = new Bike(1, 900, "DBS", "Electric", 0.5, 100);

        for (int i = 0; i < 100; i++) {
            bikeList.getItems().add(bike);
        }
    }

    //methodes for all panes
    public void openPane() {
        closeAll();
        listPane.setVisible(true);
    }

    public void closeAll() {
        listPane.setVisible(false);
        infoEditRepair.setVisible(false);
        repairPane.setVisible(false);
        bikeInfo.setVisible(false);
        editPane.setVisible(false);
        repairPane.setVisible(false);
        registerPane.setVisible(false);
    }

    @FXML private void cancel(){
        closeAll();
        listPane.setVisible(true);
    }

    //methods for info
    public Bike findBike(int bikeId){
        /*for(int i = 0; i < bikes.size(); i++){
            if(bikeId == bikes.get(i).getId()){
                return bikes.get(i);
            }
        }
        return null;*/

        ObservableList<Bike> bike = bikeList.getItems();
        for(int i = 0; i < bike.size(); i++){
            if(bikeId == bike.get(i).getId()){
                return bike.get(i);
            }
        }

        return null;
    }

    @FXML private void selectedRow(){
        Bike bike = (Bike)bikeList.getItems().get(bikeList.getSelectionModel().getSelectedIndex());
        showInfo(bike);
    }

    @FXML private void search(){
        String bikeID = searchInput.getText();
        int id = -1;
        try {
            id = Integer.parseInt(bikeID);
        }catch (Exception e){
            searchInput.setText("Write a number");
            searchInput.setStyle("-fx-text-fill: red");
        }

        if(id >= 0) {
            Bike bike = findBike(id);
            if (bike instanceof Bike && bike != null) {
                showInfo(bike);
            }
        }
    }

    @FXML private void showInfo(Bike bike) {
        closeAll();
        infoEditRepair.setVisible(true);
        bikeInfo.setVisible(true);

        idOutput.setText(Integer.toString(bike.getId()));
        typeInfo.setText(bike.getType());
        makeInfo.setText(bike.getMake());
        priceInfo.setText(Double.toString(bike.getPrice()));

        //convert localdate to string
        /*LocalDate date = bike.getPurchased();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String purchaseDate = date.format(formatter);

        dateInfo.setText(purchaseDate);*/
        batteryInfo.setText(Double.toString(bike.getBatteryPercentage()));
        distanceInfo.setText(Integer.toString(bike.getDistanceTraveled()));

    }

    @FXML private void showInfoBack(){
        Bike bike = findBike(Integer.parseInt(idOutput.getText()));
        showInfo(bike);
    }

    //methods for repair
    @FXML private void openRepairPane(){
        closeAll();
        infoEditRepair.setVisible(true);
        repairPane.setVisible(true);
    }

    //Register repair before sent
    @FXML private void beforeRepair(){
        LocalDate date = dateSent.getValue();
        String description = descriptionBefore.getText();
    }

    @FXML private void afterRepair(){
        LocalDate date = dateReturn.getValue();
        String priceInput = priceRepair.getText();
        int price = -1;
        try{
            price = Integer.parseInt(priceInput);
        }
        catch (Exception e){
            priceRepair.setText("Write a number");
        }
        String description = descriptionDone.getText();
    }

    //methodes for edit
    @FXML private void openEditPane(){
        int id = Integer.parseInt(idOutput.getText());
        Bike bike = findBike(id);

        typeEdit.setText(bike.getType());
        makeEdit.setText(bike.getMake());
        priceEdit.setText(Double.toString(bike.getPrice()));
        dateEdit.setValue(bike.getPurchased());
        batteryEdit.setText(Double.toString(bike.getBatteryPercentage()));
        //status
        distanceEdit.setText(Integer.toString(bike.getDistanceTraveled()));

        closeAll();
        infoEditRepair.setVisible(true);
        editPane.setVisible(true);
    }

    @FXML private boolean deleteBike(){
        int id = Integer.parseInt(idOutput.getText());
        return true;
    }

    @FXML private void openRegBike(){
        closeAll();
        registerPane.setVisible(true);
    }


    @FXML private void selectedType(){
        String selected = typeReg.getSelectionModel().getSelectedItem();

        if(selected.equals("New type")){
            TextInputDialog newType = new TextInputDialog("New type");
            newType.setContentText("Enter new type: ");
            Optional<String> result = newType.showAndWait();
            if(result.isPresent()){
                typeReg.getItems().add(result.get());
            }
        }
    }

    @FXML private boolean regBikeOk(){
        boolean ok = true;

        while (ok) {
            if (makeReg.getText().trim().isEmpty()) {
                makeReg.setText("Empty");
                ok = false;
            }

            if (typeReg.getSelectionModel().getSelectedItem() == null) {
                typeReg.setValue("Type1");

                ok = false;
            }

            if (dateReg.getValue() == null) {
                dateReg.setValue(LocalDate.now());
                ok = false;
            }

            if (priceReg.getText().trim().isEmpty()) {
                priceReg.setText("Field is blank");
                ok = false;
            }
            else{
                try{
                     double price = Double.parseDouble(priceReg.getText());
                }
                catch(Exception e){
                    ok = false;
                    priceReg.setText("Write a number");
                }
            }
        }
        return ok;
    }

    @FXML private void regBike(){
        if(regBikeOk()){
            Double price = Double.parseDouble(priceReg.getText());
            LocalDate date = dateReg.getValue();
            String type = typeReg.getSelectionModel().getSelectedItem();
            String make = makeReg.getText();

            Bike bike = new Bike(price, date, type, make);
            //add bike to dbh

            priceReg.clear();
            makeReg.clear();
            typeReg.setValue(null);
            dateReg.setValue(null);
        }
    }




}
