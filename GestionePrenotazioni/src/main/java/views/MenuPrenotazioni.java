package views;

import data_access.Gateway;
import utils.DataFilter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MenuPrenotazioni extends JPanel {

    private final ArrayList<String> years = DataFilter.getYears();

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolbar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnCercaPrenotazione;
    private JTable tabellaPrenotazioni;
    private JComboBox cbFiltroAnni;

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

        // Bottoni azioni nella toolbar
        btnAggiungiPrenotazione = new JButton("Aggiungi");
        btnCercaPrenotazione = new JButton("Cerca");

        // ComboBox filtraggio anni
        cbFiltroAnni = new JComboBox(years.toArray());

        // Popola la tabella con le informazioni nel database
        Gateway gateway = new Gateway();
        String initialQuery = "SELECT * FROM Prenotazioni";
        ResultSet resultSet = gateway.execSelectQuery(initialQuery);
        tabellaPrenotazioni = new JTable(gateway.buildCustomTableModel(resultSet));
    }

    // Setup della tabella delle prenotazioni
    private void setupTable() {

        tabellaPrenotazioni.getTableHeader().setReorderingAllowed(false);
        tabellaPrenotazioni.setCellSelectionEnabled(false);
        tabellaPrenotazioni.setRowSelectionAllowed(true);
        tabellaPrenotazioni.setDefaultEditor(Object.class, null);
        tabellaPrenotazioni.removeColumn(tabellaPrenotazioni.getColumnModel().getColumn(0));

        // Modifiche estetiche
        int rowHeight = 40;
        tabellaPrenotazioni.setRowHeight(rowHeight);
        tabellaPrenotazioni.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(255, 255, 102)); // Imposta il colore rosso per la riga selezionata
                } else if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(220, 232, 234));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabellaPrenotazioni);
        mainPanelPrenotazioni.add(scrollPane, BorderLayout.CENTER);
    }

    // Setup toolbar
    private void setupToolbar() {

        // Setting buttons
        btnAggiungiPrenotazione.setFocusPainted(false);
        btnCercaPrenotazione.setFocusPainted(false);
        btnAggiungiPrenotazione.setToolTipText("Aggiungi prenotazione");
        btnCercaPrenotazione.setToolTipText("Cerca prenotazione");

        // Crea un separatore orizzontale per distanziare i bottoni dalla combobox
        int separatorWidth = 1500;
        Component horizontalStrut = Box.createHorizontalStrut(separatorWidth);
        toolbar.add(btnAggiungiPrenotazione);
        toolbar.add(btnCercaPrenotazione);
        toolbar.add(horizontalStrut);

        // Setting combobox
        cbFiltroAnni.setSelectedItem(years.get(years.size() - 1));
        toolbar.add(new JLabel("Mostra per anno: "));
        toolbar.add(cbFiltroAnni);

        toolbar.setFloatable(false);

        pnlToolbar.add(toolbar, BorderLayout.CENTER);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.NORTH);
    }
}
