/*
 * This file contains all the functions regarding all communication with the database.
 * @author Fredrik Mediå
 */

package myapp.dbhandler;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import myapp.data.Bike;
import myapp.data.Docking;
import myapp.hasher.Hasher;

class DBH {

    Connection db = null;

    String host     = "mysql.stud.iie.ntnu.no";
    String username = "fredrmed";
    String password = "IOFa0YRq";
    String database = "fredrmed";

    public DBH() {

    }

    private Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?" + "user=" + username + "&password=" + password);
        } catch (SQLException e) {
            // Handling any errors
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
        return null;
    }

    private String dateTranslate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    // May not be necessary since LocalDate dont use time
    private String dateTimeTranslate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return date.format(formatter);
    }

    private String execSQL(PreparedStatement sql) {
        try {
            sql.executeUpdate();
            sql.close();
            return null;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return e.toString();
        }
    }

    public String registerBike(Bike bike) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return "Unable to reach database. :(";
            }
            stmt = db.prepareStatement("INSERT INTO bikes (price, make, type) VALUES (?,?,?)");
            stmt.setDouble(1, bike.getPrice());
            stmt.setString(2, bike.getMake());
            stmt.setString(3, bike.getType());

        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }

        return execSQL(stmt);
    }

    public String registerDocking(Docking dock) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return "Unable to reach database. :(";
            }
            stmt = db.prepareStatement("INSERT INTO dokcing_stations (name, maxSlots) VALUES (?,?)");
            stmt.setString(1, dock.getName());
            stmt.setInt(2, dock.getCapacity());


        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }

        return execSQL(stmt);
    }


    public String registerUser(User user) {

        String password = "";
        String salt = "";

        String sql = "INSERT INTO users (userTypeID, email, password, salt, firstname, lastname, phone, landcode) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        return null;
    }
}

// Just for testing purposes
class DBTest {
    public static void main(String[] args) {

    }
}
