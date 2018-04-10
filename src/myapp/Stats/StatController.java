package myapp.Stats;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.chart.*;

public class StatController extends Application{

    @Override public void start(Stage stage) {
        stage.setTitle("Statistics");
        stage.setWidth(500);
        stage.setHeight(500);

        //Menu to choose statistic to show, put in a border pane
        BorderPane root = new BorderPane();
        Menu statMenu = new Menu("Statistics");
        MenuItem stat1 = new MenuItem("Bike location overview");
        MenuItem stat2 = new MenuItem("Number of bikes at each docking station");
        MenuItem stat3 = new MenuItem("How many bikes were active this week");
        statMenu.getItems().addAll(stat1,stat2,stat3);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(statMenu);
        root.setTop(menuBar);
        //Button back = new Button("Return to stat menu");

        stat1.setOnAction(e -> {
            Label inputLabel = new Label("BikeID:");
            TextField inputID = new TextField();
            Button getBike = new Button("Get bike stats");
            BorderPane stat1Pane = new BorderPane();
            FlowPane top = new FlowPane();
            top.getChildren().add(inputLabel);
            top.getChildren().add(inputID);
            top.getChildren().add(getBike);
            stat1Pane.setTop(top);
            FlowPane center = new FlowPane();
            getBike.setOnAction((ActionEvent event) -> {
                DummyBikeInfo newBike = new DummyBikeInfo(inputID.getText());
                int repair = newBike.getHoursAtRepair();
                int docked = newBike.getHoursAtDocking();
                int active = newBike.getHourActive();
                int tot = (repair + docked + active);
                System.out.println(repair);
                System.out.println(docked);
                System.out.println(active);
                System.out.println(tot);
                ObservableList<PieChart.Data> pieChartData =
                        FXCollections.observableArrayList(
                                new PieChart.Data("At repair", (repair * 100) / tot),
                                new PieChart.Data("At docking station", (docked * 100) / tot),
                                new PieChart.Data("Rented", (active * 100) / tot));
                final PieChart chart = new PieChart(pieChartData);
                chart.setTitle("");
                center.getChildren().add(chart);
                stage.show();
            });
            stat1Pane.setCenter(center);
            root.setCenter(stat1Pane);
        });

        stat2.setOnAction(e -> {
            FlowPane center = new FlowPane();
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String,Number> bc =
                    new BarChart<>(xAxis,yAxis);
            bc.setTitle("Number of bikes at each docking station");
            xAxis.setLabel("Docking station ID");
            yAxis.setLabel("Number of docked bikes");

            XYChart.Series series1 = new XYChart.Series();
            series1.setName("10:00");
            series1.getData().add(new XYChart.Data("1234", 12));
            series1.getData().add(new XYChart.Data("1235", 15));
            series1.getData().add(new XYChart.Data("1236", 10));
            bc.getData().addAll(series1);
            center.getChildren().add(bc);
            root.setCenter(center);
        });

        //root.setCenter(grid);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
