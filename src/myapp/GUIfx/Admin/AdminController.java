package myapp.GUIfx.Admin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import myapp.GUIfx.DialogWindows;
import myapp.GUIfx.MainApp.Main;
import myapp.GUIfx.SignIn.SignInController;
import myapp.data.User;
import myapp.dbhandler.DBH;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The AdminController is the controller of AdminPane.fxml.
 * The class contains methods to fill the AdminPane with information.
 * It also contains methods to log out, see other admins contact information, register new admins
 * and change the users own information.
 *
 * @author Sara Hjelle
 */
public class AdminController implements Initializable{
    @FXML private SplitPane adminPane;
    @FXML private VBox adminInfo;
    @FXML private VBox signupPane;

    //info
    @FXML private Label firstnameInfo;
    @FXML private Label surnameInfo;
    @FXML private Label phoneInfo;
    @FXML private Label emailInfo;
    @FXML private HBox infoButtons;

    private User user;

    private DBH dbh = new DBH();
    private User[] admins;
    @FXML private ListView<User> adminList;
    private Main main = new Main();

    private DialogWindows dw = new DialogWindows();

    //change password
    @FXML private PasswordField oldPassword;
    @FXML private PasswordField newPassword;

    //edit
    @FXML private TextField firstNameEdit;
    @FXML private TextField surnameEdit;
    @FXML private TextField phoneEdit;
    @FXML private TextField emailEdit;
    @FXML private HBox editButtons;

    //new admin
    @FXML private TextField firstNameReg;
    @FXML private TextField lastNameReg;
    @FXML private TextField phoneReg;
    @FXML private TextField emailReg;

    public void initialize(URL url, ResourceBundle rb){
        adminList.setCellFactory(e -> new AdminCell());
        refreshList();
    }

    /**
     * The refreshList() method fills the ListView with the correct information from the database.
     * The method runs on a separate thread, which makes is possible to do something else while
     * the information is loading into the list.
     */
    @FXML private void refreshList(){
        adminList.getItems().clear();
        Thread thread = new Thread(() -> {
            admins  = dbh.getAllAdminUsers();

            Platform.runLater(() -> {
                for (int i = 0; i < admins.length; i++){
                    adminList.getItems().add(admins[i]);
                }
            });
        });
        thread.start();
    }

    /**
     * The openPane method is used to make the adminpage visible.
     * The method takes in a user object, which is the current logged in user.
     * This method is used in the appController.
     * @param user the user who logged into the application.
     */
    public void openPane(User user){
        this.user = user;
        adminPane.setVisible(true);
        adminInfo.requestLayout();
        close();
        firstnameInfo.setText(user.getFirstname());
        surnameInfo.setText(user.getLastname());
        phoneInfo.setText(Integer.toString(user.getPhone()));
        emailInfo.setText(user.getEmail());
    }

    public void closePane(){
        adminPane.setVisible(false);
    }

    /**
     * The changePassword method makes it possible for the logged in user to
     * change a his/hers password.
     */
    @FXML private void changePassword(){
        String old = oldPassword.getText();
        String newPass = newPassword.getText();

        boolean ok = dbh.changePassword(user, newPass, old);

        if(ok){
            dw.informationWindow("Your password were sucsessfully changed!", "Password changed");
            oldPassword.clear();
            newPassword.clear();
        }

        else{
            dw.errorWindow("Could not change password", "Error");
        }
    }

