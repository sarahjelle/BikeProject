package myapp.data.Simulation;

import java.util.*;
import myapp.dbhandler.*;
import myapp.data.Bike;
import static myapp.data.Bike.*;
import myapp.data.Docking;
import myapp.data.Location;
import myapp.data.User;
import myapp.GUIfx.Map.MapsAPI;
import org.jcp.xml.dsig.internal.dom.DOMKeyInfo;


public class Simulation implements Runnable{
    private Bike[] bikes;
    private Docking[] docking_stations;
    private User[] users;
    private int updateInterval = 60000; //milliseconds
    private int sleepTime = 2000; //millieconds
    private Boolean stop = false;
    private static final double ERROR_TOLERANSE = 0.0000001;
    private final double percentageOfUsersToMove = 0.10;

    /*
        Simulate that users pay and check out a bike at a docking station.
        Simulate movement by sending new GPS coordinates and charging level per minute.
        Simulate check in at the same or at another docking station
        Simulate power usage for charging at each docking station
        Each trip should be simulated in a java Thread
        The total simulation should have multiple threads running simultaneously.
     */

    // Jeg trenger funksjoner i DBH:
        // getDockingStationForBike(Bike) for å kunne registrere at sykkelen un-dockes i databasen
        // Rimelig sikker på at det er en feil i DBHens metode dockBike()
        // Denne metoden er også uforståelig... Hva er id?? docking stasjon id??

    public Simulation(){
        DBH handler = new DBH();
        this.users = handler.getAllCustomers();
        this.docking_stations = handler.getAllDockingStationsWithBikes();
        ArrayList<Bike> arr = new ArrayList<>();
        for (int i = 0; i < docking_stations.length; i++) {
            Bike[] bikesList = docking_stations[i].getBikes();
            for (int j = 0; j < bikesList.length; j++) {
                arr.add(bikesList[j]);
            }
        }
        this.bikes = new Bike[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            bikes[i] = arr.get(i);
        }
    }

    public void run(){
        User[] userSubset = getUserSubset();
        // Get subset of bikes
        Bike[] subset = getBikeSubset(userSubset);
        // Choose random end docking stations, no matter where the bikes are
        Docking[] endStations = getEndDockingStations(subset);
        // Get Router objects for all bikes that will move
        Router[] routers = getRouters(userSubset, subset, endStations);

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

                    routers[i] = new Router(routers[i].getUser(), newBike, routers[i].getEnd(), newEnd);
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

    public User[] getUserSubset(){
        User[] subset = new User[(int)(users.length * percentageOfUsersToMove )];
        Random rand = new Random();
        for (int i = 0; i < subset.length; i++) {
            boolean precentMoreThanOnceInSubset = false;
            do{
                subset[i] = users[rand.nextInt(users.length)];
                for (int j = 0; j < subset.length; j++) {
                    if(j != i){
                        if(subset[j] != null){
                            if(subset[j] == subset[i]){
                                precentMoreThanOnceInSubset = true;
                            }
                        }
                    }
                }
            } while(precentMoreThanOnceInSubset);
        }
        return subset;
    }

    public Bike[] getBikeSubset(User[] usersToMove){
        Bike[] subset = new Bike[usersToMove.length];
        Random rand = new Random();
        for (int i = 0; i < subset.length; i++) {
            boolean precentMoreThanOnceInSubset = false;
            do{
                subset[i] = bikes[rand.nextInt(bikes.length)];
                for (int j = 0; j < subset.length; j++) {
                    if(j != i){
                        if(subset[j] != null){
                            if(subset[j] == subset[i]){
                                precentMoreThanOnceInSubset = true;
                            }
                        }
                    }
                }
            } while(precentMoreThanOnceInSubset);
        }
        return subset;
    }

    /*
    * getNewSubset() returns a Bike[] that consists of 10% of all the registered bikes in the database,
    * that has a status number = Bike.AVAILABLE (1).
    * All the bikes are un-docked from their docking_stations before being returned (both in database and in object).
    */
    private Bike[] getNewSubset(){
        int numberOfBikes = (int)(users.length * percentageOfUsersToMove );
        if(numberOfBikes < 1){
            numberOfBikes = 1;
        }
        Bike[] subset = new Bike[numberOfBikes];
        Random rand = new Random();
        for (int i = 0; i < numberOfBikes; i++) {
            subset[i] = docking_stations[rand.nextInt(docking_stations.length)].rentBike(users[rand.nextInt(users.length)]);
        }
        return subset;
        /*
        Bike[] subset = new Bike[numberOfBikes]; // 10% of users will move;
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
                //Find what station bike is docked at and undock
                
                for (int j = 0; j < docking_stations.length; j++) {
                    Bike[] bikesHere = docking_stations[j].getBikes();
                    for (int k = 0; k < bikesHere.length; k++) {
                        if(bikesHere[k].getId() == lookingAt.getId()){
                            docking_stations[j].rentBike(users[i]);
                            //DBH handler = new DBH();
                            //handler.undockBike(lookingAt, docking_stations[j]);
                        }
                    }
                }
            } else{
                System.out.println("Do-while loop did not work...");
            }
        }
        */
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

    public Router[] getRouters(User[] userSubset, Bike[] bikeSubset, Docking[] endStations){
        if(bikeSubset.length == endStations.length && bikeSubset.length == userSubset.length){
            Router[] routers = new Router[userSubset.length];
            for (int i = 0; i < routers.length; i++) {
                //Find the station bike[i] is at
                Docking start = getStationIDForBike(bikeSubset[i]);
                if(start != null){
                    routers[i] = new Router(userSubset[i], bikeSubset[i], start, endStations[i]);
                }

            }
            return routers;
        } else{
            return null;
        }
    }

    private Docking getStationIDForBike(Bike bike){
        for (int i = 0; i < docking_stations.length; i++) {
            Bike[] subset = docking_stations[i].getBikes();
            for (int j = 0; j < subset.length; j++) {
                if(subset[j].getId() == bike.getId()){
                    return docking_stations[i];
                }
            }
        }
        return null;
    }

    public void setUpdateInterval(int millis){
        if(millis >= 0){
            this.updateInterval = millis;
        }
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
        Simulation sim = new Simulation();
        sim.setUpdateInterval(3000);

        Thread simThread = new Thread(sim);
        simThread.start();

    }
}

