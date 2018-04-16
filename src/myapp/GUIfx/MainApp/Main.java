package myapp.GUIfx.MainApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.stage.*;


public class Main extends Application{
    private void login(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("myapp/GUIfx/SignIn/SignIn.fxml"));
        Scene scene = new Scene(root, 300, 300);

        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadApp(Stage primaryStage) throws Exception{
        System.out.println("loading app");
        Parent root = FXMLLoader.load(getClass().getResource("App.fxml"));
        System.out.println("loading app 2");
        Scene scene = new Scene(root, 700,500);

        primaryStage.setTitle("FXML Welcome");
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
        //login(primaryStage);
        loadApp(primaryStage);
    }

    public static void main(String[]args){
        launch(args);
    }
}