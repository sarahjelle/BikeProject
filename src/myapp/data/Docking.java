package myapp.data;


import com.sun.org.apache.xpath.internal.SourceTree;
import myapp.data.Bike;
import myapp.dbhandler.DBH;


import java.util.ArrayList;
import java.util.HashMap;

public class Docking {
    private int id;
    private String name;
    private Location location;
    private int capacity;
    private ArrayList<Bike> bikes;

    DBH dbh = new DBH();

    public Docking(int id, String name, Location location, int capacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.bikes = new ArrayList<>(capacity);
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

    public void addBike(Bike bike) {
        int spot = firstOpen();
        if(spot >= 0){
            bikes.add(spot, bike);
            dbh.dockBike(id, spot, bike);
        }
    }

    // To use when creating docking station from DB
    public void forceAddBike(Bike bike, int spot) {
        spot--;
        if(spot >= 0){
            bikes.add(spot, bike);
        }
    }

    //Helper function for finding first open spot
    public int firstOpen() {
        int i = 0;
        while(bikes.get(i) != null){
            i++;
        }
        if (i <= bikes.size()){
            return i;
        }
        return -1;
    }

    public int openSpaces() {
        int count = 0;
        for (int i = 0; i < bikes.size(); i++){
            if(bikes.get(i) == null){
                count++;
            }
        }
        return count;
    }

    public boolean removeBike(int bikeId) {
        for(int i = 0; i < bikes.size(); i++) {
            if(bikes.get(i).getId() == bikeId) {
                bikes.add(i, null);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Bike> getBikes() {
        return bikes;
    }

}
