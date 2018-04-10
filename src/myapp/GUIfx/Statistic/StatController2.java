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
    private BorderPane statPane1;
    @FXML
    private BorderPane statPane;
    @FXML
    private TextField bikeIdInput;

    //stat2 attributes;
    @FXML
    private FlowPane stat2Pane;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private BarChart<String, Number> dockStat;

    public void initialize() {
        xAxis.setLabel("Docking station ID");
        yAxis.setLabel("Number of docked bikes");
        XYChart.Series<String, Number> series = new XYChart.Series();
        series.getData().add(new XYChart.Data("1234    ", 12));
        series.getData().add(new XYChart.Data("1235", 15));
        series.getData().add(new XYChart.Data("1236", 10));
        dockStat.getData().add(series);
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
        statPane1.setVisible(true);
    }

    @FXML private void openStat2() {
        closeAll();
        stat2Pane.setVisible(true);
    }

    @FXML private void closeAll(){
        statPane1.setVisible(false);
        stat2Pane.setVisible(false);
    }

    public void stat1() {
        String inputId = bikeIdInput.getText();

        DummyBikeInfo newBike = new DummyBikeInfo(inputId);
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
        statPane1.setCenter(chart);
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
