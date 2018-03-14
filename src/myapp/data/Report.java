package myapp.data;


import java.util.*;

public class Report {
    private int bikeID;
    private int batteryPercentage;
    private Location location;
    private Date dateTime;


    public Report(int bikeID, int batteryPercentage, Location location, Date dateTime){
        this.bikeID = bikeID;
        this.batteryPercentage = batteryPercentage;
        this.location = location;
        this.dateTime = dateTime;
    }

    public int getBikeID() {
        return bikeID;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public Location getLocation() {
        return location;
    }

    public Date getDateTime() {
        return dateTime;
    }

}
