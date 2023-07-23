package views;

import controller.TablePrenotazioniController;
import data_access.Gateway;
import utils.DataFilter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
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

    // Controller della tabella
    TablePrenotazioniController tablePrenotazioniController;

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolbar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnCercaPrenotazione;
    private JButton btnSalva;
    private JTable tabellaPrenotazioni;
    private JComboBox cbFiltroAnni;
    private JScrollPane scrollPane;

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

        // Mostra la query in base al valore della comboBox
        tablePrenotazioniController = new TablePrenotazioniController(tabellaPrenotazioni);
        tabellaPrenotazioni = tablePrenotazioniController.initView(cbFiltroAnni);

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
        DefaultTableCellRenderer cellRenderer = createCellRenderer();

        // Renderer per l'header
        DefaultTableCellRenderer headerRenderer = createHeaderRenderer();

        // Assegna i renderer
        for(int columnIndex = 0; columnIndex < tabellaPrenotazioni.getColumnCount(); columnIndex++) {
            tabellaPrenotazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
        tabellaPrenotazioni.getTableHeader().setDefaultRenderer(headerRenderer);

        scrollPane = new JScrollPane(tabellaPrenotazioni);
        mainPanelPrenotazioni.add(scrollPane, BorderLayout.CENTER);
    }

    // Renderer estetica celle
    private DefaultTableCellRenderer createCellRenderer() {
        return new DefaultTableCellRenderer() {
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
    }

    // Renderer estetica header
    private DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
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
                addPrenotazioneDialog();
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
        cbFiltroAnni.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    tabellaPrenotazioni.setModel(tablePrenotazioniController.initView(cbFiltroAnni).getModel());
                    ((AbstractTableModel) tabellaPrenotazioni.getModel()).fireTableDataChanged();

                    for(int columnIndex = 0; columnIndex < tabellaPrenotazioni.getColumnCount(); columnIndex++) {
                        tabellaPrenotazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(createCellRenderer());
                    }
                    tabellaPrenotazioni.getTableHeader().setDefaultRenderer(createHeaderRenderer());

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        toolbar.setFloatable(false);

        pnlToolbar.add(toolbar, BorderLayout.CENTER);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.NORTH);
    }

    // Setting dialog di aggiunta prenotazione
    private void addPrenotazioneDialog(){
        JDialog dialogNuovaPrenotazione = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Aggiungi nuova prenotazione", true);
        dialogNuovaPrenotazione.setLayout(new BorderLayout());
        dialogNuovaPrenotazione.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialogNuovaPrenotazione.setResizable(false);

        /* Panel dedicato agli elementi del form */
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(nameField, gbc);
        /* --------------------------------------- */

        /* Panel dedicato ai buttons */
        JPanel pnlButtons = new JPanel(new FlowLayout());
        JButton btnConferma = new JButton("Aggiungi");
        JButton btnAnnulla = new JButton("Annulla");
        btnConferma.setFocusPainted(false);
        btnAnnulla.setFocusPainted(false);

        // Annulla -> chiude il dialog
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogNuovaPrenotazione.dispose();
            }
        });

        //TODO: Aggiungi -> aggiunge la prenotazione nel db e fa refresh della tabella!

        pnlButtons.add(btnConferma);
        pnlButtons.add(btnAnnulla);
        /* --------------------------------------- */

        dialogNuovaPrenotazione.add(pnlForm, BorderLayout.CENTER);
        dialogNuovaPrenotazione.add(pnlButtons, BorderLayout.SOUTH);
        dialogNuovaPrenotazione.pack();
        dialogNuovaPrenotazione.setVisible(true);
    }
}
