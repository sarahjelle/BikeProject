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

/**
 * <p>A set of methods using URL-requests to the Google Maps Geocode API and Elevation API,
 * to gather information about latitude, longitude and altitude of locations, from the URLs' JSON-responses.
 * <p>Note: The GeoCode API and the Elevation API are limited to 2500 free daily requests.</p>
 */

public class MapsAPI {

    private static final String API_KEY = "AIzaSyA8jBARruH9LiUFxc-DQNLaKRrw6nmyHho";
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
}

class MapsTest{
    public static void main(String[]args){
        String loc = "Bautavegen 3, 7056 Trondheim";
        MapsAPI map = new MapsAPI();
        Double[] latlong = map.getLatLong(loc);
        double lat = latlong[0];
        double lng = latlong[1];
        System.out.println(loc + " is at : lat: " + lat + " long: " + lng);

        //Should be: Bautavegen 3 7056 Ranheim is at : lat: 63.420924 long: 10.527217
    }
}


