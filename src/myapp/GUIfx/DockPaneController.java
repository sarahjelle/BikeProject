package myapp.GUIfx;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import myapp.data.Docking;

public class DockPaneController {
    @FXML private TableView<Docking> docktable;
    @FXML private TextField dockNameField;
    @FXML private TextField addressField;
    @FXML private TextField capacityField;

    @FXML
    protected void addDockingStation(ActionEvent event) {
        ObservableList<Docking> data = docktable.getItems();
        data.add(new Docking(dockNameField.getText(),
                addressField.getText(),
                capacityField.getText()
        ));

        dockNameField.setText("");
        addressField.setText("");
        capacityField.setText("");
    }
}
