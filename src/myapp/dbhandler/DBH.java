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

    private LocalDate dateTimeToDateOnly(String datetime) {
        String date[] = datetime.split(" ")[0].split("-");
        return LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
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

    public void updateBikeArray(ArrayList<Bike> arrayToUpdate) {
        arrayToUpdate = getAllBikes();
    }

    public void updateBikeArray(Bike[] arrayToUpdate) {
        arrayToUpdate = getAllBikesOA();
    }

    public void updateLoggedBikeArray(ArrayList<Bike> arrayToUpdate) {
        arrayToUpdate = getLoggedBikes();
    }

    public void updateLoggedBikeArray(Bike[] arrayToUpdate) {
        arrayToUpdate = getLoggedBikesOA();
    }

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

            stmt = db.prepareStatement("SELECT b.bikeID, b.make, b.type, b.price, b.status, b.purchaseDate, l.logTime, l.batteryPercentage, l.latitude, l.longitude, l.totalKM FROM bikes b INNER JOIN (SELECT bikeID, MAX(logTime) AS NewestEntry FROM bike_logs GROUP BY bikeID) am ON b.bikeID = am.bikeID INNER JOIN  bike_logs l ON am.bikeID = l.bikeID AND am.NewestEntry = l.logTime UNION SELECT bikeID,  make, type, price, status, NULL AS logTime, '0' AS batteryPercentage, '0' AS latitude, '0' AS longitude, '0' AS totalKM FROM bikes c WHERE c.bikeID NOT IN (SELECT bikeID FROM bike_logs)");
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
                        dateTimeToDateOnly(bikeset.getString("purchasedDate"))
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

            stmt = db.prepareStatement("SELECT b.bikeID, b.make, b.type, b.price, b.status, l.logTime, l.batteryPercentage, l.latitude, l.longitude, l.totalKM FROM bikes b INNER JOIN (SELECT bikeID, MAX(logTime) AS NewestEntry FROM bike_logs GROUP BY bikeID) am ON b.bikeID = am.bikeID INNER JOIN  bike_logs l ON am.bikeID = l.bikeID AND am.NewestEntry = l.logTime");
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
                                bikeset.getDouble("longitude"),
                                dateTimeToDateOnly(bikeset.getString("logTime"))
                        ),
                        bikeset.getInt("status"),
                        dateTimeToDateOnly(bikeset.getString("purchasedDate"))
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
            return bikesNotUpdated.toArray(new Bike[bikesNotUpdated.size()]);
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

    public boolean dockBike(int stationId, int slotId, Bike bike) {
        db = connect();
        PreparedStatement stmt = null;
        try {
            if(db == null) {
                return false;
            }
            stmt = db.prepareStatement("UPDATE slots SET bikeID = ? WHERE stationID = ? AND slotID = ?");

            stmt.setInt(1, bike.getId());
            stmt.setInt(2, stationId);
            stmt.setInt(3, slotId);


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

    //Martin
    public User[] getAllCustomers(){
        db = connect();
        PreparedStatement stmt = null;
        ArrayList<User> usersList = new ArrayList<>();
        User[] users = null;
        try {
            if(db == null) {
                return users;
            }
            stmt = db.prepareStatement("SELECT users.userID, users.userTypeID, users.email, users.firstname, users.phone, users.landcode FROM users WHERE users.userTypeID = 3");

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
            users = usersList.toArray(users);
            return users;
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return users;
    }
    //Martin
    public Docking[] getAllDockingStationsWithBikes(){
        Docking[] stations = null;
        stations = getAllDockingStations().toArray(stations);

        db = connect();
        PreparedStatement stmt = null;
        for (int i = 0; i < stations.length; i++) {
            try {
                if(db == null) {
                    return null;
                }
                stmt = db.prepareStatement("SELECT slots.bikeID, slots.slotID, slots.stationID FROM slots WHERE slots.stationID = ?");
                stmt.setInt(1, stations[i].getId());

                ResultSet resultSet = execSQLRS(stmt);
                ArrayList<BikeSlotPair> slotPairs = new ArrayList<>();


                while(resultSet.next()){
                    slotPairs.add(new BikeSlotPair(resultSet.getInt("bikeID"), resultSet.getInt("slotID"), resultSet.getInt("stationID")));
                }


                stmt.close();
                db.close();

                ArrayList<Docking> docking_stations = getAllDockingStations();
                ArrayList<Bike> bikes = new ArrayList<>();

                db = connect();
                for (int j = 0; j < slotPairs.size(); j++) {
                    stmt = db.prepareStatement("SELECT bikes.bikeID, bikes.price, bikes.purchaseDate, bikes.totalTrips, bikes.totalKm, bikes.make, bikes.type, bikes.status FROM bikes WHERE bikes.bikeID = ?");
                    stmt.setInt(1, slotPairs.get(i).getBike_id());
                    int stationID = slotPairs.get(i).getStation_id();
                    ResultSet set = execSQLRS(stmt);
                    while(set.next()){
                        Docking station = null;
                        for (int k = 0; k < docking_stations.size(); k++) {
                            if(docking_stations.get(k).getId() == stationID){
                                station = docking_stations.get(k);
                            }
                        }
                        Double[] locArr = {station.getLocation().getLatitude(), station.getLocation().getLongitude(), station.getLocation().getAltitude()};
                        bikes.add(new Bike(
                           set.getInt("bikeID"),
                           set.getString("make"),
                           set.getDouble("price"),
                           set.getString("type"),
                           0.0,
                            set.getInt("totalKm"),
                           new Location("", locArr),
                           set.getInt("status"),
                           dateTimeToDateOnly(set.getString("purchaseDate"))
                        ));
                    }
                }

                for (int j = 0; j < slotPairs.size(); j++) {
                    int stationID = slotPairs.get(j).getStation_id();
                    int slotID = slotPairs.get(j).getSlot_id();
                    int bikeID = slotPairs.get(j).getBike_id();

                    for (int k = 0; k < docking_stations.size(); k++) {
                        if(docking_stations.get(k).getId() == stationID){
                            Bike bikeToDockHere = null;
                            for (int l = 0; l < bikes.size(); l++) {
                                if(bikes.get(l).getId() == bikeID){
                                    bikeToDockHere = bikes.get(l);
                                }
                            }
                            docking_stations.get(k).forceAddBike(bikeToDockHere, slotID);
                        }
                    }
                }
                stations = docking_stations.toArray(stations);
                return stations;
            } catch(SQLException e) {
                System.out.println("Error: " + e);
            }
        }
        return stations;
    }
    //Martin
    public boolean undockBike(Bike bike, Docking dock){
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }

            stmt = db.prepareStatement("UPDATE slots SET slots.bikeID = NULL WHERE slots.stationID = ? AND slots.bikeID = ?");
            stmt.setInt(1, dock.getId());
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
    //Martin
    public boolean rentBike(User userRentingBike, Bike bikeToRent, Docking start){
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }
            stmt = db.prepareStatement("INSERT INTO trips (bikeID, startStation, startTime, userID) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, bikeToRent.getId());
            stmt.setInt(2, start.getId());
            java.util.Date utilDate = new java.util.Date();
            stmt.setDate(3, new java.sql.Date(utilDate.getTime()));
            stmt.setInt(4, userRentingBike.getUserID());
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
    public boolean endTrip(User user, Bike bike, Docking end){
        db = connect();
        PreparedStatement stmt = null;
        try{
            if(db == null){
                return false;
            }
            stmt = db.prepareStatement("UPDATE trips SET endTime = ?, endStation = ? WHERE bikeID = ? AND trips.userID = ? AND endStation IS NULL AND endTime IS NULL");
            java.util.Date jutilDate = new java.util.Date();
            stmt.setDate(1, new java.sql.Date(jutilDate.getTime()));
            stmt.setInt(2, end.getId());
            stmt.setInt(3, bike.getId());
            stmt.setInt(4, user.getUserID());

            boolean output = execSQLBool(stmt, db);
            stmt.close();
            db.close();
            return output;
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
        Bike[] bikes = dbh.getAllBikesOA();
        for(Bike bike : bikes) {
            System.out.println(bike.toString() + " Location: \n" + bike.getLocation().toString());
        }
    }
}
