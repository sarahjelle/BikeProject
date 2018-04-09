package myapp.data;

import java.time.LocalDate;
import java.util.*;

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


    public Bike(double price, LocalDate purchased, String type, String make) {
        this.price = price;
        this.purchased = purchased;
        this.type = type;
        this.make = make;
    }

    public Bike(int id, double price, String make, String type, double batteryPercentage, int distanceTraveled, Location location){
        this.id = id;
        this.make = make;
        this.type = type;
        this.price = price;
        this.purchased = purchased;
        this.batteryPercentage = batteryPercentage;
        this.location = location;
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
    public Bike(int id, double price, String make, String type, double batteryPercentage, int distanceTraveled){
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    // Added by Mediå for testing. Needs to be more complex!
    public String toString() {
        return "ID: " + id + " Type: " + type + " Make: " + make;
    }
}
