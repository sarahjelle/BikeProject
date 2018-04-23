package myapp.GUIfx.Statistic;

import myapp.data.Bike;
import myapp.data.Docking;
import myapp.dbhandler.DBH;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class connects to the database through a DBH-object. From the database, an arraylist with all bikes
 * are generated and an araylist with all docking stations and the docked bikes.
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

    /**
     * A method to create a matrix of Objects that represents all docking stations, with the corresponding
     * capacity and occupied slots on that docking station.
     * @return Object[][] list of lists with docking station name, capacity and occupied slots.
     */
    public Object[][] dockingStatistics() {
        Object[][] stat1list = new Object[3][docks.size()];
        for (int i = 0; i < docks.size(); i++) {
            stat1list[0][i] = docks.get(i).getName().split(",")[0];
            stat1list[1][i] = docks.get(i).getCapacity();
            stat1list[2][i] = docks.get(i).getUsedSpaces();
        }
        return stat1list;
    }

    /**
     * A method that loops through all bikes in the bike-Arraylist and count the number of bikes with status
     * on trip, in repair or available.
     * @return int[] a list of the number of bikes that are on trip, in repair or available
     */
    public int[] bikeAvailability(){
        int[] availStats = new int[3];
        for (Bike b : bikes){
            // On trip
            if (b.getStatus() == 2){
                availStats[0]++;
            }
            // In repair
            if (b.getStatus() == 3){
                availStats[1]++;
            }
            // Available
            if (b.getStatus() == 1){
                availStats[2]++;
            }
        }
        return availStats;
    }

    /**
     * A method that calculates the average number of total km travelled by the bikes and the average number of trips
     * taken by the bikes docked at each docking station.
     * @return Object[][] a list of lists containing all docking stations and the average number of total km and
     * number of trips taken by the bikes at each station.
     */
    public Object[][] bikeStats(){
        Object[][] bikeStatistics = new Object[3][docks.size()];
        int totkm=0;
        int tottrips=0;
        double aveKm=0;
        double aveTrip=0;
        Bike[] bikesAtDock;
        for (int i=0; i<docks.size(); i++){
            totkm = 0;
            tottrips = 0;
            aveKm=0;
            aveTrip=0;
            if (docks.get(i)!=null && docks.get(i).getBikes()!=null) {
                bikesAtDock = docks.get(i).getBikes();
                int count=0;
                for (int j = 0; j < bikesAtDock.length; j++) {
                    if (bikesAtDock[j] != null) {
                        count++;
                        totkm += (double)bikesAtDock[j].getDistanceTraveled();
                        tottrips += (double)bikesAtDock[j].getTotalTrips();
                    }
                }
                if(count > 0) {
                    aveKm = totkm/count;
                    aveTrip = tottrips/count;
                }
            }
            bikeStatistics[0][i] = docks.get(i).getName().split(",")[0];
            bikeStatistics[1][i] = aveKm;
            bikeStatistics[2][i] = aveTrip;
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
