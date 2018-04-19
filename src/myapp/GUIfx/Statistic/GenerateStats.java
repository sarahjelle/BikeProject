package myapp.GUIfx.Statistic;

import myapp.data.Bike;
import myapp.data.Docking;
import myapp.dbhandler.DBH;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/*
 Status:
         1 = Available;
         2 = In Trip;
         3 = In Repair;
*/

public class GenerateStats {
    private DBH dbh;
    private ArrayList<Bike> bikes;
    private ArrayList<Docking> docks;
    private DataUpdater du;
    private Thread duThread;

    public GenerateStats() {
        this.dbh = new DBH();
        this.bikes = null;
        this.bikes = dbh.getAllBikes();
        this.docks = new ArrayList<Docking>(Arrays.asList(dbh.getAllDockingStationsWithBikes()));
        this.du = new DataUpdater();
        this.duThread = new Thread(du);
        duThread.start();
    }

    public String[] dockingName() {
        String[] dockName = new String[docks.size()];
        for (int i=0; i<docks.size(); i++) {
            dockName[i] = docks.get(i).getName().split(",")[0];
        }
        return dockName;
    }

    public int[] dockCapacity(){
        int[] dockCap = new int[docks.size()];
        for (int i=0; i<docks.size(); i++) {
            dockCap[i] = docks.get(i).getUsedSpaces();
        }
        return dockCap;
    }

    public int[] bikeAvailability(){
        int[] availStats = new int[3];
        for (Bike b : bikes){
            if (b.getStatus() == 1){
                availStats[0]++;
            }
            if (b.getStatus() == 2){
                availStats[1]++;
            }
            if (b.getStatus() == 3){
                availStats[2]++;
            }
        }
        return availStats;
    }

    public int[][] bikeStats(){
        int[][] bikeStatistics = new int[4][bikes.size()];
        for (int i=0; i<bikes.size(); i++){
            bikeStatistics[0][i]=bikes.get(i).getId();
            bikeStatistics[1][i]=bikes.get(i).getDistanceTraveled();
            bikeStatistics[2][i]=bikes.get(i).getTotalTrips();
            bikeStatistics[3][i]=(int)bikes.get(i).getBatteryPercentage();
        }
        return bikeStatistics;
    }

    class DataUpdater implements Runnable{
        public Boolean stop = false;
        private int UPDATE_INTERVAL = 5000; // ms
        public DataUpdater(){}

        public DataUpdater(int UPDATE_INTERVAL){
            this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        }

        public void run(){
            while(!stop){
                //Do work
                bikes = dbh.getAllBikes();
                docks = new ArrayList<>(Arrays.asList(dbh.getAllDockingStationsWithBikes()));
                try{
                    Thread.sleep(UPDATE_INTERVAL);
                } catch(InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }

        public void stop(){
            this.stop = true;
        }
    }
}
