package myapp.data.Simulation;

import java.util.*;
import myapp.dbhandler.*;
import myapp.data.Bike;
import static myapp.data.Bike.*;
import myapp.data.Docking;
import myapp.data.Location;
import myapp.map.MapsAPI;
import org.jcp.xml.dsig.internal.dom.DOMKeyInfo;


public class Simulation implements Runnable{
    private Bike[] bikes;
    private Docking[] docking_stations;
    private int updateInterval = 60000; //milliseconds
    private int sleepTime = 2000; //millieconds
    private Boolean stop = false;
    private static final double ERROR_TOLERANSE = 0.0000001;
    private final double percentageOfBikesToMove = 0.10;

    /*
    * Constructor should receive ONLY the bikes that will be moved.
    * And ALL the docking stations available to move to (not repair etc)
    */

    /*
        Simulate that users pay and check out a bike at a docking station.
        Simulate movement by sending new GPS coordinates and charging level per minute.
        Simulate check in at the same or at another docking station
        Simulate power usage for charging at each docking station
        Each trip should be simulated in a java Thread
        The total simulation should have multiple threads running simultaneously.
     */

    // Jeg trenger funksjoner i DBH:
        // getDockingStationForBike(Bike) for å kunne registrere at sykkelen dockes ut i databasen
        // Rimelig sikker på at det er en feil i DBHens metode dockBike()
        // Denne metoden er også uforståelig... Hva er id?? docking stasjon id??

    public Simulation(Bike[] bikes, Docking[] docking_stations){
        this.bikes = bikes;
        this.docking_stations = docking_stations;
    }

    public Simulation(){
        DBH handler = new DBH();
        this.bikes = null;
        this.bikes = handler.getAllBikes().toArray(bikes);
        this.docking_stations = null;
        this.docking_stations = handler.getDocking().toArray(docking_stations);
    }

    public void run(){
        // Get subset of bikes
        Bike[] subset = getNewSubset();
        // Choose random end docking stations, no matter where the bikes are
        Docking[] endStations = getEndDockingStations(subset);
        // Get Router objects for all bikes that will move
        Router[] routers = getRouters(subset, endStations);

        Thread[] threads = new Thread[routers.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(routers[i]);
            threads[i].run();
        }

        while(!stop){
            for (int i = 0; i < routers.length; i++) {
                if(routers[i].hasArrived() && routers[i].isDocked()){
                    //No problem, create new router
                    //Get new bike
                    Bike bikeThatFinished = routers[i].getBike();
                    Bike newBike = null;
                    do{
                        newBike = getAvailableBike(subset);
                    } while (newBike == bikeThatFinished);

                    //Get new docking station end
                    Docking newEnd = null;
                    do{
                        Bike[] b = new Bike[1];
                        b[0] = newBike;
                        Docking[] arr = getEndDockingStations(b);
                        newEnd = arr[0];
                    } while(newEnd.getLocation() == newBike.getLocation());

                    routers[i] = new Router(newBike, newEnd);
                    threads[i] = new Thread(routers[i]);
                    threads[i].run();
                } else if(routers[i].hasArrived() && !routers[i].isDocked()){
                    // Bike has arrived but could not dock.
                    //      stop router,
                    //      give it new end (this will update start, get new waypoints)
                    //      reset hasArrived
                    //      restart router thread

                    // stop thread
                    routers[i].stop();
                    routers[i].resetStartLocation();
                    // Give router new endlocation
                    Docking couldNotDockTo = routers[i].getEnd();
                    Bike[] arr = new Bike[1];
                    arr[0] = routers[i].getBike();
                    Docking[] newEnd = null;
                    do{
                        newEnd = getEndDockingStations(arr);
                    } while(newEnd[0] == couldNotDockTo);

                    routers[i].setEnd(newEnd[0]);
                    //reset hasArrived
                    routers[i].resetHasArrived();
                    //restart thread
                    routers[i].setRunnable();
                    threads[i].run();
                }
            }
        }
        // Simulation ends, end all routers
        for (int i = 0; i < routers.length; i++) {
            routers[i].stop();
        }
    }

    private Bike[] getNewSubset(){
        Bike[] subset = new Bike[(int)(bikes.length * percentageOfBikesToMove)]; // 10% of bikes will move;
        Random rand = new Random();
        for (int i = 0; i < subset.length; i++) {
            boolean presentMoreThanOnce = false;
            Bike lookingAt = null;
            do{
                //Choose random bike from "bikes"
                lookingAt = null;
                do{
                    lookingAt = bikes[rand.nextInt(bikes.length)];
                } while(lookingAt.getStatus() != Bike.AVAILABLE);

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

    public void setUpdateInterval(int millis){
        if(millis >= 0){
            this.updateInterval = millis;
        }
    }

    public void regRentalAndUndock(Bike[] bikesToRent){

    }

    public Bike getAvailableBike(Bike[] movingBikes){
        Bike output = null;
        Random rand = new Random();
        boolean bikeIsInMovingSubset = false;
        do{
            output = bikes[rand.nextInt(bikes.length)];
            for (int i = 0; i < movingBikes.length; i++) {
                if(output == movingBikes[i]){
                    bikeIsInMovingSubset = true;
                }
            }
        } while(!bikeIsInMovingSubset);
        return output;
    }


}


class SimTest{
    public static void main(String[]args){
        //int id, String name, Location location, int capacity
        DBH handler = new DBH();

        ArrayList<Docking> list = handler.getDocking();
        Docking[] docking_stations = new Docking[list.size()];
        docking_stations = list.toArray(docking_stations);

        ArrayList<Bike> bike_list = handler.getAllBikes();
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
        sim.setUpdateInterval(30);

        Thread simThread = new Thread(sim);
        simThread.start();

    }
}

