package views;

import data_access.Gateway;
import utils.DataFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MenuPrenotazioni extends JPanel {

    // Valori per modifiche estetiche
    private final int SEPARATOR_WIDTH = 1520;
    private final Font CELL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Color ALTERNATE_CELL_COLOR = new Color(220, 232, 234);
    private final Color SELECTION_COLOR = new Color(255, 255, 102);
    private final Color HEADER_BACKGROUND = Color.LIGHT_GRAY;

    // Anni contenuti nella cbFiltroAnni
    private final ArrayList<String> YEARS = DataFilter.getYears();

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolbar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnCercaPrenotazione;
    private JButton btnSalva;
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
        btnSalva = new JButton("Salva");
        btnAggiungiPrenotazione = new JButton("Aggiungi");
        btnCercaPrenotazione = new JButton("Cerca");

        // ComboBox filtraggio anni
        cbFiltroAnni = new JComboBox(YEARS.toArray());

        // Popola la tabella con le informazioni nel database
        Gateway gateway = new Gateway();
        //TODO: nella query devi filtrare gli anni della combobox!!
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

        // Dimensione righe tabella
        int rowHeight = 40;
        tabellaPrenotazioni.setRowHeight(rowHeight);

        // Renderer per il testo delle celle
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(CELL_FONT);

                // Colora le righe alternativamente
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(ALTERNATE_CELL_COLOR);
                }

                // Colora di giallo la riga selezionata
                if(isSelected){
                    c.setBackground(SELECTION_COLOR);
                }

                return c;
            }
        };

        // Renderer per l'header
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(HEADER_FONT);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setBackground(HEADER_BACKGROUND);

                return c;
            }
        };

        // Assegna i renderer
        for(int columnIndex = 0; columnIndex < tabellaPrenotazioni.getColumnCount(); columnIndex++) {
            tabellaPrenotazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
        tabellaPrenotazioni.getTableHeader().setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(tabellaPrenotazioni);
        mainPanelPrenotazioni.add(scrollPane, BorderLayout.CENTER);
    }

    // Setup toolbar
    private void setupToolbar() {

        // Setting buttons
        btnSalva.setFocusPainted(false);
        btnAggiungiPrenotazione.setFocusPainted(false);
        btnCercaPrenotazione.setFocusPainted(false);
        btnSalva.setToolTipText("Salva sul drive");
        btnAggiungiPrenotazione.setToolTipText("Aggiungi prenotazione");
        btnCercaPrenotazione.setToolTipText("Cerca prenotazione");

        // Azione: aggiunta di una nuova prenotazione
        btnAggiungiPrenotazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialogNuovaPrenotazione = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Aggiungi nuova prenotazione", true);
                dialogNuovaPrenotazione.setLayout(new BorderLayout());
                dialogNuovaPrenotazione.setLocationRelativeTo(null);

                JPanel pnlForm = new JPanel();

                dialogNuovaPrenotazione.add(pnlForm, BorderLayout.CENTER);
                dialogNuovaPrenotazione.pack();
                dialogNuovaPrenotazione.setVisible(true);
            }
        });

        // Crea un separatore orizzontale per distanziare i bottoni dalla combobox
        Component horizontalStrut = Box.createHorizontalStrut(SEPARATOR_WIDTH);
        toolbar.add(btnSalva);
        toolbar.add(btnAggiungiPrenotazione);
        toolbar.add(btnCercaPrenotazione);
        toolbar.add(horizontalStrut);

        // Setting combobox
        cbFiltroAnni.setSelectedItem(YEARS.get(YEARS.size() - 1));
        cbFiltroAnni.setFocusable(false);
        toolbar.add(new JLabel("Mostra per anno: "));
        toolbar.add(cbFiltroAnni);
        ((JLabel) cbFiltroAnni.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        toolbar.setFloatable(false);

        pnlToolbar.add(toolbar, BorderLayout.CENTER);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.NORTH);
    }

    // Setting dialog di aggiunta prenotazione
    private void addPrenotazioneDialog(){

    }
}
