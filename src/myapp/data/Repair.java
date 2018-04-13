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

    private DBH dbh = new DBH();


    // Repair Request
    public Repair (int bikeID, String desc, LocalDate date) {
        this.bikeID = bikeID;
        this.desc = desc;
        this.requestDate = date;
    }

    // Return from DB
    public Repair (int bikeID, int caseID, String desc, String returnDesc, LocalDate requestDate, LocalDate returnDate, double price) {
        this.bikeID = bikeID;
        this.caseID = caseID;
        this.desc = desc;
        this.returnDesc = returnDesc;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
        this.price = price;
    }

    public boolean startRepairRequest() {
        if(dbh.registerRepairRequest(bikeID, desc, requestDate)) {
            return true;
        }
        return false;
    }

    public boolean finishRepairRequest(String returnDesc, LocalDate returnDate, double price) {
        if(dbh.finishRepairRequest(caseID, returnDesc, returnDate, price)) {
            this.returnDesc = returnDesc;
            this.returnDate = returnDate;
            this.price = price;

            return true;
        }
        return false;
    }

    public int getBikeID() {
        return bikeID;
    }

    public int getCaseID() {
        return caseID;
    }

    public String getDesc() {
        return desc;
    }

    public String getReturnDesc() {
        return returnDesc;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public LocalDate getReturnDate() {
        return requestDate;
    }

    public double getPrice() {
        return price;
    }

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
