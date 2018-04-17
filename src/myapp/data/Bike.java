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
    private int distanceTraveled;
    private int totalTrips;
    private Location location;
    private Repair[] repairs = new Repair[0];
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

    // To be used when sending to DB
    public Bike(double price, LocalDate purchased, String type, String make) {
        this.price = price;
        this.purchased = purchased;
        this.type = type;
        this.make = make;
    }

    // To be used returned from DB
    public Bike(int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status, LocalDate purchased){
        this.id = id;
        this.make = make;
        this.type = type;
        this.price = price;
        this.purchased = purchased;
        this.batteryPercentage = batteryPercentage;
        this.location = location;
        this.distanceTraveled = distanceTraveled;
        this.status = status;
    }

    // UNKNOWN USAGE
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

    // UNKNOWN USAGE
    public Bike(int id, String type, double price, LocalDate purchased, String make, double batteryPercentage, int status, int distanceTraveled, Location location){
        this.id = id;
        this.type = type;
        this.price = price;
        this.purchased = purchased;
        this.make = make;
        this.batteryPercentage = batteryPercentage;
        this.status = status;
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

    public Repair[] getRepairs() {
        return repairs;
    }

    public void setRepairs(Repair[] repairs) {
        this.repairs = repairs;
    }

    public boolean setStatus(int sta) {
        if(sta <= 4 && sta >= 1) {
            status = sta;
            return true;
        }
        return false;
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

    private Repair getLatestRepairRequest() {
        Repair repair = null;
        for(Repair rep : repairs) {
            if(repair == null) {
                repair = rep;
            } else if(repair.getCaseID() < rep.getCaseID()) {
                repair = rep;
            }
        }

        return repair;
    }

    public boolean addRepairRequest(String desc, LocalDate date) {
        if(repairs.length == 0) {
            repairs = new Repair[1];
            repairs[0] = new Repair(id, desc, date);
            if(repairs[0].startRepairRequest()) {
                status = Bike.REPAIR;
                return true;
            }
        } else {
            Repair[] temp = new Repair[repairs.length + 1];
            for(int i = 0; i < repairs.length; i++) {
                temp[i] = repairs[i];
            }
            temp[temp.length - 1] = new Repair(id, desc, date);
            if(temp[temp.length - 1].startRepairRequest()) {
                status = Bike.REPAIR;
                return true;
            }
        }
        return false;
    }

    public boolean finishLastRepairRequest(String returnDesc, LocalDate returnDate, double price) {
        Repair rep = getLatestRepairRequest();
        if(rep.finishRepairRequest(returnDesc, returnDate, price)) {
            status = Bike.AVAILABLE;
            return true;
        }
        return false;

    }

    // Added by Mediå for testing. Needs to be more complex!
    public String toString() {
        String repairsList = "\tNo repairs";
        if(repairs != null) {
            repairsList = "";
            for(Repair repair : repairs) {
                repairsList += "\t" + repair.toString() + "\n\n";
            }
        }

        return "ID: " + id + " Type: " + type + " Make: ";// + make + "\n\tRepairs:\n" + repairsList + "\n";
    }
}
