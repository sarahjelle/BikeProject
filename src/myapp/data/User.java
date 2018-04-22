package myapp.data;
import java.util.Random;

/**
 * User is a representation of the people either maintaining the system, renting the bikes or repairing them.
 */
public class User {
    public final static int ADMINISTRATOR = 1,
                            REPAIRMAN = 2,
                            CUSTOMER = 3,
                            SOFTDELETE = 99;

    private final int UserID;
    private final int UserClass;

    private final String firstname;
    private final String lastname;
    private final int phone;
    private final String email;
    private final String landcode;
    private final String password;


    /**
     * When creating a new user in the code, this is the constructor to be used. It creates a temporary password for the user,
     * and holds all information needed to be registered in the database.
     *
     * @param UserClass     not in use at the moment, but can be used for adding users with different level of access
     * @param firstname     the firstname of the user
     * @param lastname      the lastname of the user
     * @param phone         the phonenumber of the user
     * @param email         the email and username of the person. Passwords and information is sent to this. Used to login user
     * @param landcode      the landcode, to support phones from other countries.
     */
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

    /**
     * This constructor is used by the Database, returning a fulfilled user with all information needed.
     * @param UserID        the users ID
     * @param UserClass     the level of access the user is given
     * @param firstname     the firstname of the user
     * @param lastname      the lastname of the usr
     * @param phone         the phonenumber of the user
     * @param email         the email and username of the person. Passwords and information is sent to this. Used to login user
     * @param landcode      the landcode, to support phones from other countries.
     */
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
