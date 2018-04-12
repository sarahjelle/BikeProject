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

        Router[] routers = getRouters(userSubset);

        Thread[] threads = new Thread[routers.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(routers[i]);
            threads[i].run();
        }

        while(!stop){
            for (int i = 0; i < routers.length; i++) {
                if(routers[i].hasArrived() && routers[i].isDocked()){
                    //No problem, create new router
                    routers[i].stop();
                    User newUser = getNewUser(routers[i].getUser(), userSubset);
                    Docking start = routers[i].getStartStation();
                    Docking end = null;
                    Random rand = new Random();
                    do{
                        end = docking_stations[rand.nextInt(docking_stations.length)];
                    } while(end == start);

                    Bike bikeToMove = start.rentBike(newUser);
                    routers[i] = new Router(newUser, bikeToMove, start, end);
                    threads[i] = new Thread(routers[i]);
                    threads[i].start();
                } else if(routers[i].hasArrived() && !routers[i].isDocked()){
                    //Bike has arrived, but could not dock
                    routers[i].stop();
                    User user = routers[i].getUser();
                    Bike bike = routers[i].getBike();
                    Docking start = routers[i].getEnd();
                    Docking end = null;
                    Random rand = new Random();
                    do{
                        end = docking_stations[rand.nextInt(docking_stations.length)];
                    } while(end == start);
                    routers[i] = new Router(user, bike, start, end);
                    threads[i] = new Thread(routers[i]);
                    threads[i].start();
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

    private Router[] getRouters(User[] userSubSet){
        Router[] routers = new Router[userSubSet.length];
        Random rand = new Random();
        for (int i = 0; i < routers.length; i++) {
            Docking start = docking_stations[rand.nextInt(docking_stations.length)];
            Docking end = docking_stations[rand.nextInt(docking_stations.length)];
            User customer = userSubSet[i];
            Bike bike = start.rentBike(customer);
            routers[i] = new Router(customer, bike, start, end);
        }
        return routers;
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

    public User getNewUser(User finished, User[] subset){
        User output = null;
        Random rand = new Random();
        boolean presentInSubset = false;
        for (int i = 0; i < users.length; i++) {
            do{
                output = users[rand.nextInt(users.length)];

                for (int j = 0; j < subset.length; j++) {
                    if(output == subset[j]){
                        presentInSubset = true;
                    }
                }
            } while(output == finished || !presentInSubset);
        }
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

