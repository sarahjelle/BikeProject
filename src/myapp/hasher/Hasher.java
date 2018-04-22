package myapp.hasher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static javax.swing.JOptionPane.*;

/**
 * Hasher is a class for taking care of all there is when hashing passwords.
 */
public class Hasher {
    public Hasher(){}

    /**
     * hash takes in the String of password there is to be hashed. Its also require a salt to add a level of
     * security to the password. The password hashed can not be recovered to plain text.
     * @param passwordToHash    the password in plain text to be hashed
     * @param salt              the hashed salt to be used with the password
     * @return                  a hashed password
     */
    public String hash(String passwordToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * We recommend using this hashSalt method for hashing a string of System.currentTimeMillies() to a string of 128 character
     * before using it with the hash method.
     *
     * @param salt      a string to be hashed
     * @return          a string of 128 characters
     */
    public String hashSalt(String salt){
        String generatedHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(salt.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedHash = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedHash;
    }

}

class HashTest{
    public static void main(String[]args){
        String unhashedPwd = "martin";
        String salt = "" + System.currentTimeMillis();

        System.out.println("Pass: " + unhashedPwd);
        System.out.println("Salt: " + salt);

        String hashedSalt = new Hasher().hashSalt(salt);
        System.out.println("Hashed salt: \t" + hashedSalt);

        String hashedSalt2 = new Hasher().hashSalt(salt);
        System.out.println("Hashed salt2: \t" + hashedSalt2);

        String hashedPass = new Hasher().hash(unhashedPwd, hashedSalt);
        System.out.println("Hashed pass: \t" + hashedPass);

        String hashedPass2 = new Hasher().hash(unhashedPwd, hashedSalt);
        System.out.println("Hashed pass2: \t" + hashedPass2);

        if(hashedPass2.equals(hashedPass)){
            System.out.println("The hashing function is repeatable");
        } else{
            System.out.println("The hashing function not is repeatable");
        }

        String input = (String) showInputDialog("Write the password");
        String hashed = new Hasher().hash(input, hashedSalt);
        if(hashed.equals(hashedPass)){
            System.out.println("Correct!");
        } else{
            System.out.println("Not correct...");
        }
    }
}
