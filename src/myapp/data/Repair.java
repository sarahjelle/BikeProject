package myapp.data;

import myapp.dbhandler.DBH;

import java.time.LocalDate;

public class Repair {

    private int bikeID;
    private int caseID;
    private String desc;
    private String returnDesc;
    private LocalDate requestDate;
    private LocalDate returnDate;
    private double price;
    private boolean finished;

    private DBH dbh = new DBH();


    /**
     * This constructor is used when a repair request is generated. This alone will not register in the database,
     * but fills the required fields for registration. This followed by its method startRepairRequest
     * @param bikeID    The ID of the bike which the repair belongs to
     * @param desc      The description of the repairs needed
     * @param date      The registration date of the request
     */
    public Repair (int bikeID, String desc, LocalDate date) {
        this.bikeID = bikeID;
        this.desc = desc;
        this.requestDate = date;
        this.finished = false;
    }

    /**
     * When the registration has been added to the database the DBH uses this method to return and attach the repair to a bike.
     *
     * @param bikeID        The ID of the bike which the Repair belongs to
     * @param caseID        The caseID given by the database to keep track of the specific request
     * @param desc          The description of the repairs needed
     * @param returnDesc    The description of the repairs that have been preformed on the bike
     * @param requestDate   The date the request was created
     * @param returnDate    The date when the request is complete
     * @param price         The total price of the repair job
     */
    public Repair (int bikeID, int caseID, String desc, String returnDesc, LocalDate requestDate, LocalDate returnDate, double price) {
        this.bikeID = bikeID;
        this.caseID = caseID;
        this.desc = desc;
        this.returnDesc = returnDesc;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
        this.price = price;
        this.finished = (returnDesc != null);
    }

    /**
     * startRepairRequest is the method that has to be run in order to register the required fields in the database
     * @return a boolean based on the results from the database. True = Registered, False = Not registered
     */
    public boolean startRepairRequest() {
        if(dbh.registerRepairRequest(bikeID, desc, requestDate)) {
            return true;
        }
        return false;
    }

    /**
     * finishRepairRequest takes inn all the parameters needed to fulfill the Repair request. By using this method the Repair object
     * also gets registered in teh database as completed with the data passed through.
     *
     * @param returnDesc    The description of the repairs that have been preformed on the bike
     * @param returnDate    The date when the request is complete
     * @param price         The total price of the repair job
     * @return              a boolean based on the results from the database. True = Registered as complete, False = No changes in the database or to the object.
     */
    public boolean finishRepairRequest(String returnDesc, LocalDate returnDate, double price) {
        if(dbh.finishRepairRequest(caseID, returnDesc, returnDate, price)) {
            this.returnDesc = returnDesc;
            this.returnDate = returnDate;
            this.price = price;
            this.finished = true;

            return true;
        }
        return false;
    }

    public int getBikeID() { return bikeID; }

    public int getCaseID() { return caseID; }

    public String getDesc() { return desc; }

    public String getReturnDesc() { return returnDesc; }

    public LocalDate getRequestDate() { return requestDate; }

    public LocalDate getReturnDate() { return requestDate; }

    public double getPrice() { return price; }

    public boolean getStatus() {  return finished; }

    public String toString() {
        String fromTo = "";
        if(requestDate != null) {
            fromTo += "F: " + requestDate.toString() + " - ";
        }
        if(returnDate != null) {
            fromTo += "T: " + returnDate.toString();
        }

        return "CaseID: " + caseID + " BikeID: " + bikeID + "\n\tDesc: " + desc + "\n\tReturn Desc: " + returnDesc + "\n\t" + fromTo;
    }
}
