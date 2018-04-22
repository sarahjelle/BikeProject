package myapp.GUIfx.Admin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import myapp.GUIfx.DialogWindows;
import myapp.GUIfx.SignIn.SignInController;
import myapp.data.User;
import myapp.dbhandler.DBH;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable{
    @FXML private SplitPane adminPane;
    @FXML private GridPane adminInfo;

    //info
    @FXML private Label firstnameInfo;
    @FXML private Label surnameInfo;
    @FXML private Label phoneInfo;
    @FXML private Label emailInfo;

    private User user;
    //private User user = new User(-1, "Sara", "Hjelle", 98899919, "sarahj.98@hotmail.com", "0047");

    private DBH dbh = new DBH();
    private User[] admins;
    @FXML private ListView<User> adminList;

    private DialogWindows dw = new DialogWindows();

    public void initialize(URL url, ResourceBundle rb){
        adminList.setCellFactory(e -> new AdminCell());
        refreshList();
    }

    public void setAdmin(User user){
        this.user = user;
    }

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
    public void openPane(User user){
        //user = signInController.getUser();
        this.user = user;
        adminPane.setVisible(true);
        adminInfo.requestLayout();
        firstnameInfo.setText(user.getFirstname());
        surnameInfo.setText(user.getLastname());
        phoneInfo.setText(Integer.toString(user.getPhone()));
        emailInfo.setText(user.getEmail());
    }

    public void closePane(){
        adminPane.setVisible(false);
    }

    @FXML private void showInfo(User user){
    }

    @FXML private PasswordField oldPassword;
    @FXML private PasswordField newPassword;

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

    @FXML private void selectedRow(){
        User otherUser = adminList.getSelectionModel().getSelectedItem();
        String userName = "User: " + otherUser.getFirstname() + " " + otherUser.getLastname();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(userName);
        alert.setContentText("Choose what you want to do: ");

        ButtonType delete = new ButtonType("Delete admin");
        ButtonType password = new ButtonType("Reset password");

        alert.getButtonTypes().setAll(delete, password);

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
}
