package myapp.data;

public class Bike {
    private int id;
    private String make;
    private double batteryPercentage;
    private boolean available;
    private int distanceTraveled;

    public Bike(){

    }

    public Bike(int id, String make, double batteryPercentage, boolean available, int parkingSpotId){
        this.id = id;
        this.make = make;
        this.batteryPercentage = batteryPercentage;
        this.available = available;
        this.distanceTraveled = 0;
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
        return 250; //getInfoFromKmReader;
    }

}
