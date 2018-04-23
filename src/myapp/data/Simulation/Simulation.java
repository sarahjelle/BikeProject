package myapp.data.Simulation;

import java.util.*;

import myapp.dbhandler.*;
import myapp.data.Bike;
import myapp.data.Docking;
import myapp.data.User;

/**
 * Simulation implementing the Runnable interface, allowing it to be placed as an argument to the Thread constructor and call Thread.start();
 * Simulates that multiple User objects stored in the database rents a Bike object from a random start Docking object, and moves the Bike along a path defined by
 * Google's Maps API directions function.
 * One given set of User, Bike and Docking (start and end) objects are placed in Router runnables.
 * This class manages these Router objects, continously checking if they have arrived and if they have, the router threads are stopped.
 * If all the Rotuer objects has arrived, they are all stopped, and the Simulation itself is stopped.
 * @author Martin Moan
 */

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


    /**
     * Constructs and initialized a Simulation Runnable with data received from the database.
     * Constructor gets all the available Docking objects from the database, each containing the Bike objects that are currently docked there, and all the User objects stored in the database.
     * UPDATE_INTERVAL is set to default value of 5000 milliseconds.
     * The percentage of User objects the simulation will use is set with default value of 10% (0.1).
     */
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

    /**
     * Constructs and initialized a Simulation with data recieved from the database.
     * Constructor gets all the available Docking objects from the database, each containing the Bike objects that are currently docked there, and all the User objects stored in the database.
     * The percentage of User objects the simulation will use is set with default value of 10% (0.1).
     * @param UPDATE_INTERVAL update interval used to update to database in milliseconds.
     */
    public Simulation(int UPDATE_INTERVAL){
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
        this.routers = null;
        this.UPDATE_INTERVAL = UPDATE_INTERVAL;
        this.initialized = true;
    }

    /**
     * Method to be called by a Thread object containing simulation object, when Thread.start(); is called.
     */
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
                int numberOfBikes = 0;
                Bike[] bikesHere = docking_stations[i].getBikes();
                for (int j = 0; j < bikesHere.length; j++) {
                    if(bikesHere[j] != null){
                        numberOfBikes++;
                    }
                }
                double dockPowerUsage = numberOfBikes * STATION_POWER_USAGE_PR_BIKE;
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

    /**
     * Method used to end the simulation and all its containing Router Runnables.
     * All the Routers will get their stop(); methods called and try to dock their bikes to first their designated end-stations.
     * If they are not able to dock to their end-stations they will try to dock to their original start-stations.
     */
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

    /**
     * Returns the state of the Simulation object. The method will return true if the Simulation constructor has finished, false if not.
     * This method is to be used before starting a tread containing the Simulation object.
     * This is to avoid starting a Thread before the Simulation object has received all the information it needs from the database.
     * @return constructor finished (true/false)
     */
    public boolean isInitialized(){
        return initialized;
    }

    /**
     * Sets the Simulation and its containing Router objects update intervals in milliseconds.
     * @param millis update interval to use in milliseconds
     */
    public void setUpdateInterval(int millis){
        if(millis >= 0){
            this.UPDATE_INTERVAL = millis;
        }
    }

    /**
     * Returns a User[] that contains by default 10% of all the User objects stored in the database.
     * The size/length of the output array, depends on the percentageOfUsersToMove and the NumberOfUsersToMove variables.
     * By defualt the percentageOfUsersToMove is set to 0.1 (10%) and the NumberOfUsersToMove is set to -1
     * If the NumberOfUsersToMove is set to any other value that is equal to or greater than 0, the output of this method is an array of User objects with length equal to NumberOfUsersToMove.
     * E.G.: If the NumberOFUsersToMove variable has been changed by the client with the setNumberOfUsers() method, this method will return an array of User objects with length equal to NumberOfUsersToMove.
     * If not, this method will return an array of User object with length equal to the set percentageOfUsersToMove (defualt: 10%) of all the User objects stored in the database.
     * @return User[] with length = percentageOfUsersToMove * "All-Users-Stored-In-Database.length" or length = NumberOfUsersToMove
     */
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

        ArrayList<User> safeUserList = new ArrayList<User>(Arrays.asList(users));

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
                    subset[i] = safeUserList.get(rand.nextInt(safeUserList.size()));
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
                safeUserList.remove(subset[i]);
            }
            return subset;
        }
    }

    /**
     * Returns a Router[] with initialized Router objects for all the User objects in the input User[].
     * These Router objects are ready to be passed into Thread objects, and calling Tread.start(); on these Thread objects will promptly start simulating
     * moving the Routers containing bike object to the end Docking object and dock with it, thus ending its trip.
     * @param userSubSet Array of Users to rent a bike with, and simulate moving from a randomly given start Docking object to randomly chosen end Docking object.
     * @return Router[] containing Router Runnables ready to start.
     */
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
                bike = start.rentBike(customer);
            } while(start == null || bike == null || bike.getLocation().getLatitude() == null || bike.getLocation().getLongitude() == null);
            do{
                end = docking_stations[rand.nextInt(docking_stations.length)];
            } while(start == end || end == null);

            routers[i] = new Router(customer, bike, start, end);
            routers[i].setUpdateInterval(UPDATE_INTERVAL);
            System.out.println("Router " + (i+1) + " created");
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

    /**
     * Sets the percentage of all User objects stored in the database to use in the simulation.
     * This variable will be ignored if setNumberOfUsers() is called with valid argument.
     * Default value is 0.1 (10%)
     * @param percentageOfUsersToMove
     * @return true if 0 < intput <= 1, false if not.
     */
    public boolean setUserPercentage(double percentageOfUsersToMove){
        if(percentageOfUsersToMove <= 1 && percentageOfUsersToMove > 0){
            this.percentageOfUsersToMove = percentageOfUsersToMove;
            return true;
        } else{
            return false;
        }
    }

    /**
     * Sets the actual number of User objects stored in the database to use in the simulation.
     * @param NumberOfUsers must be in range [0, All-Users-In-Database>.
     * @return true if NumberOfUsers argument is in valid range.
     */
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

        boolean validInput = false;
        Double percentage = null;
        do{
            String input = javax.swing.JOptionPane.showInputDialog("Enter percentage (0.0 - 1.0) of test-customers to use in the simulation: ");
            try{
                percentage = Double.parseDouble(input);
                if(percentage >= 0.0 && percentage <= 1.0){
                    validInput = true;
                }
            } catch(Exception e){
                javax.swing.JOptionPane.showMessageDialog(null, "Please enter a valid number between 0.0 and 1.0");
                validInput = false;
            }

        } while(!validInput);

        sim.setUserPercentage(percentage);
        //sim.setNumberOfUsers(3);

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

