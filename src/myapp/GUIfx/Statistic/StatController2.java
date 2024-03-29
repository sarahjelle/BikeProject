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

import java.lang.management.PlatformManagedObject;

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
                                    new PieChart.Data("On trip: "+bikeAv[0], bikeAv[0]),
                                    new PieChart.Data("In repair: "+bikeAv[1], bikeAv[1]),
                                    new PieChart.Data("Available: "+bikeAv[2], bikeAv[2]));
                    pieChart = new PieChart(pieChartData);
                    pieChart.setTitle("Bike statuses for all bikes");

                    Platform.runLater(() -> {
                        stat1Pane.setCenter(pieChart);
                    });
                } else{
                    //pieChart.getData().clear();
                    int valueCounter = 0;
                    String[] names = {"On trip: "+ bikeAv[0],
                            "In repair: " + bikeAv[1],
                            "Available: " + bikeAv[2]};

                    for(final PieChart.Data data : pieChart.getData()){
                        final int counter = valueCounter;
                        Platform.runLater(() -> {
                            data.setName(names[counter]);
                        });
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
        private int currentNumOfXYDataPoints=0;

        public DockingChartUpdater(){}

        public DockingChartUpdater(int UPDATE_INTERVAL){
            this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        }

        public void run(){
            while(!stop){
                Object[][] dockStats = stats.dockingStatistics();
                if(dockStat == null){
                    CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Docking station name");
                    NumberAxis yAxis = new NumberAxis(0,100,5);
                    yAxis.setLabel("Slots");
                    XYChart.Series<String, Number> cap = new XYChart.Series();
                    XYChart.Series<String, Number> taken = new XYChart.Series();
                    for (int i=0; i<dockStats[0].length; i++){
                        cap.getData().add(new XYChart.Data(dockStats[0][i], dockStats[1][i]));
                        taken.getData().add(new XYChart.Data(dockStats[0][i],dockStats[2][i]));
                    }
                    currentNumOfXYDataPoints=cap.getData().size();
                    dockStat = new BarChart<>(xAxis,yAxis);
                    dockStat.getData().addAll(cap,taken);
                    cap.setName("Station capacity");
                    taken.setName("Occupied slots");
                    Platform.runLater(() -> {
                        stat2Pane.setCenter(dockStat);
                    });
                }else{
                    // Check for change in columns
                    if (dockStats[0].length < currentNumOfXYDataPoints) {
                        XYChart.Series<String, Number> capSeries = dockStat.getData().get(0);
                        XYChart.Series<String, Number> takenSeries = dockStat.getData().get(1);

                        for (int i = 0; i < capSeries.getData().size(); i++) {
                            XYChart.Data<String, Number> capObj = capSeries.getData().get(i);
                            XYChart.Data<String, Number> takenObj = takenSeries.getData().get(i);
                            boolean present = false;
                            for (int j = 0; j < dockStats[0].length; j++) {
                                if(capObj.getXValue().toString().equals((String) dockStats[0][j])){
                                    present = true;
                                    break;
                                }
                            }
                            if(!present){
                                capSeries.getData().remove(capObj);
                                takenSeries.getData().remove(takenObj);
                            }
                        }
                    }
                    else if (dockStats[0].length > currentNumOfXYDataPoints){
                        // Columns have been added
                        XYChart.Series<String, Number> capSeries = dockStat.getData().get(0);
                        XYChart.Series<String, Number> takenSeries = dockStat.getData().get(1);
                        for (int i = 0; i < dockStats[0].length; i++){
                            //XYChart.Data<String, Number> capObj = capSeries.getData().get(i);
                            //XYChart.Data<String, Number> takenObj = takenSeries.getData().get(i);
                            boolean present = false;
                            for (int j = 0; j < capSeries.getData().size(); j++) {
                                if(capSeries.getData().get(j).getXValue().toString().equals((String) dockStats[0][i])){
                                    present = true;
                                    break;
                                }
                            }
                            if(!present){
                                // Add column
                                final int counter = i;
                                Platform.runLater(() -> {
                                    capSeries.getData().add(new XYChart.Data<String, Number>((String)dockStats[0][counter], (Number)dockStats[1][counter]));
                                    takenSeries.getData().add(new XYChart.Data<String, Number>((String)dockStats[0][counter], (Number)dockStats[2][counter]));
                                });

                                //capObj.setYValue((Number)dockStats[1][i]);
                                //takenObj.setYValue((Number)dockStats[2][i]);
                            }

                            //dockStat.getData().get(0).getData().add(new XYChart.Data(dockStats[0][i], dockStats[1][i]));
                            //dockStat.getData().get(1).getData().add(new XYChart.Data(dockStats[0][i], dockStats[2][i]));
                        }
                    }
                    // Update existing columns
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
                        valueCounter=0;
                        seriesCounter++;
                        valueCounter = 0;
                    }
                    currentNumOfXYDataPoints = dockStat.getData().get(0).getData().size();
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
                if(kmStat == null){
                    final CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Docking station");
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Average value");
                    ScatterChart.Series<String, Number> totkm = new ScatterChart.Series<>();
                    totkm.setName("Average total km for docked bikes");
                    for (int i=0; i<bStats[0].length; i++){
                        totkm.getData().add(new ScatterChart.Data<String, Number>((String)bStats[0][i], (Number)bStats[1][i]));
                    }
                    last_update_size = totkm.getData().size();
                    kmStat = new ScatterChart<>(xAxis,yAxis);
                    kmStat.getData().addAll(totkm);
                    Platform.runLater(() -> {
                        stat3Pane.setCenter(kmStat);
                    });
                } else{
                    if(bStats[0].length > last_update_size){
                        // Add new stations
                        ScatterChart.Series<String, Number> series = kmStat.getData().get(0);

                        for (int i = series.getData().size(); i < bStats[0].length; i++) {
                            final int counter = i;
                            Platform.runLater(() -> {
                                series.getData().add(
                                        new ScatterChart.Data<String, Number>(
                                                (String) bStats[0][counter],
                                                (Number)bStats[1][counter]
                                        )
                                );
                            });
                        }
                    } else if(bStats[0].length < last_update_size){
                        // Remove stations existing on graph that are not present in bStats
                        ScatterChart.Series<String, Number> series = kmStat.getData().get(0);

                        for (int i = 0; i < series.getData().size(); i++) {
                            ScatterChart.Data<String, Number> data = series.getData().get(i);
                            boolean present = false;
                            for (int j = 0; j < bStats[0].length; j++) {
                                if(data.getXValue().toString().equals((String) bStats[0][j])){
                                    present = true;
                                    break;
                                }
                            }
                            if(!present){
                                series.getData().remove(data);
                            }
                        }
                    }
                    // Update stations values
                    ScatterChart.Series<String, Number> series = kmStat.getData().get(0);
                    int valueCounter = 0;
                    for (int i = 0; i < series.getData().size(); i++) {
                        ScatterChart.Data<String, Number> data = series.getData().get(i);
                        data.setYValue((Number)bStats[1][valueCounter]);
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
                    final CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("Docking station");
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Average value");
                    ScatterChart.Series<String, Number> tottrip = new ScatterChart.Series<String, Number>();
                    tottrip.setName("Average number of total trips for docked bikes");
                    for (int i=0; i<bStats[0].length; i++){
                        tottrip.getData().add(new ScatterChart.Data<String, Number>( (String)bStats[0][i], (Number)bStats[2][i]));
                    }
                    last_update_size = bStats[0].length;//tottrip.getData().size();
                    tripStat = new ScatterChart<>(xAxis,yAxis);
                    tripStat.getData().addAll(tottrip);
                    Platform.runLater(() -> {
                        stat4Pane.setCenter(tripStat);
                    });
                } else{
                    if (bStats[0].length > last_update_size) {
                        // Add new stations
                        ScatterChart.Series<String, Number> series = tripStat.getData().get(0);

                        for (int i = series.getData().size(); i < bStats[0].length; i++) {
                            System.out.println("Adding new data-point");
                            final int counter = i;
                            Platform.runLater(() -> {
                                series.getData().add(
                                        new ScatterChart.Data<String, Number>(
                                                (String) bStats[0][counter],
                                                (Number)bStats[2][counter]
                                        )
                                );
                            });
                        }
                        /*
                        // Stations have been removed
                        tripStat.getData().get(0).getData().removeAll();
                        tripStat.getData().get(1).getData().removeAll();
                        // Adds datapoints to the series
                        for (int i = 0; i < bStats[0].length; i++) {
                            tripStat.getData().get(0).getData().add(new XYChart.Data(bStats[0][i], bStats[2][i]));
                        }
                        */
                    }
                    else if (bStats[0].length < last_update_size){
                        // Remove stations existing on graph that are not present in bStats
                        ScatterChart.Series<String, Number> series = tripStat.getData().get(0);

                        for (int i = 0; i < series.getData().size(); i++) {
                            ScatterChart.Data<String, Number> data = series.getData().get(i);
                            boolean present = false;
                            for (int j = 0; j < bStats[0].length; j++) {
                                if(data.getXValue().toString().equals((String) bStats[0][j])){
                                    present = true;
                                    break;
                                }
                            }
                            if(!present){
                                Platform.runLater(() -> {
                                    series.getData().remove(data);
                                });
                            }
                        }
                        /*
                        // Stations have been added
                        for (int i = tripStat.getData().get(0).getData().size(); i < bStats[0].length; i++) {
                            tripStat.getData().get(0).getData().add(new XYChart.Data(bStats[0][i], bStats[2][i]));
                        }
                        */
                    }
                    // Update stations values
                    ScatterChart.Series<String, Number> series = tripStat.getData().get(0);
                    int valueCounter = 0;
                    for (int i = 0; i < series.getData().size(); i++) {
                        ScatterChart.Data<String, Number> data = series.getData().get(i);
                        data.setYValue((Number)bStats[2][valueCounter]);
                        valueCounter++;
                    }
                }
                last_update_size = bStats[0].length; //tripStat.getData().get(0).getData().size();

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
