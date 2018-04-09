package myapp.Test;

import myapp.data.*;
import myapp.map.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class BikeTest {
    private Location location1 = new Location("Sverres Gate 14", true);
    private Location location2 = new Location("Dronningens Gate 64", true);
    private Location location3 = new Location("Beddingen 4", true);
    private Bike bike1 = new Bike(1, "Merida", 97.0, true, 100, location1);
    private Bike bike2 = new Bike(2, "Kona", 30.0, true, 200, location2);
    private Bike bike3 = new Bike(3, "Merida", 48.0, true, 300, location3);

    @Test
    public void getId() throws Exception {
        int id = bike1.getId();
        assertEquals(id, 1);
    }

    @Test
    public void getMake() throws Exception {
        String make = bike2.getMake();
        assertEquals(make, "Kona");
    }

    @Test
    public void getBatteryPercentage() throws Exception {
        Double bp = bike3.getBatteryPercentage();
        assertEquals(Double.valueOf(bp), Double.valueOf(48.0));
    }

    @Test
    public void setBatteryPercentage() throws Exception {
        bike1.setBatteryPercentage(100.0);
        assertEquals(Double.valueOf(bike1.getBatteryPercentage()), (Double)100.0);
    }

    @Test
    public void isAvailable() throws Exception {
        assertEquals(true, bike1.isAvailable());
    }

    @Test
    public void setAvailable() throws Exception {
        bike1.setAvailable(false);
        assertEquals(bike1.isAvailable(), false);
    }

    @Test
    public void getDistanceTraveled() throws Exception {
        assertEquals(bike2.getDistanceTraveled(), 200);
    }

    @Test
    public void setLocation() throws Exception {
        Location l = new Location("Falsensvei 26", true);
        bike2.setLocation(l);
        assertEquals(l, bike2.getLocation());
    }

    @Test
    public void getLocation() throws Exception {
        Location loc = bike1.getLocation();
        //assertEquals((String)loc, "Sverres Gate 14 = { latitute=59.91116349999999, longitute=10.7754596, altitude=24.63603591918945}");
    }

    @Test
    public void getReport() throws Exception {
    }

}