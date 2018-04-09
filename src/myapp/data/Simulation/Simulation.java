package myapp.data.Simulation;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import myapp.data.Bike;
import myapp.data.Docking;
import myapp.data.Location;
import myapp.map.MapsAPI;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.print.Doc;

public class Simulation implements Runnable{
    private Bike[] bikes;
    private Docking[] docking_stations;
    private int updateInterval = 60; //Seconds
    private int sleepTime = 2; //Seconds
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
        */

        Bike[] currentlyMovingBikes = getNewSubset();
        Location[] startLocations = getStartLocations(currentlyMovingBikes);
        Docking[] endLocations = getEndLocations(currentlyMovingBikes, startLocations);

        Router[] routers = new Router[currentlyMovingBikes.length];
        for (int i = 0; i < routers.length; i++) {
            routers[i] = new Router(currentlyMovingBikes[i], endLocations[i]);
        }

        while (!stop) {
            long StartTime = System.currentTimeMillis();
            /*Bike[] currentlyMovingBikes = getNewSubset();
            Docking[] EndLocations = getEndLocations(currentlyMovingBikes, docking_stations); // Bikes move to these locations

            */
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
            for (int i = 0; i < bikes.length; i++) {
                System.out.println(currentlyMovingBikes[i].getLocation().getLatitude() + ", " + currentlyMovingBikes[i].getLocation().getLongitude());
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
        Bike[] currentlyMovingBikes = new Bike[bikes.length];
        for (int i = 0; i < currentlyMovingBikes.length; i++) {
            currentlyMovingBikes[i] = bikes[i];
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
}

class SimTest{
    public static void main(String[]args){
        //int id, String name, Location location, int capacity
        Docking[] docking_stations = new Docking[3];
        docking_stations[0] = new Docking(1, "Rema 1000 Ranheim", new Location("Rema 1000 Ranheim", true), 50);
        docking_stations[1] = new Docking(2, "Coop Prix Ranheim", new Location("Coop Prix Ranheim", true), 20);
        docking_stations[2] = new Docking(2, "Martin hjem", new Location("Bautavegen 3, 7056 Ranheim", true), 20);

        Bike[] bikes = new Bike[1];
        //int id, String make, double batteryPercentage, boolean available, int distanceTraveled, Location location
        bikes[0] = new Bike(1, "Trek", 100, true,0, docking_stations[0].getLocation());

        Simulation sim = new Simulation(bikes, docking_stations);
        sim.setUpdateInterval(3);

        Thread simThread = new Thread(sim);
        simThread.start();
        while (true){
            //System.out.println(bikes[0].getLocation().getLatitude() + ", " + bikes[0].getLocation().getLongitude());
        }
    }
}

