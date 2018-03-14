/*
 * This file contains all the functions regarding all communication with the database.
 * @author Fredrik Mediå
 */

package myapp.dbhandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import myapp.instances.Bike;

class DBH {

    Connection db = null;

    String host     = "localhost";
    String username = "root";
    String password = "asdasd";
    String database = "bikerental";

    DBH () {
        try {
            db = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?" + "user=" + username + "&password=" + password);

        } catch (SQLException e) {
            // Handling any errors
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }


    public boolean addBike(Object bike) {
        if (bike instanceof Bike) {
            String make = ((Bike) bike).getMake();
            int price = 10000;
            double batteryPercentage = 0.00;
            String purchaseDate = "01.01.2018"; // Test date until javas date NOW function is implemented
            //int parkingSpotId = ((Bike) bike).getParkingSpotId(); - Does not exist yet

            String sql = "INSERT INTO bikes (price, purchaseDate, totalTrips) VALUES (" + price + ", '" + purchaseDate + "', " + "0)";

            try {
                Statement state = db.createStatement();
                state.executeUpdate(sql);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
        return true;
    }
}

// Just for testing purposes
class DBTest {
    public static void main(String[] args) {
        DBH db = new DBH();
        Bike newB = new Bike(10, "Merida", 100.00, false, 1);
        db.addBike(newB);
    }
}
