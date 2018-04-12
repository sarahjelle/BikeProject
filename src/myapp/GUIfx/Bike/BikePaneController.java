package myapp.GUIfx.Bike;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;

public class BikePaneController {
    @FXML private BikeCenterController centerPaneController;
    @FXML private TextField searchInput;
    @FXML private BorderPane bikePane;

    //fiks så man kommer inn på listen hver gang man trykker
    public void openPane(){
        centerPaneController.cancel();
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
        int bikeId;
        try{
           bikeId = Integer.parseInt(id);
            centerPaneController.search(bikeId);
        }
        catch (Exception e){
            searchInput.setText("Write a number");
            searchInput.setStyle("-fx-text-fill: #9f0000");
        }

    }
}
