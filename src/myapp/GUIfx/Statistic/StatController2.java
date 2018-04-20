package myapp.GUIfx.Statistic;

import javafx.application.Platform;
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
import myapp.data.Bike;

public class StatController2 {
    @FXML
    private BorderPane statPane;
    @FXML
    private TextField bikeIdInput;
    @FXML
    private BorderPane stat1Pane;
    private PieChart pieChart;
    @FXML
    private BorderPane stat2Pane;
    private BarChart<String, Number> dockStat;
    @FXML
    private BorderPane stat3Pane;
    private ScatterChart<String,Number> bikeStat;
    @FXML
    private GenerateStats stats = new GenerateStats();

    private BikeAvailiabilityUpdater bau;
    private Thread bauThread;

    private DockingChartUpdater dcu;
    private Thread dcuThread;

    private BikeBatteryUpdater bbu;
    private Thread bbuThread;


    public void initialize() {
        openStat1();
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
        stat2();
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
        if(bau == null){
            bau = new BikeAvailiabilityUpdater();
        }
        if(bauThread == null){
            bauThread = new Thread(bau);
            bauThread.start();
        }
    }

    public void stat2(){
        if(dcu == null){
            dcu = new DockingChartUpdater();
        }
        if(dcuThread == null){
            dcuThread = new Thread(dcu);
            dcuThread.start();
        }
    }

    public void stat3(){
        if(bbu == null){
            bbu = new BikeBatteryUpdater();
        }
        if(bbuThread == null){
            bbuThread = new Thread(bbu);
            bbuThread.start();
        }
    }

    private class BikeAvailiabilityUpdater implements Runnable{
        private Boolean stop = false;
        private int UPDATE_INTERVAL = 5000; //ms

        public BikeAvailiabilityUpdater(){}

        public BikeAvailiabilityUpdater(int UPDATE_INTERVAL){
            this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        }

