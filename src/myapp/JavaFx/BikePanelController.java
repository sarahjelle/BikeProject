package myapp.JavaFx;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import myapp.data.Bike;
import myapp.data.Location;
import myapp.dbhandler.DBH;

import java.awt.*;
import java.time.LocalDate;
import java.util.Date;

public class BikePanelController {
    @FXML public BorderPane bikeBorderPane;
    @FXML private Text actiontarget;
    @FXML private HBox registerBikePanel;
    @FXML private TextField priceInput;
    @FXML private TextField makeInput;
    @FXML private TextField typeInput;
    @FXML private DatePicker datepicker;

    public void openPanel(){
        bikeBorderPane.setVisible(true);
    }

    @FXML public void registerBikeButton(){
        registerBikePanel.setVisible(true);
    }
    @FXML private void editBike(){}

    @FXML private void handleSubmitButtonAction(){
        DBH db = new DBH();

        String type = typeInput.getText();
        String make = makeInput.getText();
        int price = Integer.parseInt(priceInput.getText());
        LocalDate date = datepicker.getValue();

        Bike bike = new Bike(price, date, type, make);
        db.addBike(bike);

    }
}
