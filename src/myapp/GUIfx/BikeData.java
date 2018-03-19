package myapp.GUIfx;

import javafx.beans.property.SimpleIntegerProperty;

import myapp.data.Bike;

public class BikeData {
    private final SimpleIntegerProperty bikeId = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty totalTrips = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty totalKm= new SimpleIntegerProperty(0);

    public BikeData(Bike bike){
        this(bike.getId(), bike.getTotalTrips(), bike.getDistanceTraveled());
    }

    public BikeData(int bikeId, int totalTrips, int totalKm){
        setBikeId(bikeId);
        setTotalTrips(totalTrips);
        setTotalKm(totalKm);
    }

    public int getBikeId(){
        return bikeId.get();
    }

    public void setBikeId(int id){
        bikeId.set(id);
    }

    public int getTotalTrips(){
        return totalTrips.get();
    }

    public void setTotalTrips(int trips){
        totalTrips.set(trips);
    }

    public int getTotalKm(){
        return totalKm.get();
    }

    public void setTotalKm(int km){
        totalKm.set(km);
    }



}