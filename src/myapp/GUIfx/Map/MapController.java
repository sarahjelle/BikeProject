package myapp.GUIfx.Map;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.net.URL;

public class MapController extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map");
        Button btn = new Button();
        btn.setText("Show map");
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 800, 650));
        primaryStage.show();
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WebView browser = new WebView();
                root.getChildren().add(browser);
                URL url = getClass().getResource("map.html");
                browser.getEngine().load(url.toExternalForm());
                primaryStage.show();
            }
        });

    }
}