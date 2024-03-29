package myapp.data;

import myapp.GUIfx.Map.MapsAPI;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Location is a object representing the Location-holders position. This is supposed to belong to
 * for example a Bike or a Docking object.
 */
public class Location implements Serializable {

    private String name;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private LocalDate localDate;

    /**
     * This constructor is meant for creating location object where to lat, long and altitude is unknown.
     * By entering the name and setting getCoords to true it will automatically create a complete Location object.
     * @param name          the address of the location
     * @param getCoords     True = getting coords from google maps, False = not getting coords.
     */
    public Location(String name, boolean getCoords) {
        this.name = name;
        Double[] latlongalt = MapsAPI.get().getLatLongAlt(name);

        if (getCoords && !(latlongalt == null ||
                latlongalt[0] == null || latlongalt[1] == null || latlongalt[2] == null)) {

            this.latitude  = latlongalt[0];
            this.longitude = latlongalt[1];
            this.altitude  = latlongalt[2];
        }
    }

    public Location(Double latitude, Double longitude, LocalDate ld){
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = 0.;
        this.localDate = ld;
        this.name = "";
    }

    /**
     * empty constructor for testing purposes, Not safe to use without caution!
     */
    public Location() {
        this.name      = null;
        this.latitude  = null;
        this.longitude = null;
        this.altitude  = null;
    }

    public Location(String name, Double... coords) {
        this.name = name;
        if (coords.length >= 1) {
            this.latitude = coords[0];
            if (coords.length >= 2) {
                this.longitude = coords[1];
                if (coords.length >= 3) {
                    this.altitude = coords[2];
                }
            }
        }
    }

    /**
     * Constructor to be used when only lat and long is known
     * @param lat   the latitude of the position
     * @param lng   the longitude of the position
     */
    public Location(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public boolean setName(String name) {
        this.name = name;
        return true;
    }

    public boolean setLatitude(Double latitude) {
        this.latitude = latitude;
        return true;
    }

    public boolean setLongitude(Double longitude) {
        this.longitude = longitude;
        return true;
    }

    public boolean setAltitude(Double altitude) {
        this.altitude = altitude;
        return true;
    }

    @Override
    public String toString() {
        return (name != null ? name : "nameless") + " = { latitute=" + latitude + ", longitute=" + longitude + ", altitude=" + altitude + "}";
    }

    /**
     * <p>Determines wether this {@code Location} equals the {@code Object}.</p>
     * <p>They are equal if the {@code Object} is an instance of a {@code Location}
     * and both their latitude and their longitude are equal.</p>
     * @param o the {@code Object} to compare to
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Location)) return false;

        Location l = (Location) o;
        return ((latitude  == null && l.getLatitude() == null) || latitude.equals(l.getLatitude()) )
                && ((longitude == null && l.getLongitude() == null) || longitude.equals(l.getLongitude()));

    }
}

