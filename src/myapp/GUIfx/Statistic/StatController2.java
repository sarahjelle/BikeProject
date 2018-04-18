package myapp.GUIfx.Statistic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import myapp.Stats.DummyBikeInfo;

public class StatController2 {
    @FXML
    private BorderPane statPane;
    @FXML
    private BorderPane stat1Pane;
    @FXML
    private BorderPane stat2Pane;
    @FXML
    private BorderPane stat3Pane;
    @FXML
    private GenerateStats stats = new GenerateStats();
    @FXML private TextField searchInput;

    public void initialize() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Docking station");
        NumberAxis yAxis = new NumberAxis(0,100,5);
        yAxis.setLabel("Slots");
        XYChart.Series<String, Number> cap = new XYChart.Series();
        XYChart.Series<String, Number> taken = new XYChart.Series();
        Object[][] dockStats = stats.dockingStatistics();
        for (int i=0; i<dockStats[0].length; i++){
            cap.getData().add(new XYChart.Data(dockStats[0][i], dockStats[1][i]));
            taken.getData().add(new XYChart.Data(dockStats[0][i],dockStats[2][i]));
        }
        BarChart<String, Number> dockChart = new BarChart<>(xAxis,yAxis);
        dockChart.getData().addAll(cap,taken);
        cap.setName("Total number of slots");
        taken.setName("Occupied slots");
        stat2Pane.setCenter(dockChart);
    }

    public void closePane() {
        closeAll();
        statPane.setVisible(false);
    }

    public void openPane() {
        statPane.setVisible(true);
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
        stat4();
        //stat3();
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
                        new PieChart.Data("On trip", bikeAv[1]),
                        new PieChart.Data("In repair", bikeAv[2]),
                        new PieChart.Data("Available", bikeAv[0]));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("");
        final Label caption = new Label("");
        caption.setTextFill(Color.AQUA);
        caption.setStyle("-fx-font: 24 arial;");

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e ->  {
                            caption.setTranslateX(e.getSceneX());
                            caption.setTranslateY(e.getSceneY());
                            caption.setText(String.valueOf(data.getPieValue()) + "%");
                    });
        }
        stat1Pane.setCenter(chart);
    }

    public void stat4(){
        int[][] bStats = stats.bikeStats();
        final NumberAxis xAxis = new NumberAxis(0,250,10);
        final NumberAxis yAxis = new NumberAxis();
        final ScatterChart<Number,Number> scatter = new ScatterChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("BikeID");
        yAxis.setLabel("");
        scatter.setTitle("Trip information on all bikes");
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Total km");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Total trips");
        for (int i=0; i<bStats[0].length; i++) {
            series1.getData().add(new XYChart.Data(bStats[0][i], bStats[1][i]));
            series2.getData().add(new XYChart.Data(bStats[0][i], bStats[2][i]));
        }
        scatter.getData().addAll(series1,series2);
        stat3Pane.setCenter(scatter);
    }
/*
    public void stat3(){
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("BikeId");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");
        XYChart.Series distTrav = new XYChart.Series();
        distTrav.setName("Distance travelled");
        XYChart.Series totTrips = new XYChart.Series();
        totTrips.setName("Total number of trips");

        int[][] bStats = stats.bikeStats();
        for (int i=0; i<bStats[0].length; i++){
            distTrav.getData().add(new XYChart.Data(String.valueOf(bStats[0][i]), bStats[1][i]));
            totTrips.getData().add(new XYChart.Data(String.valueOf(bStats[0][i]), bStats[2][i]));
        }
        BarChart<String, Number> bikeStat = new BarChart<>(xAxis,yAxis);
        bikeStat.getData().addAll(distTrav,totTrips,battery);
        stat3Pane.setCenter(bikeStat);
    }
    */
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