        public void run(){
            while(!stop){
                int[] bikeAv = stats.bikeAvailability();
                if(pieChart == null){
                    ObservableList<PieChart.Data> pieChartData =
                            FXCollections.observableArrayList(
                                    new PieChart.Data("On trip", bikeAv[1]),
                                    new PieChart.Data("In repair", bikeAv[2]),
                                    new PieChart.Data("Available", bikeAv[0]));
                    pieChart = new PieChart(pieChartData);
                    pieChart.setTitle("");

                    Platform.runLater(() -> {
                        stat1Pane.setCenter(pieChart);
                    });
                } else{
                    //pieChart.getData().clear();
                    int valueCounter = 0;
                    for(final PieChart.Data data : pieChart.getData()){
                        data.setPieValue(bikeAv[valueCounter]);
                        valueCounter++;
                    }
                }
                try{
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }

        public void stop(){
            this.stop = true;
        }


    }

    private class DockingChartUpdater implements Runnable{
        private Boolean stop = false;
        private int UPDATE_INTERVAL = 5000; //ms

        public DockingChartUpdater(){}

        public DockingChartUpdater(int UPDATE_INTERVAL){
            this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        }

        public void run(){
            while(!stop){
                Object[][] dockStats = stats.dockingStatistics();

                if(dockStat == null){
                    CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Docking station");
                    NumberAxis yAxis = new NumberAxis(0,100,5);
                    yAxis.setLabel("Slots");
                    XYChart.Series<String, Number> cap = new XYChart.Series();
                    XYChart.Series<String, Number> taken = new XYChart.Series();
                    for (int i=0; i<dockStats[0].length; i++){
                        cap.getData().add(new XYChart.Data(dockStats[0][i], dockStats[1][i]));
                        taken.getData().add(new XYChart.Data(dockStats[0][i],dockStats[2][i]));
                    }
                    dockStat = new BarChart<>(xAxis,yAxis);
                    dockStat.getData().addAll(cap,taken);
                    cap.setName("Total number of slots");
                    taken.setName("Occupied slots");
                    Platform.runLater(() -> {
                        stat2Pane.setCenter(dockStat);
                    });
                } else{
                    // Update already present columns
                    // HJELP MARTIN, nå er det to series som må oppdateres
                    int valueCounter = 0;
                    int seriesCounter = 0;
                    for(XYChart.Series<String, Number> data : dockStat.getData()){
                        for(XYChart.Data<String, Number> d : data.getData()){
                            if(seriesCounter == 0){
                                d.setYValue((int)dockStats[1][valueCounter]);
                            } else if(seriesCounter == 1){
                                d.setYValue((int)dockStats[2][valueCounter]);
                            }
                            valueCounter++;
                        }
                        seriesCounter++;
                    }
                    // Add columns that are not present
                    for(int i = dockStat.getData().size(); i < dockStats[0].length; i++){
                        // TO SERIER, virker dette??
                        // There is only one data-series, so get(0) works
                        dockStat.getData().get(0).getData().add(new XYChart.Data(dockStats[0][i], dockStats[1][i]));
                        dockStat.getData().get(1).getData().add(new XYChart.Data(dockStats[0][i], dockStats[2][i]));
                    }
                }




                try{
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }

        public void stop(){
            this.stop = true;
        }
    }

    private class BikeBatteryUpdater implements Runnable{
        private Boolean stop = false;
        private int UPDATE_INTERVAL = 5000; //ms
        private int last_update_size = 0;

        public BikeBatteryUpdater(){}

        public BikeBatteryUpdater(int UPDATE_INTERVAL){
            this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        }

        public void run(){
            while(!stop){
                Object[][] bStats = stats.bikeStats();
                //int[][] bStats = {{1,2,3,4,5,6,7,8,9,10}, {1,12,16,11,9,3,1,6,3,9}, {1,3,6,2,4,3,1,2,8,2}};
                if(bikeStat == null){
                    last_update_size = bStats[0].length;
                    final CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Docking station");
                    NumberAxis yAxis = new NumberAxis(0,1,.1);
                    yAxis.setLabel("Average value");
                    XYChart.Series totkm = new XYChart.Series();
                    totkm.setName("Average total km for docked bikes");
                    XYChart.Series tottrip = new XYChart.Series();
                    tottrip.setName("Average number of total trips for docked bikes");
                    for (int i=0; i<bStats[0].length; i++){
                        totkm.getData().add(new XYChart.Data(bStats[0][i], bStats[1][i]));
                        tottrip.getData().add(new XYChart.Data(bStats[0][i], bStats[2][i]));
                    }
                    bikeStat = new ScatterChart<>(xAxis,yAxis);
                    bikeStat.getData().addAll(totkm,tottrip);
                    Platform.runLater(() -> {
                        stat3Pane.setCenter(bikeStat);
                    });
                } else if(bStats[0].length != last_update_size){
                    //More bikes has been registered
                    for (int i = bikeStat.getData().get(0).getData().size(); i < bStats[0].length; i++) {
                        bikeStat.getData().get(0).getData().add(new XYChart.Data(bStats[0][i], bStats[1][i]));
                        bikeStat.getData().get(1).getData().add(new XYChart.Data(bStats[0][i], bStats[2][i]));
                    }
                } else{
                    int valueCounter = 0;
                    int seriesCounter = 0;
                    int bikeCounter = 0;
                    for(final XYChart.Series<String, Number> dataSeries : bikeStat.getData()){
                        for(final XYChart.Data<String, Number> data : dataSeries.getData()) {
                            if (seriesCounter == 0) {
                                data.setYValue((double)bStats[1][valueCounter]);
                            } else if (seriesCounter == 1) {
                                data.setYValue((double)bStats[2][valueCounter]);
                            }
                            valueCounter++;
                        }
                        seriesCounter++;
                    }
                }

                last_update_size = bStats[0].length;

                try{
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }

        public void stop(){
            this.stop = true;
        }
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
