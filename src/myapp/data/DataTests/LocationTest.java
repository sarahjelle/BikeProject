package myapp.data.DataTests;

import org.junit.Before;
import org.junit.Test;
import myapp.GUIfx.Map.*;
import myapp.data.*;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class LocationTest {
    private Location location;

    private final MapsAPI map = new MapsAPI();
    private final String NAME = "Nidarosdomen, Trondheim";
    private final Double LAT = map.getLatLongAlt(NAME)[0];
    private final Double LONG = map.getLatLongAlt(NAME)[1];
    private final Double ALT = map.getLatLongAlt(NAME)[2];
    private final Double ERROR_TOLERANCE = 0.00000001;

    @Before
    public void reset(){
        resetLocation();
    }

    @Test
    public void getName() {
        assertEquals(NAME, location.getName());
    }

    @Test
    public void getLatitude() {
        assertEquals(LAT, location.getLatitude());
    }

    @Test
    public void getLongitude() {
        assertEquals(LONG, location.getLongitude());
    }

    @Test
    public void getAltitude() {
        assertEquals(ALT, location.getAltitude());
    }

    @Test
    public void equals() {
        Location newLoc = new Location(NAME, LAT, LONG, ALT);
        assertTrue(location.equals(newLoc));
        newLoc = new Location("Munkholmen Trondheim", true);
        assertFalse(location.equals(newLoc));
    }

    public void resetLocation(){
        this.location = new Location(NAME, LAT, LONG, ALT);
    }
}