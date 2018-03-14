package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddBikePanel extends JPanel {
    private BikeSubmitListener submitlistener = new BikeSubmitListener();
    // Add bike submit button
    private JButton addbikeSubmit;
    // Input fields
    private JTextField inputBikeID;
    private JTextField inputPrice;
    private JComboBox inputType;
    private JComboBox inputMake;
    public AddBikePanel(){
            // Creating an add bike panel
            setLayout(new GridBagLayout());
            setBackground(Color.BLUE);
            // Submit button with action listener
            addbikeSubmit = new JButton("SUBMIT");
            addbikeSubmit.setFont(new Font("MONOSPACED",Font.PLAIN,14));
            addbikeSubmit.addActionListener(submitlistener);
            // Header label
            JLabel regbike = new JLabel("REGISTER NEW BIKE");
            regbike.setFont(new Font("MONOSPACED",Font.BOLD,30));
            // Constraints object for placement of components on panel
            GridBagConstraints c = new GridBagConstraints();
            c.anchor=GridBagConstraints.PAGE_START;
            c.gridwidth=2;
            c.gridx=0;
            c.gridy=0;
            // Adding header
            add(regbike,c);
            c.anchor=GridBagConstraints.CENTER;
            c.insets=new Insets(100,0,0,0);
            c.gridwidth=1;
            c.gridx=0;
            c.gridy=1;
            // Adding bikeID label and textfield
            JLabel bikeID = new JLabel("BikeID: ");
            bikeID.setFont(new Font("MONOSPACED",Font.BOLD,16));
            //addPanel.add(bikeID,c);
            c.gridx=1;
            c.gridy=1;
            inputBikeID = new JTextField(10);
            //addPanel.add(inputBikeID,c);
            c.insets=new Insets(0,0,0,0);
            c.gridx=0;
            c.gridy=2;
            // Adding price label and textfield
            JLabel price = new JLabel("Price: ");
            price.setFont(new Font("MONOSPACED",Font.BOLD,16));
            add(price,c);
            inputPrice = new JTextField(10);
            c.gridx=1;
            c.gridy=2;
            add(inputPrice,c);
            // Adding type and combobox for selecting type
            JLabel type = new JLabel("Type: ");
            type.setFont(new Font("MONOSPACED",Font.BOLD,16));
            c.gridx=0;
            c.gridy=3;
            add(type,c);
            String[] typeSelection = {"El-bike", "Bike", "Child"};
            inputType = new JComboBox(typeSelection);
            inputType.setSelectedIndex(0);
            c.gridx=1;
            c.gridy=3;
            add(inputType,c);
            // Adding make and combobox for selecting make
            JLabel make = new JLabel("Make: ");
            make.setFont(new Font("MONOSPACED",Font.BOLD,16));
            c.gridx=0;
            c.gridy=4;
            add(make,c);
            String[] makeSelection = {"Evelo","Biktrix","JetBike","Volt"};
            inputMake = new JComboBox(makeSelection);
            inputMake.setSelectedIndex(0);
            c.gridx=1;
            c.gridy=4;
            add(inputMake,c);
            GridBagConstraints d = new GridBagConstraints();
            d.gridx=1;
            d.gridy=5;
            d.anchor=GridBagConstraints.PAGE_END;
            d.insets=new Insets(100,0,0,0);
            add(addbikeSubmit,d);
    }

        public class BikeSubmitListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent event) {
                JButton src = (JButton) event.getSource();
                if (src.equals(addbikeSubmit)) {
                    // Burde ha getNewBikeID som gir neste id-nummer skriver 909090 foreløpig
                    // Getting the input info
                    String out = "Price: " + inputPrice.getText() + "\n" +
                            "Type: " + inputType.getSelectedItem() + "\n" +
                            "Make: " + inputMake.getSelectedItem() + "\n";
                    JOptionPane.showMessageDialog(null, "New bike with BikeID: 909090 was submitted to database with the following info:\n" + out);
                }
            }
        }
}
