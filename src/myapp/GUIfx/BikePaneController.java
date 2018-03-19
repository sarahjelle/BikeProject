package myapp.GUIfx;
import javafx.fxml.FXML;
import myapp.data.Bike;
import java.time.*;

public class BikePaneController {
    private Bike[] bikes = new Bike[3];
    @FXML


    public void createBikes(){
        bikes[0] = new Bike(100, LocalDate.parse("2018-07-01"), "Electric", "Trek");
        bikes[1] = new Bike(200, LocalDate.parse("2018-07-01"), "Electric", "Trek");
        bikes[2] = new Bike(300, LocalDate.parse("2018-07-01"), "Electric", "Trek");
        bikes[3] = new Bike(400, LocalDate.parse("2018-07-01"), "Electric", "Trek");
    }

    public void createTable(){
        for(int i = 0; i < bikes.length; i++){
            bikes[i].getId();
        }
    }
}
