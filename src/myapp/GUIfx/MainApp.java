package myapp.GUIfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import myapp.data.Bike;

import java.time.LocalDate;

public class MainApp {
    private ObservableList<Bike> bikeData = FXCollections.observableArrayList();

    public MainApp(){
        bikeData.add(new Bike(100, LocalDate.parse("2018-07-01"), "Electric", "Trek"));
        bikeData.add(new Bike(200, LocalDate.parse("2017-07-01"), "Electric", "Trek"));
        bikeData.add(new Bike(300, LocalDate.parse("2016-07-01"), "Electric", "Trek"));
        bikeData.add(new Bike(400, LocalDate.parse("2015-07-01"), "Electric", "Trek"));
    }

    public ObservableList<Bike> getBikeData(){
        return bikeData;
    }
}
