package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditBikePanel extends JPanel {
    private BikeSubmitListener submitlistener = new BikeSubmitListener();
    // Edit bike submit button
    private JButton editbikeSubmit;
    // Input fields
    private JTextField inputBikeID;
    private JTextField inputPrice;
    private JComboBox inputType;
    private JComboBox inputMake;
    public EditBikePanel(){
        // Creating an edit bike panel
        setLayout(new GridBagLayout());
        //setBackground(Color.BLUE);
        // Submit button with action listener
        editbikeSubmit = new JButton("SUBMIT");
        editbikeSubmit.setFont(new Font("MONOSPACED",Font.PLAIN,14));
        editbikeSubmit.addActionListener(submitlistener);
        // Header label
        JLabel editbike = new JLabel("EDIT EXISTING BIKE");
        editbike.setFont(new Font("MONOSPACED",Font.BOLD,30));
        // Constraints object for placement of components on panel
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.PAGE_START;
        c.gridwidth=2;
        c.gridx=0;
        c.gridy=0;
        // Adding header
        add(editbike,c);
        c.anchor=GridBagConstraints.CENTER;
        //c.insets=new Insets(100,0,0,0);
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=1;
        // Adding bikeID label and textfield
        JLabel bikeID = new JLabel("BikeID: ");
        bikeID.setFont(new Font("MONOSPACED",Font.BOLD,16));
        add(bikeID,c);
        c.gridx=1;
        c.gridy=1;
        inputBikeID = new JTextField(10);
        add(inputBikeID,c);
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
        // editing make and combobox for selecting make
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
        add(editbikeSubmit,d);
    }

    public class BikeSubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JButton src = (JButton) event.getSource();
            if (src.equals(editbikeSubmit)) {
                // Getting the input info
                String out = "Price: " + inputPrice.getText() + "\n" +
                        "Type: " + inputType.getSelectedItem() + "\n" +
                        "Make: " + inputMake.getSelectedItem() + "\n";
                JOptionPane.showMessageDialog(null, "Bike with BikeID: " + inputBikeID.getText() + "\nwas submitted to database with the following updated info:\n" + out);
            }
        }
    }
}
