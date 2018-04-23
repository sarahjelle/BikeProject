/*
 * This file contains all the functions regarding all communication with the database.
 * @author Fredrik Mediaa
 */

package myapp.dbhandler;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.toIntExact;

import com.sun.org.apache.regexp.internal.RE;
import myapp.GUIfx.Map.MapsAPI;
import myapp.MailHandler.MailHandler;
import myapp.data.*;
import myapp.hasher.*;

import javax.mail.MessagingException;
import javax.print.Doc;

import static myapp.data.User.*;

/**
 * DBH is the systems DataBaseHandler. This means that every insert, delete, update or
 * select query to the DB goes through this object. This is to ensure opening and closing all
 * connections to the DB is done correctly. We also accomplish a tidy way of doing DB management.
 *
 * @author Fredrik Mediaa
 * @author Martin Moan
 */

public class DBH {

    private Connection db = null;

    private String host     = "mysql.stud.iie.ntnu.no";
    private String username = "fredrmed";
    private String password = "IOFa0YRq";
    private String database = "fredrmed";

    /*
     * CONNECTION METHOD.
     */

    /**
     * Connect takes no parameters and is used to create a connection to the database details listed in private attributes for DBH.
     * It also takes care of setAutoCommit to false.
     *
     * @author Fredrik Mediaa
     */
    private void connect() {
        try {
            if (db != null) {
                db.close();
            }
            Connection DBCon = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?" + "user=" + username + "&password=" + password + "&useSSL=false");
            DBCon.setAutoCommit(false);
            db = DBCon;

        } catch (SQLException e) {
            // Handling any errors
            e.printStackTrace();
        }
    }

