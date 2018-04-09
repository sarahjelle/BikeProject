package myapp.GUIfx;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;


public class Main extends Application{
    private void signin(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("SignIn.fxml"));

        Scene scene = new Scene(root, 300, 300);

        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadApp(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("App.fxml"));

        Scene scene = new Scene(root, 700,500);

        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        signin(primaryStage);
    }

    public static void main(String[]args){
        launch(args);
    }
}