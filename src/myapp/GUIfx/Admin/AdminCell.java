package myapp.GUIfx.Admin;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import myapp.data.User;

/**
 * The AdminCell class creates custom ListCell, so the list of admins
 * in the application can contain multiple values.
 * The cell is composed of a HBox with different Labels, and the Labels are filled
 * with information about the admin.
 *
 * @author Sara Hjelle
 */
public class AdminCell extends ListCell<User> {
    private HBox hbox;
    private Label userId;
    private Label firstName;
    private Label surname;
    private Label phone;
    private Label email;

    /**
     * The constructor creates all the Labels and adds them to the HBox.
     * The width of each Label is set, so the elements of the list is places on the same
     * space in each row.
     */
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

    /**
     * 
     * @param user
     * @param empty
     */
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
