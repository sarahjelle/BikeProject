package myapp.data;

import java.util.*;

public class Bike {
    private int id;
    private String make;
    private double batteryPercentage;
    private boolean available;
    private int distanceTraveled;
    private Location location;

    public Bike(){

    }

    public Bike(int id, String make, double batteryPercentage, boolean available, int parkingSpotId, int distanceTraveled, Location location){
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

    public void setLocation() {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Report getReport(int bikeID, int batteryPercentage, Location location, Date dateTime) {
        return new Report(bikeID, batteryPercentage, location, dateTime);
    }
}
