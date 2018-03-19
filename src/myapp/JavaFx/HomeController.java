package myapp.JavaFx;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.awt.event.ActionEvent;

public class HomeController {
    @FXML
    public BikePanelController bpController;

    @FXML private void regBike(){
        bpController.openPanel();
    }

    @FXML private void reg_bike(ActionEvent event){
        bpController.registerBikeButton();
    }

    @FXML private void dock_station(){}

    @FXML private void statistic(){}

    @FXML private void map(){}

    @FXML private void handleKey(){}


}
