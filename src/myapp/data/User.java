package myapp.data;

public class User {
    final static int ADMINISTRATOR = 0;
    final static int REPERATØR = 1;
    final static int KUNDE = 2;

    private final int UserID;
    private final int UserClass;

    private final String firstname;
    private final String lastname;
    private final int phone;
    private final String email;
    private final String landcode;

    public User(int UserClass, int UserID, String firstname, String lastname, int phone, String email, String landcode){
        this.UserID = UserID;
        this.UserClass = UserClass;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.landcode = landcode;
    }

    public User(int UserID, String firstname, String lastname, int phone, String email, String landcode){
        this.UserID = UserID;
        this.UserClass = KUNDE;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.landcode = landcode;
    }

    public int getUserID(){
        return UserID;
    }

    public int getUserClass(){
        return UserClass;
    }

    public String getFirstname(){
        return firstname;
    }

    public String getLastname(){
        return lastname;
    }

    public int getPhone(){
        return phone;
    }

    public String getEmail(){
        return email;
    }

    public String getLandcode(){
        return landcode;
    }


}
