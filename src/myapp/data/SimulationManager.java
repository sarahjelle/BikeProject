package myapp.data;

public class SimulationManager {
    private Simulation sim;
    private Thread simThread;

    public SimulationManager(Bike[] bikes, Docking[] docking_stations){
        this.sim = new Simulation(bikes, docking_stations);
        this.simThread = new Thread(sim);
    }

    public void start(){
        simThread.start();
    }

    public void stop(){
        sim.stop(true);
    }
}

class ManagerTest{
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

        SimulationManager man = new SimulationManager(bikes, docking_stations);
        man.start();
        for (int i = 0; i < 10000; i++) {

        }
        man.stop();
    }
}
