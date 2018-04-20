package myapp.data.Simulation;

import java.util.*;

import com.sun.org.apache.xpath.internal.SourceTree;
import myapp.dbhandler.*;
import myapp.data.Bike;
import myapp.data.Docking;
import myapp.data.User;

public class Simulation implements Runnable{
    private Boolean stop = false;
    private Boolean initialized = false;
    private Bike[] bikes;
    private Docking[] docking_stations;
    private User[] users;
    private Router[] routers;
    private static int UPDATE_INTERVAL = 5000; //milliseconds
    private static final double ERROR_TOLERANSE = 0.0000001;
    private int sleepTime = 2000; //millieconds
    private double percentageOfUsersToMove = 0.1;
    private static int NumberOfUsersToMove = -1;
    private final double STATION_POWER_USAGE_PR_BIKE = 230 * 2.5; //Volt * Amp

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
        this.bikes = arr.toArray(bikes);
        /*for (int i = 0; i < arr.size(); i++) {
            bikes[i] = arr.get(i);
        }*/
        this.routers = null;
        this.initialized = true;
    }

    public void run(){
        System.out.println("Starting Simulation");
        User[] userSubset = getUserSubset();

        this.routers = getRouters(userSubset);

        Thread[] threads = new Thread[routers.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(routers[i]);
            threads[i].start();
        }

        while(!stop){
            DBH handler = new DBH();
            for (int i = 0; i < docking_stations.length; i++) {
                double dockPowerUsage = docking_stations[i].getBikes().length * STATION_POWER_USAGE_PR_BIKE;
                docking_stations[i].setPowerUsage(dockPowerUsage);
                handler.logDocking(docking_stations[i]);
            }

            for (int i = 0; i < routers.length; i++) {
                if(routers[i].hasArrived() && routers[i].isDocked()){
                    routers[i].stop();
                    /*
                    //No problem, create new router
                    System.out.println("Bike has arrived, getting new router");
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
                    */
                } else if(routers[i].hasArrived() && !routers[i].isDocked()){
                    //Bike has arrived, but could not dock
                    try{
                        Thread.sleep(3000);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    if(routers[i].hasArrived() && !routers[i].isDocked()){
                        routers[i].stop();
                        /*System.out.println("Simulation: Bike could not dock, getting new router");
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
                        */
                    } else{
                        System.out.println("Bike has arrived and is docked");
                        routers[i].stop();
                    }

                }
            }
            //Check if all routers have arrived, if they have end simulation
            boolean allArrived = true;
            for (int i = 0; i < threads.length; i++) {
                if(threads[i].isAlive()){
                    allArrived = false;
                }
            }
            if(allArrived){
                stop();
            }
            try{
                Thread.sleep(UPDATE_INTERVAL);
            } catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
        // Simulation ends, end all routers
        for (int i = 0; i < routers.length; i++) {
            routers[i].stop();
        }
    }

    public void stop(){
        if(this.routers != null){
            for (int i = 0; i < routers.length; i++) {
                if(routers[i] != null){
                    routers[i].stop();
                }
            }
        }
        this.stop = true;
    }

    public boolean isInitialized(){
        return initialized;
    }

    public void setUpdateInterval(int millis){
        if(millis >= 0){
            this.UPDATE_INTERVAL = millis;
        }
    }

    private User[] getUserSubset(){
        int percentage;
        if(percentageOfUsersToMove <= 1){
            percentage = (int)(users.length * percentageOfUsersToMove);
        } else{
            percentage = users.length;
        }

        if(percentage <= 0){
            percentage = 1;
        }

        if(NumberOfUsersToMove >= 0){
            percentage = NumberOfUsersToMove;
        }

        User[] subset = new User[percentage];
        if(percentage == users.length){
            for (int i = 0; i < subset.length; i++) {
                subset[i] = users[i];
            }
            return subset;
        } else{
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
    }

    private Router[] getRouters(User[] userSubSet){
        Router[] routers = new Router[userSubSet.length];
        Random rand = new Random();
        DBH handler = new DBH();
        Bike[] undockedBikes = handler.getAllBikesOnTrip();
        for (int i = 0; i < routers.length; i++) {
            User customer = userSubSet[i];
            Docking start = null;
            Docking end = null;
            Bike bike = null;
            do{
                start = docking_stations[rand.nextInt(docking_stations.length)];
                end = docking_stations[rand.nextInt(docking_stations.length)];
                bike = start.rentBike(customer);
            } while(start == null || end == null || bike == null || bike.getLocation().getLatitude() == null || bike.getLocation().getLongitude() == null);
            routers[i] = new Router(customer, bike, start, end);
            routers[i].setUpdateInterval(UPDATE_INTERVAL);
        }
        return routers;
    }

    private Bike getAvailableBike(Bike[] movingBikes){
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

    private User getNewUser(User finished, User[] subset){
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

    public boolean setUserPercentage(double percentageOfUsersToMove){
        if(percentageOfUsersToMove <= 1 && percentageOfUsersToMove > 0){
            this.percentageOfUsersToMove = percentageOfUsersToMove;
            return true;
        } else{
            return false;
        }
    }

    public boolean setNumberOfUsers(int NumberOfUsers){
        if(NumberOfUsers >= 0 && NumberOfUsers < bikes.length){
            this.NumberOfUsersToMove = NumberOfUsers;
            return true;
        } else{
            return false;
        }
    }
}

class SimTest{
    public static void main(String[] args){
        //int id, String name, Location location, int capacity
        Simulation sim = new Simulation();
        sim.setUpdateInterval(3000);

        Thread simThread = new Thread(sim);
        simThread.start();

        while(!sim.isInitialized()){
            try{
                Thread.sleep(500);
            } catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
        System.out.println("Simulation is initialized");
        javax.swing.JOptionPane.showMessageDialog(null, "End simulation? ");
        sim.stop();

        if(javax.swing.JOptionPane.showConfirmDialog(null, "Remove any unfinished trips and dock?") == 0){
            DBH handler = new DBH();
            handler.removeAllUnfinishedTrips();
        }

    }
}

