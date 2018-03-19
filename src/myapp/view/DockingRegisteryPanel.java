package myapp.view;

import oracle.jvm.hotspot.jfr.JFR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DockingRegisteryPanel extends JPanel{
    private final JLabel stedsnavn;
    private final JLabel adresse;
    private final JLabel antPlasser;

    private final JTextField stdNavn;
    private final JTextField adr;
    private final JTextField antPl;

    private final JButton commit;

    public DockingRegisteryPanel(){
        GridLayout layout = new GridLayout(4, 1);
        layout.setHgap(10);
        layout.setVgap(10);
        setLayout(layout);

        JPanel row0 = new JPanel(new GridLayout(1, 2));
        JPanel row1 = new JPanel(new GridLayout(1, 2));
        JPanel row2 = new JPanel(new GridLayout(1, 2));
        JPanel row3 = new JPanel(new FlowLayout());

        this.stedsnavn = new JLabel("Stedsnavn: ");
        this.adresse = new JLabel("Adresselinje: ");
        this.antPlasser = new JLabel("Antall plasser: ");
        this.stdNavn = new JTextField(30);
        this.adr = new JTextField(30);
        this.antPl = new JTextField(30);

        this.commit = new JButton("Commit changes");
        commit.addActionListener(new CommitChangesListener());

        row0.add(stedsnavn, 0, 0);
        row0.add(stdNavn, 0, 1);
        row1.add(adresse, 0, 0);
        row1.add(adr, 0, 1);
        row2.add(antPlasser, 0, 0);
        row2.add(antPl, 0, 1);
        row3.add(commit);

        add(row0);
        add(row1);
        add(row2);
        add(row3);

    }

    public void setActionListener(ActionListener listener){
        for (int i = 0; i < commit.getActionListeners().length; i++) {
            commit.removeActionListener(commit.getActionListeners()[i]);
        }
        commit.addActionListener(listener);
    }

    class CommitChangesListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            System.out.println("You pressed the commit button");
        }
    }
}

class GT extends JFrame{

    public GT(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setTitle("TestWindow");
        setLayout(new BorderLayout());

        JMenuBar mb = new JMenuBar();
        JMenu bike = new JMenu("Bike");
        JMenu docking_station = new JMenu("Docking station");
        JMenu statistic = new JMenu("Statistic");
        JMenu map = new JMenu("Map");
        mb.add(bike);
        mb.add(docking_station);
        mb.add(statistic);
        mb.add(map);

        //Adding options to bike
        JMenuItem bike_reg = new JMenuItem("Register");
        JMenuItem bike_edit = new JMenuItem("Edit");
        JMenuItem bike_delete = new JMenuItem("Delete");
        JMenuItem bike_status = new JMenuItem("Show status");
        JMenuItem bike_repair_reg = new JMenuItem("Register repair");
        JMenuItem bike_repair_return = new JMenuItem("Return from repair");

        bike.add(bike_reg);
        bike.add(bike_edit);
        bike.add(bike_delete);
        bike.add(bike_status);
        bike.add(bike_repair_reg);
        bike.add(bike_repair_return);

        JMenuItem dock_add = new JMenuItem("Add");
        JMenuItem  dock_edit = new JMenuItem("Edit");
        JMenuItem  dock_delete = new JMenuItem("Delete");
        JMenuItem  dock_status = new JMenuItem("Status");

        docking_station.add(dock_add);
        docking_station.add(dock_edit);
        docking_station.add(dock_delete);
        docking_station.add(dock_status);

        //Adding options to statistic
        JMenuItem statistic1 = new JMenuItem("Statistic1");
        JMenuItem  statistic2 = new JMenuItem("Statistic2");
        JMenuItem  statistic3 = new JMenuItem("Statistic3");

        statistic.add(statistic1);
        statistic.add(statistic2);
        statistic.add(statistic3);
        add(mb, BorderLayout.NORTH);

        dock_add.addActionListener(new RegisterActionListener());
        //pack();
        setVisible(true);
    }

    class OuterCommitChangesListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            System.out.println("You pressed the commit button outside the DockingRegistryPanel class");
            Object src = event.getSource();
            if(src instanceof DockingRegisteryPanel){
                DockingRegisteryPanel doc = (DockingRegisteryPanel) src;
            }
        }
    }

    class RegisterActionListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            System.out.println("You pressed a button");
            DockingRegisteryPanel pnl = new DockingRegisteryPanel();
            pnl.setActionListener(new OuterCommitChangesListener());
            add(pnl, BorderLayout.CENTER);
            validate();
        }
    }

    public static void main(String[]args){
        GT g = new GT();
    }
}
