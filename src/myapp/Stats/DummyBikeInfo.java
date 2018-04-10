package myapp.Stats;

public class DummyBikeInfo {
    private String bikeID;
    private int hoursAtRepair = 10;
    private int hoursAtDocking = 10;
    private int hourActive = 4;

    public DummyBikeInfo(String bikeID){
        this.bikeID=bikeID;
    }

    public int getHoursAtRepair(){
        return hoursAtRepair;
    }
    public int getHoursAtDocking(){
        return hoursAtDocking;
    }
    public int getHourActive(){
        return hourActive;
    }
}
