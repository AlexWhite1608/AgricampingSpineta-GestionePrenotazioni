package views;

import controllers.TableArriviController;
import controllers.TableCalendarioController;
import controllers.TablePartenzeController;

import javax.swing.*;
import javax.swing.border.Border;
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
        mainPanelArriviPartenze.setLayout(new BorderLayout());

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

        // Impostazione del tableModel
        tableArriviController.setTableModel();

        // Panel per tabellaArrivi
        JPanel pnlTabellaArrivi = new JPanel(new BorderLayout());
        pnlTabellaArrivi.add(new JScrollPane(tabellaArrivi), BorderLayout.CENTER);
        Border blackline = BorderFactory.createTitledBorder("ARRIVI");
        pnlTabellaArrivi.setBorder(blackline);

        JScrollPane scrollPane = new JScrollPane(tabellaArrivi);
        pnlTabellaArrivi.add(scrollPane);
        mainPanelArriviPartenze.add(pnlTabellaArrivi, BorderLayout.WEST);
    }

    // Setup tabella arrivi
    private void setupTablePartenze() throws SQLException {

        // Impostazioni di base della tabella
        tabellaPartenze.getTableHeader().setReorderingAllowed(false);
        tabellaPartenze.setCellSelectionEnabled(true);
        tabellaPartenze.setRowSelectionAllowed(false);
        tabellaPartenze.setColumnSelectionAllowed(false);
        tabellaPartenze.setDefaultEditor(Object.class, null);
        tabellaPartenze.setGridColor(Color.BLACK);

        // Impostazione del tableModel
        tablePartenzeController.setTableModel();

        // Panel per tabellaPartenze
        JPanel pnlTabellaPartenze = new JPanel(new BorderLayout());
        pnlTabellaPartenze.add(new JScrollPane(tabellaPartenze), BorderLayout.CENTER);
        Border blackline = BorderFactory.createTitledBorder("PARTENZE");
        pnlTabellaPartenze.setBorder(blackline);

        JScrollPane scrollPane = new JScrollPane(tabellaPartenze);
        pnlTabellaPartenze.add(scrollPane);
        mainPanelArriviPartenze.add(pnlTabellaPartenze, BorderLayout.EAST);
    }

    // Setup della toolbar
    private void setupToolbar() {

    }
}
