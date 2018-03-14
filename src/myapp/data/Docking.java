package myapp.data;


import myapp.data.Bike;


import java.util.ArrayList;

public class Docking {
    private int id;
    private String name;
    private Location location;
    private int capacity;
    private ArrayList<Bike> bikes;

    public Docking(int id, String name, Location location, int capacity){
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.bikes = new ArrayList<Bike>(capacity);
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getCapacity(){
        return capacity;
    }

    public int getOpenSpaces(){
        return getCapacity() - bikes.size();
    }

    public void addBike(Bike bike){
        bikes.add(bike);
    }

    public boolean removeBike(int bikeId) {
        int pos = getBikePos(bikeId);
        if (pos >= 0){
            bikes.remove(getBikePos(bikeId));
            return true;
        }
        return false;

    }

    // Helper-method to removeBike
    private int getBikePos(int bikeId) {
        for(int i = 0; i < bikes.size(); i++){
            if(bikeId == bikes.get(i).getId()){
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Bike> getBikes() {
        return bikes;
    }

    /*
    public void inTrip(String user, int bikeId){
        metode som registrer at sykkelen er på en tur og legger den inn i trips-tabellen i databasen.
     */






}
