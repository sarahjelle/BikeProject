/*
 * This file contains all the functions regarding all communication with the database.
 * @author Fredrik Mediå
 */

package myapp.dbhandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import myapp.data.Bike;

public class DBH {

    Connection db = null;

    String host     = "localhost";
    String username = "root";
    String password = "asdasd";
    String database = "bikerental";

    public DBH () {
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

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String purchased = dateFormat.format(((Bike) bike).getPurchased());

            System.out.println("II: " + purchased);

            String sql = "INSERT INTO bikes (price, purchaseDate, totalTrips, totalKM, bikeType, make) VALUES ("
                    + ((Bike) bike).getPrice() + ", '"
                    + purchased + "', "
                    + ((Bike) bike).getTotalTrips() + ", "
                    + ((Bike) bike).getDistanceTraveled() + ", '"
                    + ((Bike) bike).getType() + "', '"
                    + ((Bike) bike).getMake() + "')";

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
