package myapp.Test;

import myapp.data.Location;
import org.junit.Test;
import myapp.data.Bike;
import myapp.data.Docking;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class BikeTest {
    private static Location location1 = new Location("Sverres Gate 14", true);
    private static Location location2 = new Location("Dronningens Gate 64", true);
    private static Location location3 = new Location("Beddingen 4", true);
    private static Bike bike1 = new Bike(1, "Merida", 97.0, "Electric", 100, 0, location1);
    private static Bike bike2 = new Bike(2, "Kona", 30.0, "Electric", 200, 0, location2);
    private static Bike bike3 = new Bike(3, "Merida", 48.0, "Electric", 300., 0, location3);

    LocalDate time1 = LocalDate.now();

    private static Docking docking1 = new Docking(1, "HiST Kalvskinnet", location1, 25);

    @Test
    public void getId() throws Exception {
        assertEquals(1, docking1.getId());
    }

    @Test
    public void getMake() throws Exception {
        assertEquals("Merida", bike1.getMake());
    }

    @Test
    public void getPrice() throws Exception {
        assertEquals((Double) 97.0, (Double) bike1.getPrice());
    }

    @Test
    public void getPurchased() throws Exception {
        assertEquals(time1, bike1.getPurchased());
    }

    @Test
    public void getType() throws Exception {
        assertEquals("Electric", bike2.getType());
    }

    @Test
    public void getBatteryPercentage() throws Exception {
        assertEquals((Double) 300., (Double)bike3.getBatteryPercentage());
    }

    @Test
    public void setBatteryPercentage() throws Exception {
        bike3.setBatteryPercentage(70.);
        assertEquals((Double) 70., (Double) bike3.getBatteryPercentage());
    }

    @Test
    public void getDistanceTraveled() throws Exception {
        assertEquals(0, bike2.getDistanceTraveled());
    }

    @Test
    public void setDistanceTraveled() throws Exception {
        bike2.setDistanceTraveled(300);
        assertEquals(300, bike2.getDistanceTraveled());
    }

    @Test
    public void getTotalTrips() throws Exception {
        assertEquals(0, bike1.getTotalTrips());
    }

    @Test
    public void setTotalTrips() throws Exception {
        bike1.setTotalTrips();
    }

    @Test
    public void setLocation() throws Exception {
    }

    @Test
    public void getLocation() throws Exception {
    }

}