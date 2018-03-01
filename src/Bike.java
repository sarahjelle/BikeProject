public class Bike {
    private int wheelSize;
    private String make;

    public Bike(String make, int wheelSize){
        this.wheelSize = wheelSize;
        this.make = make;
    }

    public String getMake(){
        return make;
    }

    public int getWheelSize(){
        return wheelSize;
    }
}
