package myapp.data.Simulation;

import myapp.GUIfx.Map.*;
import myapp.data.*;
import myapp.dbhandler.*;

import java.util.Random;

public class Router implements Runnable{
    private Boolean stop = false;
    private MapsAPI map = new MapsAPI();
    private final User customer;
    private final Bike bikeToMove;
    private Location start;
    private Docking startStation;
    private Docking end;
    private Location[] WayPoints;
    private boolean hasArrived = false;
    private boolean isDocked = false;
    private static int UPDATE_INTERVAL = 60000; // millis
    private static final double ERROR_TOLERANCE = 0.0000001;
    private static final double AVRG_BIKE_SPEED = 0.4305; // m/s
    private static final double POWER_USAGE_PER_S = 0.005; // W / m


    private int WayPointsIterator = 1;
    private long StartTime = -1; //Time when starting a step
    private long TotalStartTime = -1;
    private long TotalTime = -1;

    public Router(User customer, Bike bikeToMove, Docking start, Docking end){
        if(customer == null){
            throw new IllegalArgumentException("Customer is null");
        }
        if(bikeToMove == null){
            throw new IllegalArgumentException("Bike is null");
        }
        if(start == null){
            throw new IllegalArgumentException("Start Docking station is null");
        }
        if(end == null){
            throw new IllegalArgumentException("End Docking station is null");
        }
        this.customer = customer;
        this.bikeToMove = bikeToMove;
        this.start = bikeToMove.getLocation();
        this.end = end;
        this.WayPoints = getWayPoints();
        if(WayPoints == null || WayPoints.length <= 0){
            hasArrived = true;
            stop = true;
        }
        this.startStation = start;
        System.out.println("ROUTER CREATED: ");
        System.out.println("User: " + customer.getUserID() + " " + customer.getFirstname() + " " + customer.getLastname());
        System.out.println("Bike: " + bikeToMove.getId());
        System.out.println("Start station: " + startStation.getId() + " " + startStation.getName());
        System.out.println("End station: " + end.getId() + " " + end.getName());
        System.out.println();
    }

