package myapp.map;

import myapp.data.Location;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * <p>A set of methods using URL-requests to the Google Maps Geocode API and Elevation API,
 * to gather information about latitude, longitude and altitude of locations, from the URLs' JSON-responses.
 * <p>Note: The GeoCode API and the Elevation API are limited to 2500 free daily requests.</p>
 */

public class MapsAPI {

    private static final String API_KEY = "AIzaSyA8jBARruH9LiUFxc-DQNLaKRrw6nmyHho";
    private static final String ROADS_API_KEY = "AIzaSyDlJ5qke9-Dw-3-cpk1okWXSXWg3MIRSLc";
    // Sindre Toft Nordal API KEY:
    // https://maps.googleapis.com/maps/api/elevation/json?locations=LATITUDE,LONGITUDE&key=YOUR_API_KEY
    // https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY
    // "adress=trondheim" returns the first search result for Trondheim on Google Maps

    public MapsAPI(){}
    private static MapsAPI instance;

    public static MapsAPI get(){
        if (instance == null){
            instance = new MapsAPI();
        }
        return instance;
    }


    public Location getLocation(String where) {
        if (where.equals("")){
            return null;
        }
        Double[] lla = getLatLongAlt(where);

        if (lla == null) {
            return null;
        }

        return new Location(where, lla);
    }

    public Double getAltitude(String where) {
        Double[] latlong = getLatLong(where);
        if(latlong == null){
            return null;
        }
        return getAltitude(latlong);
    }


    public Double[] getLatLongAlt(String where){
        Double[] latlong = getLatLong(where);
        Double alt = getAltitude(latlong);
        if (latlong == null || alt == null){
            return null;
        }
        return new Double[]{latlong[0], latlong[1], alt};
    }


