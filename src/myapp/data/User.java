package myapp.data;
import java.util.Random;
public class User {
    public final static int ADMINISTRATOR = 1,
                            REPAIRMAN = 2,
                            CUSTOMER = 3,
                            SOFTDELETE = 99;

    private final int UserID;
    private final int UserClass;

    private String firstname;
    private String lastname;
    private int phone;
    private String email;
    private final String landcode;
    private final String password;


    //Complete new user to be registered in DB
    public User(int UserClass, String firstname, String lastname, int phone, String email, String landcode){
        this.UserID = -1;
        this.UserClass = UserClass;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.landcode = landcode;

        Random rand = new Random();
        char[] pwd = new char[10];
        for (int i = 0; i < pwd.length; i++) {
            pwd[i] = (char) (rand.nextInt(121-33) + 33); //[33, 121] except 96
            if(pwd[i] == 96){
                i--;
            }
        }
        String pw = "";
        for (int i = 0; i < pwd.length; i++) {
            pw += "" + pwd[i];
        }
        this.password = pw;
    }

    //To be used when an already registered user logs in
    public User(int UserID, int UserClass, String firstname, String lastname, int phone, String email, String landcode){
        this.UserID = UserID;
        this.UserClass = UserClass;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.landcode = landcode;
        this.password = null;
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

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    public String getLastname(){
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getPhone(){
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLandcode(){
        return landcode;
    }

    public String getPassword(){
        return password;
    }

    public String toString(){
        return "ID: " + UserID + " UserTypeID: " + UserClass + " firstname: " + firstname + " lastname: " + lastname;
    }
}

class UserTest{
    public static void main(String[]args){
        User meg = new User(0, "Martin", "Moan", 93285466, "martin.moan@gmail.com", "0047");
        System.out.println(meg.getPassword());
    }
}
