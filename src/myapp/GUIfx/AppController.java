package myapp.GUIfx;

import javafx.fxml.FXML;

public class AppController {
    @FXML private BikePaneController bikeController;
    @FXML private DockStationCenter dockController;

    @FXML private void bike(){
        System.out.println(bikeController);
        closeAll();
        bikeController.openPane();
    }

    @FXML private void docking(){
        closeAll();
        dockController.openPane();
    }

    @FXML private void map(){

    }

    @FXML private void statistic(){

    }

    private void closeAll(){
        bikeController.closePane();
        dockController.closePane();
    }
}
