package Setup;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.Random;

public class GenerateContent {

    private final String bikesTableName = "bikes";
    private final String dockingTableName = "docking_stations";
    private final String slotsTableName = "slots";

    private final String repair_casesTableName = "repair_cases";
    private final String repair_listsTableName = "repair_lists";
    private final String repair_optionsTableName = "repair_options";

    private final String usersTableName = "users";
    private final String user_typesTableName = "user_types";
    private final String bike_logsTableName = "bike_logs";
    private final String tripsTableName = "trips";

    private final int NumberOfBikes = 250;
    private final int NumberOfStations = 10;
    private final int MaxSlots = 25;

    public GenerateContent(){
        BufferedWriter bWriter = null;
        FileWriter fWriter = null;
        try{
            //Setup file, and writer
            String fileName = "database_content.sql";
            File outputFile = new File(fileName);
            fWriter = new FileWriter(outputFile);
            bWriter = new BufferedWriter(fWriter);

            Random rand = new Random();
            //Create full content string
            String toWrite = "/*\n\t" +
                             "For this script to work, the script DBSetup.sql must be run first,\n\t" +
                             "so that any auto incremented id numbers are regenerated.\n\t" +
                             "But this will delete any content previously registered in the database\n" +
                             "*/\n\n";

            //Create bikes
            String[] bikeTypes = {"Bysykkel", "Hybrid", "Terreng", "Landevei"};
            String[] bikeMakes = {"DBS", "Trek", "Merida", "Scott"};
            String bikes = "";
            for (int i = 0; i < NumberOfBikes; i++) {
                //price, purchaseDate, totalTrips, totalKm, make, type
                int price = rand.nextInt(4500) + 500;
                String make = bikeMakes[rand.nextInt(bikeMakes.length)];
                String type = bikeTypes[rand.nextInt(bikeTypes.length)];
                bikes += "INSERT INTO "+  bikesTableName + " (price, make, type) VALUES ("+ price + ", \"" + make + "\", \"" + type + "\");\n";
            }


            //Create dockingstations
            String[] stationNames = {"Moholt", "Lade", "Kalvskinnet", "Gløshaugen", "Øya", "Tyholt", "Persaunet", "Charlottenlund", "Bakklandet", "Ila"};
            int[] slotsOnStations = new int[NumberOfStations];

            String docking_stations = "\n";
            for (int i = 0; i < NumberOfStations; i++) {
                //name, maxSlots
                String name = stationNames[i];
                int slots = rand.nextInt(MaxSlots);
                if(slots <= 0){
                    slots = 10;
                }
                slotsOnStations[i] = slots;
                docking_stations += "INSERT INTO " + dockingTableName + " (stationName, maxSlots) VALUES (\"" + name + "\", " + slots + ");\n";
            }

            //Create slots on dockingstations
            String slots = "\n";
            for (int i = 0; i < slotsOnStations.length; i++) {
                //slotID, stationID
                for (int j = 0; j < slotsOnStations[i]; j++) {
                    int slotID = j + 1;
                    int stationID = i + 1;
                    slots += "INSERT INTO " + slotsTableName + " (slotID, stationID) VALUES (" + slotID + ", " + stationID + ");\n";
                }
            }

            //Create user_types
            String user_types = "\n";
            user_types += "INSERT INTO " + user_typesTableName + " (description) VALUES (\"Administrator\");\n";
            user_types += "INSERT INTO " + user_typesTableName + " (description) VALUES (\"Reperatør\");\n";
            user_types += "INSERT INTO " + user_typesTableName + " (description) VALUES (\"Kunde\");\n";



            toWrite += bikes + docking_stations + slots + user_types;
            //Write content-string to file
            bWriter.write(toWrite);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if(bWriter != null){
                try{
                    bWriter.close();
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            if(fWriter != null){
                try{
                    fWriter.close();
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[]args){
        GenerateContent g = new GenerateContent();
    }
}
