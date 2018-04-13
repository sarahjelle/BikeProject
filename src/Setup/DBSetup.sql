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
  bikeID int,
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
  batteryPercentage int NOT NULL,
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