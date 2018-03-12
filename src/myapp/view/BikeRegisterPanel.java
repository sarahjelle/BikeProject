package myapp.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BikeRegisterPanel {
    private JPanel bike_reg = new JPanel();
    private JPanel input = new JPanel();
    private JPanel confirm;
    private JTextField dateField = new JTextField(8);
    private JTextField priceField = new JTextField(10);
    private JTextField makeField = new JTextField(20);
    private JTextField typeField = new JTextField(10);//should be a list

    public BikeRegisterPanel() {
        bike_reg.setLayout(new FlowLayout());
        bike_reg.add(input = new inputPanel(), BorderLayout.CENTER);
        bike_reg.add(confirm = new confirmPanel(), BorderLayout.CENTER);
    }

    private class confirmPanel extends JPanel{
        public confirmPanel(){
            JButton ok = new JButton("Ok");
            JButton cancel = new JButton("Cancel");
            add(ok);
            add(cancel);
            ok.addActionListener(new buttonListener());
            cancel.addActionListener(new buttonListener());
        }
    }

    private class inputPanel extends JPanel{
        public inputPanel(){
            setLayout(new GridLayout(4, 1,1,1));
            add(new JLabel("Price: ", JLabel.RIGHT));
            add(priceField);
            add(new JLabel("Make: ", JLabel.RIGHT));
            add(makeField);
            add(new JLabel("Date: ", JLabel.RIGHT));
            add(dateField);
            add(new JLabel("Type: ", JLabel.RIGHT));
            add(typeField);

        }
    }
    public JPanel getBike_reg() {
        return bike_reg;
    }

    private class buttonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String choice = button.getText();

            if(choice.equals("Ok")){

            }

        }
    }
}
