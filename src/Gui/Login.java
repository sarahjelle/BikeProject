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
        BorderLayout border = new BorderLayout();
        GridLayout grid = new GridLayout(2,2);
        JPanel loginPanel = new JPanel();
        JPanel mainPanel = new JPanel();
        loginPanel.setBackground(Color.ORANGE);
        mainPanel.setBackground(Color.ORANGE);
        mainPanel.setLayout(border);
        loginPanel.setLayout(grid);

        JLabel username = new JLabel("Username:");
        JTextField userInput = new JTextField(20);

        loginPanel.add(username);
        loginPanel.add(userInput);

        JLabel password = new JLabel("Password: ");
        JPasswordField passwordInput = new JPasswordField(20);

        loginPanel.add(password);
        loginPanel.add(passwordInput);

        //loginPanel.setPreferredSize(new Dimension(200,10));

        submit = new JButton("SUBMIT");
        submit.addActionListener(submitListener);

        mainPanel.add(loginPanel,border.NORTH);
        mainPanel.add(submit,border.PAGE_END);

        setContentPane(mainPanel);
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
