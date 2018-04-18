package myapp.GUIfx.Bike;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.Repair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RepairCell extends ListCell<Repair>{

    private HBox hbox;
    private Label dateSent;
    private Label price;
    private Label status;

    public RepairCell(){
        hbox = new HBox();
        dateSent = new Label();
        dateSent.setPrefWidth(100);
        price = new Label();
        price.setPrefWidth(100);
        status = new Label();
        price.setPrefWidth(100);
        hbox.getChildren().addAll(dateSent, price, status);
    }

    @Override
    public void updateItem(Repair item, boolean empty){
        super.updateItem(item, empty);

        if(item != null && !empty){
            price.setText(Double.toString(item.getPrice()));

            LocalDate date = item.getRequestDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            dateSent.setText(date.format(formatter));

            if(item.getStatus()){
                status.setText("Finished");
            }

            else{
                status.setText("Not finished");
            }

            setGraphic(hbox);
        }
    }
}
