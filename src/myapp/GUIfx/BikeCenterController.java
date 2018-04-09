package myapp.GUIfx;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import myapp.data.Bike;
import myapp.dbhandler.DBH;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BikeCenterController implements Initializable{
    //attributes for the tableview:
    /*@FXML private TableView<Bike> bikeTable;
    @FXML private TableColumn<Bike, Integer> bikeId;
    @FXML private TableColumn<Bike, Integer> totalTrips;
    @FXML private TableColumn<Bike, Integer> totalKm;*/

    //attributes for bike registration
    @FXML VBox regBikePane;
    @FXML private ComboBox<String> type;
    @FXML private DatePicker purchaseDate;
    @FXML private TextField priceInput;
    @FXML private TextField makeInput;

    //attributes for repair
    @FXML private VBox repairBikePane;

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


    //listview
    @FXML private ListView listView;

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

        for(int i = 0; i < 25; i++) {
            Bike a = new Bike(1, "Electric", 899.90, LocalDate.now(), "Trek", 0.5, true, 100, null);
            Bike b = new Bike(2, "Type 2", 599.87, LocalDate.now(), "DBS", 0.8, false, 200, null);
            Bike c = new Bike(3, "Type 3", 699.87, LocalDate.now(), "Trek", 0.2, true, 300, null);
            Bike d = new Bike(4, "Type 4", 799.87, LocalDate.now(), "DBS", 1, false, 400, null);
            //addBikeData(b); //hører til tabell
            listView.getItems().addAll(a, b, c, d);
        }


        System.out.println("Initialize Done");
    }

    /*public void addBikeData(Bike bike){
       listView.getItems().add(bike);
    }*/

    //opens the registration pane
    public void openRegisterPane(){
        regBikePane.setVisible(true);
        listView.setVisible(false);
        repairBikePane.setVisible(false);
        System.out.println("Du har trykket");
    }

    //opens the repairPane
    public void openRepairPane(){
        listView.setVisible(false);
        regBikePane.setVisible(false);
        repairBikePane.setVisible(true);
    }

    //choose and add a type (in register new bike)
    public void typeSelected(){
        String selected = type.getSelectionModel().getSelectedItem();
        System.out.println(selected);

        if(selected.equals("New type")){
            TextInputDialog newType = new TextInputDialog();
            newType.setHeaderText("New type");
            newType.setGraphic(null);
            try {
                String type2 = newType.showAndWait().get();
                System.out.println(type);
                type.getItems().add(type2);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //Register new bike
    public void regBike(){
        DBH dbh = new DBH();
        String typeSelected = type.getSelectionModel().getSelectedItem();
        double price = Double.parseDouble(priceInput.getText());
        LocalDate date = purchaseDate.getValue();
        String make = makeInput.getText();
        Bike bike = new Bike(price, date, make, typeSelected);
        dbh.addBike(bike);
    }

    /*@FXML private void getItemsFromDatabase(){
        Database database = new Database();
        String[] types = database.getTypes()
        type.getItems().addAll(types)
    }*/

    //Goes back to main page
    public void cancel(){
        listView.setVisible(true);
        regBikePane.setVisible(false);
        repairBikePane.setVisible(false);
        bikeInfo.setVisible(false);
    }

    //register repair
    public void regRepair(){
    }

    //
    @ FXML private void showInfo(Bike bike){
        listView.setVisible(false);
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
}

