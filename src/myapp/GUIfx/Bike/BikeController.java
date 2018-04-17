package myapp.GUIfx.Bike;

//listview

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static myapp.data.Bike.*;
import  myapp.data.Bike;
import myapp.data.Repair;
import myapp.dbhandler.DBH;

import javax.xml.soap.Text;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;



public class BikeController implements Initializable {
    //DBH
    DBH dbh = new DBH();
    ArrayList<Bike> bikes = dbh.getAllBikes();
    int id = -1;

    //listview
    @FXML
    private BorderPane listPane;
    @FXML
    private ListView<Bike> bikeList;
    @FXML
    private TextField searchInput;


    //info, edit, repair
    @FXML
    private BorderPane infoEditRepair;

    //info
    @FXML
    private SplitPane bikeInfo;
    @FXML
    private Label idOutput;
    @FXML
    private Label typeInfo;
    @FXML
    private Label makeInfo;
    @FXML
    private Label priceInfo;
    @FXML
    private Label dateInfo;
    @FXML
    private Label batteryInfo;
    @FXML
    private Label statusInfo;
    @FXML
    private Label distanceInfo;

    //List of repairs
    @FXML
    private ListView<Repair> repairList;
    private Repair[] repairs;

    //repair
    @FXML
    private BorderPane repairPaneBefore;
    @FXML
    private BorderPane repairPaneAfter;
    @FXML
    private DatePicker dateSent;
    @FXML
    private TextField descriptionBefore;
    @FXML
    private DatePicker dateReturn;
    @FXML
    private TextField priceRepair;
    @FXML
    private TextField descriptionDone;

    //Edit
    @FXML
    private BorderPane editPane;
    @FXML
    private ComboBox<String> typeEdit;
    @FXML
    private TextField makeEdit;
    @FXML
    private TextField priceEdit;
    @FXML
    private DatePicker dateEdit;
    @FXML
    private TextField batteryEdit;
    @FXML
    private ComboBox statusEdit;
    @FXML
    private TextField distanceEdit;

    //Register new bike:
    @FXML
    private VBox registerPane;
    @FXML
    private ComboBox<String> typeReg;
    @FXML
    private DatePicker dateReg;
    @FXML
    private TextField priceReg;
    @FXML
    private TextField makeReg;


    public void initialize(URL url, ResourceBundle rb) {
        bikeList.setCellFactory(e -> new BikeCell());
        repairList.setCellFactory(e -> new RepairCell());
        refresh();

        /*Bike bike = new Bike(1, "DBS", 900, "Electric", 0.5, 100);

        for (int i = 0; i < 100; i++) {
            bikeList.getItems().add(bike);
        }*/
    }

    @FXML
    private void refresh() {
        bikeList.getItems().clear();
        Thread thread = new Thread(() -> {
            bikes = dbh.getAllBikes();

            Platform.runLater(() -> {
                for (int i = 0; i < bikes.size(); i++) {
                    bikeList.getItems().add(bikes.get(i));
                }
            });
        });

        thread.start();
    }


    @FXML
    private void refreshRepair(Bike bike) {
        repairList.getItems().clear();

        Thread thread = new Thread(() -> {
            //repairs = bike.getRepairs();
            repairs = dbh.getAllRepairsForBike(bike.getId());
            Platform.runLater(() -> {
                for (int i = 0; i < repairs.length; i++) {
                    repairList.getItems().add(repairs[i]);
                }
            });
        });

        thread.start();
    }

    public void setId(int id){
        this.id = id;
    }

    //methodes for all panes
    public void openPane() {
        refresh();
        closeAll();
        listPane.setVisible(true);
    }

    public void closeAll() {
        //refresh();
        listPane.setVisible(false);
        infoEditRepair.setVisible(false);
        bikeInfo.setVisible(false);
        editPane.setVisible(false);
        repairPaneBefore.setVisible(false);
        repairPaneAfter.setVisible(false);
        registerPane.setVisible(false);
    }

    @FXML
    private void cancel() {
        //refresh();
        closeAll();
        listPane.setVisible(true);
    }