    public void run(){
        long StartTime = System.currentTimeMillis();
        while(!stop){
            if(!hasArrived){
                move();
                try{
                    Thread.sleep(2000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                if((System.currentTimeMillis() - StartTime) >= UPDATE_INTERVAL){
                    //Update loc to DB
                    DBH handler = new DBH();
                    Location actNewLoc = null;
                    try{
                        actNewLoc = map.SnapToRoad(new Location(null, bikeToMove.getLocation().getLatitude(), bikeToMove.getLocation().getLongitude()));
                    } catch (Exception e){
                        // MapsAPI key has expired
                        e.printStackTrace();
                    }
                    if(actNewLoc == null || actNewLoc.getLatitude() == null || actNewLoc.getLongitude() == null){

                    } else{
                        bikeToMove.setLocation(actNewLoc);
                    }

                    System.out.println(bikeToMove.getLocation().getLatitude() + ", " + bikeToMove.getLocation().getLongitude() + " : " + bikeToMove.getBatteryPercentage());
                    Bike[] arr = {bikeToMove};
                    Bike[] ret = handler.logBikes(arr);
                    StartTime = System.currentTimeMillis();
                }
            } else{
                stop = true;
            }
        }
        if(!isDocked){
            if(end.dockBike(bikeToMove)){
                System.out.println();
                System.out.println();
                System.out.println("Router docked bike successfully to end-station after thread forced stop");
                System.out.println("Bike: ");
                System.out.println(bikeToMove.toString());
                System.out.println("End-station:");
                System.out.println(end.toString());
                System.out.println();
                System.out.println();
            } else if(startStation.dockBike(bikeToMove)){
                System.out.println();
                System.out.println();
                System.out.println("Router unsuccessfull in docking to end-station.");
                System.out.println("Router docked bike successfully to start-station after thread forced stop");
                System.out.println("Bike: ");
                System.out.println(bikeToMove.toString());
                System.out.println("Start-station:");
                System.out.println(startStation.toString());
                System.out.println();
                System.out.println();
            } else{
                System.out.println();
                System.out.println();
                System.out.println("Router unsuccessfull in docking to end-station nor to start-station after thread forced stop.");
                System.out.println("Bike: ");
                System.out.println(bikeToMove.toString());
                System.out.println("Start-station:");
                System.out.println(startStation.toString());
                System.out.println("End-station:");
                System.out.println(end.toString());
                System.out.println();
                System.out.println();
            }
        }
    }

    public void stop(){
        this.stop = true;
    }

    private void move(){
        if(TotalStartTime < 0){
            TotalStartTime = System.currentTimeMillis();
        }
        if(!hasArrived){
            //Move bike
            if(WayPointsIterator < WayPoints.length && WayPointsIterator >= 0){
                Location atNow = bikeToMove.getLocation();
                Location nextLocation = WayPoints[WayPointsIterator];
                double distance = getDistance(atNow, nextLocation);
                double timeShouldTake = (distance / AVRG_BIKE_SPEED); //Seconds


                if(StartTime == -1){
                    StartTime = System.currentTimeMillis();
                }

                long ElapsedTimeMillis = System.currentTimeMillis() - StartTime;
                long ElapsedTimeSeconds = ElapsedTimeMillis / 1000;
                double percentageToWayPoint = ElapsedTimeSeconds / timeShouldTake;
                if(percentageToWayPoint >= 1){
                    //Bike should have arrived at WayPoint[WayPointsIterator]
                    WayPointsIterator++;
                    bikeToMove.setLocation(nextLocation);
                    StartTime = -1; //Reset time
                } else{
                    // Bike has not yet arrived at WayPoint[WayPointsIterator]
                    double latAt = atNow.getLatitude();
                    double lngAt = atNow.getLongitude();

                    double latTo = nextLocation.getLatitude();
                    double lngTo = nextLocation.getLongitude();

                    double dLat = latTo - latAt;
                    double dLng = lngTo - lngAt;

                    double latChange = dLat * percentageToWayPoint;
                    double lngChange = dLng * percentageToWayPoint;

                    double newLat = latAt + latChange;
                    double newLng = lngAt + lngChange;

                    Location newLoc = new Location(null, newLat, newLng);
                    bikeToMove.setLocation(newLoc);


                    int dist = (int) getDistance(new Location(latAt, lngAt), new Location(newLat, newLng)) / 1000;
                    System.out.println();
                    Random rand = new Random();
                    double batteryLeft = bikeToMove.getBatteryPercentage() - (POWER_USAGE_PER_S * rand.nextDouble());
                    System.out.println("Distance: " + distance + " BatteryLeft: " + batteryLeft + " Battery decended: " + (POWER_USAGE_PER_S * rand.nextDouble()));
                    bikeToMove.setDistanceTraveled(dist);
                    bikeToMove.setBatteryPercentage(batteryLeft);
                    if(bikeToMove.getBatteryPercentage() < 0.0){
                        bikeToMove.setBatteryPercentage(0.0);
                    }

                    //System.out.println(newLat + ", " + newLng);
                    double checkLat = Math.abs(bikeToMove.getLocation().getLatitude() - nextLocation.getLatitude());
                    double checkLng = Math.abs(bikeToMove.getLocation().getLongitude() - nextLocation.getLongitude());
                    if(checkLat <= ERROR_TOLERANCE && checkLng <= ERROR_TOLERANCE){
                        //Bike has arrived at WayPoint[WayPointsIterator]
                        System.out.println();
                        System.out.println("BIKE HAS ARRIVED AT WAYPOINT:");
                        System.out.println(nextLocation);
                        System.out.println();
                        WayPointsIterator++;
                        bikeToMove.setLocation(nextLocation);
                        StartTime = -1;
                    }
                }
            } else{
                //Bike has arrived
                TotalTime = System.currentTimeMillis() - TotalStartTime;
                long totTimeSecs = TotalTime / 1000;
                System.out.println();
                System.out.println("BIKE HAS ARRIVED AT FINAL LOCATION:");
                System.out.println(end.getLocation());
                System.out.println("AFTER " + totTimeSecs + " SECONDS");
                System.out.println();
                hasArrived = true;
                //Dock to endStation
                isDocked = end.dockBike(bikeToMove);
                System.out.println("ROUTER ABLE TO DOCK TO END: " + isDocked);
            }
        } else{
            isDocked = end.dockBike(bikeToMove);
            System.out.println("ROUTER ABLE TO DOCK TO END: " + isDocked);
        }
    }

    public boolean hasArrived(){
        return hasArrived;
    }

    public boolean hasValidRoute(){
        if(WayPoints == null){
            return false;
        } else if(WayPoints.length == 0){
            return false;
        } else{
            return true;
        }
    }

    public boolean isDocked(){
        return isDocked;
    }

    public void setEnd(Docking station){
        this.start = bikeToMove.getLocation();
        this.end = station;
        this.WayPoints = getWayPoints();
    }

    public Docking getEnd(){
        return end;
    }

    public Bike getBike(){
        return bikeToMove;
    }

    public User getUser(){
        return customer;
    }

    public Location[] getWayPoints(){
        Location[] WP = map.getWayPoints(start, end.getLocation());
        if(WP == null){
            hasArrived = true;
        } else {
        }
        return WP;
    }

    public Docking getStartStation(){
        return startStation;
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

    public static void setUpdateInterval(int updateInterval) {
        UPDATE_INTERVAL = updateInterval;
    }
}
