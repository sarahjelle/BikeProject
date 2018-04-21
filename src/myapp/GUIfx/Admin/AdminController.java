package myapp.GUIfx.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;

public class AdminController {
    @FXML private SplitPane adminPane;

    public void openPane(){
        adminPane.setVisible(true);
    }

    public void closePane(){
        adminPane.setVisible(false);
    }


}
