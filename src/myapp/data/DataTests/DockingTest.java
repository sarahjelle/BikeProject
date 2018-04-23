package myapp.data.DataTests;


import myapp.data.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class DockingTest {
    private Docking dock;

    private final int ID = 1;
    private final String NAME = "Docking Station";
    private final Location LOCATION = new Location(0.0, 0.0);
    private final int CAPACITY = 10;
    private final int STATUS = Docking.AVAILABLE;

    @Before
    public void before(){
        resetDock();
    }

    @Test
    public void getFreeSpaces() {
        assertEquals(10, dock.getFreeSpaces());
    }

    @Test
    public void forceAddBike() {
        //ID, MAKE, PRICE, TYPE, BATTERY_PERCENTAGE, DIST_TRAVELED, LOCATION, STATUS, PURCHASE_DATE, TOTAL_TRIPS
        Bike bike = new Bike(1, "Make", 100.0, "Type", 1.0, 0, new Location(0.0, 0.0), Bike.AVAILABLE, LocalDate.now(), 0);
        dock.forceAddBike(bike, 1);
        Bike[] bikes = dock.getBikes();
        assertEquals(bike, bikes[0]);
        assertSame(bike, bikes[0]);
    }

    @Test
    public void findFirstOpen() {
        assertEquals(0, dock.findFirstOpen());
        Bike bike = new Bike(1, "Make", 100.0, "Type", 1.0, 0, new Location(0.0, 0.0), Bike.AVAILABLE, LocalDate.now(), 0);
        dock.forceAddBike(bike, 1);
        assertEquals(1, dock.findFirstOpen());
    }

    @Test
    public void getUsedSpaces() {
        Bike bike = new Bike(1, "Make", 100.0, "Type", 1.0, 0, new Location(0.0, 0.0), Bike.AVAILABLE, LocalDate.now(), 0);
        dock.forceAddBike(bike, 1);
        assertEquals(1, dock.getUsedSpaces());
    }

    @Test
    public void undockBike() {
        Bike bike = new Bike(1, "Make", 100.0, "Type", 1.0, 0, new Location(0.0, 0.0), Bike.AVAILABLE, LocalDate.now(), 0);
        dock.forceAddBike(bike, 1);
        assertTrue(dock.undockBike(bike.getId()));
        Bike[] bikes = new Bike[CAPACITY];
        assertArrayEquals(bikes, dock.getBikes());
        assertEquals(bikes.length, dock.getBikes().length);
    }

    @Test
    public void getBikes() {
        Bike bike = new Bike(1, "Make", 100.0, "Type", 1.0, 0, new Location(0.0, 0.0), Bike.AVAILABLE, LocalDate.now(), 0);
        dock.forceAddBike(bike, 1);
        Bike[] bikes = new Bike[CAPACITY];
        bikes[0] = bike;
        assertArrayEquals(bikes, dock.getBikes());
    }

    private void resetDock(){
        //int id, String name, Location location, int capacity, int status
        this.dock = new Docking(ID, NAME, LOCATION, CAPACITY, STATUS);
    }
}