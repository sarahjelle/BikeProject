package myapp.GUIfx.SignIn;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import myapp.GUIfx.MainApp.Main;

public class SignInController {
    Main main = new Main();
    @FXML private Text actiontarget;

    @FXML private TextField userId;

    @FXML private PasswordField passwordField;

    @FXML
    protected void handleSubmitButtonActiton(){
        if(userId.getText().equals("Admin") && passwordField.getText().equals("123")){
            Stage currentStage = (Stage)userId.getScene().getWindow();
            currentStage.close();

            System.out.println(userId.getText());
            try{
                main.loadApp(currentStage);
            }
            catch(Exception e){e.printStackTrace();}
        }
        else{
            actiontarget.setText("Try again!");
        }
    }

}
