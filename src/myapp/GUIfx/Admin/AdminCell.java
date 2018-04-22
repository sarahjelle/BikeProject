package myapp.GUIfx.Admin;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.User;

public class AdminCell extends ListCell<User> {
    private HBox hbox;
    private Label userId;
    private Label firstName;
    private Label surname;
    private Label phone;
    private Label email;

    public AdminCell(){
        hbox = new HBox(50);
        userId = new Label();
        userId.setPrefWidth(10);
        firstName = new Label();
        firstName.setPrefWidth(100);
        surname = new Label();
        surname.setPrefWidth(50);
        phone = new Label();
        phone.setPrefWidth(70);
        email = new Label();
        email.setPrefWidth(200);
        hbox.getChildren().addAll(firstName, phone, email);
    }

    @Override
    public void updateItem(User user, boolean empty){
        super.updateItem(user, empty);
        if(user != null && !empty){
            //userId.setText(Integer.toString(user.getUserID()));
            firstName.setText(user.getFirstname() + " " + user.getLastname());
            //surname.setText(user.getLastname());
            phone.setText(Integer.toString(user.getPhone()));
            email.setText(user.getEmail());

            setGraphic(hbox);
        }
    }
}
