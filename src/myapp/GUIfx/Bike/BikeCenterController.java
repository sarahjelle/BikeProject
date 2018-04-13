package myapp.GUIfx.Bike;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import myapp.data.Bike;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class BikeCenterController implements Initializable{
    //attributes for the tableview:
    /*@FXML private TableView<Bike> bikeTable;
    @FXML private TableColumn<Bike, Integer> bikeId;
    @FXML private TableColumn<Bike, Integer> totalTrips;
    @FXML private TableColumn<Bike, Integer> totalKm;*/

    //attributes for bike registration
    @FXML VBox regBikePane;
    @FXML private ComboBox<String> typeInput;
    @FXML private DatePicker purchaseDate;
    @FXML private TextField priceInput;
    @FXML private TextField makeInput;
    //error
    @FXML private Label priceError;

    //attributes for repair
    @FXML private SplitPane repairBikePane;

    //attributes for info
    @FXML private BorderPane bikeInfo;
    @FXML private Text bikeidOutput;
    @FXML private Text typeOutput;
    @FXML private Text makeOutput;
    @FXML private Text priceOutput;
    @FXML private Text dateOutput;
    @FXML private Text batteryOutput;
    @FXML private Text availableOutput;
    @FXML private Text distanceOutput;

    //attributes for edit
    @FXML private VBox bikeEditPane;
    @FXML private TextField typeEdit;
    @FXML private TextField makeEdit;
    @FXML private TextField priceEdit;
    @FXML private DatePicker dateEdit;
    @FXML private TextField batteryEdit;
    @FXML private ComboBox statusEdit;
    @FXML private TextField distanceEdit;

    //listview
    @FXML private ListView listView;
    //private DBH dbh = new DBH();
    //private ArrayList<Bike> bikes = dbh.getBikes();



    public void initialize(URL url, ResourceBundle rb) {
        //for tableview
        /*System.out.println("Initialize");
        bikeId.setCellValueFactory(new PropertyValueFactory<BikeData, Integer>("bikeId"));
        totalTrips.setCellValueFactory(new PropertyValueFactory<BikeData, Integer>("totalTrips"));
        totalKm.setCellValueFactory(new PropertyValueFactory<BikeData, Integer>("totalKm"));*/

        /*listView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new BikeCell();
            }
        });*/

        listView.setCellFactory(e -> new BikeCell());

        /*databaser
        for(int i = 0; i < bikes.size(); i++) {
            listView.getItems().add(bikes.get(i));
        }*/

        for(int i = 0; i < 25; i++){
            Bike a = new Bike(i, "Electric", 899.90, LocalDate.now(), "Trek", 0.5, true, 100, null);
            Bike b = new Bike(i+1, "Type 2", 599.87, LocalDate.now(), "DBS", 0.8, false, 200, null);
            Bike c = new Bike(i+2, "Type 3", 699.87, LocalDate.now(), "Trek", 0.2, true, 300, null);
            Bike d = new Bike(i+3, "Type 4", 799.87, LocalDate.now(), "DBS", 1, false, 400, null);
            //addBikeData(b); //hører til tabell
            listView.getItems().addAll(a, b, c, d);
        }


        System.out.println("Initialize Done");
    }

    public void addBikeData(Bike bike){
       listView.getItems().add(bike);
    }

    //opens the registration pane
    public void openRegisterPane(){
        closeAll();
        regBikePane.setVisible(true);
    }

    //opens the repairPane
    public void openRepairPane(){
        closeAll();
        repairBikePane.setVisible(true);
    }

    //choose and add a type (in register new bike)
    public void typeSelected(){
        //String selected = typeInput.getValue();
        String selected = typeInput.getSelectionModel().getSelectedItem();
        System.out.println(selected);

        if(selected.equals("New type")) {
            TextInputDialog newType = new TextInputDialog("New type");
            newType.setHeaderText("New type");
            newType.setGraphic(null);
            newType.setContentText("Enter new type:");
            Optional<String> result = newType.showAndWait();
            if (result.isPresent()) {
                typeInput.getItems().add(result.get());
            } else {
                System.out.println("Cancel");
            }
        }
    }

    //checks if info is written in the textfield
    public boolean regInfoOk(){
        if(typeInput.getSelectionModel().isEmpty()){

            return false;
        }
        if(priceInput.getText().trim().equals("")){
            priceError.setVisible(true);
            return false;
        }

        if(makeInput == null){
            makeInput.setText("Missing make");
            return false;
        }

        if(purchaseDate.getValue() == null){
            return false;
        }

        regBike();
        return true;
    }

    //Register new bike
    public void regBike(){
        //DBH dbh = new DBH();
        String typeSelected = typeInput.getSelectionModel().getSelectedItem();
        double price = Double.parseDouble(priceInput.getText());
        LocalDate date = purchaseDate.getValue();
        String make = makeInput.getText();
        Bike bike = new Bike(price, date, make, typeSelected);
        addBikeData(bike);
        System.out.println("Vellykket registrering");
        //dbh.addBike(bike);
    }

    /*@FXML private void getItemsFromDatabase(){
        Database database = new Database();
        String[] types = database.getTypes()
        type.getItems().addAll(types)
    }*/

    //Goes back to main page
    public void cancel(){

        closeAll();
        listView.setVisible(true);
    }

    //register repair
    public void regRepair(){
    }

    //
    //@FXML private ImageView image1;
    @ FXML private void showInfo(Bike bike){
        closeAll();
        bikeInfo.setVisible(true);
        bikeidOutput.setText(Integer.toString(bike.getId()));
        typeOutput.setText(bike.getType());
        makeOutput.setText(bike.getMake());
        priceOutput.setText(Double.toString(bike.getPrice()));

        //convert date to String
        LocalDate date = bike.getPurchased();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String purchaseDate = date.format(formatter);

        dateOutput.setText(purchaseDate);
        batteryOutput.setText(Double.toString(bike.getBatteryPercentage()));

        if(bike.isAvailable()){
            availableOutput.setText("Available");
        }
        else{
            availableOutput.setText("Not available");
        }

        distanceOutput.setText(Integer.toString(bike.getDistanceTraveled()));

    }

    @FXML private void selectedRow(){
        Bike bike = (Bike)listView.getItems().get(listView.getSelectionModel().getSelectedIndex());
        showInfo(bike);
    }

    public Bike bikeById(int bikeId){
        //ListView<Bike> bikeList = new ListView<>(listView.getItems());
        ObservableList<Bike> bike = listView.getItems();

        for(int i = 0; i < bike.size(); i++){
            if(bike.get(i).getId() == bikeId){
                return bike.get(i);
            }
        }
        return null;
    }


    public void search(int id){
        if(bikeById(id) instanceof Bike && bikeById(id) != null){
            Bike bike = bikeById(id);
            showInfo(bike);
        }
        else{
            System.out.println("Unvalid search");
        }
    }

    public void closeAll(){
        listView.setVisible(false);
        regBikePane.setVisible(false);
        bikeEditPane.setVisible(false);
        bikeInfo.setVisible(false);
        repairBikePane.setVisible(false);
    }



    @FXML private void openEdit(){
        closeAll();
        bikeEditPane.setVisible(true);
       String bikeId = bikeidOutput.getText();
       int id = Integer.parseInt(bikeId);
       Bike bike = bikeById(id);
       typeEdit.setText(bike.getType());
       makeEdit.setText(bike.getMake());
       priceEdit.setText(Double.toString(bike.getPrice()));
       dateEdit.setValue(bike.getPurchased());
       batteryEdit.setText(Double.toString(bike.getBatteryPercentage()));
       //statusEdit.setValue(1);
    }

    @FXML private boolean deleteBike(){
        String bikeId = bikeidOutput.getText();
        int id = Integer.parseInt(bikeId);

        Alert delete = new Alert(Alert.AlertType.CONFIRMATION);
        delete.setTitle("Delete bike");
        delete.setContentText("Are you sure you want to delete bike with bikeid " + id + "?");

        Optional<ButtonType> result = delete.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK){
            //if boolean == true
            Alert deleted = new Alert(Alert.AlertType.INFORMATION);
            deleted.setHeaderText("Deleted");
            deleted.setContentText("Bike with bikeId " + id + " was sucsessfully deleted");
            Optional<ButtonType> result2 = deleted.showAndWait();

            if(result2.get() == ButtonType.OK || result2.get() == ButtonType.CLOSE){
                closeAll();
                listView.setVisible(true);
            }
            return true;
        }

        else{
            System.out.println("Cancel");
            return false;
        }
    }
}

