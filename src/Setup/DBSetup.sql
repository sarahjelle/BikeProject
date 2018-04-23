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
DROP TABLE IF EXISTS bikeTypes;

CREATE TABLE bikes (
  bikeID INT NOT NULL AUTO_INCREMENT,
  price INT NOT NULL,
  purchaseDate DATE NOT NULL,
  totalTrips INT DEFAULT 0,
  status INT DEFAULT 1,
  make VARCHAR(25) NOT NULL,
  type VARCHAR(30) NOT NULL,
  batteryPercentage DOUBLE DEFAULT 1.0,
  totalKm BIGINT DEFAULT 0,
  PRIMARY KEY (bikeID)
);

CREATE TABLE bikeTypes (
  typeID INT NOT NULL AUTO_INCREMENT,
  description VARCHAR(30) NOT NULL,
  active SMALLINT DEFAULT 1,
  PRIMARY KEY (typeID)
);

/**
 * REPAIR SECTION
 */

#Repair registered on a given bike at given time
CREATE TABLE repair_cases (
  repairCaseID INT NOT NULL AUTO_INCREMENT,
  bikeID INT NOT NULL,
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
  userID INT NOT NULL AUTO_INCREMENT,
  userTypeID INT NOT NULL,
  email varchar(30) NOT NULL,
  password varchar(255) NOT NULL,
  salt varchar(255) NOT NULL,
  firstname varchar(50) NOT NULL,
  lastname varchar(50) NOT NULL,
  phone INT(8),
  landcode VARCHAR(7) NOT NULL,
  PRIMARY KEY (userID)
);

CREATE TABLE user_types(
  userTypeID INT NOT NULL AUTO_INCREMENT,
  description varchar(30) NOT NULL,
  PRIMARY KEY (userTypeID)
);

/**
 * DOCKING SECTION
 */

CREATE TABLE docking_stations(
  stationID INT NOT NULL AUTO_INCREMENT,
  stationName varchar(255) NOT NULL,
  maxSlots INT NOT NULL DEFAULT 0,
  latitude FLOAT( 10, 6 ) NOT NULL,
  longitude FLOAT( 10, 6 ) NOT NULL,
  status INT NOT NULL DEFAULT 1,
  PRIMARY KEY(stationID)
);

CREATE TABLE docking_log(
  stationID INT NOT NULL,
  logTime DATETIME NOT NULL DEFAULT NOW(),
  energyUsage INT NOT NULL,
  usedSlots INT NOT NULL,
  PRIMARY KEY(stationID, logTime)
);

CREATE TABLE slots(
  slotID INT NOT NULL,
  bikeID INT UNIQUE DEFAULT NULL,
  stationID INT NOT NULL,
  PRIMARY KEY(stationID, slotID)
);

/**
 * LOGGING SECTION
 */

CREATE TABLE bike_logs(
  bikeID INT NOT NULL,
  logTime DATETIME NOT NULL DEFAULT NOW(),
  latitude FLOAT( 10, 6 ) NOT NULL,
  longitude FLOAT( 10, 6 ) NOT NULL,
  altitude FLOAT( 10, 6 ) NOT NULL,
  batteryPercentage DOUBLE NOT NULL,
  totalKm BIGINT DEFAULT 0,
  PRIMARY KEY(bikeID, logTime)
);

/**
 * TRIP SECTION
 */

CREATE TABLE trips(
  tripID INT NOT NULL AUTO_INCREMENT,
  bikeID INT NOT NULL,
  startTime DATETIME NOT NULL DEFAULT NOW(),
  endTime DATETIME,
  startStation INT NOT NULL,
  endStation INT,
  userID INT NOT NULL,
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

DROP VIEW IF EXISTS undockedBikesNew;
DROP VIEW IF EXISTS newestLogsNew;
DROP VIEW IF EXISTS dockedBikesWithDocLocNew;
DROP VIEW IF EXISTS undockedBikesWithNewestLogLocNew;
DROP VIEW IF EXISTS allBikesWithLocNew;

CREATE VIEW undockedBikesNew AS (SELECT * FROM bikes b WHERE b.bikeID NOT IN (SELECT s.bikeID FROM slots s WHERE s.bikeID IS NOT NULL));

CREATE VIEW newestLogsNew AS (SELECT l.bikeID, l.latitude, l.longitude FROM bike_logs l WHERE l.logTime = (SELECT MAX(log.logTime) AS logTime FROM bike_logs log WHERE log.bikeID = l.bikeID));

CREATE VIEW dockedBikesWithDocLocNew AS (
  SELECT * FROM (SELECT b.bikeID, b.price, b.purchaseDate, b.totalTrips, b.status, b.make, b.type, b.batteryPercentage, b.totalKm, d.latitude, d.longitude FROM bikes b JOIN
    (SELECT * FROM slots) AS s ON b.bikeID = s.bikeID JOIN (SELECT * FROM docking_stations) AS d ON d.stationID = s.stationID) AS total
);

CREATE VIEW undockedBikesWithNewestLogLocNew AS (SELECT und.*, logs.latitude, logs.longitude FROM undockedBikesNew und LEFT JOIN (SELECT * FROM newestLogsNew) AS logs ON und.bikeID = logs.bikeID);

CREATE VIEW allBikesWithLocNew AS (SELECT * FROM (SELECT * FROM dockedBikesWithDocLocNew UNION (SELECT * FROM undockedBikesWithNewestLogLocNew) ORDER BY bikeID) AS total);