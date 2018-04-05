/*
 * This file contains all the functions regarding all communication with the database.
 * @author Fredrik Mediå
 */

package myapp.dbhandler;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static java.lang.Math.toIntExact;

import myapp.data.*;
import myapp.hasher.*;

public class DBH {

    private Connection db = null;

    private String host     = "mysql.stud.iie.ntnu.no";
    private String username = "fredrmed";
    private String password = "IOFa0YRq";
    private String database = "fredrmed";

    public DBH() { }

    /*
     * CONNECTION METHOD.
     */
    private Connection connect() {
        try {
            Connection DBCon = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?" + "user=" + username + "&password=" + password);
            DBCon.setAutoCommit(false);
            return DBCon;
        } catch (SQLException e) {
            // Handling any errors
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
        return null;
    }

    /*
     * TRANSLATION FOR TIME AND DATE.
     */

    private String dateTranslate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    // May not be necessary since LocalDate dont use time
    private String dateTimeTranslate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return date.format(formatter);
    }

    /*
     * EXECUTE SQL QUERIES.
     */
    private boolean execSQLBool(PreparedStatement sql, Connection db) {
        try {
            sql.executeUpdate();
            db.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            try {
                if (db != null) {
                    db.rollback();
                }
            } catch (SQLException er) {
                System.out.println("Error: " + er);
            }
            return false;
        }
    }

    private int execSQLPK(PreparedStatement sql, Connection db) {
        try {
            int affectedRows = sql.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("DB insert failed, no rows affected.");
            }
            try (ResultSet generatedKeys = sql.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int pk = toIntExact(generatedKeys.getLong(1));
                    sql.close();
                    db.commit();
                    db.close();
                    return pk;
                } else {
                    db.rollback();
                    db.close();
                    return -1;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e);
            try {
                if (db != null) {
                    db.rollback();
                    db.close();
                }
            } catch (SQLException er) {
                System.out.println("Error: " + er);
            }
            return -1;
        }
    }

    private ResultSet execSQLRS(PreparedStatement sql) {
        try {
            return sql.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    /*
     * METHODS BELONGING TO THE BIKE OBJECT.
     */

    public int registerBike(Bike bike) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return -1;
            }
            stmt = db.prepareStatement("INSERT INTO bikes (price, make, type) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setDouble(1, bike.getPrice());
            stmt.setString(2, bike.getMake());
            stmt.setString(3, bike.getType());

        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }

        return execSQLPK(stmt, db);
    }

    public ArrayList<Bike> getBikes() {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT b.bikeID, b.price, b.purchaseDate, b.totalTrips, b.totalKM, b.make, b.type FROM bikes b");
            //stmt = db.prepareStatement("SELECT b.bikeID, b.price, b.purchaseDate, b.totalTrips, b.totalKM, b.make, b.type, l.longitude, l.latitude, l.altitude, l.batteryPercentage FROM bikes b JOIN bike_logs l WHERE b.bikeID = l.bikeID");
            ResultSet bikeset = execSQLRS(stmt);
            ArrayList<Bike> bikes = new ArrayList<Bike>();
            while(bikeset.next()) {
                bikes.add(new Bike(
                        bikeset.getInt("bikeID"),
                        bikeset.getDouble("price"),
                        bikeset.getString("make"),
                        bikeset.getString("type"),
                        50,// bikeset.getDouble("batteryPercentage"),
                        bikeset.getInt("totalKM")
                        /*new Location("Coords",
                                bikeset.getDouble("latitude"),
                                bikeset.getDouble("longitude"),
                                bikeset.getDouble("altitude")
                        )*/
                ));
            }
            stmt.close();
            db.close();
            return bikes;
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public Bike[] updateBikes(Bike[] bikes) {
        db = connect();
        PreparedStatement stmt = null;
        ArrayList<Bike> bikesNotUpdated = new ArrayList<Bike>();
        Bike[] toReturn = null;
        try {
            if(db == null) {
                return bikes;
            }
            stmt = db.prepareStatement("INSERT INTO bike_logs (bikeID, longitude, latitude, altitude, batteryPercentage, totalKM) VALUES (?,?,?,?,?,?)");

            for (int i = 0; i < bikes.length; i++) {
                stmt.setDouble(1, bikes[i].getId());
                stmt.setDouble(2, bikes[i].getLocation().getLongitude());
                stmt.setDouble(3, bikes[i].getLocation().getLatitude());
                stmt.setDouble(4, bikes[i].getLocation().getAltitude());
                stmt.setDouble(5, bikes[i].getBatteryPercentage());
                stmt.setDouble(6, bikes[i].getDistanceTraveled());

                if(!execSQLBool(stmt, db)) {
                    bikesNotUpdated.add(bikes[i]);
                }
            }
            stmt.close();
            db.close();
            return bikesNotUpdated.toArray(new Bike[0]);
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return bikes;
    }

    /*
     * METHODS BELONGING TO THE DOCKING OBJECT.
     */

    public int registerDocking(Docking dock) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return -1;
            }
            stmt = db.prepareStatement("INSERT INTO dokcing_stations (name, maxSlots) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, dock.getName());
            stmt.setInt(2, dock.getCapacity());


        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }

        return execSQLPK(stmt, db);
    }


    /*
     * METHODS BELONGING TO THE USER OBJECT.
     */

    public int registerUser(User user) {
        Hasher hasher = new Hasher();
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return -1;
            }
            String salt = hasher.hashSalt(System.currentTimeMillis() + "");

            String password = hasher.hash(user.getPassword(), salt);

            stmt = db.prepareStatement("INSERT INTO users (userTypeID, email, password, salt, firstname, lastname, phone, landcode) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user.getUserClass());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, password);
            stmt.setString(4, salt);
            stmt.setString(5, user.getFirstname());
            stmt.setString(6, user.getLastname());
            stmt.setInt(7, user.getPhone());
            stmt.setString(8, user.getLandcode());

            user = null;
            return execSQLPK(stmt, db);
        } catch(SQLException e) {
            System.out.println("Error: " + e);
            return -1;
        }
    }
}

// Just for testing purposes
class DBTest {
    public static void main(String[] args) {
        DBH dbh = new DBH();
        Bike[] bikes = new Bike[3];
        bikes[0] = new Bike(1, 100.2, "DBS","Terreng",10.1, 10, new Location("Heime", 63.420924, 10.527217, 0.1));
        bikes[1] = new Bike(2, 200.2, "DBSS","TerrengG",10.2, 20, new Location("Heime", 63.420924, 10.527217, 0.2));
        bikes[2] = new Bike(3, 300.2, "DBSSS","TerrengGG",10.3, 30, new Location("Heime", 63.420924, 10.527217, 0.3));
        bikes = dbh.updateBikes(bikes);
        for (Bike bike : bikes) {
            System.out.println(bike.toString());
        }
        System.out.println("Finished " + bikes.length);
    }
}
