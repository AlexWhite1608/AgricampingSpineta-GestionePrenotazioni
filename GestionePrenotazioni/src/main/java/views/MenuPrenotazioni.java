package views;

import data_access.Gateway;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
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

        tabellaPrenotazioni.getTableHeader().setReorderingAllowed(false);
        tabellaPrenotazioni.setDefaultEditor(Object.class, null);
        tabellaPrenotazioni.removeColumn(tabellaPrenotazioni.getColumnModel().getColumn(0));

        // Modifiche estetiche
        int rowHeight = 40;
        tabellaPrenotazioni.setRowHeight(rowHeight);
        tabellaPrenotazioni.setDefaultRenderer(Object.class, new TableCellRenderer(){
            private DefaultTableCellRenderer DEFAULT_RENDERER =  new DefaultTableCellRenderer();
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0){
                    c.setBackground(Color.WHITE);
                }
                else {
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
        toolbar.add(btnAggiungiPrenotazione);
        toolbar.add(btnCercaPrenotazione);
        toolbar.setFloatable(false);

        pnlToolbar.add(toolbar, BorderLayout.CENTER);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.NORTH);
    }
}
