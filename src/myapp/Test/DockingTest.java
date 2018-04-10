package myapp.Test;

import myapp.data.Bike;
import myapp.data.Location;
import myapp.data.Docking;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DockingTest {
    private static Location location1 = new Location("Sverres Gate 14", true);
    private static Location location2 = new Location("Dronningens Gate 64", true);
    private static Location location3 = new Location("Beddingen 4", true);
    private static Bike bike1 = new Bike(1, "Merida", 97.0, "Electric", 100, 0, location1, 0);
    private static Bike bike2 = new Bike(2, "Kona", 30.0, "Electric", 200, 0, location2, 0);
    private static Bike bike3 = new Bike(3, "Merida", 48.0, "Electric", 300, 0, location3, 0);

    private static Docking docking1 = new Docking(1, "HiST Kalvskinnet", location1, 25);
    @Test
    public void addBikes(){
        docking1.addBike(bike1);
        docking1.addBike(bike2);
        assertEquals(docking1.getBikes().get(0), bike1);
        assertEquals(docking1.getBikes().get(1), bike2);
    }

    @Test
    public void getId() throws Exception {
        assertEquals(1, docking1.getId());
    }

    @Test
    public void getName() throws Exception {
        assertEquals("HiST Kalvskinnet", docking1.getName());
    }

    @Test
    public void setName() throws Exception {
        docking1.setName("NTNU Kalvskinnet");
        assertEquals(docking1.getName(), "NTNU Kalvskinnet");
    }

    @Test
    public void getCapacity() throws Exception {
        assertEquals(25, docking1.getCapacity());
    }

    @Test
    public void getOpenSpaces() throws Exception {
        docking1.addBike(bike1);
        docking1.addBike(bike2);
        assertEquals(23, docking1.openSpaces());
    }

    /*
    @Test
    public void removeBike() throws Exception {
        boolean bol = true;
        docking1.addBike(bike1);
        docking1.addBike(bike2);
        docking1.addBike(bike3);
        docking1.removeBike(bike1.getId());
        ArrayList<Bike> bikes = docking1.getBikes();
        System.out.println(docking1.getBikes().size());
        System.out.println(bikes);
        for(int i = 0; i < bikes.size(); i++){
            if(bikes.get(i).getId() == bike1.getId()){
                 bol = false;
            }
        }
        assertEquals(true, bol);
    }
    */
}