package myapp.GUIfx;

import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import myapp.data.Bike;
import javafx.scene.control.*;


import java.time.LocalDate;

public class BikePaneController {
    @FXML private BikeCenterController centerPaneController;
    @FXML private TextField searchInput;
    @FXML private BorderPane bikePane;

    public void openPane(){
        bikePane.setVisible(true);
    }

    public void closePane(){
        bikePane.setVisible(false);
    }

    @FXML private void openRegBike(){
        //bikeTable.setVisible(false);
        centerPaneController.openRegisterPane();
    }

    @FXML private void openRepairPane(){
        centerPaneController.openRepairPane();
    }

    @FXML private void openBike(){
        String id = searchInput.getText();
        int bikeId = Integer.parseInt(id);
        /*try{
           int bikeId = Integer.parseInt(id);
        }
        catch (Exception e){}*/

        centerPaneController.search(bikeId);

    }
}