    /**
     * forceClose takes no parameters and is used to make sure every methods in the DBH object closes the connection whenever it is done communicating.
     * It also rollback any changes done to the DB which haven't been executed.
     *
     * @author Fredrik Mediaa
     */
    private void forceClose() {
        try {
            if(db != null) {
                db.rollback();
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * TRANSLATION FOR TIME AND DATE.
     */

    /**
     * dateTimeToDateOnly is a conversion method for converting a String formatted like yyyy-MM-dd hh:mm:ss to a LocalDate object.
     *
     * @param   datetime    the String containing both date and time.
     * @return              the LocalDate object created with details from datetime.
     * @author Fredrik Mediaa
     */
    private LocalDate dateTimeToDateOnly(String datetime) {
        String date[] = datetime.split(" ")[0].split("-");
        return LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
    }

    /**
     * dateToLocalDate is a conversion method for converting a String formatted like yyyy-MM-dd to a LocalDate object.
     *
     * @param   date        the String containing only date.
     * @return              the LocalDate object created from the details from date.
     * @author Fredrik Mediaa
     */
    private LocalDate dateToLocalDate(String date) {
        if(date != null) {
            String dateArr[] = date.split("-");
            return LocalDate.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
        }
        return null;
    }



    /*
     * EXECUTE SQL QUERIES.
     */

    /**
     * execSQLBool is a query execution method returning a boolean based on the results from the sql query.
     *
     * @param   sql     the statement prepared on beforehand.
     * @return          boolean based on the result from the query. True = OK, False = something went wrong.
     * @see PreparedStatement
     * @author Fredrik Mediaa
     */
    private boolean execSQLBool(PreparedStatement sql) {
        try {
            sql.executeUpdate();
            db.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (db != null) {
                    db.rollback();
                }
            } catch (SQLException er) {
                er.printStackTrace();
            }
            return false;
        }
    }

    /**
     * execSQLPK is a query execution method returning the PrimaryKey of an insert query
     *
     * @param   sql     the statement prepared on beforehand.
     * @return          an int based on the result from the insert query.
     * @see PreparedStatement
     * @author Fredrik Mediaa
     */
    private int execSQLPK(PreparedStatement sql) {
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
            e.printStackTrace();
            forceClose();
            return -1;
        }
    }

    /**
     * execSQLRS is a query execution method returning a ResultSet based on results from the sql query
     *
     * @param   sql     the statement prepared on beforehand.
     * @return          returns a ResultSet with all results from DB.
     * @see ResultSet
     * @see PreparedStatement
     * @author Fredrik Mediaa
     */
    private ResultSet execSQLRS(PreparedStatement sql) {
        try {
            return sql.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    /*
     * METHODS BELONGING TO THE BIKE OBJECT.
     */

    /**
     * registerBike is a function taking care of inserting new entries into the database in relation to the Bike object.
     *
     * @param   bike    the bike that is ready to be registered in the database.
     * @return          returns the PrimaryKey also known as the bikes ID
     * @see Bike
     * @author Fredrik Mediaa
     */
    public int registerBike(Bike bike) {
        PreparedStatement stmt = null;
        try {

            String purchased = (bike.getPurchased() == null) ? purchased = LocalDate.now().toString() : bike.getPurchased().toString();

            connect();
            if(db == null) {
                return -1;
            }

            stmt = db.prepareStatement("INSERT INTO bikes (price, purchaseDate, make, type) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setDouble(1, bike.getPrice());
            stmt.setString(2, purchased);
            stmt.setString(3, bike.getMake());
            stmt.setString(4, bike.getType());

        } catch(SQLException e) {
            e.printStackTrace();
            forceClose();
        }
        return execSQLPK(stmt);
    }

    /**
     * getAllBikesOA is a helper method for the original getAllBikes. This method just converts the ArrayList of Bike into a normal array
     *
     * @return          returns a normal Bike[] array of all bikes
     * @author Fredrik Mediaa
     */
    public Bike[] getAllBikesOA() {
        ArrayList<Bike> bikes = getAllBikes();
        return bikes.toArray(new Bike[bikes.size()]);
    }

    /**
     * getAllBikes is the original method and is to be used when looking for all bikes registered in the database.
     * It takes care of gathering all repairs belonging to every specific bike and pairing them together.
     * It also takes care of the location for each bike. Docked bikes will get location from Docking station, while bikes
     * out and about will have the latest logged location.
     * <p>
     * Because of GUI mainly use ArrayLists I decided to return this.
     *
     * @return          returns an ArrayList of the object Bike
     * @see Bike
     * @see ArrayList
     * @see Repair
     * @author Fredrik Mediaa
     */
    public ArrayList<Bike> getAllBikes() {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM allBikesWithLocNew WHERE status != ?");
            stmt.setInt(1, Bike.DELETE);
            ResultSet bikeset = execSQLRS(stmt);
            ArrayList<Bike> bikes = new ArrayList<>();
            while(bikeset.next()) {
                bikes.add(new Bike(
                        bikeset.getInt("bikeID"),
                        bikeset.getString("make"),
                        bikeset.getDouble("price"),
                        bikeset.getString("type"),
                        bikeset.getDouble("batteryPercentage"),
                        bikeset.getInt("totalKm"),
                        new Location(
                                bikeset.getDouble("latitude"),
                                bikeset.getDouble("longitude")
                        ),
                        bikeset.getInt("status"),
                        dateTimeToDateOnly(bikeset.getString("purchaseDate")),
                        bikeset.getInt("totalTrips")
                ));
            }

            stmt.close();
            db.close();

            // Adding repairs to the right bike.
            Repair[] allRepairs = getAllRepairs();
            for(Bike bike : bikes) {
                ArrayList<Repair> repairsForBike = new ArrayList<>();
                for (int i = 0; i < allRepairs.length; i++) {
                    if (allRepairs[i].getBikeID() == bike.getId()) {
                        repairsForBike.add(allRepairs[i]);
                    }
                }
                if (repairsForBike.size() > 0) {
                    Repair[] toInsert = new Repair[repairsForBike.size()];
                    bike.setRepairs(repairsForBike.toArray(toInsert));
                }
            }

            return bikes;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getBikeByID finds a bike and returns it based on the ID passed in.
     *
     * @param   bikeToFind  the bike that should be found in the database.
     * @return              the bike in its full detail updated by the database.
     * @see Bike
     * @author Fredrik Mediaa
     */
    public Bike getBikeByID(int bikeToFind) {
        ArrayList<Bike> bikes = getAllBikes();
        for(Bike bike : bikes) {
            if (bike.getId() == bikeToFind) {
                return bike;
            }
        }
        return null;
    }

    /**
     * getBikeByID is a method for passing in a Bike object.
     * This object bikeID will be sent to the original method taking care returning the bike asked for.
     *
     * @param   bike    the Bike object wanted to be found in the database.
     * @return          returns the bike with new details updated from database.
     * @see Bike
     * @author Fredrik Mediaa
     */
    public Bike getBikeByID(Bike bike) {
        return getBikeByID(bike.getId());
    }

    /**
     * getBikesByStatus is a method for passing in a spesific status id. This method returns an ArrayList of Bikes containing this status.
     *
     * @param   status  the status id of bikes wanted to be returned
     * @return          ArrayList of Bike objects filtrated by status id
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    private ArrayList<Bike> getBikesByStatus(int status) {
        ArrayList<Bike> bikes = getAllBikes();
        ArrayList<Bike> result = new ArrayList<>();
        for(Bike bike : bikes) {
            if(bike.getStatus() == status) {
                result.add(bike);
            }
        }
        return result;
    }

    /**
     * getAllBikesOnTrip returns an ArrayList of Bike objects which has status Bike.TRIP.
     *
     * @return          the ArrayList of Bike objects
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public Bike[] getAllBikesOnTrip(){
        PreparedStatement stmt = null;
        ArrayList<Bike> outList = new ArrayList<>();
        try{
            connect();
            if(db == null){
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM undockedBikesWithNewestLogLocNew");
            ResultSet set = execSQLRS(stmt);
            while(set.next()){
                outList.add(new Bike(
                        set.getInt("bikeID"),
                        set.getString("make"),
                        set.getDouble("price"),
                        set.getString("type"),
                        set.getDouble("batteryPercentage"),
                        set.getInt("totalKm"),
                        new Location(
                                set.getDouble("latitude"),
                                set.getDouble("longitude")
                        ),
                        set.getInt("status"),
                        dateTimeToDateOnly(set.getString("purchaseDate")),
                        set.getInt("totalTrips")
                ));
            }

            if(outList.size() > 0) {
                Bike[] bikes = new Bike[outList.size()];
                bikes = outList.toArray(bikes);
                return bikes;
            }
        } catch(SQLException e){
            forceClose();
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Official method for getting an ArrayList of Bike objects with status Bike.AVAILABLE
     *
     * @return          ArrayList of Bike objects
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<Bike> getBikesWithStatusAvailable() {
        return getBikesByStatus(Bike.AVAILABLE);
    }

    /**
     * Official method for getting an ArrayList of Bike objects with status Bike.TRIP
     *
     * @return          ArrayList of Bike objects
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<Bike> getBikesWithStatusInTrip() {
        return getBikesByStatus(Bike.TRIP);
    }

    /**
     * Official method for getting an ArrayList of Bike objects with status Bike.REPAIR
     *
     * @return          ArrayList of Bike objects
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<Bike> getBikesWithStatusRepair() {
        return getBikesByStatus(Bike.REPAIR);
    }

    /**
     * Official method for getting an ArrayList of Bike objects with status Bike.DELETE
     *
     * @return          ArrayList of Bike objects
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<Bike> getBikesWithStatusSoftDelete() {
        return getBikesByStatus(Bike.DELETE);
    }

    /**
     * getLoggedBikesOA is an alternative method for getLoggedBikes to get an normal Bike array instead of ArrayList
     *
     * @return          returns an normal Bike array
     * @see Bike
     * @author Fredrik Mediaa
     */
    public Bike[] getLoggedBikesOA() {
        ArrayList<Bike> bikes = getLoggedBikes();
        return bikes.toArray(new Bike[bikes.size()]);
    }

    /**
     * getLoggedBikes is the main method for getting ArrayList of Bike objects containing their latest location logged
     *
     * @return          returns an ArrayList of Bike objects with the latest updated Locations
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<Bike> getLoggedBikes() {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM undockedBikesWithNewestLogLocNew");
            ResultSet bikeset = execSQLRS(stmt);
            ArrayList<Bike> bikes = new ArrayList<Bike>();

            while(bikeset.next()) {


                bikes.add(new Bike(
                        bikeset.getInt("bikeID"),
                        bikeset.getString("make"),
                        bikeset.getDouble("price"),
                        bikeset.getString("type"),
                        bikeset.getDouble("batteryPercentage"),
                        bikeset.getInt("totalKm"),
                        new Location(
                                bikeset.getDouble("latitude"),
                                bikeset.getDouble("longitude")
                        ),
                        bikeset.getInt("status"),
                        dateTimeToDateOnly(bikeset.getString("purchaseDate")),
                        bikeset.getInt("totalTrips")
                ));
            }
            stmt.close();
            db.close();
            return bikes;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deleteBike use the softDelete status int Bike.DELETE to update the bike of the specific ID.
     *
     * @param   id      the ID of the bike which should be soft deleted.
     * @return          boolean based on the result from the query. True = OK, False = something went wrong.
     * @author Fredrik Mediaa
     */
    public boolean deleteBike(int id) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return false;
            }
            stmt = db.prepareStatement("UPDATE bikes SET status = ? WHERE bikeID = ?");

            stmt.setInt(1, Bike.DELETE);
            stmt.setInt(2, id);

            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * deleteBike is an alternative method where passing in a Bike object returns the same as if it was only passed an int with id
     *
     * @param   bike    the Bike object which is supposed to be deleted
     * @return          boolean based on the result from the query. True = OK, False = something went wrong.
     * @see Bike
     * @author Fredrik Mediaa
     */
    public boolean deleteBike(Bike bike) {
        return deleteBike(bike.getId());
    }

    /**
     * updateBike fully updates the database to match the Bike object passed in.
     *
     * @param   bike    the Bike object wanted to be mirrored in the database
     * @return          boolean based on the result from the query. True = OK, False = something went wrong.
     * @see Bike
     * @author Fredrik Mediaa
     */
    public boolean updateBike(Bike bike) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return false;
            }

            if(bike.getPurchased() == null) {
                stmt = db.prepareStatement("UPDATE bikes SET price = ?, make = ?, type = ?, status = ? WHERE bikeID = ?");
                stmt.setDouble(1, bike.getPrice());
                stmt.setString(2, bike.getMake());
                stmt.setString(3, bike.getType());
                stmt.setInt(4, bike.getStatus());
                stmt.setInt(5, bike.getId());
            } else {
                stmt = db.prepareStatement("UPDATE bikes SET price = ?, purchaseDate = ?, make = ?, type = ?, status = ? WHERE bikeID = ?");
                stmt.setDouble(1, bike.getPrice());
                stmt.setString(2, bike.getPurchased().toString());
                stmt.setString(3, bike.getMake());
                stmt.setString(4, bike.getType());
                stmt.setInt(5, bike.getStatus());
                stmt.setInt(6, bike.getId());
            }

            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * updateBikeFromLog is method for taking care of updating distanceTraveled and batteryPercentage in the database.
     * Requires the method calling this already having updated a DB object.
     *
     * @param   bike    the Bike object with all the values wanted to be updated
     * @return          a boolean based on the result of the query. True = OK, False = something went wrong.
     * @see Bike
     * @author Fredrik Mediaa
     */
    private void updateBikeFromLog(Bike bike) {
        PreparedStatement stmt = null;
        try {

            if(db == null) {
                return;
            }
            stmt = db.prepareStatement("UPDATE bikes SET totalKm = ?, batteryPercentage = ? WHERE bikeID = ?");
            stmt.setInt(1, bike.getDistanceTraveled());
            stmt.setDouble(2, bike.getBatteryPercentage());
            stmt.setInt(3, bike.getId());

            execSQLBool(stmt);

            stmt.close();
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
    }

    /**
     * logBikes takes in a Bike array and loops through creating sql insert queries for each one. Bike objects failed to insert
     * will be returned in a Bike array.
     *
     * @param   bikes   a Bike object array with all bikes containing the information to be logged
     * @return          a Bike object array of failed bikes.
     * @see Bike
     * @author Fredrik Mediaa
     */
    public Bike[] logBikes(Bike[] bikes) {
        PreparedStatement stmt = null;
        ArrayList<Bike> bikesNotUpdated = new ArrayList<>();
        Bike[] toReturn = null;
        try {
            connect();
            if(db == null) {
                return bikes;
            }
            stmt = db.prepareStatement("INSERT INTO bike_logs (bikeID, longitude, latitude, altitude, batteryPercentage, totalKM) VALUES (?,?,?,?,?,?)");
            MapsAPI map = new MapsAPI();
            for (int i = 0; i < bikes.length; i++) {
                stmt.setDouble(1, bikes[i].getId());
                stmt.setDouble(2, bikes[i].getLocation().getLongitude());
                stmt.setDouble(3, bikes[i].getLocation().getLatitude());
                if(bikes[i].getLocation().getAltitude() != null){
                    Double DAlt = Double.parseDouble(bikes[i].getLocation().getAltitude().toString());
                    double dAlt = (double) DAlt;
                    int iAlt = (int) dAlt;
                    stmt.setInt(4, iAlt);
                } else{
                    String sAlt = map.getAltitude(bikes[i].getLocation().getLatitude(), bikes[i].getLocation().getLongitude()).toString();
                    Double DAlt = Double.parseDouble(sAlt);
                    double dAlt = (double) DAlt;
                    int iAlt = (int) dAlt;

                    stmt.setInt(4, iAlt);
                }
                stmt.setDouble(4, 0.0);
                stmt.setDouble(5, bikes[i].getBatteryPercentage());
                stmt.setInt(6, bikes[i].getDistanceTraveled());

                if(!execSQLBool(stmt)) {
                    bikesNotUpdated.add(bikes[i]);
                }
                updateBikeFromLog(bikes[i]);
            }
            stmt.close();
            db.close();
            return bikesNotUpdated.toArray(new Bike[bikesNotUpdated.size()]);
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return bikes;
    }

    /**
     * getBikeMakes returns all the different makes as an ArrayList of String objects.
     *
     * @return      ArrayList of String objects with all the different makes.
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<String> getBikeMakes() {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT DISTINCT make FROM bikes ORDER BY make");
            ResultSet makes = execSQLRS(stmt);
            ArrayList<String> makesArray = new ArrayList<>();

            while(makes.next()) {
                makesArray.add(makes.getString("make"));
            }

            stmt.close();
            db.close();
            return makesArray;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getBikeTypes returnes all the different types as an ArrayList of String objects.
     *
     * @return      ArrayList of String objects with all the different types.
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<String> getBikeTypes() {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT description FROM bikeTypes WHERE active = 1 ORDER BY description");
            ResultSet type = execSQLRS(stmt);
            ArrayList<String> typeArray = new ArrayList<>();

            while(type.next()) {
                typeArray.add(type.getString("description"));
            }

            stmt.close();
            db.close();
            return typeArray;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deleteBikeType sets the description given to an inactive state.
     * @param   desc    String object of the description wanted to be deleted
     * @return          a boolean based on the result, True = deleted, False = not deleted
     */
    public boolean deleteBikeType(String desc) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return false;
            }
            stmt = db.prepareStatement("UPDATE bikeTypes SET active = 0 WHERE description = ?");
            stmt.setString(1, desc);

            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * addBikeType inserts a new type of bike to the bikeTypes table enabling users to use the new type
     * @param   desc    String object with the name of the wanted type
     * @return          a boolean based on the result, True = added, False = not added
     */
    public boolean addBikeType(String desc) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return false;
            }
            stmt = db.prepareStatement("INSERT INTO bikeTypes (description) VALUES (?)");
            stmt.setString(1, desc);

            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * changeStatus takes care of changing status of a bike in the database for other methods.
     *
     * @param   bikeID  the bike ID of the bike to be changed
     * @param   status  the value of the status to be changed to
     * @author Fredrik Mediaa
     */
    private void changeStatus(int bikeID, int status) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return;
            }

            stmt = db.prepareStatement("UPDATE bikes SET status = ? WHERE bikeID = ?");
            stmt.setInt(1, status);
            stmt.setInt(2, bikeID);

            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return;
            }
            stmt.close();
            db.close();
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
    }


