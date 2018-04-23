package myapp.data.DataTests;

import java.time.LocalDate;

import myapp.data.Bike;
import myapp.data.Location;
import myapp.data.Repair;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BikeTest {
    private Bike bike;

    private final int ID = 1;
    private final String MAKE = "TestMake";
    private final double PRICE = 100.0;
    private final String TYPE = "TestType";
    private final double BATTERY_PERCENTAGE = 1.0;
    private final int DIST_TRAVELED = 0;
    private final Location LOCATION = new Location(0.0, 0.0);
    private final int STATUS = Bike.AVAILABLE;
    private final LocalDate PURCHASE_DATE = LocalDate.now();
    private final int TOTAL_TRIPS = 0;

    private final double ERROR_TOLERANCE = 0.000001;

    @Before
    public void setup(){
        resetBike();
    }

    public void resetBike(){
        this.bike = new Bike(ID, MAKE, PRICE, TYPE, BATTERY_PERCENTAGE, DIST_TRAVELED, LOCATION, STATUS, PURCHASE_DATE, TOTAL_TRIPS);
    }

    @Test
    public void getId() {
        assertEquals(ID, bike.getId());
        assertSame(ID, bike.getId());
    }

    @Test
    public void getMake() {
        assertEquals(MAKE, bike.getMake());
        assertSame(MAKE, bike.getMake());
    }

    @Test
    public void setMake() {
        String newMake = "NewMake";
        bike.setMake(newMake);
        assertEquals(newMake, bike.getMake());
    }

    @Test
    public void getPrice() {
        assertEquals(PRICE, bike.getPrice(), ERROR_TOLERANCE);
    }

    @Test
    public void setPrice() {
        double newPrice = 0.0;
        bike.setPrice(newPrice);
        assertEquals(newPrice, bike.getPrice(), ERROR_TOLERANCE);
    }

    @Test
    public void getPurchased() {
        assertEquals(PURCHASE_DATE, bike.getPurchased());
        assertSame(PURCHASE_DATE, bike.getPurchased());
    }

    @Test
    public void setPurchased() {
        LocalDate newDate = LocalDate.now();
        bike.setPurchased(newDate);
        assertEquals(newDate, bike.getPurchased());
        assertSame(newDate, bike.getPurchased());
    }

    @Test
    public void getType() {
        assertEquals(TYPE, bike.getType());
        assertSame(TYPE, bike.getType());
    }

    @Test
    public void setType() {
        String newType = "NewType";
        bike.setType(newType);
        assertEquals(newType, bike.getType());
        assertSame(newType, bike.getType());
    }

    @Test
    public void getBatteryPercentage() {
        assertEquals(BATTERY_PERCENTAGE, bike.getBatteryPercentage(), ERROR_TOLERANCE);
    }

    @Test
    public void setBatteryPercentage() {
        double newBat = 100.0;
        bike.setBatteryPercentage(newBat);
        assertEquals(newBat, bike.getBatteryPercentage(), ERROR_TOLERANCE);
    }

    @Test
    public void getDistanceTraveled() {
        assertEquals(DIST_TRAVELED, bike.getDistanceTraveled());
    }

    @Test
    public void setDistanceTraveled() {
        int newDist = 1000;
        bike.setDistanceTraveled(newDist);
        assertEquals(newDist, bike.getDistanceTraveled());
    }

    @Test
    public void getTotalTrips() {
        assertEquals(TOTAL_TRIPS, bike.getTotalTrips());
    }

    @Test
    public void setTotalTrips() {
        int newTotTrips = TOTAL_TRIPS + 1;
        bike.setTotalTrips();
        assertEquals(newTotTrips, bike.getTotalTrips());
    }

    @Test
    public void getRepairs() {
        Repair[] repairs = {new Repair(bike.getId(), "RepairDescription", LocalDate.now())};
        bike.addRepairRequest(repairs[0].getDesc(), repairs[0].getRequestDate());
        assertEquals(repairs[0].getCaseID(), bike.getRepairs()[0].getCaseID());
        //assertArrayEquals(repairs, bike.getRepairs());
    }

    @Test
    public void setRepairs() {
        Repair[] repairs = {new Repair(bike.getId(), "RepairDescription", LocalDate.now())};
        bike.setRepairs(repairs);
        assertEquals(repairs[0].getCaseID(), bike.getRepairs()[0].getCaseID());
        //assertArrayEquals(repairs, bike.getRepairs());
    }

    @Test
    public void setStatus() {
        int newStatus = Bike.DELETE;
        bike.setStatus(newStatus);
        assertEquals(newStatus, bike.getStatus());
    }

    @Test
    public void getStatus() {
        assertEquals(STATUS, bike.getStatus());
    }

    @Test
    public void setLocation() {
        Location newLoc = new Location("Nidarosdomen Trondheim", true);
        bike.setLocation(newLoc);
        assertEquals(newLoc, bike.getLocation());
        assertSame(newLoc, bike.getLocation());
    }

    @Test
    public void getLocation() {
        assertEquals(LOCATION, bike.getLocation());
    }

    @Test
    public void equals() {
        Bike newBike = new Bike(ID, MAKE, PRICE, TYPE, BATTERY_PERCENTAGE, DIST_TRAVELED, LOCATION, STATUS, PURCHASE_DATE, TOTAL_TRIPS);
        assertTrue(bike.equals(newBike));

        newBike = new Bike(2, MAKE, PRICE, TYPE, BATTERY_PERCENTAGE, DIST_TRAVELED, LOCATION, STATUS, PURCHASE_DATE, TOTAL_TRIPS);
        assertFalse(bike.equals(newBike));
    }

    @Test
    public void string() {
        String expected = "ID: " + ID + " Type: " + TYPE + " Make: ";
        assertEquals(expected, bike.toString());
    }
}