package myapp.GUIfx.Statistic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import myapp.Stats.DummyBikeInfo;

public class StatController2 {
    @FXML
    private BorderPane stat1Pane;
    @FXML
    private BorderPane statPane;
    @FXML
    private TextField bikeIdInput;
    @FXML
    private BorderPane stat2Pane;
    @FXML
    private BorderPane stat3Pane;
    @FXML
    private GenerateStats stats = new GenerateStats();

    public void initialize() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Docking station name");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Bike capacity");
        XYChart.Series<String, Number> dock = new XYChart.Series();
        XYChart.Series<String, Number> capacity = new XYChart.Series();
        int[] cap = stats.dockCapacity();
        String[] name = stats.dockingName();
        for (int i=0; i<cap.length; i++){
            dock.getData().add(new XYChart.Data(name[i], cap[i]));
            //capacity.getData().add(new XYChart.Data());
        }
        BarChart<String, Number> dockStat = new BarChart<>(xAxis,yAxis);
        dockStat.getData().addAll(dock);
        stat2Pane.setCenter(dockStat);
    }

    public void closePane() {
        closeAll();
        statPane.setVisible(false);
    }

    public void openPane() {
        statPane.setVisible(true);
        System.out.println("Open stat");
    }

    @FXML private void openStat1() {
        closeAll();
        stat1();
        stat1Pane.setVisible(true);
    }

    @FXML private void openStat2() {
        closeAll();
        stat2Pane.setVisible(true);
    }

    @FXML private void openStat3() {
        closeAll();
        stat3();
        stat3Pane.setVisible(true);
    }

    @FXML private void closeAll(){
        stat1Pane.setVisible(false);
        stat2Pane.setVisible(false);
        stat3Pane.setVisible(false);
    }

    public void stat1() {
        int[] bikeAv = stats.bikeAvailability();
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Available", bikeAv[0]),
                        new PieChart.Data("On trip", bikeAv[1]),
                        new PieChart.Data("In repair", bikeAv[2]));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("");
        stat1Pane.setCenter(chart);
    }

    public void stat3(){
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("BikeId");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");
        XYChart.Series distTrav = new XYChart.Series();
        distTrav.setName("Distance travelled");
        XYChart.Series totTrips = new XYChart.Series();
        totTrips.setName("Total number of trips");
        XYChart.Series battery = new XYChart.Series();
        battery.setName("Battery percentage");

        int[][] bStats = stats.bikeStats();
        for (int i=0; i<10; i++){
            distTrav.getData().add(new XYChart.Data(String.valueOf(bStats[0][i]), bStats[1][i]));
            totTrips.getData().add(new XYChart.Data(String.valueOf(bStats[0][i]), bStats[2][i]));
            battery.getData().add(new XYChart.Data(String.valueOf(bStats[0][i]), bStats[3][i]));
           /* distTrav.getData().add(new XYChart.Data(bStats[0][i], bStats[1][i]));
            distTrav.getData().add(new XYChart.Data(bStats[0][i], bStats[2][i]));
            distTrav.getData().add(new XYChart.Data(bStats[0][i], bStats[3][i]));*/
        }
        BarChart<String, Number> bikeStat = new BarChart<>(xAxis,yAxis);
        bikeStat.getData().addAll(distTrav,totTrips,battery);
        stat3Pane.setCenter(bikeStat);
    }
}



    /*@FXML private BarChart dockStat;
    @FXML private void openStat2(){
        statPane2.setVisible(true);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        //BarChart<String,Number> bc =
                new BarChart<>(xAxis,yAxis);
        dockStat.setTitle("Number of bikes at each docking station");
        xAxis.setLabel("Docking station ID");
        yAxis.setLabel("Number of docked bikes");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("10:00");
        series1.getData().add(new XYChart.Data("1234    ", 12));
        series1.getData().add(new XYChart.Data("1235", 15));
        series1.getData().add(new XYChart.Data("1236", 10));
        dockStat.getData().addAll(series1);
    }
}*/


        /*stat2.setOnAction(e -> {
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
}*/
