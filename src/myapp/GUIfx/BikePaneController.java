package myapp.GUIfx;

import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import myapp.data.Bike;
import javafx.scene.control.*;

import java.time.LocalDate;

public class BikePaneController {
    @FXML private TableView<BikeData> bikeTable;
    @FXML private TableColumn<BikeData, Integer> bikeId;
    @FXML private TableColumn<BikeData, Integer> totalTrips;
    @FXML private TableColumn<BikeData, Integer> totalKm;
    @FXML public BikeCenterController centerPaneController;

    @FXML
    private void initialize(){
        System.out.println("Initialize");
        bikeId.setCellValueFactory(new PropertyValueFactory<BikeData, Integer>("bikeId"));
        totalTrips.setCellValueFactory(new PropertyValueFactory<BikeData, Integer>("totalTrips"));
        totalKm.setCellValueFactory(new PropertyValueFactory<BikeData, Integer>("totalKm"));

        for(int i = 0; i < 100; i++){
            Bike b = new Bike(1,"Make",0.5, true, 100, null);
            addBikeData(b);
        }

        System.out.println("Initialize Done");
    }

    public void addBikeData(Bike bike){
        bikeTable.getItems().add(new BikeData(bike));
    }

    @FXML private void openRegBike(){
        bikeTable.setVisible(false);
        centerPaneController.regBikeButton();
    }



    /*public void createBikes(){
        bikes[0] = new Bike(100, LocalDate.parse("2018-07-01"), "Electric", "Trek");
        bikes[1] = new Bike(200, LocalDate.parse("2018-07-01"), "Electric", "Trek");
        bikes[2] = new Bike(300, LocalDate.parse("2018-07-01"), "Electric", "Trek");
        bikes[3] = new Bike(400, LocalDate.parse("2018-07-01"), "Electric", "Trek");
    }

    public void createTable(){
        for(int i = 0; i < bikes.length; i++){
            bikes[i].getId();
        }
    }*/
}
