package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BikePanel extends JPanel{
    // Listeners
    private AddBikeListener addListener = new AddBikeListener();
    private EditBikeListener editListener = new EditBikeListener();
    // Buttons
    private JButton addbikeButton =  new JButton("ADD");
    private JButton editbikeButton = new JButton("EDIT");
    private JButton deletebikeButton = new JButton("DELETE");
    private JButton repairButton = new JButton("REPAIR");
    private JButton statusbikeButton = new JButton("STATUS");
    // Toolbar
    private JToolBar biketools = new JToolBar(SwingConstants.VERTICAL);
    // Layout for toolbar and the cardpanel
    private BorderLayout border = new BorderLayout();
    // The card panel
    private JPanel outputCardPanel = new JPanel();
    private CardLayout card = new CardLayout();
    private JPanel mainPanel;
    private AddBikePanel addPanel;
    private EditBikePanel editPanel;


    public BikePanel() {
        //-- BIKE PANEL
        setBackground(Color.BLUE);
        setLayout(border);
        // Adding buttons to toolbar
        biketools.add(addbikeButton);
        biketools.add(editbikeButton);
        biketools.add(deletebikeButton);
        biketools.add(repairButton);
        biketools.add(statusbikeButton);
        // Adding listener
        addbikeButton.addActionListener(addListener);
        editbikeButton.addActionListener(editListener);
        //deletebikeButton.addActionListener(bikeListener);
        //repairButton.addActionListener(bikeListener);
        //statusbikeButton.addActionListener(bikeListener);
        // Setting look and feel of toolbar
        biketools.setBackground(Color.BLUE);
        biketools.setBorderPainted(false);
        addbikeButton.setBackground(Color.LIGHT_GRAY);
        editbikeButton.setBackground(Color.LIGHT_GRAY);
        deletebikeButton.setBackground(Color.LIGHT_GRAY);
        repairButton.setBackground(Color.LIGHT_GRAY);
        statusbikeButton.setBackground(Color.LIGHT_GRAY);
        addbikeButton.setForeground(Color.YELLOW);
        editbikeButton.setForeground(Color.YELLOW);
        deletebikeButton.setForeground(Color.YELLOW);
        repairButton.setForeground(Color.YELLOW);
        statusbikeButton.setForeground(Color.YELLOW);
        addbikeButton.setFont(new Font("MONOSPACED", Font.BOLD, 20));
        editbikeButton.setFont(new Font("MONOSPACED", Font.BOLD, 20));
        deletebikeButton.setFont(new Font("MONOSPACED", Font.BOLD, 20));
        repairButton.setFont(new Font("MONOSPACED", Font.BOLD, 20));
        statusbikeButton.setFont(new Font("MONOSPACED", Font.BOLD, 20));
        // Adding toolbar to panel
        add(biketools, border.LINE_START);
        // Adding the cards to the panel
        outputCardPanel.setLayout(card);
        outputCardPanel.setBackground(Color.BLUE);
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLUE);
        outputCardPanel.add(mainPanel,"main");
        card.show(outputCardPanel,"main");
        // Adding to main panel
        add(outputCardPanel,border.CENTER);

    }

    public class AddBikeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton src = (JButton) e.getSource();
            if (src.equals(addbikeButton)){
                addPanel = new AddBikePanel();
                outputCardPanel.add(addPanel,"add");
                card.show(outputCardPanel,"add");
            }
        }
    }

    public class EditBikeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton src = (JButton) e.getSource();
            if (src.equals(editbikeButton)){
                editPanel = new EditBikePanel();
                outputCardPanel.add(editPanel,"edit");
                card.show(outputCardPanel,"edit");
            }
        }
    }
}
