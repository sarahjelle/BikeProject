/*
 * This file contains all the functions regarding all communication with the database.
 * @author Fredrik Mediå
 */

package myapp.dbhandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import myapp.data.Bike;

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
   /*
    bikeID int NOT NULL AUTO_INCREMENT,
    price int NOT NULL,
    purchaseDate date NOT NULL,
    totalTrips int NOT NULL,
    totalKM decimal NOT NULL,
    type varchar(25) NOT NULL,
    make varchar(25) NOT NULL,
   */
    public boolean addBike(Object bike) {
        if (bike instanceof Bike) {
            String make = ((Bike) bike).getMake();
            String type = ((Bike) bike).getType();
            double price = ((Bike) bike).getPrice();
            double batteryPercentage = ((Bike) bike).getBatteryPercentage();
            String[] purchased = ((Bike) bike).getPurchased().toString().split(" ");

            String sql = "INSERT INTO bikes (price, purchaseDate, totalTrips, totalKM, type, make) VALUES ("
                    + ((Bike) bike).getPrice() + ", '"
                    + purchased[0] + "', "
                    + ((Bike) bike).getTotalTrips() + ", "
                    + ((Bike) bike).getType() + ", "
                    + ((Bike) bike).getMake() + ")";

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
