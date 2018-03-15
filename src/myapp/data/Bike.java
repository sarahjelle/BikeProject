package myapp.data;

import java.time.LocalDate;

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

    public Bike(int price, LocalDate purchased, String type, String make){
        this.price = price;
        this.purchased = purchased;
        this.type = type;
        this.make = make;
    }

    public Bike(int id, String make, double batteryPercentage, boolean available, int distanceTraveled, Location location){
        this.id = id;
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
