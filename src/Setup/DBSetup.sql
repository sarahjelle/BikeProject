DROP TABLE IF EXISTS repair_lists;
DROP TABLE IF EXISTS slots;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_types;
DROP TABLE IF EXISTS repair_options;
DROP TABLE IF EXISTS repair_cases;
DROP TABLE IF EXISTS bike_logs;
DROP TABLE IF EXISTS docking_log;
DROP TABLE IF EXISTS docking_stations;
DROP TABLE IF EXISTS bikes;

CREATE TABLE bikes (
  bikeID int NOT NULL AUTO_INCREMENT,
  price int NOT NULL,
  purchaseDate DATE NOT NULL,
  totalTrips int DEFAULT 0,
  status int DEFAULT 1,
  make VARCHAR(25) NOT NULL,
  type VARCHAR(25) NOT NULL,
  batteryPercentage DOUBLE DEFAULT 0.0,
  totalKm int DEFAULT 0,
  PRIMARY KEY (bikeID)
);

/**
 * REPAIR SECTION
 */

#Repair registered on a given bike at given time
CREATE TABLE repair_cases (
  repairCaseID int NOT NULL AUTO_INCREMENT,
  bikeID int NOT NULL,
  dateCreated DATE NOT NULL,
  dateReceived DATE DEFAULT NULL,
  description TEXT NOT NULL,
  returnDescription TEXT DEFAULT NULL,
  price DOUBLE DEFAULT NULL,
  PRIMARY KEY (repairCaseID)
);


/**
 * USER SECTION
 */

CREATE TABLE users(
  userID int NOT NULL AUTO_INCREMENT,
  userTypeID int NOT NULL,
  email varchar(30) NOT NULL UNIQUE,
  password varchar(255) NOT NULL,
  salt varchar(255) NOT NULL,
  firstname varchar(50) NOT NULL,
  lastname varchar(50) NOT NULL,
  phone int(8),
  landcode VARCHAR(7) NOT NULL,
  PRIMARY KEY (userID)
);

CREATE TABLE user_types(
  userTypeID int NOT NULL AUTO_INCREMENT,
  description varchar(30) NOT NULL,
  PRIMARY KEY (userTypeID)
);

/**
 * DOCKING SECTION
 */

CREATE TABLE docking_stations(
  stationID int NOT NULL AUTO_INCREMENT,
  stationName varchar(255) NOT NULL,
  maxSlots int NOT NULL DEFAULT 0,
  latitude FLOAT( 10, 6 ) NOT NULL,
  longitude FLOAT( 10, 6 ) NOT NULL,
  PRIMARY KEY(stationID)
);

CREATE TABLE docking_log(
  stationID int NOT NULL,
  logTime DATETIME NOT NULL DEFAULT NOW(),
  energyUsage int NOT NULL,
  usedSlots int NOT NULL,
  PRIMARY KEY(stationID, logTime)
);

CREATE TABLE slots(
  slotID int NOT NULL,
  bikeID int UNIQUE DEFAULT NULL,
  stationID int NOT NULL,
  PRIMARY KEY(stationID, slotID)
);

/**
 * LOGGING SECTION
 */

CREATE TABLE bike_logs(
  bikeID int NOT NULL,
  logTime DATETIME NOT NULL DEFAULT NOW(),
  latitude FLOAT( 10, 6 ) NOT NULL,
  longitude FLOAT( 10, 6 ) NOT NULL,
  altitude FLOAT( 10, 6 ) NOT NULL,
  batteryPercentage DOUBLE NOT NULL,
  totalKm int DEFAULT 0,
  PRIMARY KEY(bikeID, logTime)
);

/**
 * TRIP SECTION
 */

CREATE TABLE trips(
  tripID int NOT NULL AUTO_INCREMENT,
  bikeID int NOT NULL,
  startTime DATETIME NOT NULL DEFAULT NOW(),
  endTime DATETIME,
  startStation int NOT NULL,
  endStation int,
  userID int NOT NULL,
  PRIMARY KEY(tripID)
);

/**
* FOREIGN KEY SECTION
*/

ALTER TABLE repair_cases
  ADD FOREIGN KEY (bikeID) REFERENCES bikes(bikeID);

ALTER TABLE users
  ADD FOREIGN KEY (userTypeID) REFERENCES user_types(userTypeID);

