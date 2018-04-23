package myapp.data;

import java.time.LocalDate;

/**
 * Bike is a class containing all information about a real life bike needed in this project.
 */
public class Bike {
    private int id;
    private LocalDate purchased;
    private String type;
    private String make;
    private double price;
    private double batteryPercentage;
    private int distanceTraveled;
    private int totalTrips = 0;
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

    /**
     * this constructor is the one to use when creating a new bike Object from scratch. This is an object which is meant to be sent
     * to the Database for further use.
     *
     * @param price         the price of the bike
     * @param purchased     the date of purchase
     * @param type          the type of the bike
     * @param make          the make of the bike
     */
    public Bike(double price, LocalDate purchased, String type, String make) {
        this.price = price;
        this.purchased = purchased;
        this.type = type;
        this.make = make;
    }

    // To be used returned from DB

    /**
     * This constructor is used when returning a Bike object from the database to the program.
     *
     * @param id                    the id of the bike
     * @param make                  the make of the bike
     * @param price                 the price of the bike
     * @param type                  the type for the bike
     * @param batteryPercentage     the battery percentage of the bike
     * @param distanceTraveled      how far the bike has traveled
     * @param location              the location of the bike
     * @param status                the status of the bike
     * @param purchased             the date of purchase date of the bike
     * @param totalTrips            the number of trips the bike has had.
     */
    public Bike(int id,  String make, double price, String type, double batteryPercentage, int distanceTraveled, Location location, int status, LocalDate purchased, int totalTrips){
        this.id = id;
        this.make = make;
        this.type = type;
        this.price = price;
        this.purchased = purchased;
        this.batteryPercentage = batteryPercentage;
        this.location = location;
        this.distanceTraveled = distanceTraveled;
        this.status = status;
        this.totalTrips = totalTrips;
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

    public void setMake(String make){
        this.make = make;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getPurchased() {
        return purchased;
    }

    public void setPurchased(LocalDate purchased) {
        this.purchased = purchased;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    /**
     * equals compares the ID of the given bike with its own ad returns true if they are equal.
     * @param bike      the bike to compare IDs to
     * @return          True = Equal, False = Not equal
     */
    public boolean equals(Bike bike) {
        if (bike.getId() == id) {
            return true;
        }
        return false;
    }

    /**
     * getLatestRepairRequest returns the latest Repair object in the Repair object array hold by the Bike object.
     * @return      the latest added Repair object
     */
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

    /**
     * addRepairRequest takes the parameters given and creates a new Repair object. It also takes the Repair object and register it to the database directly through the startRepairRequest method in the Repair Object.
     * @param desc      the description of the problem
     * @param date      the date of register
     * @return          a boolean based on the results given by the DBH object. If it got registered to the DB it returns true, otherwise it returns false
     */
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

    /**
     * finishLastRepairRequest takes the parameters given and finishes the last Repair object in the Repair object array. It also register it to the database through finishRepairRequest method in the Repair object.
     * @param   returnDesc  the description of the fixed bike
     * @param   returnDate  the date of return
     * @param   price       the cost of the repair
     * @return              a boolean based on results from DBH. True = Finished request, False = Not finished request
     */
    public boolean finishLastRepairRequest(String returnDesc, LocalDate returnDate, double price) {
        Repair rep = getLatestRepairRequest();
        if(rep.finishRepairRequest(returnDesc, returnDate, price)) {
            status = Bike.AVAILABLE;
            return true;
        }
        return false;

    }

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