    /**
     * The selectedRow() method is used when the user clicks on a row in the list of admins.
     * When a row is selected a dialogwindow is opened and the user can chose to delete another admin.
     * We chose to make this a possibility because there is a limited amount of user on this application,
     * but normally only some administrators would be able to delete another user.
     */
    @FXML private void selectedRow(){
        User otherUser = adminList.getSelectionModel().getSelectedItem();
        String userName = "User: " + otherUser.getFirstname() + " " + otherUser.getLastname();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(userName);
        alert.setContentText("Choose what you want to do: ");

        ButtonType delete = new ButtonType("Delete admin");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(delete, cancel);

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == delete){
            boolean deleted = dbh.deleteUser(otherUser);
            if(deleted){
                dw.informationWindow("User is now deleted", userName);
            }
            else{
                dw.errorWindow("Could not delete user!", userName);
            }
        }
    }

    @FXML private void openEdit(){
        infoButtons.setVisible(false);
        editButtons.setVisible(true);
        firstnameInfo.setVisible(false);
        surnameInfo.setVisible(false);
        emailInfo.setVisible(false);
        phoneInfo.setVisible(false);
        firstNameEdit.setVisible(true);
        firstNameEdit.setText(user.getFirstname());
        surnameEdit.setVisible(true);
        surnameEdit.setText(user.getLastname());
        phoneEdit.setVisible(true);
        phoneEdit.setText(Integer.toString(user.getPhone()));
        emailEdit.setVisible(true);
        emailEdit.setText(user.getEmail());
    }

    @FXML private void close(){
        infoButtons.setVisible(true);
        editButtons.setVisible(false);
        firstnameInfo.setVisible(true);
        surnameInfo.setVisible(true);
        emailInfo.setVisible(true);
        phoneInfo.setVisible(true);
        firstNameEdit.setVisible(false);
        surnameEdit.setVisible(false);
        phoneEdit.setVisible(false);
        emailEdit.setVisible(false);
        signupPane.setVisible(false);
    }

    /**
     * Method makes it possible for the logged in user to edit his/hers
     * own information.
     */
    @FXML private void edit(){
        String firstName = user.getFirstname();
        String lastName = user.getLastname();
        int phone = user.getPhone();
        String email = user.getEmail();

        if(!firstNameEdit.getText().trim().isEmpty()){
            firstName = firstNameEdit.getText().trim();
        }

        if(!surnameEdit.getText().trim().isEmpty()){
            lastName = surnameEdit.getText().trim();
        }

        if(!phoneEdit.getText().trim().isEmpty()){
            try {
                phone = Integer.parseInt(phoneEdit.getText().trim());
            }catch(Exception e){}
        }

        if(!emailEdit.getText().trim().isEmpty()){
            email = emailEdit.getText().trim();
        }

        boolean ok = dw.confirmWindow("Are you sure you want to change your information to: " +
                "\nFirst name: " + firstName + "\nSurname: " + lastName + "\nPhone: " + phone + "\nEmail: " + email,
                "Change your information");

        if(ok){
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setPhone(phone);
            user.setEmail(email);

            boolean updated = dbh.updateUser(user);
            if(updated){
                dw.informationWindow("Your information is now updated", "Updated information");
                openPane(user);
            }
            else{
                dw.errorWindow("Your information could not be updated", "Information update failed");
            }
        }
    }

    @FXML private void openSignUp(){
        adminInfo.setVisible(false);
        signupPane.setVisible(true);
    }

    @FXML private void signUp(){
        if(signUpOK()){
            String firstName = firstNameReg.getText().trim();
            String surname = lastNameReg.getText().trim();
            int phone = Integer.parseInt(phoneReg.getText().trim());
            String email = emailReg.getText().trim();

            User newUser = new User(-1, firstName, surname, phone, email, "0047");
            int id = dbh.registerUser(newUser, true);

            if(id > 0){
                dw.informationWindow("New user is registered, and an email is sent with a new " +
                        "password to: " + email, "Information registered!");
            }
            else{
                dw.errorWindow("Something went wrong with the database...", "Could not register user");
            }
        }
    }

    private boolean signUpOK(){
        boolean ok = true;

        if(firstNameReg.getText().trim().isEmpty()){
            firstNameReg.setPromptText("Field is empty");
            ok = false;
        }

        if(lastNameReg.getText().trim().isEmpty()){
            lastNameReg.setPromptText("Field is empty");
            ok = false;
        }

        if(emailReg.getText().trim().isEmpty()){
            emailReg.setPromptText("Field is empty");
            ok = false;
        }

        String phone = phoneReg.getText().trim();

        if(phoneReg.getText().trim().isEmpty()){
            phoneReg.setPromptText("Field is empty");
            ok = false;
        }
        else if(phone.length() != 8){
            phoneReg.setPromptText("8 numeric characters");
            ok = false;
        }
        else{
            try{
                int tlf = Integer.parseInt(phone);
            }catch (Exception e){
                ok = false;
                phoneReg.setPromptText("Only numeric characters");
            }
        }

        return ok;
    }

    @FXML private void logOut(){
        boolean ok = dw.confirmWindow("Are you sure you want to log out of the application? ",
                "Log out");

        if(ok){
            Stage currentStage = (Stage)adminPane.getScene().getWindow();
            try{
                main.login(currentStage);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
