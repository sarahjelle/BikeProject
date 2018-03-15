package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    // Listener
    private PanelListener buttonPanelListener = new PanelListener();
    // Panels
    private JPanel panelCardContainer = new JPanel();
    private ToolBarPanel panelButtonContainer = new ToolBarPanel(buttonPanelListener);
    private BikePanel panelBike = new BikePanel();
    private DockPanel panelDock = new DockPanel();
    private StatPanel panelStats = new StatPanel();
    private MapPanel panelMap = new MapPanel();
    private CardLayout card = new CardLayout();
    private BorderLayout border = new BorderLayout();
    private JPanel mainPanel = new JPanel();

    public Menu(){
        super("Main menu");

        //-- CARD LAYOUT PANEL FOR SWITCHING BETWEEN BUTTON OPTIONS

        panelCardContainer.setLayout(card);
        //mainPanel.setBackground(Color.BLUE);
        panelCardContainer.add(mainPanel,"main");
        panelCardContainer.add(panelBike, "bike");
        panelCardContainer.add(panelDock,"dock");
        panelCardContainer.add(panelStats,"stat");
        panelCardContainer.add(panelMap,"map");

        card.show(panelCardContainer,"main");

        //-- ADDING TO MAIN FRAME
        Container pane = this.getContentPane();
        pane.add(panelButtonContainer,border.PAGE_START);
        pane.add(panelCardContainer,border.CENTER);
    }
    public class PanelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JButton src = (JButton) event.getSource();
            if (src.equals(panelButtonContainer.buttonBike)){
                card.show(panelCardContainer,"bike");
            }
            if (src.equals(panelButtonContainer.buttonDock)){
                card.show(panelCardContainer,"dock");
            }
            if (src.equals(panelButtonContainer.buttonStat)){
                card.show(panelCardContainer,"stat");
            }
            if (src.equals(panelButtonContainer.buttonMap)){
                card.show(panelCardContainer,"map");
            }
        }
    }

}
