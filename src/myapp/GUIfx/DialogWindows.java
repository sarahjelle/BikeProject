package myapp.GUIfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The DialogWindows class contains methods to create different dialogwindows.
 * The methods are used in the GUI classes to give the user useful information,
 * or get input from user.
 *
 * @author Sara Hjelle
 */
public class DialogWindows {

    /**
     * This method creates a error dialogwindow with a custom message.
     * @param message The message to be displayed in the dialogbox.
     * @param header The title of the dialogbox.
     */
    public void errorWindow(String message, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * This method creates a dialogwindow to confirm an action.
     * The content and headertext is custom, so it fits the situation.
     * @param content The message to be displayed in the dialog window.
     * @param header The title of the window.
     * @return true = confirmed, false = cancel or declined.
     */
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

    /**
     * Method to give a user information by creating a dialog window.
     * @param information The information you want to display in the dialog window.
     * @param header The title you want for your dialog window.
     */
    public void informationWindow(String information, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(information);
        alert.showAndWait();
    }

    /**
     * This method creates a dialogwindow with an inputfield.
     * The method can be used when you want a user to write in the new value.
     * @param content the informational text you want to appear in the dialogwindow.
     * @param header the header of the window.
     * @return if user writes something in the inputfield and presses "ok" this text is returned,
     * if the user presses cancel the method returns null.
     */
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

    /**
     * This method creates a dialogwindow with a custom header, content text, and a choicebox.
     * The choicebox shows a list of Strings the user can choose between.
     * @param content the text to describe what the user should do.
     * @param header the header of the dialogwindow
     * @param choices arrayList of choices you want to be displayed in the box with choices.
     * @return the method returns the chosen String, if one is chosen, or null if nothing is chosen or window is cancelled.
     */
    public String choiceDialog(String content, String header, ArrayList<String> choices){
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(1),choices);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            return result.get();
        }

        return null;
    }
}
