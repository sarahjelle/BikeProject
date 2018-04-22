package myapp.GUIfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

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

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        boolean ok = false;

        if (result.isPresent() && result.get() == yesButton) {
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

    public String inputDialog(String content, String header){
        String input = null;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            input = result.get();
        }

        return input;
    }
}
