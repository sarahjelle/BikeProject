package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JButton submit;
    private LoginListener submitListener;

    public Login() {
        submitListener = new LoginListener();
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        JPanel loginPanel = new JPanel();
        //JPanel mainPanel = new JPanel();
        //loginPanel.setBackground(Color.ORANGE);
        //mainPanel.setBackground(Color.ORANGE);
        //mainPanel.setLayout(border);
        loginPanel.setLayout(grid);
        c.anchor=GridBagConstraints.ABOVE_BASELINE_LEADING;
        c.gridx=0;
        c.gridy=0;
        JLabel username = new JLabel("Username:");
        loginPanel.add(username,c);
        c.gridx=1;
        c.gridy=0;
        JTextField userInput = new JTextField(20);
        loginPanel.add(userInput,c);
        c.gridx=0;
        c.gridy=1;
        JLabel password = new JLabel("Password: ");
        loginPanel.add(password,c);
        JPasswordField passwordInput = new JPasswordField(20);
        c.gridx=1;
        c.gridy=1;
        loginPanel.add(passwordInput,c);

        submit = new JButton("SUBMIT");
        submit.addActionListener(submitListener);
        c.gridy=10;
        //c.gridwidth=2;
        //mainPanel.add(loginPanel,border.NORTH);
        //mainPanel.add(submit,border.PAGE_END);
        loginPanel.add(submit,c);
        setContentPane(loginPanel);
        pack();
    }

    private class LoginListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton src = (JButton) e.getSource();
            if (src.equals(submit)){
                dispose();
                Menu gui = new Menu();
                gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gui.setSize(800,600);
                gui.setVisible(true);
            }
        }
    }
}
