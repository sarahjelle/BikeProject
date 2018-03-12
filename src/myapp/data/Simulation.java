package myapp.data;

public class Simulation implements Runnable{
    private Bike[] bikes;
    private Docking[] docking_stations;
    private int updateInterval = 60;
    private volatile boolean interrupted = false;

    public Simulation(Bike[] bikes, Docking[] docking_stations){
        this.bikes = bikes;
        this.docking_stations = docking_stations;
    }

    public void setUpdateInterval(int newUpdateInterval){
        updateInterval = newUpdateInterval;
    }

    public void run(){
        while(!interrupted){
            long StartTime = System.currentTimeMillis();
            long elapsedTimeMilliSeconds = 0;
            while(elapsedTimeMilliSeconds / 1000 < updateInterval){
                for (int i = 0; i < bikes.length; i++) {
                    // Choose random start- and endpoint (docking_station locations) for all bikes
                    // And get the best route according to the Google Maps API
                }

                for (int i = 0; i < bikes.length; i++) {
                    // Move all the bikes in their respective directions, according to Google Maps API route,
                    // in some given step length (2m / 30cm etc). This movement is not drawn to screen here.
                }
                long EndTime = System.currentTimeMillis();
                elapsedTimeMilliSeconds = EndTime - StartTime;
            }

            for (int i = 0; i < bikes.length; i++) {
                // Update bikes locations in the database with the Database client class
            }
        }

    }

    public void stop(){
        this.interrupted = true;
    }


}

class SimTest{
    public static void main(String[]args){
        //int id, String make, double batteryPercentage, boolean available, int parkingSpotId
        Bike[] bikes = new Bike[3];
        bikes[0] = new Bike(1, "Trek", 100, true, 1);
        bikes[1] = new Bike(2, "Trek", 100, true, 2);
        bikes[2] = new Bike(3, "Trek", 100, true, 3);

        //int id, String name, Location location, int capacity
        Docking[] docking_stations = new Docking[5];
        docking_stations[0] = new Docking(1, "Kalvskinnet", new Location("Kalvskinnet", false), 50);
        docking_stations[1] = new Docking(2, "Moholt", new Location("Moholt", false), 20);
        docking_stations[2] = new Docking(3, "Berg", new Location("Berg", false), 5);
        docking_stations[3] = new Docking(4, "Charlottenlund", new Location("Charlottenlund", false), 120);
        docking_stations[4] = new Docking(5, "Seoul", new Location("Seoul", false), 3000);

        Simulation sim = new Simulation(bikes, docking_stations);

        sim.run();

        long start = System.currentTimeMillis();
        long elapsed = 0;
        while(elapsed / 1000 < 60){
            for (Bike b : bikes) {
                System.out.println("BikeID: " + b.getId() + " " + b.getDistanceTraveled() + " meters traveled " + b.getBatteryPercentage());
            }
            long end = System.currentTimeMillis();
            elapsed = end - start;
        }

        sim.stop();
        System.exit(0);
    }
}
