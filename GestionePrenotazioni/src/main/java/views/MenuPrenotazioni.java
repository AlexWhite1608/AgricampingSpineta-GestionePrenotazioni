package views;

import data_access.Gateway;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuPrenotazioni extends JPanel {

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolbar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnCercaPrenotazione;
    private JTable tabellaPrenotazioni;

    public MenuPrenotazioni() throws SQLException {
        createUIComponents();
        setupToolbar();
        setupTable();

        setLayout(new BorderLayout());
        add(mainPanelPrenotazioni, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() throws SQLException {
        // Main panel
        mainPanelPrenotazioni = new JPanel();
        mainPanelPrenotazioni.setLayout(new BorderLayout());

        // Panel toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolbar = new JToolBar();
        btnAggiungiPrenotazione = new JButton("Aggiungi");
        btnCercaPrenotazione = new JButton("Cerca");
        btnAggiungiPrenotazione.setFocusPainted(false);
        btnCercaPrenotazione.setFocusPainted(false);
        btnAggiungiPrenotazione.setToolTipText("Aggiungi prenotazione");
        btnCercaPrenotazione.setToolTipText("Cerca prenotazione");

        // Popola la tabella con le informazioni nel database
        Gateway gateway = new Gateway();
        String initialQuery = "SELECT * FROM Prenotazioni";
        ResultSet resultSet = gateway.execSelectQuery(initialQuery);
        tabellaPrenotazioni = new JTable(gateway.buildCustomTableModel(resultSet));
    }

    // Setup della tabella delle prenotazioni
    private void setupTable() {
        int rowHeight = 30;
        tabellaPrenotazioni.setRowHeight(rowHeight);
        tabellaPrenotazioni.getTableHeader().setReorderingAllowed(false);
        tabellaPrenotazioni.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(tabellaPrenotazioni);
        mainPanelPrenotazioni.add(scrollPane, BorderLayout.CENTER);
    }

    // Setup toolbar
    private void setupToolbar() {
        toolbar.add(btnAggiungiPrenotazione);
        toolbar.add(btnCercaPrenotazione);
        toolbar.setFloatable(false);

        pnlToolbar.add(toolbar, BorderLayout.CENTER);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.NORTH);
    }
}
