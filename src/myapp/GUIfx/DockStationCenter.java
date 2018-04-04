package myapp.GUIfx;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.webkit.dom.DocumentImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import myapp.data.Bike;
import myapp.data.Docking;

import java.net.URL;
import java.util.ResourceBundle;

public class DockStationCenter implements Initializable{
    @FXML private ListView dockingList;
    @FXML private BorderPane dockPane;

    public void initialize(URL url, ResourceBundle rb){
        dockingList.setCellFactory(e -> new DockingCell());

        System.out.println("Initialize start");

        Docking a = new Docking(1, "Kalvskinnet", null, 100);
        Docking b = new Docking(2, "Solsiden", null, 50);
        Docking c = new Docking(3, "Moholt", null, 20);
        Docking d = new Docking(4, "Lerkendal", null, 30);

        for(int i= 0; i < 25; i++ ) {
            dockingList.getItems().addAll(a, b, c, d);
        }
    }

    public void openPane(){
        dockPane.setVisible(true);
    }

    public void closePane(){
        dockPane.setVisible(false);

    }

    @FXML private Label dockIdOutput;
    @FXML private Label nameOutput;
    @FXML private Label openSpaces;
    @FXML private BorderPane bikeInfo;

    //Register new docking station
    @FXML private VBox regDock;

    @FXML private void selectedRow() {
        Docking dock = (Docking) dockingList.getItems().get(dockingList.getSelectionModel().getSelectedIndex());
        dockIdOutput.setText("Docking id: " + Integer.toString(dock.getId()));
        nameOutput.setText("Name: " + dock.getName());
        openSpaces.setText("Open spaces: " + Integer.toString(dock.getOpenSpaces()));
        dockingList.setVisible(false);
        bikeInfo.setVisible(true);
    }

    @FXML private void openRegDock(){

        regDock.setVisible(true);
    }

    //metode for å lukke alt
}
