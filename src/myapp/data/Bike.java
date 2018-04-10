package myapp.data;

import java.time.LocalDate;
import java.util.Comparator;

public class Bike {
    private int id;
    private LocalDate purchased;
    private String type;
    private String make;
    private double price;
    private double batteryPercentage;
    private boolean available;
    private int distanceTraveled;
    private int totalTrips;
    private Location location;
    /*
    Status:
    1 = Available;
    2 = In Trip;
    3 = In Repair;
    4 = Soft delete;
     */
    private int status;

    public static int   AVAILABLE   = 1,
            TRIP        = 2,
            REPAIR      = 3,
            DELETE      = 4;

    public Bike(double price, LocalDate purchased, String type, String make) {
        this.price = price;
        this.purchased = purchased;
        this.type = type;
        this.make = make;
    }

    public Bike(int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status){
        this.id = id;
        this.make = make;
        this.type = type;
        this.price = price;
        this.purchased = LocalDate.now();
        this.batteryPercentage = batteryPercentage;
        this.location = location;
        this.distanceTraveled = distanceTraveled;
        this.status = status;
    }
    public Bike(int id, String make, double price, String type, double batteryPercentage, int distanceTraveled){
        this.id = id;
        this.make = make;
        this.type = type;
        this.price = price;
        this.batteryPercentage = batteryPercentage;
        this.location = null;
        if (distanceTraveled != 0){
            this.distanceTraveled = distanceTraveled;
        } else {
            this.distanceTraveled = 0;
        }
    }

    public Bike(int id, String type, double price, LocalDate purchased, String make, double batteryPercentage, boolean available, int distanceTraveled, Location location){
        this.id = id;
        this.type = type;
        this.price = price;
        this.purchased = purchased;
        this.make = make;
        this.batteryPercentage = batteryPercentage;
        this.available = available;
        this.location = location;
        if (distanceTraveled != 0){
            this.distanceTraveled = distanceTraveled;
        } else {
            this.distanceTraveled = 0;
        }
    }

    public int getId() {
        return id;
    }

    public String getMake(){
        return make;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getPurchased() {
        return purchased;
    }

    public String getType() {
        return type;
    }

    public double getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(double batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getDistanceTraveled(){
        return distanceTraveled; //getInfoFromKmReader;
    }

    public void setDistanceTraveled(int dist){
        this.distanceTraveled = distanceTraveled + dist;
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(){
        this.totalTrips++;
    }

    public int getStatus() {
        return status;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public boolean equals(Bike bike) {
        if (bike.getId() == id) {
            return true;
        }
        return false;
    }

    // Added by Mediå for testing. Needs to be more complex!
    public String toString() {
        return "ID: " + id + " Type: " + type + " Make: " + make;
    }
}
