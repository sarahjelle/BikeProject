/*
 * This file contains all the functions regarding all communication with the database.
 * @author Fredrik Mediå
 */

package myapp.dbhandler;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static java.lang.Math.toIntExact;

import com.sun.org.apache.regexp.internal.RE;
import myapp.GUIfx.Map.MapsAPI;
import myapp.data.*;
import myapp.hasher.*;

import javax.print.Doc;

import static myapp.data.User.*;
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
            Connection DBCon = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?" + "user=" + username + "&password=" + password + "&useSSL=false");
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

    private LocalDate dateTimeToDateOnly(String datetime) {
        String date[] = datetime.split(" ")[0].split("-");
        return LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
    }

    private LocalDate dateToLocalDate(String datetime) {
        if(datetime != null) {
        String date[] = datetime.split("-");
        return LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        }
        return null;
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

            String purchased = (bike.getPurchased() == null) ? purchased = LocalDate.now().toString() : bike.getPurchased().toString();

            stmt = db.prepareStatement("INSERT INTO bikes (price, purchaseDate, make, type) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setDouble(1, bike.getPrice());
            stmt.setString(2, purchased);
            stmt.setString(3, bike.getMake());
            stmt.setString(4, bike.getType());

        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }

        return execSQLPK(stmt, db);
    }

    public Bike[] getAllBikesOA() {
        ArrayList<Bike> bikes = getAllBikes();
        return bikes.toArray(new Bike[bikes.size()]);
    }

    public ArrayList<Bike> getAllBikes() {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM allBikesWithLocNew");
            ResultSet bikeset = execSQLRS(stmt);
            ArrayList<Bike> bikes = new ArrayList<>();
            while(bikeset.next()) {
                bikes.add(new Bike(
                        bikeset.getInt("bikeID"),
                        bikeset.getString("make"),
                        bikeset.getDouble("price"),
                        bikeset.getString("type"),
                        bikeset.getDouble("batteryPercentage"),
                        bikeset.getInt("totalKM"),
                        new Location(
                                bikeset.getDouble("latitude"),
                                bikeset.getDouble("longitude")
                        ),
                        bikeset.getInt("status"),
                        dateTimeToDateOnly(bikeset.getString("purchaseDate"))
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
            System.out.println("Error: " + e);
        }
        return null;
    }

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

    public Bike[] getAllBikesOnTrip(){
        db = connect();
        PreparedStatement stmt = null;
        ArrayList<Bike> outList = new ArrayList<>();
        try{
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
                        set.getInt("totalKM"),
                        new Location(
                                set.getDouble("latitude"),
                                set.getDouble("longitude")
                        ),
                        set.getInt("status"),
                        dateTimeToDateOnly(set.getString("purchaseDate"))
                ));
            }
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            if(stmt != null){
                try{
                    stmt.close();
                } catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
            if(db != null){
                try{
                    db.close();
                } catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
        }
        if(outList != null){
            Bike[] bikes = new Bike[outList.size()];
            bikes = outList.toArray(bikes);
            return bikes;
        } else{
            return null;
        }

    }

    public ArrayList<Bike> getBikesWithStatusAvailable() {
        return getBikesByStatus(1);
    }

    public ArrayList<Bike> getBikesWithStatusInTrip() {
        return getBikesByStatus(2);
    }

    public ArrayList<Bike> getBikesWithStatusRepair() {
        return getBikesByStatus(3);
    }

    public ArrayList<Bike> getBikesWithStatusSoftDelete() {
        return getBikesByStatus(4);
    }

    private ArrayList<Bike> localGetBikes() {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return null;
            }

            stmt = db.prepareStatement("SELECT b.bikeID, b.make, b.type, b.price, b.status, b.purchaseDate, l.logTime, l.batteryPercentage, l.latitude, l.longitude, l.totalKM FROM bikes b INNER JOIN (SELECT bikeID, MAX(logTime) AS NewestEntry FROM bike_logs GROUP BY bikeID) am ON b.bikeID = am.bikeID INNER JOIN bike_logs l ON am.bikeID = l.bikeID AND am.NewestEntry = l.logTime UNION SELECT bikeID, make, type, price, status, purchaseDate, NULL AS logTime, '0' AS batteryPercentage, '0' AS latitude, '0' AS longitude, '0' AS totalKM FROM bikes c WHERE c.bikeID NOT IN (SELECT bikeID FROM bike_logs)");
            ResultSet bikeset = execSQLRS(stmt);
            ArrayList<Bike> bikes = new ArrayList<>();
            while(bikeset.next()) {
                bikes.add(new Bike(
                        bikeset.getInt("bikeID"),
                        bikeset.getString("make"),
                        bikeset.getDouble("price"),
                        bikeset.getString("type"),
                        bikeset.getDouble("batteryPercentage"),
                        bikeset.getInt("totalKM"),
                        new Location(
                                bikeset.getDouble("latitude"),
                                bikeset.getDouble("longitude")
                        ),
                        bikeset.getInt("status"),
                        dateTimeToDateOnly(bikeset.getString("purchaseDate"))
                ));
            }

            Repair[] repairs = getAllRepairs();
            for(Bike bike : bikes) {
                ArrayList<Repair> localRep = new ArrayList<>();
                for(Repair locRep : repairs) {
                    if(locRep.getBikeID() == bike.getId()) {
                        localRep.add(locRep);
                    }
                }
                if(localRep.size() > 0) {
                    Repair[] toUse = new Repair[localRep.size()];
                    toUse = localRep.toArray(toUse);
                    bike.setRepairs(toUse);
                }
            }

            stmt.close();
            db.close();
            return bikes;
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public Bike[] getLoggedBikesOA() {
        ArrayList<Bike> bikes = getLoggedBikes();
        return bikes.toArray(new Bike[bikes.size()]);
    }

    public ArrayList<Bike> getLoggedBikes() {
        db = connect();
        PreparedStatement stmt = null;
        try {
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
                        bikeset.getInt("totalKM"),
                        new Location(
                                bikeset.getDouble("latitude"),
                                bikeset.getDouble("longitude")
                        ),
                        bikeset.getInt("status"),
                        dateTimeToDateOnly(bikeset.getString("purchaseDate"))
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

    public boolean deleteBike(int id) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return false;
            }
            stmt = db.prepareStatement("UPDATE bikes SET status = 4 WHERE bikeID = ?");

            stmt.setInt(1, id);

            if(!execSQLBool(stmt, db)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return false;
    }

    public boolean deleteBike(Bike bike) {
        return deleteBike(bike.getId());
    }

    public boolean updateBike(Bike bike) {
        db = connect();
        PreparedStatement stmt = null;
        try {
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

            if(!execSQLBool(stmt, db)) {
                stmt.close();
                db.close();
                return false;
            }
            stmt.close();
            db.close();
            return true;
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return false;
    }

    public Bike[] logBikes(Bike[] bikes) {
        db = connect();
        PreparedStatement stmt = null;
        ArrayList<Bike> bikesNotUpdated = new ArrayList<>();
        Bike[] toReturn = null;
        try {
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
                    stmt.setDouble(4, bikes[i].getLocation().getAltitude());
                } else{
                    stmt.setDouble(4, map.getAltitude(bikes[i].getLocation().getLatitude(), bikes[i].getLocation().getLongitude()));
                }

                stmt.setDouble(5, bikes[i].getBatteryPercentage());
                stmt.setDouble(6, bikes[i].getDistanceTraveled());

                if(!execSQLBool(stmt, db)) {
                    bikesNotUpdated.add(bikes[i]);
                }
            }
            stmt.close();
            db.close();
            return bikesNotUpdated.toArray(new Bike[bikesNotUpdated.size()]);
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return bikes;
    }

    public ArrayList<String> getBikeMakes() {
        db = connect();
        PreparedStatement stmt = null;
        try {
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
            System.out.println("Error: " + e);
        }
        return null;
    }

    public ArrayList<String> getBikeTypes() {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT DISTINCT type FROM bikes ORDER BY type");
            ResultSet type = execSQLRS(stmt);
            ArrayList<String> typeArray = new ArrayList<>();

            while(type.next()) {
                typeArray.add(type.getString("make"));
            }

            stmt.close();
            db.close();
            return typeArray;
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
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

            Location loc = new Location(dock.getName(), true);
            stmt = db.prepareStatement("INSERT INTO dokcing_stations (name, maxSlots, latitude, longitude) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, dock.getName());
            stmt.setInt(2, dock.getCapacity());
            stmt.setDouble(3, dock.getLocation().getLatitude());
            stmt.setDouble(4, dock.getLocation().getLongitude());


        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }

        return execSQLPK(stmt, db);
    }

    public ArrayList<Docking> getAllDockingStations() {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM docking_stations");
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
                        dockingSet.getInt("maxSlots")
                ));
            }

            stmt.close();
            db.close();
            return docks;
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public void updateBikesInDockingStation(int dockID, Bike[] bikes) {
        Docking[] docks = getAllDockingStationsWithBikes();
        for (int i = 0; i < docks.length; i++) {
            if (docks[i].getId() == dockID) {
                bikes = docks[i].getBikes();
                break;
            }
        }
    }

    //Martin
    public boolean rentBike(User userRentingBike, Bike bikeToRent, int dockID){
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }
            java.util.Date utilDate = new java.util.Date();

            stmt = db.prepareStatement("INSERT INTO trips (bikeID, startStation, userID) VALUES (?, ?, ?)");
            stmt.setInt(1, bikeToRent.getId());
            stmt.setInt(2, dockID);
            stmt.setInt(3, userRentingBike.getUserID());
            if(execSQLBool(stmt, db)) {
                if(undockBike(bikeToRent, dockID)) {
                    stmt.close();
                    db.close();
                    db = connect();
                    bikeToRent.setStatus(Bike.TRIP);
                    stmt = db.prepareStatement("UPDATE bikes SET status = ? , totalTrips = ? WHERE bikeID = ?");
                    stmt.setInt(1, Bike.TRIP);
                    stmt.setInt(2, bikeToRent.getTotalTrips() + 1);
                    stmt.setInt(3, bikeToRent.getId());
                    execSQLBool(stmt, db);
                    stmt.close();
                    db.close();
                    return true;
                }
            }
            stmt.close();
            db.close();
        } catch(SQLException ex){
            ex.printStackTrace();
        }

        return false;
    }

    //Martin
    public boolean endRent(Bike bike, int dockID, int spot){
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }
            stmt = db.prepareStatement("UPDATE trips SET endTime = ?, endStation = ? WHERE bikeID = ? AND endStation IS NULL AND endTime IS NULL");
            java.util.Date jutilDate = new java.util.Date();
            stmt.setDate(1, new java.sql.Date(jutilDate.getTime()));
            stmt.setInt(2, dockID);
            stmt.setInt(3, bike.getId());

            if(execSQLBool(stmt, db)) {
                if(dockBike(bike, dockID, spot)) {
                    stmt.close();
                    db.close();
                    db = connect();
                    bike.setStatus(Bike.AVAILABLE);
                    stmt = db.prepareStatement("UPDATE bikes SET status = ? WHERE bikeID = ?");
                    stmt.setInt(1, Bike.AVAILABLE);
                    stmt.setInt(2, bike.getId());
                    execSQLBool(stmt, db);
                    stmt.close();
                    db.close();
                    return true;
                }
            }
            stmt.close();
            db.close();

        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    //Martin
    private boolean undockBike(Bike bike, int dockID){
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }

            stmt = db.prepareStatement("UPDATE slots SET slots.bikeID = NULL WHERE slots.stationID = ? AND slots.bikeID = ?");
            stmt.setInt(1, dockID);
            stmt.setInt(2, bike.getId());
            boolean output = execSQLBool(stmt, db);
            stmt.close();
            db.close();
            return output;
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    private boolean dockBike(Bike bike, int dockID, int spot){
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }

            stmt = db.prepareStatement("UPDATE slots SET slots.bikeID = ? WHERE slots.stationID = ? AND slots.slotID = ?");
            stmt.setInt(1, bike.getId());
            stmt.setInt(2, dockID);
            stmt.setInt(3, spot);
            boolean output = execSQLBool(stmt, db);
            stmt.close();
            db.close();
            return output;
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    //Martin && Fredrik
    public Docking[] getAllDockingStationsWithBikes(){
        ArrayList<Docking> dck = getAllDockingStations();
        Docking[] stations = new Docking[dck.size()];
        stations = dck.toArray(stations);

        ArrayList<Bike> bikes = getAllBikes();

        db = connect();
        PreparedStatement stmt = null;
        try {
            for (int i = 0; i < stations.length; i++) {
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
            System.out.println("Error: " + e);
        }
        return stations;
    }

    public void logDocking (Docking dock) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return;
            }
            stmt = db.prepareStatement("INSERT INTO docking_logs (stationID, energyUsage, usedSlots) VALUES (?, ?, ?)");
            stmt.setInt(1, dock.getId());
            stmt.setDouble(2, dock.getPowerUsage());
            stmt.setInt(3, dock.getUsedSpaces());

            execSQLBool(stmt, db);
            stmt.close();
            db.close();
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
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

    public User loginUser(String email, String password) {
        db = connect();
        Hasher hasher = new Hasher();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return null;
            }
            stmt = db.prepareStatement("SELECT * FROM users WHERE email = ?");
            stmt.setString(1, email);

            ResultSet rs =execSQLRS(stmt);
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
            System.out.println("Error: " + e);
        }
        return null;
    }

    public boolean changePassword(User user, String newPassword, String oldPassword) {
        Hasher hasher = new Hasher();
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return false;
            }

            if(loginUser(user.getEmail(), oldPassword) != null) {
                String salt = hasher.hashSalt(System.currentTimeMillis() + "");

                String hashedNewPassword = hasher.hash(newPassword, salt);

                stmt = db.prepareStatement("UPDATE users SET password = ?, salt = ? WHERE userID = ?");
                stmt.setString(1, hashedNewPassword);
                stmt.setString(2, salt);
                stmt.setInt(3, user.getUserID());

                return execSQLBool(stmt, db);
            }
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return false;
    }

    public boolean forceChangePassword(User user, String newPassword) {
        Hasher hasher = new Hasher();
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return false;
            }

            String salt = hasher.hashSalt(System.currentTimeMillis() + "");

            String hashedNewPassword = hasher.hash(newPassword, salt);

            stmt = db.prepareStatement("UPDATE users SET password = ?, salt = ? WHERE userID = ?");
            stmt.setString(1, hashedNewPassword);
            stmt.setString(2, salt);
            stmt.setInt(3, user.getUserID());

            return execSQLBool(stmt, db);
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return false;
    }

    public boolean DeleteUser(User user) {
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }
            stmt = db.prepareStatement("UPDATE users SET userTypeID = ? WHERE userID = ?");

            stmt.setInt(1, User.SOFTDELETE);
            stmt.setInt(2, user.getUserID());

            boolean output = execSQLBool(stmt, db);
            stmt.close();
            db.close();
            return output;

        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    //Martin
    private User[] getUserByType(int type) {
        db = connect();
        PreparedStatement stmt = null;
        ArrayList<User> usersList = new ArrayList<>();
        User[] users = null;
        try {
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
            System.out.println("Error: " + e);
        }
        return users;
    }

    public User[] getAllCustomers(){
        return getUserByType(3);
    }

    public User[] getAllAdminUsers() {
        return getUserByType(1);
    }

    public User[] getAllRepairUsers() {
        return getUserByType(2);
    }

    /*
     * METHODS BELONGING TO THE REPAIR OBJECT
     */

    private Repair[] getRepairsByID(int bikeID) {
        db = connect();
        PreparedStatement stmt = null;
        ArrayList<Repair> repairList = new ArrayList<>();
        Repair[] repairs = null;

        try {
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
            System.out.println("Error: " + e);
        }
        return repairs;
    }

    private int getBikeIDByCaseID(int caseID) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return -1;
            }
                stmt = db.prepareStatement("SELECT bikeID FROM repair_cases WHERE caseID = ?");
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
            System.out.println("Error: " + e);
        }
        return -1;
    }

    public Repair[] getAllRepairs() {
        return getRepairsByID(0); // 0 means ALL
    }

    public Repair[] getAllRepairsForBike(int bikeID) {
        return getRepairsByID(bikeID);
    }

    public boolean registerRepairRequest(int bikeID, String desc, LocalDate date) {
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }
            java.util.Date utilDate = new java.util.Date();

            stmt = db.prepareStatement("INSERT INTO repair_cases (bikeID, description, dateCreated) VALUES (?, ?, ?)");
            stmt.setInt(1, bikeID);
            stmt.setString(2, desc);
            stmt.setString(3, date.toString());

            boolean output = execSQLBool(stmt, db);
            if(output) {
                stmt.close();

                stmt = db.prepareStatement("UPDATE bikes SET status = ? WHERE bikeID = ?");
                stmt.setInt(1, Bike.REPAIR);
                stmt.setInt(2, bikeID);

                output = execSQLBool(stmt, db);
                db.close();
                return output;
            }

        } catch(SQLException ex){
            ex.printStackTrace();
        }

        return false;
    }

    public boolean finishRepairRequest(int caseID, String desc, LocalDate date, double price) {
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }
            java.util.Date utilDate = new java.util.Date();

            stmt = db.prepareStatement("UPDATE repair_cases SET returnDescription = ?, dateReceived = ?, price = ? WHERE repairCaseID = ?");
            stmt.setString(1, desc);
            stmt.setString(2, date.toString());
            stmt.setDouble(3, price);
            stmt.setInt(4, caseID);

            boolean output = execSQLBool(stmt, db);
            if(output) {
                stmt.close();

                int bikeID = getBikeIDByCaseID(caseID);

                stmt = db.prepareStatement("UPDATE bikes SET status = ? WHERE bikeID = ?");
                stmt.setInt(1, Bike.AVAILABLE);
                stmt.setInt(2, bikeID);

                output = execSQLBool(stmt, db);
                db.close();
                return output;
            }

        } catch(SQLException ex){
            ex.printStackTrace();
        }

        return false;
    }

}

class BikeSlotPair{
    private final int bike_id;
    private final int slot_id;
    private final int station_id;

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
}

// Just for testing purposes
class DBTest {
    public static void main(String[] args) {
        DBH dbh = new DBH();

    }
}
