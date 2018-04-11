package myapp.GUIfx.Statistic;

import myapp.data.Bike;
import myapp.data.Docking;
import myapp.dbhandler.DBH;

import java.util.ArrayList;

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

    public GenerateStats() {
        dbh = new DBH();
        bikes = dbh.getAllBikesDummyLocation();
        docks = dbh.getAllDockingStations();
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
            dockCap[i] = docks.get(i).getCapacity();
        }
        return dockCap;
    }

    public int[] bikeAvailability(){
        int[] availStats = new int[3];
        for (Bike b : bikes){
            if (b.getStatus()==1){
                availStats[0]++;
            }
            if (b.getStatus()==2){
                availStats[1]++;
            }
            if (b.getStatus()==3){
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
}
