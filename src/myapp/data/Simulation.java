package myapp.data;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import myapp.map.MapsAPI;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Simulation implements Runnable{
    private Bike[] bikes;
    private Docking[] docking_stations;
    private int updateInterval = 60;
    private Boolean stop = false;
    private static final double stepLength = 0.0000003; // DO NOT TOUCH!!!
    private static final double ERROR_TOLERANSE = 0.0000001;
    private static final double AVRG_BIKE_SPEED = 15.5; // km/h



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
        Location[] StartLocations = getStartLocations(currentlyMovingBikes); // Bikes move from these locations
        Location[] EndLocations = getEndLocations(currentlyMovingBikes, StartLocations); // Bikes move to these locations

        Location[][] routes = new Location[currentlyMovingBikes.length][0];
        MapsAPI map = new MapsAPI();
        for (int i = 0; i < routes.length; i++) {
            Location[] steps = map.getWayPoints(StartLocations[i], EndLocations[i]);
            routes[i] = steps;
        }


        while (!stop) {
            long startTime = System.currentTimeMillis();

            currentlyMovingBikes = moveBikes(currentlyMovingBikes, EndLocations); //Bikes are moved here

            if(currentlyMovingBikes.length == 0){
                // All bikes has arrived
                System.out.println("All bikes have arrived");
                try{
                    Thread.sleep(3000); //Wait the remaining time until the update interval
                } catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                updateDistances(bikes, StartLocations, EndLocations);
                currentlyMovingBikes = getNewSubset();
                StartLocations = getStartLocations(currentlyMovingBikes);
                EndLocations = getEndLocations(currentlyMovingBikes, StartLocations);

                routes = new Location[currentlyMovingBikes.length][0];
                for (int i = 0; i < routes.length; i++) {
                    Location[] steps = map.getWayPoints(StartLocations[i], EndLocations[i]);
                    routes[i] = steps;
                }
            }

            long currentElapsedTime = System.currentTimeMillis() - startTime;
            try{
                long sleeptime = updateInterval * 1000 - (currentElapsedTime);
                if(sleeptime > 0){
                    Thread.sleep(sleeptime); //Wait the remaining time until the update interval
                }
                //Update bike values in db
            } catch(InterruptedException ex){
                ex.printStackTrace();
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

    public Location[] getEndLocations(Bike[] currentlyMovingBikes, Location[] StartLocations){
        Location[] EndLocations = new Location[currentlyMovingBikes.length];
        Random rand = new Random();
        for (int i = 0; i < currentlyMovingBikes.length; i++) {
            // Choose random start- and endpoint (docking_station locations) for all bikes
            double diffX = 1;
            double diffY = 1;
            do {
                System.out.println("Getting ends");
                EndLocations[i] = docking_stations[rand.nextInt(docking_stations.length)].getLocation();
                EndLocations[i].setAltitude(0.0);
                diffX = EndLocations[i].getLatitude() - StartLocations[i].getLatitude();
                diffY = EndLocations[i].getLongitude()- StartLocations[i].getLongitude();
            } while (EndLocations[i] == StartLocations[i] || (Math.abs(diffX) < ERROR_TOLERANSE && Math.abs(diffY) < ERROR_TOLERANSE )); // To make sure the bike actually moves
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

    public Bike[] moveBikes(Bike[] currentlyMovingBikes, Location[] EndLocations){
        DecimalFormat dfLat = new DecimalFormat("###.#######");
        DecimalFormat dfLong = new DecimalFormat("###.######");

        for (int i = 0; i < currentlyMovingBikes.length; i++) {
            // Move all the bikes in their respective directions, according to Google Maps API route,
            // in some given step length (2m / 30cm etc). This movement is not drawn to screen here.

            Double[] StartPoint = {currentlyMovingBikes[i].getLocation().getLatitude(), currentlyMovingBikes[i].getLocation().getLongitude()};
            Double[] EndPoint = {EndLocations[i].getLatitude(), EndLocations[i].getLongitude()};

            Double[] AB = {EndPoint[0] - StartPoint[0], EndPoint[1] - StartPoint[1]};

            double ABLength = Math.sqrt(Math.pow(AB[0], 2) + Math.pow(AB[1], 2));


            double k =  ABLength / stepLength;
            double a = (AB[0] / k);
            double b = (AB[1] / k);


            Double[] ab = {a, b};
            Double[] newPos = {StartPoint[0] + ab[0], StartPoint[1] + ab[1]};
            //System.out.println(ab[0] + " " + ab[1]);

            //currentlyMovingBikes[i].getLocation().setLatitude(Double.parseDouble(dfLat.format(newPos[0])));
            //currentlyMovingBikes[i].getLocation().setLongitude(Double.parseDouble(dfLong.format(newPos[1])));
            currentlyMovingBikes[i].getLocation().setLatitude(newPos[0]);
            currentlyMovingBikes[i].getLocation().setLongitude(newPos[1]);
            currentlyMovingBikes[i].getLocation().setAltitude(0.0);

            //System.out.println(EndLocations[i]);
            double diffX = currentlyMovingBikes[i].getLocation().getLatitude() - EndLocations[i].getLatitude();
            double diffY = currentlyMovingBikes[i].getLocation().getLongitude() - EndLocations[i].getLongitude();
            if (Math.abs(diffX) < ERROR_TOLERANSE && Math.abs(diffY) < ERROR_TOLERANSE) {
                //Bike has finished moving
                System.out.println("bikes[" + i + "] has arrived at " + EndLocations[i].getName());
                currentlyMovingBikes = removeBikeFromSubset(currentlyMovingBikes, i);
            } else {
                System.out.println();
                System.out.println("Bike[" + i + "] is at: ");
                System.out.println(currentlyMovingBikes[i].getLocation());
                System.out.println("Bike[" + i + "] is going to: ");
                System.out.println(EndLocations[i]);
                System.out.println();
            }
        }
        return currentlyMovingBikes;
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

    public static double getDistance(Location loc1, Location loc2){  // generally used geo measurement function
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
        docking_stations[0] = new Docking(1, "Kalvskinnet", new Location("NTNU Kalvskinnet", true), 50);
        docking_stations[1] = new Docking(2, "Munkholmen", new Location("Munkholmen", true), 20);
        docking_stations[2] = new Docking(2, "Martin hjem", new Location("Bautavegen 3, 7056 Ranheim", true), 20);

        Bike[] bikes = new Bike[1];
        //int id, String make, double batteryPercentage, boolean available, int distanceTraveled, Location location
        bikes[0] = new Bike(1, "Trek", 100, "Electric",0, 0, docking_stations[0].getLocation());

        Simulation sim = new Simulation(bikes, docking_stations);
        sim.setUpdateInterval(1);

        Thread simThread = new Thread(sim);
        simThread.start();
    }
}

