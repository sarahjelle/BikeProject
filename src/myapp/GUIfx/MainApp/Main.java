package myapp.GUIfx.MainApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.stage.*;
import myapp.GUIfx.Admin.AdminController;
import myapp.data.User;


public class Main extends Application{

    private void login(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../SignIn/SignIn.fxml"));
        Scene scene = new Scene(root, 300, 300);

        primaryStage.setTitle("Trondheim Bike Rental");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadApp(Stage primaryStage, User loggedInUser) throws Exception{
        System.out.println("loading app");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Parent root = (Parent) loader.load();
        AppController appctrl = loader.getController();
        appctrl.setUser(loggedInUser);
        System.out.println("loading app 2");
        Scene scene = new Scene(root, 700,500);

        primaryStage.setTitle("Trondheim Bike Rental");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("finished loading app");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
        login(primaryStage);
    }

    public static void main(String[]args){
        launch(args);
    }
}