    //methods for info
    public Bike findBike(int bikeId) {
        /*for(int i = 0; i < bikes.size(); i++){
            if(bikeId == bikes.get(i).getId()){
                return bikes.get(i);
            }
        }
        return null;*/

        ObservableList<Bike> bike = bikeList.getItems();
        for (int i = 0; i < bike.size(); i++) {
            if (bikeId == bike.get(i).getId()) {
                return bike.get(i);
            }
        }

        return null;
    }

    @FXML
    private void selectedRow() {
        Bike bike = (Bike) bikeList.getItems().get(bikeList.getSelectionModel().getSelectedIndex());
        showInfo(bike);
    }

    @FXML
    private void search() {
        String bikeID = searchInput.getText();
        int id = -1;
        try {
            id = Integer.parseInt(bikeID);
        } catch (Exception e) {
            searchInput.setText("Write a number");
            searchInput.setStyle("-fx-text-fill: red");
        }

        if (id >= 0) {
            Bike bike = findBike(id);
            if (bike instanceof Bike && bike != null) {
                showInfo(bike);
            }
        }
    }

    @FXML
    private void showInfo(Bike bike) {
        setId(bike.getId());
        closeAll();
        infoEditRepair.setVisible(true);
        bikeInfo.setVisible(true);

        idOutput.setText(Integer.toString(bike.getId()));
        typeInfo.setText(bike.getType());
        makeInfo.setText(bike.getMake());
        priceInfo.setText(Double.toString(bike.getPrice()));

        refreshRepair(bike);

        //convert localdate to string
        LocalDate date = bike.getPurchased();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String purchaseDate = date.format(formatter);
        dateInfo.setText(purchaseDate);

        batteryInfo.setText(Double.toString(bike.getBatteryPercentage()));
        distanceInfo.setText(Integer.toString(bike.getDistanceTraveled()));

        if (!getStatus(bike).equals("")) {
            statusInfo.setText(getStatus(bike));
        }

    }

    private String getStatus(Bike bike) {
        int status = bike.getStatus();
        String res = "";

        switch (status) {
            case 1:
                res = "Available";
                break;
            case 2:
                res = "Rented";
                break;
            case 3:
                res = "On repair";
                break;
            case 4:
                res = "Deleted";
                break;
        }
        return res;
    }

    @FXML
    private void showInfoBack() {
        //refresh();
        Bike bike = findBike(id);
        showInfo(bike);
    }

    //methods for repair
    @FXML
    private void openRepairPane() {
        closeAll();
        infoEditRepair.setVisible(true);

        Bike bike = findBike(id);

        if (bike.getStatus() == Bike.REPAIR) {
            /*descriptionDone.clear();
            descriptionDone.setPromptText("What was fixed?\n" +
                    "E.g. new front tire");
            priceReg.clear();
            priceReg.setPromptText("E.g. 500,00");
            dateReturn.setValue(null);
            dateReturn.setPromptText("Choose a date");*/
            repairPaneAfter.setVisible(true);
        } else {
            descriptionBefore.clear();
            descriptionBefore.setPromptText("What need to be fixed? \n" +
                    "E.g. need new front tire, ...");
            dateSent.setValue(null);
            dateSent.setPromptText("Choose a date");
            repairPaneBefore.setVisible(true);
        }
    }

    //Register repair before sent
    @FXML
    private void beforeRepair() {
        LocalDate date = dateSent.getValue();
        String description = descriptionBefore.getText();
        Bike bike = findBike(id);

        boolean ok = bike.addRepairRequest(description, date);

        if(ok){
            refresh();
            informationWindow("Repair request were added: " +
                    "\n", "Repair is added");
            showInfo(findBike(id));
        }
        refresh();
    }

    //Register repair on return
    @FXML
    private void afterRepair() {
        Bike bike = findBike(id);
        LocalDate date = dateReturn.getValue();
        String priceInput = priceRepair.getText();
        String description = descriptionDone.getText();
        int price = -1;
        try {
            price = Integer.parseInt(priceInput);
        } catch (Exception e) {
            priceRepair.setText("Write a number");
        }

        if (price >= 0) {
            bike.finishLastRepairRequest(description, date, price);
        }
    }

