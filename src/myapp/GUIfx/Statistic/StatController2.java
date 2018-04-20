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
    private ScatterChart<String,Number> kmStat;
    @FXML
    private BorderPane stat4Pane;
    private ScatterChart<String,Number> tripStat;
    @FXML
    private GenerateStats stats = new GenerateStats();

    private BikeAvailiabilityUpdater bau;
    private Thread bauThread;

    private DockingChartUpdater dcu;
    private Thread dcuThread;

    private AverageKmUpdater aku;
    private Thread akuThread;

    private AverageTripUpdater atu;
    private Thread atuThread;
    

    public void closePane() {
        closeAll();
        statPane.setVisible(false);
    }

    public void openPane() {
        statPane.setVisible(true);
        stat1Pane.setVisible(true);
        stat1();
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

    @FXML private void openStat4() {
        closeAll();
        stat4();
        stat4Pane.setVisible(true);
    }

    @FXML private void closeAll(){
        stat1Pane.setVisible(false);
        stat2Pane.setVisible(false);
        stat3Pane.setVisible(false);
        stat4Pane.setVisible(false);
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
        if(aku == null){
            aku = new AverageKmUpdater();
        }
        if(akuThread == null){
            akuThread = new Thread(aku);
            akuThread.start();
        }
    }

    public void stat4(){
        if(atu == null){
            atu = new AverageTripUpdater();
        }
        if(atuThread == null){
            atuThread = new Thread(atu);
            atuThread.start();
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

    private class AverageKmUpdater implements Runnable{
        private Boolean stop = false;
        private int UPDATE_INTERVAL = 5000; //ms
        private int last_update_size = 0;

        public AverageKmUpdater(){}

        public AverageKmUpdater(int UPDATE_INTERVAL){
            this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        }

        public void run(){
            while(!stop){
                Object[][] bStats = stats.bikeStats();
                //int[][] bStats = {{1,2,3,4,5,6,7,8,9,10}, {1,12,16,11,9,3,1,6,3,9}, {1,3,6,2,4,3,1,2,8,2}};
                if(kmStat == null){
                    last_update_size = bStats[0].length;
                    final CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Docking station");
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Average value");
                    XYChart.Series totkm = new XYChart.Series();
                    totkm.setName("Average total km for docked bikes");
                    for (int i=0; i<bStats[0].length; i++){
                        totkm.getData().add(new XYChart.Data(bStats[0][i], bStats[1][i]));
                    }
                    kmStat = new ScatterChart<>(xAxis,yAxis);
                    kmStat.getData().addAll(totkm);
                    Platform.runLater(() -> {
                        stat3Pane.setCenter(kmStat);
                    });
                } else if(bStats[0].length != last_update_size){
                    //More bikes has been registered
                    for (int i = kmStat.getData().get(0).getData().size(); i < bStats[0].length; i++) {
                        kmStat.getData().get(0).getData().add(new XYChart.Data(bStats[0][i], bStats[1][i]));
                    }
                } else{
                    int valueCounter = 0;
                    int seriesCounter = 0;
                    int bikeCounter = 0;
                    for(final XYChart.Series<String, Number> dataSeries : kmStat.getData()){
                        for(final XYChart.Data<String, Number> data : dataSeries.getData()) {
                                data.setYValue((double)bStats[1][valueCounter]);
                            valueCounter++;
                        }
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

    private class AverageTripUpdater implements Runnable{
        private Boolean stop = false;
        private int UPDATE_INTERVAL = 5000; //ms
        private int last_update_size = 0;

        public AverageTripUpdater(){}

        public AverageTripUpdater(int UPDATE_INTERVAL){
            this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        }

        public void run(){
            while(!stop){
                Object[][] bStats = stats.bikeStats();
                if(tripStat == null){
                    last_update_size = bStats[0].length;
                    final CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Docking station");
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Average value");
                    XYChart.Series tottrip = new XYChart.Series();
                    tottrip.setName("Average number of total trips for docked bikes");
                    for (int i=0; i<bStats[0].length; i++){
                        tottrip.getData().add(new XYChart.Data(bStats[0][i], bStats[2][i]));
                    }
                    tripStat = new ScatterChart<>(xAxis,yAxis);
                    tripStat.getData().addAll(tottrip);
                    Platform.runLater(() -> {
                        stat4Pane.setCenter(tripStat);
                    });
                } else if(bStats[0].length != last_update_size){
                    //More bikes has been registered
                    for (int i = tripStat.getData().get(0).getData().size(); i < bStats[0].length; i++) {
                        tripStat.getData().get(0).getData().add(new XYChart.Data(bStats[0][i], bStats[1][i]));
                    }
                } else{
                    int valueCounter = 0;
                    int seriesCounter = 0;
                    int bikeCounter = 0;
                    for(final XYChart.Series<String, Number> dataSeries : tripStat.getData()){
                        for(final XYChart.Data<String, Number> data : dataSeries.getData()) {
                            data.setYValue((double)bStats[2][valueCounter]);
                        }
                        valueCounter++;
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