    /*
     * METHODS BELONGING TO THE DOCKING OBJECT.
     */

    /**
     * registerDocking takes in a Docking object which then will be turned into an sql query. This method returnes the PrimaryKey of the insert statement.
     *
     * @param   dock    the Docking object whichs is to be inserted into the database
     * @return          PrimaryKey of the insert. Positive number means success, -1 means something went wrong.
     * @see Docking
     * @author Fredrik Mediaa
     */
    public int registerDocking(Docking dock) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return -1;
            }

            Location loc = new Location(dock.getName(), true);
            stmt = db.prepareStatement("INSERT INTO docking_stations (stationName, maxSlots, latitude, longitude) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, dock.getName());
            stmt.setInt(2, dock.getCapacity());
            stmt.setDouble(3, dock.getLocation().getLatitude());
            stmt.setDouble(4, dock.getLocation().getLongitude());

            int pk = execSQLPK(stmt);

            if(pk > 0) {
                generateDockingSlots(dock.getCapacity(), pk);
            }

            return pk;

        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * getAllDockingStations returns all docking stations from database as an ArrayList of Docking objects.
     *
     * @return      ArrayList of Docking objects found in database
     * @see Docking
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public ArrayList<Docking> getAllDockingStations() {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM docking_stations WHERE status = ?");
            stmt.setInt(1, Docking.AVAILABLE);

            ResultSet dockingSet = execSQLRS(stmt);
            ArrayList<Docking> docks = new ArrayList<>();
            while(dockingSet.next()) {
                docks.add(new Docking(
                        dockingSet.getInt("stationID"),
                        dockingSet.getString("stationName"),
                        new Location(
                                dockingSet.getDouble("latitude"),
                                dockingSet.getDouble("longitude")
                        ),
                        dockingSet.getInt("maxSlots"),
                        dockingSet.getInt("status")
                ));
            }

            stmt.close();
            db.close();
            return docks;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * updateBikesInDockingStation takes in the ID and finds all bikes belonging to the station and returns them as a Bike object array.
     *
     * @param   dockID  the ID of a docking station.
     * @return             a Bike object array belonging to the docking station with the ID passed in.
     * @see Bike
     * @see ArrayList
     * @author Fredrik Mediaa
     */
    public Bike[] updateBikesInDockingStation(int dockID) {
        Docking[] docks = getAllDockingStationsWithBikes();
        for (int i = 0; i < docks.length; i++) {
            if (docks[i].getId() == dockID) {
                return docks[i].getBikes();
            }
        }
        return null;
    }

    /**
     * getDockingStationByName takes a String object with the name of the docking station to be found.
     * If found the Docking object will be created and returned.
     * <p>
     * Bike object array is not added and has to be added manually
     * with updateBikesInDockingStation()
     *
     * @param   name    String object of the name belonging to the station.
     * @return          Docking object with all information belonging to the station. NOT BIKES
     * @see Docking
     * @author Fredrik Mediaa
     */
    public Docking getDockingStationByName(String name) {
        PreparedStatement stmt = null;
        Docking[] docks = getAllDockingStationsWithBikes();

        for(Docking dock : docks) {
            if(dock.getName().equals(name)) {
                return dock;
            }
        }
        return null;
    }

    /**
     * rentBike is the method to be called by a Docking object to take care of all the required actions needed
     * when renting a bike
     *
     * @param userRentingBike   the User object that is renting the bike
     * @param bikeToRent        the bike that the User object want to rent
     * @param dockID            the ID of the docking station where the bike is docked
     * @return                  boolean based on the SQL query results. True = OK, False = something went wrong
     * @see User
     * @see Bike
     * @see Docking
     * @author Fredrik Mediaa
     */
    public boolean rentBike(User userRentingBike, Bike bikeToRent, int dockID){
        PreparedStatement stmt = null;
        try{
            connect();
            if(db == null){
                return false;
            }
            java.util.Date utilDate = new java.util.Date();

            stmt = db.prepareStatement("INSERT INTO trips (bikeID, startStation, userID) VALUES (?, ?, ?)");
            stmt.setInt(1, bikeToRent.getId());
            stmt.setInt(2, dockID);
            stmt.setInt(3, userRentingBike.getUserID());
            if(execSQLBool(stmt)) {
                if(undockBike(bikeToRent, dockID)) {
                    stmt.close();
                    db.close();
                    connect();
                    bikeToRent.setStatus(Bike.TRIP);
                    stmt = db.prepareStatement("UPDATE bikes SET status = ? , totalTrips = ? WHERE bikeID = ?");
                    stmt.setInt(1, Bike.TRIP);
                    stmt.setInt(2, bikeToRent.getTotalTrips() + 1);
                    stmt.setInt(3, bikeToRent.getId());
                    execSQLBool(stmt);
                    stmt.close();
                    db.close();
                    return true;
                } else {
                    stmt.close();
                    db.close();
                    connect();
                    stmt = db.prepareStatement("DELETE FROM trips WHERE bikeID = ? AND startStation = ? AND usedID = ? AND endTime IS NULL");
                    stmt.setInt(1, bikeToRent.getId());
                    stmt.setInt(2, dockID);
                    stmt.setInt(3, userRentingBike.getUserID());
                    execSQLBool(stmt);
                }
            }
            stmt.close();
            db.close();
        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * endRent is the opposite of rentBike. This method is to be used from a Docking object when a bike wants to dock to it.
     *
     * @param   bike    the Bike object to be docked
     * @param   dockID  the ID of the docking station where the bike is to be docked
     * @param   spot    the spot where the bike is to be docked on the station.
     * @return          a boolean based on the result from the SQL query. True = OK, False = something went wrong.
     * @see Bike
     * @see Docking
     * @author Fredrik Mediaa
     */
    public boolean endRent(Bike bike, int dockID, int spot){
        PreparedStatement stmt = null;
        try{
            stmt = db.prepareStatement("UPDATE trips SET endTime = NOW(), endStation = ? WHERE bikeID = ? AND endStation IS NULL AND endTime IS NULL");
            stmt.setInt(1, dockID);
            stmt.setInt(2, bike.getId());

            if(dockBike(bike, dockID, spot)) {
                connect();
                if(execSQLBool(stmt)) {
                    stmt.close();
                    db.close();
                    connect();
                    bike = getBikeByID(bike);
                    stmt = db.prepareStatement("UPDATE bikes SET status = ? WHERE bikeID = ?");
                    if(bike.getStatus() == Bike.TRIP){
                        stmt.setInt(1, Bike.AVAILABLE);
                    } else{
                        stmt.setInt(1, bike.getStatus());
                    }

                    stmt.setInt(2, bike.getId());
                    execSQLBool(stmt);
                    stmt.close();
                    db.close();
                    return true;
                }
            }
            stmt.close();
            db.close();

        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * undockBike is used by rentBike() to take care of the undocking section of the query.
     *
     * @param   bike    the Bike object wanting to be undocked
     * @param   dockID  the ID of the docking station.
     * @return          boolean based on the results from the SQL query. True = OK, False = something went wrong
     * @see Bike
     * @author Fredrik Mediaa
     */
    private boolean undockBike(Bike bike, int dockID){
        PreparedStatement stmt = null;
        try{
            connect();
            if(db == null){
                return false;
            }

            stmt = db.prepareStatement("UPDATE slots SET slots.bikeID = NULL WHERE slots.bikeID = ?");
            //stmt.setInt(1, dockID);
            stmt.setInt(1, bike.getId());
            boolean output = execSQLBool(stmt);
            stmt.close();
            db.close();
            return output;
        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * dockBike is the opposite of undockBike() and is to be used by endRent(). This method takes care of the docking section of the SQL queries.
     *
     * @param   bike    the Bike object to be docked
     * @param   dockID  the ID of the docking station where the bike is to be docked.
     * @param   spot    the spot where the Bike object is to be docked at the docking station.
     * @return          a boolean based on the results of the SQL query. True = OK, False = something went wrong
     * @see Bike
     * @author Fredrik Mediaa
     */
    public boolean dockBike(Bike bike, int dockID, int spot){
        PreparedStatement stmt = null;
        try{
            connect();
            if(db == null){
                return false;
            }

            stmt = db.prepareStatement("UPDATE slots SET slots.bikeID = ? WHERE slots.stationID = ? AND slots.slotID = ?");
            stmt.setInt(1, bike.getId());
            stmt.setInt(2, dockID);
            stmt.setInt(3, spot);
            boolean output = execSQLBool(stmt);
            stmt.close();
            db.close();
            return output;
        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * ingStationsWithBikes returns a Docking object array containing all information about the stations
     * and a Bike object array where Bike objects are put in their right spot.
     *
     * @return      a Docking object array with everything there is to find.
     * @see Docking
     * @see Bike
     * @author Fredrik Mediaa
     * @author Martin Moan
     */
    public Docking[] getAllDockingStationsWithBikes(){
        ArrayList<Docking> dck = getAllDockingStations();
        Docking[] stations = new Docking[dck.size()];
        stations = dck.toArray(stations);

        ArrayList<Bike> bikes = getAllBikes();

        PreparedStatement stmt = null;
        try {
            for (int i = 0; i < stations.length; i++) {
                connect();
                if (db == null) {
                    return null;
                }
                stmt = db.prepareStatement("SELECT slots.bikeID, slots.slotID, slots.stationID FROM slots WHERE slots.stationID = ?");
                stmt.setInt(1, stations[i].getId());

                ResultSet resultSet = execSQLRS(stmt);
                ArrayList<BikeSlotPair> slotPairs = new ArrayList<>();


                while (resultSet.next()) {
                    slotPairs.add(new BikeSlotPair(resultSet.getInt("bikeID"), resultSet.getInt("slotID"), resultSet.getInt("stationID")));
                }

                for (int m = 0; m < slotPairs.size(); m++) {
                    for (int n = 0; n < bikes.size(); n++) {
                        if (slotPairs.get(m).getBike_id() == bikes.get(n).getId()) {
                            bikes.get(n).setLocation(new Location(stations[i].getLocation().getLatitude(), stations[i].getLocation().getLongitude()));
                            stations[i].forceAddBike(bikes.get(n), slotPairs.get(m).getSlot_id());
                        }
                    }
                }
            }
            stmt.close();
            db.close();
            return stations;

        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return stations;
    }

    /**
     * logDocking is a method for write log inserts for the Docking object passed in.
     *
     * @param dock  the Docking object to be logged
     * @see Docking
     * @author Fredrik Mediaa
     */
    public void logDocking (Docking dock) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return;
            }
            stmt = db.prepareStatement("INSERT INTO docking_log (stationID, energyUsage, usedSlots) VALUES (?, ?, ?)");
            stmt.setInt(1, dock.getId());
            stmt.setDouble(2, dock.getPowerUsage());
            stmt.setInt(3, dock.getUsedSpaces());

            execSQLBool(stmt);
            stmt.close();
            db.close();
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
    }

    /**
     * getDockingByID takes in the ID of the docking station to be found and returns it in the form of a Docking object
     *
     * @param   id  the ID of the docking station to be found
     * @return      a Docking object with the belonging ID
     * @see Docking
     * @author Fredrik Mediaa
     */
    public Docking getDockingByID(int id) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return null;
            }

            stmt = db.prepareStatement("SELECT * FROM docking_stations WHERE stationID = ?");
            stmt.setInt(1, id);
            ResultSet dockingSet = execSQLRS(stmt);
            while(dockingSet.next()) {
                Docking dock = new Docking(
                        dockingSet.getInt("stationID"),
                        dockingSet.getString("stationName"),
                        new Location(
                                dockingSet.getDouble("latitude"),
                                dockingSet.getDouble("longitude")
                        ),
                        dockingSet.getInt("maxSlots"),
                        dockingSet.getInt("status")
                );
                stmt.close();
                db.close();

                return dock;
            }

            stmt.close();
            db.close();

            return null;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * editDocking mirrors the database information with the Docking object.
     *
     * @param   updatedDock the Docking object with new information to be pushed to the database
     * @return              a boolean based on the results from the SQL query. True = OK, False = something went wrong
     * @see Docking
     * @author Fredrik Mediaa
     */
    public boolean editDocking(Docking updatedDock) {
        Docking orgDock = getDockingByID(updatedDock.getId());
        
        PreparedStatement stmt = null;
        try {

            //stmt = db.prepareStatement("UPDATE docking_stations SET stationName = ?, maxSlots = ?, latitude = ?, longitude = ?, status = ? WHERE stationID = ?");
            //stmt.setString(1, updatedDock.getName());
            //stmt.setInt(2, updatedDock.getCapacity());
            //stmt.setDouble(3, updatedDock.getLocation().getLatitude());
            //stmt.setDouble(4, updatedDock.getLocation().getLongitude());
            //stmt.setInt(5, updatedDock.getStatus());
            //stmt.setInt(6, updatedDock.getId());

            if(orgDock != null) {
                if (orgDock.getCapacity() != updatedDock.getCapacity()) {
                    int change = updatedDock.getCapacity() - orgDock.getCapacity();
                    updateDockingSlots(orgDock, change);

                    connect();
                    if(db == null) {
                        return false;
                    }
                    stmt = db.prepareStatement("UPDATE docking_stations SET maxSlots = ? WHERE stationID = ?");
                    stmt.setInt(1, updatedDock.getCapacity());
                    stmt.setInt(2, updatedDock.getId());
                }
            }

            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * deleteDocking takes care of setting status of the mirror of the Docking object passed in to Docking.DELETED in the database.
     *
     * @param   dock    the Docking object to be soft deleted the database
     * @return          a boolean based on the results from the SQL query. True = OK, False = something went wrong
     * @see Docking
     * @author Fredrik Mediaa
     */
    public boolean deleteDocking(Docking dock) {
        PreparedStatement stmt = null;
        try {

            Docking orgDock = getDockingByID(dock.getId());

            connect();
            if(db == null) {
                return false;
            }
            stmt = db.prepareStatement("UPDATE docking_stations SET status = ?, maxSlots = ? WHERE stationID = ?");
            stmt.setInt(1, Docking.DELETED);
            stmt.setInt(2, 0);
            stmt.setInt(3, dock.getId());


            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return false;
            }
            updateDockingSlots(dock, -dock.getCapacity());
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * updateDockingSlots takes care of all the logic in relation to deleting slots belonging to docking stations when the
     * amount is changed
     *
     * @param   dock            the original docking station preferably an update version from the database
     * @param   capacityChange  the amount in positive or negative direction. Positive = add, negative = delete
     */
    private void updateDockingSlots(Docking dock, int capacityChange) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return;
            }
            if(capacityChange > 0) {
                stmt = db.prepareStatement("INSERT INTO slots (stationID, slotID) VALUES (?, ?)");
                for(int i = 0; i < capacityChange; i++) {
                System.out.println("INSERTING SLOT " + (dock.getCapacity() + 1 + i));
                    stmt.setInt(1, dock.getId());
                    stmt.setInt(2, ((dock.getCapacity() + 1 + i)));
                    execSQLBool(stmt);
                }
            } else if (capacityChange < 0) {
                stmt = db.prepareStatement("DELETE FROM slots WHERE stationID = ? AND slotID = ?");
                for(int i = 0; i < Math.abs(capacityChange); i++) {
                    System.out.println("DELETING SLOT " + (dock.getCapacity() - i));
                    stmt.setInt(1, dock.getId());
                    stmt.setInt(2, dock.getCapacity() - i);
                    execSQLBool(stmt);
                }
            }

            stmt = db.prepareStatement("SELECT * FROM slots WHERE bikeID IS NULL and stationID = ?");


            stmt.close();
            db.close();
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
    }

    /**
     * generateDockingSlots is a method for generating all hte slots assosiated with the Docking object
     *
     * @param   amount      the capacity of the station
     * @param   stationID   the stations ID
     * @author Fredrik Mediaa
     */
    private void generateDockingSlots(int amount, int stationID) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return;
            }
            stmt = db.prepareStatement("INSERT INTO slots (slotID, stationID) VALUES (?, ?)");

            for(int i = 0; i < amount; i++) {
                stmt.setInt(1, i + 1);
                stmt.setInt(2, stationID);

                execSQLBool(stmt);
            }

        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
    }

    /*
     * METHODS BELONGING TO THE USER OBJECT.
     */

    /**
     * checkIfUserExist is a method to check if there already is an entry in the database with the same emailaddress
     *
     * @param   mail   the email to be checked
     * @return          a boolean based on the results. True = Exist, False = Does not exist.
     * @author Fredrik Mediaa
     */
    public boolean checkIfUserExist(String mail) {
        Hasher hasher = new Hasher();
        PreparedStatement stmt = null;
        boolean exsist = false;

        try {
            connect();
            if(db == null) {
                return true;
            }
            stmt = db.prepareStatement("SELECT * FROM users WHERE email = ?");
            stmt.setString(1, mail);

            ResultSet rs =execSQLRS(stmt);

            if(rs.next()) {
                exsist = true;
            }
            stmt.close();
            db.close();
            return exsist;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return true;
    }

    /**
     * registerUser lets you register a User objects data to the database if the email address does not exist.
     *
     * @param   user        the User object to be registered
     * @param   sendMail    a boolean that decides if an mail is sent to the user when created or not. True = Send, False = Do not send
     * @return              PrimaryKey of the entry also known as the users ID. -1 if something went wrong.
     * @see User
     * @author Fredrik Mediaa
     */
    public int registerUser(User user, boolean sendMail) {
        Hasher hasher = new Hasher();
        if(!checkIfUserExist(user.getEmail())) {
            PreparedStatement stmt = null;
            try {
                connect();
                if(db == null) {
                    return -1;
                }
                String salt = hasher.hashSalt(System.currentTimeMillis() + "");

                String password = hasher.hash(user.getPassword(), salt);

                stmt = db.prepareStatement("INSERT INTO users (userTypeID, email, password, salt, firstname, lastname, phone, landcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, User.ADMINISTRATOR);
                stmt.setString(2, user.getEmail());
                stmt.setString(3, password);
                stmt.setString(4, salt);
                stmt.setString(5, user.getFirstname());
                stmt.setString(6, user.getLastname());
                stmt.setInt(7, user.getPhone());
                stmt.setString(8, user.getLandcode());

                int pk = execSQLPK(stmt);

                if(pk > 0 && sendMail) {
                    String subj = "Welcome, " + user.getFirstname();

                    String mail = user.getEmail();

                    String msg = "You have now been registered to RentaBike!\n\nYour user details are displayed below\nUsername: " + user.getEmail() + "\nPassword: " + user.getPassword() + "\n\nWe kindly ask you to change your password at first chance.\n\n\nBest Regards,\nRentaBike Team";
                    try {
                        System.out.println("Sending mail!");
                        new MailHandler(subj, mail, msg);
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                }
                user = null;
                return pk;
            } catch(SQLException e) {
                forceClose();
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    /**
     * loginUser is a method to check if the parameters match the information in the database. If it matches all details will be turned into a User object and returned
     *
     * @param   email       the mail address identifying the user
     * @param   password    the String of characters to be matched with database entry.
     * @return              User object with the information about the user that has logged in successfully. null object if information did not match.
     * @see User
     * @author Fredrik Mediaa
     */
    public User loginUser(String email, String password) {
        Hasher hasher = new Hasher();
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM users WHERE email = ? AND userTypeID = ?");
            stmt.setString(1, email);
            stmt.setInt(2, User.ADMINISTRATOR);

            ResultSet rs = execSQLRS(stmt);
            User correctUser = null;

            if(rs.next()) {
                if(hasher.hash(password, rs.getString("salt")).equals(rs.getString("password"))) {
                    correctUser = new User(
                            rs.getInt("userID"),
                            rs.getInt("userTypeID"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getInt("phone"),
                            rs.getString("email"),
                            rs.getString("landcode")
                    );
                    stmt.close();
                    db.close();
                    return correctUser;
                } else {
                    stmt.close();
                    db.close();
                }
            }
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * updateUser is a method for updating all the information about a User object in the database
     * @param   user    The user to be updated
     * @return          a boolean based on the results from the database. True = updated, False = not updated.
     */
    public boolean updateUser(User user) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return false;
            }

            stmt = db.prepareStatement("UPDATE users SET email = ?, firstname = ?, lastname = ?, phone = ? WHERE userID = ?");
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFirstname());
            stmt.setString(3, user.getLastname());
            stmt.setInt(4, user.getPhone());
            stmt.setInt(5, user.getUserID());

            if(!execSQLBool(stmt)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * changePassword checks if the password in the database is equal to the password passed in. If so the old password would be updated
     * with the new password.
     *
     * @param   user            the User object which is changing password
     * @param   newPassword     the new password that is supposed to be updated
     * @param   oldPassword     the old password to check validity of the user.
     * @return                  a boolean based on the results. True = Password changed, False = Password did not change.
     * @see User
     * @author Fredrik Mediaa
     */
    public boolean changePassword(User user, String newPassword, String oldPassword) {
        Hasher hasher = new Hasher();
        PreparedStatement stmt = null;
        try {

            if(loginUser(user.getEmail(), oldPassword) != null) {
                String salt = hasher.hashSalt(System.currentTimeMillis() + "");

                String hashedNewPassword = hasher.hash(newPassword, salt);

                connect();
                if(db == null) {
                    return false;
                }
                stmt = db.prepareStatement("UPDATE users SET password = ?, salt = ? WHERE userID = ?");
                stmt.setString(1, hashedNewPassword);
                stmt.setString(2, salt);
                stmt.setInt(3, user.getUserID());

                return execSQLBool(stmt);
            }
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * forceChangePassword is an optional method for changing the User objects password in the database. This method
     * does not require the old password and will update the new password.
     *
     * @param   user        the User object to be updated
     * @param   newPassword the password which is to be the new password
     * @return              a boolean based on the results. True = Password changed, False = Password did not change.
     * @see User
     * @author Fredrik Mediaa
     */
    public boolean forceChangePassword(User user, String newPassword) {
        Hasher hasher = new Hasher();
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return false;
            }

            String salt = hasher.hashSalt(System.currentTimeMillis() + "");

            String hashedNewPassword = hasher.hash(newPassword, salt);

            stmt = db.prepareStatement("UPDATE users SET password = ?, salt = ? WHERE userID = ?");
            stmt.setString(1, hashedNewPassword);
            stmt.setString(2, salt);
            stmt.setInt(3, user.getUserID());

            return execSQLBool(stmt);
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return false;
    }

    /**
     * deleteUser soft deletes the user in the database. That means status is set to User.SOFTDELETE
     *
     * @param   user    the User object to be soft deleted in the database
     * @return          a boolean based on the results. True = soft deleted, False = No change in status
     * @see User
     * @author Fredrik Mediaa
     */
    public boolean deleteUser(User user) {
        PreparedStatement stmt = null;
        try{
            connect();
            if(db == null){
                return false;
            }
            stmt = db.prepareStatement("UPDATE users SET userTypeID = ?, email = NULL WHERE userID = ?");

            stmt.setInt(1, User.SOFTDELETE);
            stmt.setInt(2, user.getUserID());

            boolean output = execSQLBool(stmt);
            stmt.close();
            db.close();
            return output;

        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * forgottenPassword is a method for reseting a users password. This password will be automatically generated and updated.
     *
     * @param mail  the mail of the user where tue password is to be reset
     * @return      a boolean based on the result. True = Password changed, False = Something went wrong
     * @author Fredrik Mediaa
     */
    public boolean forgottenPassword(String mail) {
        Hasher hasher = new Hasher();
        if(!checkIfUserExist(mail)) {
            PreparedStatement stmt = null;
            try {
                Random rand = new Random();
                char[] pwd = new char[10];
                for (int i = 0; i < pwd.length; i++) {
                    pwd[i] = (char) (rand.nextInt(121-33) + 33); //[33, 121] except 96
                    if(pwd[i] == 96){
                        i--;
                    }
                }
                String pw = "";
                for (int i = 0; i < pwd.length; i++) {
                    pw += "" + pwd[i];
                }

                String salt = hasher.hashSalt(System.currentTimeMillis() + "");

                String password = hasher.hash(pw, salt);

                connect();
                if(db == null) {
                    return false;
                }
                stmt = db.prepareStatement("UPDATE users SET password = ?, salt = ? WHERE email = ?");
                stmt.setString(1, password);
                stmt.setString(2, salt);
                stmt.setString(3, mail);

                boolean updated = execSQLBool(stmt);

                if(updated) {
                    User user = loginUser(mail, pw);

                    String subj = "Changed password";

                    String msg = "Hello, " + user.getFirstname() + "\nHere is your recovery password:\n\n" + pw + "\n\nPlease change it after you have logged.\n\n\nBest Regards,\nRentaBike Team";
                    try {
                        System.out.println("Sending mail!");
                        new MailHandler(subj, mail, msg);
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                }
                return true;
            } catch(SQLException e) {
                forceClose();
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * getUserByType is a base method for the the getmethods belonging to status.
     *
     * @param   type    the status value to be found
     * @return          User object array containing only User objects with the given status
     * @see User
     * @author Fredrik Mediaa
     * @author Martin Moan
     */
    private User[] getUserByType(int type) {
        PreparedStatement stmt = null;
        ArrayList<User> usersList = new ArrayList<>();
        User[] users = null;
        try {
            connect();
            if(db == null) {
                return users;
            }
            stmt = db.prepareStatement("SELECT users.userID, users.userTypeID, users.email, users.firstname, users.lastname, users.phone, users.landcode FROM users WHERE users.userTypeID = ?");
            stmt.setInt(1, type);

            ResultSet resultSet = execSQLRS(stmt);
            while(resultSet.next()){
                usersList.add(new User(
                        resultSet.getInt("userID"),
                        resultSet.getInt("userTypeID"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getInt("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("landcode")
                ));
            }
            stmt.close();
            db.close();
            users = new User[usersList.size()];
            users = usersList.toArray(users);
            return users;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return users;
    }

    /**
     * getAllCustomers gives you all users in the database with status equal to User.CUSTOMER
     * @return      an User object array
     */
    public User[] getAllCustomers(){
        return getUserByType(3);
    }

    /**
     * getAllAdminUsers gives you all users in the database with status equal to User.ADMINISTRATOR
     * @return      an User object array
     */
    public User[] getAllAdminUsers() {
        return getUserByType(1);
    }

    /**
     * getAllRepairUsers gives you all users in the database with status equal to User.REPAIRMAN
     * @return      an User object array
     */
    public User[] getAllRepairUsers() {
        return getUserByType(2);
    }

    /*
     * METHODS BELONGING TO THE REPAIR OBJECT
     */

    /**
     * getRepairsByID is a method for getting Repair objects belonging to a specific Bike object.
     * That means you get an Repair object array in return with all the repairs registered on the bike.
     * <p>
     * Passnig in 0 as bikeID gives you the Repair objects for all bikes.
     *
     * @param   bikeID  the ID of the specific bike.
     * @return          Repair object array belonging to the bike ID
     * @see Repair
     * @author Fredrik Mediaa
     */
    private Repair[] getRepairsByID(int bikeID) {
        PreparedStatement stmt = null;
        ArrayList<Repair> repairList = new ArrayList<>();
        Repair[] repairs = null;

        try {
            connect();
            if(db == null) {
                return repairs;
            }
            if(bikeID <= 0) {
                stmt = db.prepareStatement("SELECT * FROM repair_cases");
            } else {
                stmt = db.prepareStatement("SELECT * FROM repair_cases WHERE bikeID = ?");
                stmt.setInt(1, bikeID);
            }

            ResultSet resultSet = execSQLRS(stmt);
            while(resultSet.next()){
                repairList.add(new Repair(
                        resultSet.getInt("bikeID"),
                        resultSet.getInt("repairCaseID"),
                        resultSet.getString("description"),
                        resultSet.getString("returnDescription"),
                        dateToLocalDate(resultSet.getString("dateCreated")),
                        dateToLocalDate(resultSet.getString("dateReceived")),
                        resultSet.getDouble("price")
                ));
            }
            stmt.close();
            db.close();
            repairs = new Repair[repairList.size()];
            repairs = repairList.toArray(repairs);
            return repairs;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return repairs;
    }

    /**
     * getBikeIDbyCaseID is a method where passing in the ID of a Repair object gives you the bikeID
     *
     * @param   caseID  the ID of the Repair object
     * @return          returns the ID of the Bike object, NOT the Bike object itself
     * @see Repair
     * @author Fredrik Mediaa
     */
    private int getBikeIDByCaseID(int caseID) {
        PreparedStatement stmt = null;
        try {
            connect();
            if(db == null) {
                return -1;
            }
            stmt = db.prepareStatement("SELECT bikeID FROM repair_cases WHERE repairCaseID = ?");
            stmt.setInt(1, caseID);

            int output = -1;
            ResultSet resultSet = execSQLRS(stmt);
            while(resultSet.next()){
                output = resultSet.getInt("bikeID");
            }
            stmt.close();
            db.close();

            return output;
        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * getAllRepairs uses the getRepairsByID passing in 0 giving you the Repair objects of every bike
     *
     * @return      Repair object array containing every repair registered in the database
     * @see Repair
     * @author Fredrik Mediaa
     */
    public Repair[] getAllRepairs() {
        return getRepairsByID(0); // 0 means ALL
    }

    /**
     * getAllRepairsForBike uses the getRepairsByID to find Repair objects belonging to the bike ID given
     *
     * @param   bikeID  the bike ID to search for
     * @return          Repair object array with Repair objects belonging to the specific bike
     * @see Repair
     * @author Fredrik Mediaa
     */
    public Repair[] getAllRepairsForBike(int bikeID) {
        return getRepairsByID(bikeID);
    }

    /**
     * registerRepairRequest are to be use by the Repair objects for registration in database. It takes care of
     * registering the repair in the database and updating the status of the bike to Bike.REPAIR
     *
     * @param   bikeID  the bike ID which the Repair object belongs to
     * @param   desc    a String description of the problem which needs to be fixed
     * @param   date    the date of the registration.
     * @return          a boolean based on the results. True = Repair registered, False = Repair not registered
     * @see Repair
     * @see LocalDate
     * @author Fredrik Mediaa
     */
    public boolean registerRepairRequest(int bikeID, String desc, LocalDate date) {
        PreparedStatement stmt = null;
        try{
            connect();
            if(db == null){
                return false;
            }

            stmt = db.prepareStatement("INSERT INTO repair_cases (bikeID, description, dateCreated) VALUES (?, ?, ?)");
            stmt.setInt(1, bikeID);
            stmt.setString(2, desc);
            stmt.setString(3, date.toString());

            execSQLBool(stmt);
            stmt.close();
            db.close();

            changeStatus(bikeID, Bike.REPAIR);

            return true;
        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * finishRepairRequest is a method for finishing a repair request already submitted.
     *
     * @param   caseID  the case number of the Repair object
     * @param   desc    the description of the finished job
     * @param   date    the date of the finished job
     * @param   price   the total price for the finished job
     * @return          a boolean based on the result. True = finished a request, False = something went wrong.
     * @see LocalDate
     * @author Fredrik Mediaa
     */
    public boolean finishRepairRequest(int caseID, String desc, LocalDate date, double price) {
        PreparedStatement stmt = null;
        try{
            connect();
            if(db == null){
                return false;
            }
            stmt = db.prepareStatement("UPDATE repair_cases SET returnDescription = ?, dateReceived = ?, price = ? WHERE repairCaseID = ?");
            stmt.setString(1, desc);
            stmt.setString(2, date.toString());
            stmt.setDouble(3, price);
            stmt.setInt(4, caseID);

            execSQLBool(stmt);
            stmt.close();
            db.close();

            int bikeID = getBikeIDByCaseID(caseID);
            changeStatus(bikeID, Bike.AVAILABLE);

            return true;
        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }
        return false;
    }


    /*
     * MISC USE WITH CAUTION
     */

    /**
     * getUnfinishedRipsBikeID is a helper method returning an int array of all the bike ID's with not finished trip entries.
     *
     * @return      returns an int array of the bike ID's with not finished trip entries
     * @author Fredrik Mediaa
     */
    private int[] getUnfinishedTripsBikeID() {
        PreparedStatement stmt = null;
        ArrayList<Integer> ids = new ArrayList<>();

        try {
            connect();
            if(db == null) {
                return null;
            }

            stmt = db.prepareStatement("SELECT bikeID FROM trips WHERE endTime IS NULL AND endStation IS NULL");

            ResultSet resultSet = execSQLRS(stmt);
            while(resultSet.next()){
                ids.add(new Integer(resultSet.getInt("bikeID")));
            }

            stmt.close();
            db.close();

            int[] idsToSend = new int[ids.size()];

            for(int i = 0; i < ids.size(); i++) {
                idsToSend[i] = Integer.parseInt(ids.get(i).toString());
            }

            return idsToSend;

        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }


    /**
     * findOpenSpaces is a helper method for getting all open slots in the environment, with its belonging docking station id.
     *
     * @return      BikeSlotPair object array with open slots on
     * @see BikeSlotPair
     * @author Fredrik Mediaa
     */
    private BikeSlotPair[] findOpenSpaces() {
        PreparedStatement stmt = null;
        ArrayList<BikeSlotPair> spots = new ArrayList<>();

        try {
            connect();
            if(db == null) {
                return null;
            }

            stmt = db.prepareStatement("SELECT * FROM slots WHERE bikeID IS NULL");

            ResultSet resultSet = execSQLRS(stmt);
            while(resultSet.next()){
                spots.add(new BikeSlotPair(-1, resultSet.getInt("slotID"), resultSet.getInt("stationID")));
            }

            stmt.close();
            db.close();

            BikeSlotPair[] slots = new BikeSlotPair[spots.size()];
            slots = spots.toArray(slots);

            return slots;

        } catch(SQLException e) {
            forceClose();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * removeAllUnfinishedTrips is a powerful and dangerous method. This should not be used without caution.
     * <p>
     * This method removes every unfinished entries in the trips table in the database. It also docks bikes that
     * haven't managed to be docked with the simulation. Since this removes all unfinished trips you have to be sure
     * EVERY BIKE NOT FINISHED ACTUALLY HAS COME FROM A CRASHING SIMULATION!
     *
     * @see BikeSlotPair
     * @see Bike
     * @see javax.swing.JOptionPane
     * @author Fredrik Mediaa
     */
    public void removeAllUnfinishedTrips() {
        BikeSlotPair[] slots = findOpenSpaces();
        int[] bikeIDs = getUnfinishedTripsBikeID();

        PreparedStatement stmt = null;
        try{
            if(javax.swing.JOptionPane.showConfirmDialog(null,"Are you sure?") == 0) {

                for(int id : bikeIDs) {
                    for (int j = 0; j < slots.length; j++) {
                        if(slots[j] != null) {
                            //DummyBike
                            Bike bike = new Bike(id, " ", 0.0, " ", 0.0, 0, null, 1, null, 0);

                            endRent(bike, slots[j].getStation_id(), slots[j].getSlot_id());
                            slots[j] = null;
                            System.out.println("I GOT HERE WITH : " + id);
                            break;
                        }
                    }
                }

                connect();
                if(db == null){
                    return;
                }
                stmt = db.prepareStatement("DELETE FROM trips WHERE endStation IS NULL AND endTime IS NULL");

                execSQLBool(stmt);

                stmt.close();
                db.close();
            }
        } catch(SQLException ex){
            forceClose();
            ex.printStackTrace();
        }
    }
}

class BikeSlotPair{
    private final int bike_id;
    private final int slot_id;
    private final int station_id;

    /**
     * This object holds information paring bikes to docking stations. This is meant to help routing bikes to the right slot
     * when adding Bike objects to the right Docking object.
     *
     * @param   bike_id     the ID of the bike
     * @param   slot_id     the ID of the slot which the bike belongs to
     * @param   station_id  the ID of the docking station the slot belongs to
     * @author Fredrik Mediaa
     */
    public BikeSlotPair(int bike_id, int slot_id, int station_id){
        this.bike_id = bike_id;
        this.slot_id = slot_id;
        this.station_id = station_id;
    }

    public int getBike_id(){
        return bike_id;
    }

    public int getSlot_id(){
        return slot_id;
    }

    public int getStation_id(){
        return station_id;
    }

    public String toString() {
        return "id: " + bike_id + ", slot: " + slot_id + ", station: " + station_id;
    }
}

// Just for testing purposes
class DBTest {
    public static void main(String[] args) {
        DBH dbh = new DBH();
    }
}
