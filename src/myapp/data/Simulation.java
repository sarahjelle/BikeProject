package myapp.data;
import com.sun.tools.javadoc.Start;

import java.util.*;
class Simulation implements Runnable{
    private Bike[] bikes;
    private Docking[] docking_stations;
    private int updateInterval = 60;
    private Boolean stop = false;
    private static final double stepLength = 0.00000898316 ; // 0.00000898316 Difference in radians, a one meter step makes to the geo-coords

    public Simulation(Bike[] bikes, Docking[] docking_stations){
        this.bikes = bikes;
        this.docking_stations = docking_stations;
    }

    public void run() {
        while (!stop) {
            long StartTime = System.currentTimeMillis();
            long elapsedTimeMilliSeconds = 0;

            // Need this to update distance traveled
            Location[] StartLocations = new Location[bikes.length];
            for (int i = 0; i < bikes.length; i++) {
                //Location bike_loc = bikes[i].getLocation();
                StartLocations[i] = bikes[i].getLocation();//new Location(bike_loc.getName(), bike_loc.getLatitude(), bike_loc.getLongitude());
            }

            Location[] EndLocations = new Location[bikes.length];
            Random rand = new Random();
            for (int i = 0; i < bikes.length; i++) {
                // Choose random start- and endpoint (docking_station locations) for all bikes
                // And get the best route according to the Google Maps API
                // EndLocations[i] = new Location();

                do {
                    EndLocations[i] = docking_stations[rand.nextInt(docking_stations.length)].getLocation();
                } while (EndLocations[i] == StartLocations[i]); // To make sure the bike actually moves
            }
            //Remove this!!!
            for (int i = 0; i < bikes.length; i++) {
                System.out.println("bikes[" + i + "] starting at, lat: " + bikes[i].getLocation().getLatitude() + " long: " + bikes[i].getLocation().getLongitude());
            }

            while (elapsedTimeMilliSeconds / 1000 < updateInterval) {
                for (int i = 0; i < bikes.length; i++) {
                    // Move all the bikes in their respective directions, according to Google Maps API route,
                    // in some given step length (2m / 30cm etc). This movement is not drawn to screen here.
                    /*
                    double bikeX = bikes[i].getLocation().getLatitude();
                    double bikeY = bikes[i].getLocation().getLongitude();

                    double endX = EndLocations[i].getLatitude();
                    double endY = EndLocations[i].getLongitude();

                    double dX = endX - bikeX;
                    double dY = endY - bikeY;
                    //System.out.println("dX: " + dX + " dY: " + dY);
                    //System.out.println("dY/dX = " + (dY/dX));

                    double movementX = stepLength * Math.cos(Math.atan(dY / dX));
                    double movementY = stepLength * Math.sin(Math.atan(dY / dX));

                    //MovementVector = [movementX, movementY]
                    //double movementLenght = Math.sqrt(Math.pow(movementX, 2) + Math.pow(movementY, 2));
                    //System.out.println(movementLenght);

                    bikes[i].getLocation().setLatitude(bikeX + movementX);
                    bikes[i].getLocation().setLongitude(bikeY + movementY);
                    */
                    Double[] StartPoint = {bikes[i].getLocation().getLatitude(), bikes[i].getLocation().getLongitude()};
                    Double[] EndPoint = {EndLocations[i].getLatitude(), EndLocations[i].getLongitude()};

                    Double[] AB = {EndPoint[0] - StartPoint[0], EndPoint[1] - StartPoint[1]};
                    Double[] ab = {AB[0] * stepLength, AB[1] * stepLength};
                    //double abLength = Math.sqrt(Math.pow(ab[0], 2) + Math.pow(ab[1], 2));
                    //System.out.println(abLength);
                    Double[] newPos = {StartPoint[0] + ab[0], StartPoint[1] + ab[1]};

                    bikes[i].getLocation().setLatitude(newPos[0]);
                    bikes[i].getLocation().setLongitude(newPos[1]);


                    if(bikes[i].getLocation().equals(EndLocations[i])){
                        //Bike has finished moving
                        System.out.println("bikes[" + i + "] has arrived at " + EndLocations[i]);
                    } else{
                        System.out.println(bikes[i].getLocation());
                    }
                }

                long EndTime = System.currentTimeMillis();
                elapsedTimeMilliSeconds = EndTime - StartTime;
            }
            for (int i = 0; i < bikes.length; i++) {
                // Update distance traveled in bikes
                double startX = StartLocations[i].getLatitude();
                double startY = StartLocations[i].getLongitude();

                double endX = EndLocations[i].getLatitude();
                double endY = EndLocations[i].getLongitude();

                double distance = getDistance(startX, startY, endX, endY);
                bikes[i].setDistanceTraveled((int) distance);

                System.out.println(bikes[i].getLocation().getLatitude() + " " + bikes[i].getLocation().getLongitude());
                // Update bikes locations in the database with the Database client class
                //DATABASEMANAGEROBJECT db = new DATABASEMANAGEROBJECT(); // Change this to the db manager
            }
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

    public static double getDistance(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
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
        Docking[] docking_stations = new Docking[2];
        docking_stations[0] = new Docking(1, "Kalvskinnet", new Location("NTNU Kalvskinnet", true), 50);
        docking_stations[1] = new Docking(2, "Moholt", new Location("Moholt Studentby", true), 20);

        System.out.println(docking_stations[0].getLocation().getName() + " " + docking_stations[0].getLocation().getLatitude() + " " + docking_stations[0].getLocation().getLongitude());
        System.out.println(docking_stations[1].getLocation().getName() + " " + docking_stations[1].getLocation().getLatitude() + " " + docking_stations[1].getLocation().getLongitude());

        Bike[] bikes = new Bike[1];
        bikes[0] = new Bike(1, "Trek", 100, 0, docking_stations[0].getLocation());

        Simulation sim = new Simulation(bikes, docking_stations);
        sim.setUpdateInterval(3);

        Thread simThread = new Thread(sim);
        simThread.start();

    }
}

