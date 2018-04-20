
package myapp.data;


import com.sun.org.apache.xpath.internal.SourceTree;
import myapp.data.Bike;
import myapp.dbhandler.DBH;


import java.util.ArrayList;

public class Docking {
    private int id;
    private String name;
    private Location location;
    private int capacity;
    private int status;
    private Bike[] bikes;

    private double power_usage;

    private static int MINIMUM_BAT_LEVEL = 0;
    public static int  AVAILABLE = 1,
            DELETED = 2;

    DBH dbh = new DBH();

    public Docking(int id, String name, Location location, int capacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.bikes = new Bike[capacity];
        this.status = AVAILABLE;
    }

    public Docking(int id, String name, Location location, int capacity, int status) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.bikes = new Bike[capacity];
        if(status == AVAILABLE || status == DELETED){
            this.status = status;
        } else{
            throw new IllegalArgumentException("Status argument is invalid");
        }
    }

    public Docking(String name, int capacity){
        this.name = name;
        this.capacity = capacity;
        this.location = new Location(name, true);
        this.status = AVAILABLE;
        this.bikes = new Bike[capacity];
    }

    public int getStatus(){
        return status;
    }

    public boolean setStatus(int status){
        if(status == AVAILABLE || status == DELETED){
            this.status = status;
            return true;
        } else{
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setPowerUsage(double power_usage){
        this.power_usage = power_usage;
    }

    public double getPowerUsage() {
        return power_usage;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    public int getFreeSpaces() {
        return capacity - getUsedSpaces();
    }

    /**
     * Adds a bike object to the specified spot without checking if it is
     * available or not. It is used when creating a docking station from DB.
     * @param bike the bike to add
     * @param spot the spot on the docking station
     *
     */
    public void forceAddBike(Bike bike, int spot) {
        spot--;
        bikes[spot] = bike;
    }

    /**
     * Loops through the bike array, which stores the bikes docked at the station,
     * and returns the index of the first open slot. If all the slots at the docking station
     * are taken, the method returns -1.
     * @return <code> int </code> the index of the first open slot
     */
    public int findFirstOpen() {
        for(int i = 0; i < bikes.length; i++){
            if(bikes[i] == null){
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the number of occupied slots on a docking station.
     * @return <code>int</code> number of occupied slots
     */
    public int getUsedSpaces() {
        int count = 0;
        for (int i = 0; i < bikes.length; i++){
            if(bikes[i] != null){
                count++;
            }
        }
        return count;
    }

    /**
     * Removes a bike from a docking station.
     * @param bikeId the id of the bike to be undocked
     * @return <code>true</code> if the bike was successfully undocked
     *          <code>false</code> if the bike was not removed
     */
    public boolean undockBike(int bikeId) {
        for(int i = 0; i < bikes.length; i++) {
            if(bikes[i].getId() == bikeId) {
                bikes[i] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Docks a bike to the first open spot on a docking status. Used to redock bikes that have been rented for trips.
     * @param bike the bike to dock
     * @return <code>true</code> if docking of bike was successfull
     *          <code>false</code> if the bike could not be redocked
     */
    public boolean dockBike(Bike bike) {
        int spot = findFirstOpen() + 1;
        bike.setStatus(dbh.getBikeByID(bike).getStatus());
        if(dbh.endRent(bike,id, spot)) {
            spot--;
            bikes[spot] = bike;
            return true;
        }
        return false;
    }

    /**
     *
     * @param user
     * @return
     */
    public Bike rentBike(User user) {
        bikes = dbh.updateBikesInDockingStation(id);
        Bike bike = null;
        for(int i = 0; i < bikes.length; i++) {
            if(bikes[i] != null) {
                if(bikes[i].getStatus() == Bike.AVAILABLE) {
                    if (bikes[i].getBatteryPercentage() >= MINIMUM_BAT_LEVEL) {
                        bike = bikes[i];
                        bikes[i] = null;

                        if(dbh.rentBike(user, bike, id)) {
                            Bike[] bArr = new Bike[1];
                            bike.setLocation(new Location(location.getLatitude(), location.getLongitude()));
                            bArr[0] = bike;
                            dbh.logBikes(bArr);
                            return bike;
                        }
                    }
                }
            }
        }


        return null;
    }

    /**
     * Returns a list of Bike-objects that corresponds to the bikes docked at the station.
     * The length of the list represents the capacity of the docking station.
     * If the dock is available the list-object is null, else it holds the Bike-object.
     * @return <code>Bike[]</code> a list that can hold Bike-objects.
     *
     */
    public Bike[] getBikes() {
        return bikes;
    }

    public String toString() {
        String prBikes = "";
        for (int i = 0; i < bikes.length; i++) {
            if(bikes[i] != null){
                prBikes += "\nSlot: " + (i + 1) + " - " + bikes[i].toString();
            }
        }
        return "Name: " + name + " - With ID: " + id;// + "\nBikes:" + prBikes;
    }


}
