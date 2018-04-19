package myapp.GUIfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class DialogWindows {
    public void errorWindow(String message, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public boolean confirmWindow(String content, String header) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText(content);
        alert.setHeaderText(header);

        Optional<ButtonType> result = alert.showAndWait();
        boolean ok = false;

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ok = true;
        }

        return ok;
    }

    public void informationWindow(String information, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(information);
        alert.showAndWait();
    }
}
