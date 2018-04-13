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
    private Bike[] bikes;

    private static int MINIMUM_BAT_LEVEL = 0;

    DBH dbh = new DBH();

    public Docking(int id, String name, Location location, int capacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.bikes = new Bike[capacity];
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOpenSpaces() {
        return getCapacity() - openSpaces();
    }

    // To use when creating docking station from DB
    public void forceAddBike(Bike bike, int spot) {
        spot--;
            bikes[spot] = bike;
    }

    //Helper function for finding first open spot
    public int findFirstOpen() {
        for(int i = 0; i < bikes.length; i++){
            if(bikes[i] == null){
                return i;
            }
        }
        return -1;
    }

    public int openSpaces() {
        int count = 0;
        for (int i = 0; i < bikes.length; i++){
            if(bikes[i] == null){
                count++;
            }
        }
        return count;
    }

    public boolean undockBike(int bikeId) {
        for(int i = 0; i < bikes.length; i++) {
            if(bikes[i].getId() == bikeId) {
                bikes[i] = null;
                return true;
            }
        }
        return false;
    }

    public boolean dockBike(Bike bike) {
        int spot = findFirstOpen() + 1;
        if(spot > 0){
            if(dbh.endRent(bike,id, spot)) {
                spot--;
                bikes[spot] = bike;
                return true;
            } else{
                return false;
            }
        } else{
            return false;
        }
    }

    public Bike rentBike(User user) {
        dbh.updateBikesInDockingStation(id, bikes);
        Bike bike = null;
        for(int i = 0; i < bikes.length; i++) {
            if(bikes[i] != null) {
                if (bikes[i].getBatteryPercentage() >= MINIMUM_BAT_LEVEL) {
                    bike = bikes[i];
                    bikes[i] = null;
                }
            }
        }
        if(bike != null) {
            if(dbh.rentBike(user, bike, id)) {
                return bike;
            }
        }

        return null;
    }

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
        return "Name: " + name + " - With ID: " + id + "\nBikes:" + prBikes;
    }


}
