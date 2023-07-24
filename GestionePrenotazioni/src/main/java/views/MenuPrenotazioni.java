package views;

import controller.TablePrenotazioniController;
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
import java.util.Objects;

public class MenuPrenotazioni extends JPanel {

    // Valori per modifiche estetiche
    private final int SEPARATOR_WIDTH = 1250;

    // Anni contenuti nella cbFiltroAnni
    private final ArrayList<String> YEARS = DataFilter.getYears();

    // Lista delle piazzole
    ArrayList<String> listaPiazzole = new ArrayList<>();


    // Controller della tabella
    TablePrenotazioniController tablePrenotazioniController;

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolBar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnCercaPrenotazione;
    private JButton btnSalva;
    private JButton btnAggiungiPiazzola;
    private JButton btnRimuoviPiazzola;
    private JTable tabellaPrenotazioni;
    private JComboBox cbFiltroAnni;
    private JScrollPane scrollPane;

    public MenuPrenotazioni() throws SQLException {
        tablePrenotazioniController = new TablePrenotazioniController(tabellaPrenotazioni);

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
        toolBar = new JToolBar();

        // Bottoni azioni nella toolbar
        btnSalva = new JButton("Salva");
        btnAggiungiPrenotazione = new JButton("Aggiungi");
        btnCercaPrenotazione = new JButton("Cerca");
        btnAggiungiPiazzola = new JButton("Aggiungi piazzola");
        btnRimuoviPiazzola = new JButton("Rimuovi piazzola");

        // ComboBox filtraggio anni
        cbFiltroAnni = new JComboBox(YEARS.toArray());

        // Mostra la query in base al valore della comboBox
        cbFiltroAnni.setSelectedItem(YEARS.get(YEARS.size() - 1));
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
        DefaultTableCellRenderer cellRenderer = tablePrenotazioniController.createCellRenderer();

        // Renderer per l'header
        DefaultTableCellRenderer headerRenderer = tablePrenotazioniController.createHeaderRenderer();

        // Assegna i renderer
        for(int columnIndex = 0; columnIndex < tabellaPrenotazioni.getColumnCount(); columnIndex++) {
            tabellaPrenotazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
        tabellaPrenotazioni.getTableHeader().setDefaultRenderer(headerRenderer);

        scrollPane = new JScrollPane(tabellaPrenotazioni);
        mainPanelPrenotazioni.add(scrollPane, BorderLayout.CENTER);
    }

    // Setup toolbar
    private void setupToolbar() {

        // Setting buttons
        btnSalva.setFocusPainted(false);
        btnAggiungiPrenotazione.setFocusPainted(false);
        btnCercaPrenotazione.setFocusPainted(false);
        btnAggiungiPiazzola.setFocusPainted(false);
        btnRimuoviPiazzola.setFocusPainted(false);
        btnSalva.setToolTipText("Salva sul drive");
        btnAggiungiPrenotazione.setToolTipText("Aggiungi prenotazione");
        btnCercaPrenotazione.setToolTipText("Cerca prenotazione");
        btnAggiungiPiazzola.setToolTipText("Aggiungi piazzola ");
        btnRimuoviPiazzola.setToolTipText("Rimuovi piazzola");

        // Azione: aggiunta di una nuova prenotazione
        btnAggiungiPrenotazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPrenotazioneDialog();
            }
        });

        // Azione: aggiungi piazzola
        btnAggiungiPiazzola.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupAggiungiPiazzola();
            }
        });

        // Azione: rimuovi piazzola
        btnRimuoviPiazzola.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setupRimuoviPiazzola();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Crea un separatore orizzontale per distanziare i bottoni dalla combobox
        Component horizontalStrut = Box.createHorizontalStrut(SEPARATOR_WIDTH);
        toolBar.add(btnSalva);
        toolBar.add(btnAggiungiPrenotazione);
        toolBar.add(btnCercaPrenotazione);
        toolBar.add(btnAggiungiPiazzola);
        toolBar.add(btnRimuoviPiazzola);
        toolBar.add(horizontalStrut);

        // Setting combobox
        cbFiltroAnni.setFocusable(false);
        toolBar.add(new JLabel("Mostra per anno: "));
        toolBar.add(cbFiltroAnni);
        ((JLabel) cbFiltroAnni.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cbFiltroAnni.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tablePrenotazioniController.refreshTable(tabellaPrenotazioni);
            }
        });

        toolBar.setFloatable(false);

        pnlToolbar.add(toolBar, BorderLayout.CENTER);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.NORTH);
    }

    // Setup form aggiungi piazzola
    private void setupAggiungiPiazzola(){
        JDialog aggiungiPiazzolaDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Aggiungi piazzola", true);
        aggiungiPiazzolaDialog.setLayout(new BorderLayout());
        aggiungiPiazzolaDialog.setLocationRelativeTo(null);
        aggiungiPiazzolaDialog.setResizable(false);

        /* Panel dedicato agli elementi del form */
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel labelNomePiazzola = new JLabel("Nome piazzola:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(labelNomePiazzola, gbc);

        JTextField tfNomePiazzola = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfNomePiazzola, gbc);
        /* --------------------------------------- */

        /* Panel dedicato ai buttons */
        JPanel pnlButtons = new JPanel(new FlowLayout());
        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnAnnulla = new JButton("Annulla");
        btnAggiungi.setFocusPainted(false);
        btnAnnulla.setFocusPainted(false);

        // Annulla -> chiude il dialog
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aggiungiPiazzolaDialog.dispose();
            }
        });

        //TODO: Aggiungi -> aggiunge la piazzola
        btnAggiungi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomePiazzola = tfNomePiazzola.getText();
                String query = "INSERT INTO Piazzole (Nome) VALUES (?)";
                try {
                    new Gateway().execUpdateQuery(query, nomePiazzola);
                    aggiungiPiazzolaDialog.dispose();
                    JOptionPane.showMessageDialog(MenuPrenotazioni.this,
                            String.format("Piazzola %s aggiunta correttamente!", nomePiazzola),
                            "Aggiunta piazzola",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MenuPrenotazioni.this,
                            "Impossibile aggiungere la piazzola",
                            "Errore aggiunta piazzola",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlButtons.add(btnAggiungi);
        pnlButtons.add(btnAnnulla);
        /* --------------------------------------- */

        aggiungiPiazzolaDialog.add(pnlForm, BorderLayout.CENTER);
        aggiungiPiazzolaDialog.add(pnlButtons, BorderLayout.SOUTH);
        aggiungiPiazzolaDialog.pack();
        aggiungiPiazzolaDialog.setVisible(true);
    }

    // Setup form rimuovi piazzola
    private void setupRimuoviPiazzola() throws SQLException {
        JDialog rimuoviPiazzolaDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Rimuovi piazzola", true);
        rimuoviPiazzolaDialog.setLayout(new BorderLayout());
        rimuoviPiazzolaDialog.setLocationRelativeTo(null);
        rimuoviPiazzolaDialog.setResizable(false);

        /* Panel dedicato agli elementi del form */
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Scegli piazzola:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(nameLabel, gbc);

        // Ricava tutte le piazzole salvate nel db
        ResultSet piazzoleRs = new Gateway().execSelectQuery("SELECT * FROM Piazzole");
        while (piazzoleRs.next()) {
            listaPiazzole.add(piazzoleRs.getString("Nome"));
        }
        piazzoleRs.close();

        JComboBox cbPiazzole = new JComboBox(listaPiazzole.toArray());
        cbPiazzole.setFocusable(false);
        ((JLabel) cbPiazzole.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(cbPiazzole, gbc);
        /* --------------------------------------- */

        /* Panel dedicato ai buttons */
        JPanel pnlButtons = new JPanel(new FlowLayout());
        JButton btnElimina = new JButton("Elimina");
        JButton btnAnnulla = new JButton("Annulla");
        btnElimina.setFocusPainted(false);
        btnAnnulla.setFocusPainted(false);

        // Annulla -> chiude il dialog
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rimuoviPiazzolaDialog.dispose();
            }
        });

        //TODO: Aggiungi -> rimuove la piazzola

        pnlButtons.add(btnElimina);
        pnlButtons.add(btnAnnulla);
        /* --------------------------------------- */

        rimuoviPiazzolaDialog.add(pnlForm, BorderLayout.CENTER);
        rimuoviPiazzolaDialog.add(pnlButtons, BorderLayout.SOUTH);
        rimuoviPiazzolaDialog.pack();
        rimuoviPiazzolaDialog.setVisible(true);
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