    //methodes for edit
    @FXML
    private void openEditPane() {
        Bike bike = findBike(id);

        typeEdit.getItems().clear();
        ArrayList<String> types = getTypes();
        for (int i = 0; i < types.size(); i++) {
            typeEdit.getItems().add(types.get(i));
        }
        typeEdit.setValue(bike.getType());
        makeEdit.setText(bike.getMake());
        priceEdit.setText(Double.toString(bike.getPrice()));
        dateEdit.setValue(bike.getPurchased());
        batteryEdit.setText(Double.toString(bike.getBatteryPercentage()));
        distanceEdit.setText(Integer.toString(bike.getDistanceTraveled()));

        if (!getStatus(bike).equals("")) {
            statusEdit.setValue(getStatus(bike));
        }

        closeAll();
        infoEditRepair.setVisible(true);
        editPane.setVisible(true);
    }


    //methoed to get types and update comboboxes
    private void selectedType(ComboBox<String> type) {
        String selected = type.getSelectionModel().getSelectedItem();

        if (selected != null && selected.equals("New type")) {
            TextInputDialog newType = new TextInputDialog("New type");
            newType.setContentText("Enter new type: ");
            Optional<String> result = newType.showAndWait();
            if (result.isPresent()) {
                type.getItems().add(result.get());
            }
        }
    }

    private ArrayList<String> getTypes() {
        //return dbh.getTypes();
        ArrayList<String> types = new ArrayList<String>();
        types.add("New type");
        types.add("Type1");
        types.add("Type2");

        return types;
    }

    @FXML
    private void typeRegister() {
        selectedType(typeReg);
    }

    @FXML
    private void typeEdit() {
        selectedType(typeEdit);
    }


    //methods for bike registration
    @FXML
    private void openRegBike() {
        typeReg.getItems().clear();

        dateReg.setValue(null);
        dateReg.setPromptText("Choose a date");
        makeReg.clear();
        makeReg.setPromptText("E.g. DBS");
        priceReg.clear();
        priceReg.setPromptText("E.g. 500,00");


        ArrayList<String> types = getTypes();
        for (int i = 0; i < types.size(); i++) {
            typeReg.getItems().add(types.get(i));
        }


        closeAll();
        registerPane.setVisible(true);
    }

    @FXML
    private boolean regBikeOk() {
        boolean ok = true;

        while (ok) {
            if (makeReg.getText().trim().isEmpty()) {
                makeReg.setText("Empty");
                ok = false;
            }

            if (typeReg.getSelectionModel().getSelectedItem() == null) {
                typeReg.setValue("Type1");

                ok = false;
            }

            if (dateReg.getValue() == null) {
                dateReg.setValue(LocalDate.now());
                ok = false;
            }

            if (priceReg.getText().trim().isEmpty()) {
                priceReg.setText("Field is blank");
                ok = false;
            } else {
                try {
                    double price = Double.parseDouble(priceReg.getText());
                } catch (Exception e) {
                    ok = false;
                    priceReg.setText("Write a number");
                }
            }
        }
        return ok;
    }

    @FXML
    private void regBike() {
        if (regBikeOk()) {
            Double price = Double.parseDouble(priceReg.getText());
            LocalDate date = dateReg.getValue();
            String type = typeReg.getSelectionModel().getSelectedItem();
            String make = makeReg.getText();

            Bike bike = new Bike(price, date, type, make);
            int id = dbh.registerBike(bike);
            if (id < 0) {
                errorWindow("Could not register bike. Something went wrong in the database", "Registration error");
            }
        }

        cancel();
    }

    //methods for deleting bikes
    @FXML
    private void deleteBike() {
        Bike bike = findBike(id);
        boolean ok = confirmWindow("Are you sure you want to delete bike nr. "
                + id + "?", "Delete bike?");
        if (ok) {
            boolean deleted = dbh.deleteBike(bike);
            if(deleted){
                informationWindow("Bike nr. " + id + " were sucsessfully deleted","Deleted bike" );
                closeAll();
                openPane();
            }
            else{
                errorWindow("Bike is not deleted, error in database", "Error with deleting");
            }
        }
    }


    //methods for creating dialogwindows
    private void errorWindow(String message, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private boolean confirmWindow(String content, String header) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText(content);
        alert.setHeaderText(header);

        Optional<ButtonType> result = alert.showAndWait();
        boolean ok = false;

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ok = true;
        }

        return ok;
    }

    private void informationWindow(String information, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(information);
        alert.showAndWait();
    }
}