    public Double[] getLatLong(String where) {
        URL url = createGeoCodeURL(where);
        Double[] latlong = new Double[2];

        try (InputStream is = url.openStream(); JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("results");

            if(obj.getJsonString("status").getString().equals("ZERO_RESULTS")){
                return null;
            }

            for (JsonObject test : results.getValuesAs(JsonObject.class)) {
                //System.out.println(test.toString());
                latlong[0] = test.getJsonObject("geometry").getJsonObject("location").getJsonNumber("lat").doubleValue();
                latlong[1] = test.getJsonObject("geometry").getJsonObject("location").getJsonNumber("lng").doubleValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return latlong;
    }


    public Double getAltitude(Double... latlong){
        if(latlong == null){
            return null;
        }

        URL url = createElevationURL(latlong);
        double altitude = Double.MAX_VALUE;


        try (InputStream is = url.openStream(); JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("results");
            if(obj.getJsonString("status").getString().equals("INVALID_REQUEST")){
                return null;
            }
            for (JsonObject result : results.getValuesAs(JsonObject.class)) {
                altitude = result.getJsonNumber("elevation").doubleValue();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return altitude;
    }

    private URL createGeoCodeURL(String where) {
        URL url = null;
        where = where.replace(" ", "+");
        try {
            url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="
                    + where + "&key=" + API_KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private URL createElevationURL(Double... latlong) {
        URL url = null;
        try{
            url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" + latlong[0] + "," + latlong[1] +
                    "&key=" + API_KEY);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //LEGG TIL DISSE I FERDIG PROSJEKT!!!
    private URL createRevGeoCodeURL(Location where){
        URL url = null;
        try {
            url = new URL("https://maps.google.com/maps/api/geocode/json?latlng="
                    +where.getLatitude()+","+where.getLongitude()+"&sensor=false"+ "&key=" + API_KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getAddress(Location where){
        URL url = createRevGeoCodeURL(where);
        String address = "";

        try (InputStream is = url.openStream(); JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("results");

            if(obj.getJsonString("status").getString().equals("ZERO_RESULTS")){
                return null;
            }
            JsonObject result = results.getJsonObject(0);
            address = result.getString("formatted_address");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public String getAddress(double lat, double lon){
        Location loc = new Location(null, lat, lon);
        return getAddress(loc);
    }

    private URL createWayPointsURL(Location start, Location end){
        URL url = null;
        try {
            url = new URL("https://maps.google.com/maps/api/directions/json?origin="
                    + start.getLatitude()+","+start.getLongitude()+"&destination="
                    + end.getLatitude() + "," + end.getLongitude() + "&mode=bicycling"
                    + "&key=" + API_KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public Location[] getWayPoints(Location start, Location end){
        URL url = createWayPointsURL(start, end);
        ArrayList<Location> waypoints = new ArrayList<>();
        
        try (InputStream is = url.openStream(); JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            
            if(obj.getJsonString("status").getString().equals("ZERO_RESULTS")){
                return null;
            }
            JsonArray routes = obj.getJsonArray("routes");
            JsonObject routes0 = routes.getJsonObject(0);
            JsonArray legs = routes0.getJsonArray("legs");
            JsonObject legs0 = legs.getJsonObject(0);
            JsonArray steps = legs0.getJsonArray("steps");

            waypoints.add(start);
            for (int i = 0; i < steps.size(); i++) {
                JsonObject step = steps.getJsonObject(i);
                JsonObject endLocation = step.getJsonObject("end_location");

                double latitude = endLocation.getJsonNumber("lat").doubleValue();
                double longitude = endLocation.getJsonNumber("lng").doubleValue();
                double altitude = getAltitude(latitude, longitude);
                String address = getAddress(latitude, longitude);
                Location stepEndLocation = new Location(address, latitude, longitude, altitude);
                waypoints.add(stepEndLocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Location[] output = waypoints.toArray(new Location[waypoints.size()]);
        return output;
    }

    public URL createSnapToRoadURL(Location where){
        URL url = null;
        try {
            double lat = where.getLatitude();
            double lng = where.getLongitude();
            url = new URL("https://roads.googleapis.com/v1/snapToRoads?path=" + lat + "," + lng + "&key=" + ROADS_API_KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public Location SnapToRoad(Location where){
        URL url = createSnapToRoadURL(where);
        Location snapped = null;

        try (InputStream is = url.openStream(); JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();

            JsonArray snappedPoints = obj.getJsonArray("snappedPoints");
            JsonObject location = snappedPoints.getJsonObject(0);
            JsonObject location2 = location.getJsonObject("location");

            double latitude = location2.getJsonNumber("latitude").doubleValue();
            double longitude = location2.getJsonNumber("longitude").doubleValue();
            String address = getAddress(latitude, longitude);
            snapped = new Location(address, latitude, longitude);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return snapped;
    }
}

class MapsTest{
    public static void main(String[]args){
        MapsAPI map = new MapsAPI();

        String start = "Bautavegen 3, 7056 Trondheim";
        Double[] latlongStart = map.getLatLong(start);
        Location startLoc = new Location(start, latlongStart[0], latlongStart[1]);

        String end = "Olav Tryggvasons gate 40, 7011 Trondheim";
        Double[] latlongEnd = map.getLatLong(end);
        Location endLoc = new Location(end, latlongEnd[0], latlongEnd[1]);



        Location[] waypoints = map.getWayPoints(startLoc, endLoc);
        for (int i = 0; i < waypoints.length; i++) {
            System.out.println(waypoints[i].getName());
        }


        //Should be: Bautavegen 3 7056 Ranheim is at : lat: 63.420924 long: 10.527217
        //https://maps.google.com/maps/api/geocode/json?latlng=63.420937,10.527159&sensor=false&key=AIzaSyA8jBARruH9LiUFxc-DQNLaKRrw6nmyHho
        //Roseborg gate 1: 63.434365,10.414946
        //Sverresgate 15: 63.4290398,10.3893292
    }
}