ALTER TABLE slots
  ADD FOREIGN KEY (bikeID) REFERENCES bikes(bikeID),
  ADD FOREIGN KEY (stationID) REFERENCES docking_stations(stationID);

ALTER TABLE docking_log
  ADD FOREIGN KEY (stationID) REFERENCES docking_stations(stationID);

ALTER TABLE bike_logs
  ADD FOREIGN KEY (bikeID) REFERENCES bikes(bikeID);

ALTER TABLE trips
  ADD FOREIGN KEY (startStation) REFERENCES docking_stations(stationID),
  ADD FOREIGN KEY (endStation) REFERENCES docking_stations(stationID),
  ADD FOREIGN KEY (bikeID) REFERENCES bikes(bikeID),
  ADD FOREIGN KEY (userID) REFERENCES users(userID);

DROP VIEW IF EXISTS newestLogs;
DROP VIEW IF EXISTS undockedBikes;
DROP VIEW IF EXISTS bikesWithDockingLocation;
DROP VIEW IF EXISTS undockedBikesWithNewestLogLoc;
DROP VIEW IF EXISTS allBikesWithLoc;

CREATE VIEW newestLogs AS (SELECT b.*, l.latitude, l.longitude, l.altitude FROM bikes b JOIN bike_logs l WHERE b.bikeID = l.bikeID AND l.logTime = (SELECT MAX(logTime) FROM bike_logs log WHERE log.bikeID = b.bikeID));

CREATE VIEW undockedBikes AS SELECT * FROM bikes WHERE bikeID NOT IN (SELECT bikeID FROM slots WHERE bikeID IS NOT NULL);

CREATE VIEW bikesWithDockingLocation AS (SELECT b.bikeID, b.price, b.purchaseDate, b.totalTrips, b.status, b.make, b.type, b.batteryPercentage, b.totalKm, fromDock.latitude, fromDock.longitude FROM bikes b INNER JOIN
  (SELECT s.bikeID, s.stationID, ds.latitude, ds.longitude FROM slots s INNER JOIN
    (SELECT d.stationID, d.latitude, d.longitude FROM docking_stations d) ds ON ds.stationID = s.stationID
  WHERE s.bikeID IS NOT NULL) fromDock ON fromDock.bikeID = b.bikeID);

CREATE VIEW undockedBikesWithNewestLogLoc AS (SELECT l.bikeID, b.price, b.purchaseDate, b.totalTrips, b.status, b.make, b.type, l.batteryPercentage, l.totalKm, l.latitude, l.longitude FROM newestLogs l LEFT JOIN (SELECT * FROM bikes) b ON b.bikeID = l.bikeID);

CREATE VIEW allBikesWithLoc AS SELECT * FROM bikesWithDockingLocation b UNION (SELECT * FROM undockedBikesWithNewestLogLoc l WHERE l.bikeID != b.bikeID);


CREATE VIEW undockedBikesNew AS (SELECT * FROM bikes b WHERE b.bikeID NOT IN (SELECT s.bikeID FROM slots s WHERE s.bikeID IS NOT NULL));

CREATE VIEW newestLogsNew AS (SELECT l.bikeID, l.latitude, l.longitude FROM bike_logs l WHERE l.logTime = (SELECT MAX(log.logTime) AS logTime FROM bike_logs log WHERE log.bikeID = l.bikeID));

CREATE VIEW dockedBikesWithDocLocNew AS (
  SELECT * FROM (SELECT b.bikeID, b.price, b.purchaseDate, b.totalTrips, b.status, b.make, b.type, b.batteryPercentage, b.totalKm, d.latitude, d.longitude FROM bikes b JOIN
    (SELECT * FROM slots) AS s ON b.bikeID = s.bikeID JOIN (SELECT * FROM docking_stations) AS d ON d.stationID = s.stationID) AS total
);

CREATE VIEW undockedBikesWithNewestLogLocNew AS (SELECT und.*, logs.latitude, logs.longitude FROM undockedBikesNew und LEFT JOIN (SELECT * FROM newestLogsNew) AS logs ON und.bikeID = logs.bikeID);

CREATE VIEW allBikesWithLocNew AS (SELECT * FROM (SELECT * FROM dockedBikesWithDocLocNew UNION (SELECT * FROM undockedBikesWithNewestLogLocNew) ORDER BY bikeID) AS total);