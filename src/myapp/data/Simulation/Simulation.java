package myapp.data.Simulation;

import java.util.*;
import myapp.dbhandler.*;
import myapp.data.Bike;
import myapp.data.Docking;
import myapp.data.Location;
import myapp.GUIfx.Map.MapsAPI;


public class Simulation implements Runnable{
    private Bike[] bikes;
    private Docking[] docking_stations;
    private int updateInterval = 60000; //milliseconds
    private int sleepTime = 2000; //millieconds
    private Boolean stop = false;
    private static final double ERROR_TOLERANSE = 0.0000001;

    private static final String API_KEY = "AIzaSyA8jBARruH9LiUFxc-DQNLaKRrw6nmyHho";
    private static final String ROADS_API_KEY = "AIzaSyDlJ5qke9-Dw-3-cpk1okWXSXWg3MIRSLc";

    /*
    * Constructor should receive ONLY the bikes that will be moved.
    * And ALL the docking stations available to move to (not repair etc)
    */
    public Simulation(Bike[] bikes, Docking[] docking_stations){
        this.bikes = bikes;
        this.docking_stations = docking_stations;
    }

    public void run(){
        while(!stop){
            // Get subset of bikes
            Bike[] subset = getNewSubset();
            // Choose random end docking stations, no matter where the bikes are
            Docking[] endStations = getEndDockingStations(subset);
            // Get Router objects for all bikes that will move
            Router[] routers = getRouters(subset, endStations);

            boolean allBikesHasArrived = false;
            while(!allBikesHasArrived){
                for (int i = 0; i < routers.length; i++) {
                    if(routers[i].hasValidRoute()){
                        if(!routers[i].hasArrived()){
                            allBikesHasArrived = false;
                            routers[i].move();
                        }
                    }
                }
                try{
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private Bike[] getNewSubset(){
        Bike[] subset = new Bike[(int)(bikes.length * 0.10)]; // 10% of bikes will move;
        Random rand = new Random();
        for (int i = 0; i < subset.length; i++) {
            boolean presentMoreThanOnce = false;
            Bike lookingAt = null;
            do{
                //Choose random bike from "bikes"
                lookingAt = bikes[rand.nextInt(bikes.length)];

                //Check that the bike is not present in the subset more than once
                for (int j = 0; j < subset.length; j++) {
                    if(j != i){
                        if(subset[j] != null){
                            if(subset[i] == lookingAt){
                                presentMoreThanOnce = true;
                            }
                        }
                    }
                }
            } while(!presentMoreThanOnce);
            if(!presentMoreThanOnce){
                subset[i] = lookingAt;
            } else{
                System.out.println("Do-while loop did not work...");
            }
        }
        return subset;
    }

    private Docking[] getEndDockingStations(Bike[] workingSubSet){
        Docking[] endStations = new Docking[workingSubSet.length];
        Random rand = new Random();
        for (int i = 0; i < workingSubSet.length; i++) {
            //Choose random end station from all available docking stations
            endStations[i] = docking_stations[rand.nextInt(docking_stations.length)];
            //Does not matter if several bikes has same end station
        }
        return endStations;
    }

    public Router[] getRouters(Bike[] workingSubSet, Docking[] endStations){
        if(workingSubSet.length == endStations.length){
            Router[] routers = new Router[workingSubSet.length];
            for (int i = 0; i < routers.length; i++) {
                routers[i] = new Router(workingSubSet[i], endStations[i]);
            }
            return routers;
        } else{
            return null;
        }
    }


    /*
    public void run() {
        /*
        * create subset of bikes
        * create start locations
        * create endloc
        * loop start:
            * move bikes
            *   - if ALL bikes has arrived:
            *       - update subset
            *       - update distances
            *       - update start locations
            *       - choose new end locations
            * wait remaining update interval
            *
        * repeat


        DBH handler = new DBH();
        Bike[] currentlyMovingBikes = getNewSubset();
        Location[] startLocations = getStartLocations(currentlyMovingBikes);
        Docking[] endLocations = getEndLocations(currentlyMovingBikes, startLocations);

        Router[] routers = new Router[currentlyMovingBikes.length];
        for (int i = 0; i < routers.length; i++) {
            routers[i] = new Router(currentlyMovingBikes[i], endLocations[i]);
        }

        while (!stop) {
            System.out.println("WORKING");
            long StartTime = System.currentTimeMillis();
            /*Bike[] currentlyMovingBikes = getNewSubset();
            Docking[] EndLocations = getEndLocations(currentlyMovingBikes, docking_stations); // Bikes move to these locations


            boolean allBikesHaveArrived = true;
            for (int i = 0; i < routers.length; i++) {
                if(!routers[i].HasArrived()){
                    allBikesHaveArrived = false;
                }
            }
            if(allBikesHaveArrived){
                currentlyMovingBikes = getNewSubset();
                startLocations = getStartLocations(currentlyMovingBikes);
                endLocations = getEndLocations(currentlyMovingBikes, startLocations);
                routers = new Router[currentlyMovingBikes.length];
                for (int i = 0; i < routers.length; i++) {
                    routers[i] = new Router(currentlyMovingBikes[i], endLocations[i]);
                }
            }

            while(System.currentTimeMillis() - StartTime <= updateInterval){
                for (int i = 0; i < routers.length; i++) {
                    routers[i].move();
                }
                try{
                    Thread.sleep(sleepTime * 1000);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            //Update to DB
            handler.logBikes(currentlyMovingBikes);
            for (int i = 0; i < currentlyMovingBikes.length; i++) {
                //System.out.println(currentlyMovingBikes[i].getLocation().getLatitude() + ", " + currentlyMovingBikes[i].getLocation().getLongitude());
            }
        }
    }

    public Location[] getStartLocations(Bike[] currentlyMovingBikes){
        Location[] StartLocations = new Location[currentlyMovingBikes.length];
        for (int i = 0; i < currentlyMovingBikes.length; i++) {
            StartLocations[i] = currentlyMovingBikes[i].getLocation();
        }
        return StartLocations;
    }

    public Docking[] getEndLocations(Bike[] currentlyMovingBikes, Location[] StartLocations){
        Docking[] EndLocations = new Docking[currentlyMovingBikes.length];
        Random rand = new Random();
        for (int i = 0; i < currentlyMovingBikes.length; i++) {
            // Choose random start- and endpoint (docking_station locations) for all bikes
            double diffX = 1;
            double diffY = 1;
            do {
                EndLocations[i] = docking_stations[rand.nextInt(docking_stations.length)];
                diffX = EndLocations[i].getLocation().getLatitude() - StartLocations[i].getLatitude();
                diffY = EndLocations[i].getLocation().getLongitude()- StartLocations[i].getLongitude();
            } while (EndLocations[i].getLocation() == StartLocations[i] || (Math.abs(diffX) < ERROR_TOLERANSE && Math.abs(diffY) < ERROR_TOLERANSE )); // To make sure the bike actually moves
        }
        return EndLocations;
    }

    public Bike[] removeBikeFromSubset(Bike[] subset, int index){
        if(index < 0 || index >= subset.length){
            return null;
        } else{
            Bike[] output = new Bike[subset.length - 1];
            for (int i = 0; i < subset.length; i++) {
                if(i != index){
                    output[i] = subset[i];
                } else{
                    //skip
                }
            }
            return output;
        }
    }

    public Bike[] getNewSubset(){
        Bike[] currentlyMovingBikes = new Bike[(int)(bikes.length * 0.10)];
        Random rand = new Random();
        for (int i = 0; i < currentlyMovingBikes.length; i++) {
            currentlyMovingBikes[i] = bikes[rand.nextInt(bikes.length)];
        }
        return currentlyMovingBikes;
    }

    public void updateDistances(Bike[] currentlyMovingBikes, Location[] StartLocations, Location[] EndLocations){
        for (int i = 0; i < currentlyMovingBikes.length; i++) {
            // Update distance traveled in bikes
            double startX = StartLocations[i].getLatitude();
            double startY = StartLocations[i].getLongitude();

            double endX = EndLocations[i].getLatitude();
            double endY = EndLocations[i].getLongitude();

            double distance = getDistance(StartLocations[i], EndLocations[i]);
            currentlyMovingBikes[i].setDistanceTraveled((int) distance);

            System.out.println(currentlyMovingBikes[i].getLocation().getLatitude() + " " + currentlyMovingBikes[i].getLocation().getLongitude());
            // Update bikes locations in the database with the Database client class
            //DATABASEMANAGEROBJECT db = new DATABASEMANAGEROBJECT(); // Change this to the db manager
        }
    }

    public Boolean isStopped() {
        return stop;
    }

    public void stop(Boolean stop) {
        System.out.println("Stopping thread");
        this.stop = stop;
    }

    public void setUpdateInterval(int newUpdateInterval){
        this.updateInterval = newUpdateInterval;
    }

    private static double getDistance(Location loc1, Location loc2){  // generally used geo measurement function
        double lat1 = loc1.getLatitude();
        double lon1 = loc1.getLongitude();
        double lat2 = loc2.getLatitude();
        double lon2 = loc2.getLongitude();

        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }
    */
}


class SimTest{
    public static void main(String[]args){
        //int id, String name, Location location, int capacity
        DBH handler = new DBH();

        ArrayList<Docking> list = handler.getAllDockingStations();
        Docking[] docking_stations = new Docking[list.size()];
        docking_stations = list.toArray(docking_stations);

        ArrayList<Bike> bike_list = handler.getAllBikesDummyLocation();
        Bike[] bikes = new Bike[bike_list.size()];
        bikes = bike_list.toArray(bikes);

        for (int i = 0; i < bikes.length; i++) {
            System.out.println(bikes[i].toString() + bikes[i].getLocation().toString());
        }

        System.out.println();
        for (int i = 0; i < docking_stations.length; i++) {
            System.out.println(docking_stations[i].getName());
        }

        Simulation sim = new Simulation(bikes, docking_stations);
        //sim.setUpdateInterval(30);

        Thread simThread = new Thread(sim);
        simThread.start();

    }
}

