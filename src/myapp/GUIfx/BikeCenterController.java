package myapp.GUIfx;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class BikeCenterController {
    @FXML
    VBox regBikePane;

    public void regBikeButton(){
        regBikePane.setVisible(true);
    }
}
