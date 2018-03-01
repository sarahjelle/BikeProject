import java.util.ArrayList;
import java.util.HashMap;

public class Docking {
    private int id;
    private String name;
    private double[] location = new double[2];
    private int capacity;
    private HashMap<Integer, Bike> parkedBikes;

    public Docking(int id, String name, double lat, double lon, int capacity){
        this.id = id;
        this.name = name;
        this.location[0] = lat;
        this.location[1] = lon;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public double[] getLocation(){
        return location;
    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public int getCapacity(){
        return capacity;
    }

    public int getOpenSpaces(){
        return parkedBikes.size();
    }

    public void addBike(Bike bike){
        parkedBikes.put(bike.getId(), bike);
    }

    public void removeBike(int bikeId) {
        parkedBikes.remove(bikeId);
    }

    /*
    public void inTrip(String user, int bikeId){
        metode som registrer at sykkelen er på en tur og legger den inn i trips-tabellen i databasen.
     */






}
