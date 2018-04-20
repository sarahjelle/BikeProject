package myapp.GUIfx.SignIn;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import myapp.GUIfx.DialogWindows;
import myapp.GUIfx.MainApp.Main;
import myapp.data.User;
import myapp.dbhandler.DBH;

public class SignInController {
    Main main = new Main();
    @FXML private Text actiontarget;

    @FXML private TextField userId;

    @FXML private PasswordField passwordField;

    @FXML private VBox signUpPane;
    @FXML private VBox logInPane;
    @FXML private HBox buttonBar;

    private DBH dbh = new DBH();
    private User user;
    private DialogWindows dw = new DialogWindows();

    @FXML private TextField firstNameReg;
    @FXML private TextField lastNameReg;
    @FXML private TextField phoneReg;
    @FXML private TextField emailReg;

    @FXML
    protected void logIn(){
        if(loginOk()) {
            String email = userId.getText();
            String password = passwordField.getText();

            user = dbh.loginUser(email, password);

            if(user != null && user instanceof User){
                Stage currentStage = (Stage)userId.getScene().getWindow();
                currentStage.close();

                System.out.println(userId.getText());
                try{
                    main.loadApp(currentStage);
                }
                catch(Exception e){e.printStackTrace();}
            }
            else{
                actiontarget.setText("Username or password is invalid");
            }
        }
    }

    private boolean loginOk(){
        boolean ok = true;

        if(userId.getText().trim().isEmpty()){
            ok = false;
        }

        if(passwordField.getText().trim().isEmpty()){
            ok = false;
        }

        return ok;
    }

    @FXML private void openSignUp(){
        signUpPane.setVisible(true);
        logInPane.setVisible(false);
        buttonBar.setVisible(false);
    }

    @FXML private void back(){
        signUpPane.setVisible(false);
        logInPane.setVisible(true);
        buttonBar.setVisible(true);
    }

    @FXML private void signUp(){
        if(signUpOk()){
            String firstname = firstNameReg.getText().trim();
            String lastname = lastNameReg.getText().trim();
            int phone = Integer.parseInt(phoneReg.getText().trim());
            String email = emailReg.getText();

            User newUser = new User(-1, firstname, lastname, phone, email, "0047");
            int id = dbh.registerUser(newUser, true);

            if(id > 0){
                dw.informationWindow("Your information is registered and a password is sent " +
                        "to " + email + ".\nSign in with new password.","Information registered" );
                back();
            }
            else{
                dw.informationWindow("Something went wrong with the database...", "Could not register user");
            }

        }
    }

    private boolean signUpOk(){
        boolean ok = true;

        if(firstNameReg.getText().trim().isEmpty()){
            ok = false;
            firstNameReg.setPromptText("Field is empty");
        }

        if(lastNameReg.getText().trim().isEmpty()){
            ok = false;
            lastNameReg.setPromptText("Field is empty");
        }

        if(phoneReg.getText().trim().isEmpty()){
            ok = false;
            phoneReg.setPromptText("Field is empty");
        }
        else if(phoneReg.getText().trim().length() != 8){
            ok = false;
            phoneReg.setPromptText("8 numeric characters");
        }
        else{
            try{
                int phone = Integer.parseInt(phoneReg.getText().trim());
            }catch (Exception e){
                phoneReg.setPromptText("Only numeric characters");
            }
        }

        if(emailReg.getText().trim().isEmpty()){
            ok = false;
            emailReg.setPromptText("Field is empty");
        }
        return ok;
    }

    public void forgotPassword(){

    }

}
