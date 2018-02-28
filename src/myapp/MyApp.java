package myapp;

import myapp.view.MyWindow;

/**
 * Use this class to start the application
 * @author nilstes
 */
public class MyApp {

    /**
     * Main method for my application
     */
    public static void main(String[] args) throws Exception {
        MyWindow window = new MyWindow("The Window");
        window.setVisible(true);
        /*
        * Dette er en kommentar jeg (Martin) la til i branchen TestBranch
        */
        System.out.println("Dette er en testendring");
   }  
}
