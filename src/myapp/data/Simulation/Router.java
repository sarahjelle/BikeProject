package myapp.data.Simulation;
import myapp.GUIfx.Map.*;
import myapp.data.*;
import myapp.dbhandler.*;

import javax.print.Doc;

public class Router implements Runnable{
    private Boolean stop = false;
    private final User customer;
    private final Bike bikeToMove;
    private Location start;
    private Docking startStation;
    private Docking end;
    private Location[] WayPoints; //WayPoints[0] = start, WayPoints[WayPoints.length -1] = end
    private boolean hasArrived = false;
    private boolean isDocked = false;
    private static int UPDATE_INTERVAL = 60000; //milliseconds

    private MapsAPI map = new MapsAPI();
    private final double AVRG_BIKE_SPEED = 0.4305; // m/s
    private static final double ERROR_TOLERANCE = 0.0000001;
    private int WayPointsIterator = 1;

    private long StartTime = -1; //Time when starting a step
    private long TotalStartTime = -1;
    private long TotalTime = -1;

    public Router(User customer, Bike bikeToMove, Docking start, Docking end){
        this.customer = customer;
        this.bikeToMove = bikeToMove;
        this.start = bikeToMove.getLocation();
        this.end = end;
        this.WayPoints = getWayPoints();
        if(WayPoints.length <= 0){
            hasArrived = true;
            stop = true;
        }
        this.startStation = start;
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
                    Bike[] arr = {bikeToMove};
                    Bike[] ret = handler.logBikes(arr);
                    if(ret == null || ret.length <= 0){
                        System.out.println("Error updating bike location to DB: " + bikeToMove.toString());
                    }
                    StartTime = System.currentTimeMillis();
                }
            } else{
                stop = true;
            }
        }
    }

    public void stop(){
        this.stop = true;
    }

    public void move(){
        if(TotalStartTime < 0){
            TotalStartTime = System.currentTimeMillis();
        }
        if(!hasArrived){
            //Move bike
            System.out.println(end.getLocation());
            System.out.println(start);
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
                    //double newAlt = map.getAltitude(newLat, newLng);
                    //String address = map.getAddress(newLat, newLng);
                    //Location actNewLoc = map.SnapToRoad(new Location(null, newLat, newLng));
                    bikeToMove.setLocation(new Location(null, newLat, newLng));


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
            }
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
            for (int i = 0; i < WP.length; i++) {
                System.out.println(WP[i]);
            }
        }
        return WP;
    }

    /*public void resetStartLocation(){
        this.start = bikeToMove.getLocation();
    }*/

    public void resetHasArrived(){
        this.hasArrived = false;
    }

    public void setRunnable(){
        this.stop = false;
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
}
