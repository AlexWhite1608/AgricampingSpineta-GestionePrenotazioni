package views;

import controllers.TableArriviController;
import controllers.TableCalendarioController;
import controllers.TablePartenzeController;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;

public class MenuArriviPartenze extends JPanel {

    // I due controller per le due tabelle
    TableArriviController tableArriviController;
    TablePartenzeController tablePartenzeController;

    private JPanel mainPanelArriviPartenze;
    private JTable tabellaArrivi;
    private JTable tabellaPartenze;
    private JPanel pnlToolbar;
    private JToolBar toolBar;

    public MenuArriviPartenze() throws SQLException {

        createUIComponents();
        setupToolbar();
        setupTableArrivi();
        setupTablePartenze();

        setLayout(new BorderLayout());
        add(mainPanelArriviPartenze, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelArriviPartenze = new JPanel();
        mainPanelArriviPartenze.setLayout(new GridLayout(1, 2));

        // Inizializzazione delle tabelle e relativi controllers
        tabellaArrivi = new JTable();
        tabellaPartenze = new JTable();
        tableArriviController = new TableArriviController(tabellaArrivi);
        tablePartenzeController = new TablePartenzeController(tabellaPartenze);

        // Inizializza la toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
    }

    // Setup tabella arrivi
    private void setupTableArrivi() throws SQLException {

        // Impostazioni di base della tabella
        tabellaArrivi.getTableHeader().setReorderingAllowed(false);
        tabellaArrivi.setCellSelectionEnabled(true);
        tabellaArrivi.setRowSelectionAllowed(false);
        tabellaArrivi.setColumnSelectionAllowed(false);
        tabellaArrivi.setDefaultEditor(Object.class, null);
        tabellaArrivi.setGridColor(Color.BLACK);
        tabellaArrivi.setRowHeight(40);

        // Impostazione del tableModel e dei renderer
        tableArriviController.setTableModel();
        tableArriviController.createCellRenderer();
        tableArriviController.createHeaderRenderer();

        // Panel per tabellaArrivi
        JPanel pnlTabellaArrivi = new JPanel(new BorderLayout());
        pnlTabellaArrivi.add(new JScrollPane(tabellaArrivi), BorderLayout.CENTER);

        // Imposta il bordo del panel
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "ARRIVI", TitledBorder.CENTER, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlTabellaArrivi.setBorder(titledBorder);

        JScrollPane scrollPane = new JScrollPane(tabellaArrivi);
        pnlTabellaArrivi.add(scrollPane);
        mainPanelArriviPartenze.add(pnlTabellaArrivi, BorderLayout.WEST);
    }

    // Setup tabella arrivi
    private void setupTablePartenze() throws SQLException {

        // Impostazioni di base della tabella
        tabellaPartenze.getTableHeader().setReorderingAllowed(false);
        tabellaPartenze.setCellSelectionEnabled(true);
        tabellaPartenze.setRowSelectionAllowed(true);
        tabellaPartenze.setColumnSelectionAllowed(true);
        tabellaPartenze.setDefaultEditor(Object.class, null);
        tabellaPartenze.setGridColor(Color.BLACK);
        tabellaPartenze.setRowHeight(40);

        // Impostazione del tableModel e dei renderer
        tablePartenzeController.setTableModel();
        tablePartenzeController.createCellRenderer();
        tablePartenzeController.createHeaderRenderer();

        // Panel per tabellaPartenze
        JPanel pnlTabellaPartenze = new JPanel(new BorderLayout());
        pnlTabellaPartenze.add(new JScrollPane(tabellaPartenze), BorderLayout.CENTER);

        // Imposta il bordo del panel
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "PARTENZE", TitledBorder.CENTER, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlTabellaPartenze.setBorder(titledBorder);

        JScrollPane scrollPane = new JScrollPane(tabellaPartenze);
        pnlTabellaPartenze.add(scrollPane);
        mainPanelArriviPartenze.add(pnlTabellaPartenze, BorderLayout.EAST);
    }

    // Setup della toolbar
    private void setupToolbar() {

    }
}
