package views;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import controllers.MessageController;
import controllers.TablePrenotazioniController;
import controllers.TextFieldsController;
import data_access.Gateway;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;
import utils.DataFilter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class MenuPrenotazioni extends JPanel {

    // Valori per modifiche estetiche
    private final int SEPARATOR_WIDTH = 1270;
    private final int DIALOG_SEPARATOR_WIDTH = 30;

    // Anni contenuti nella cbFiltroAnni
    private final ArrayList<String> YEARS = DataFilter.getYears();

    // Controller della tabella
    TablePrenotazioniController tablePrenotazioniController;

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolBar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnFiltraPrenotazione;
    private JButton btnSalva;
    private JButton btnAggiungiPiazzola;
    private JButton btnRimuoviPiazzola;
    private JTable tabellaPrenotazioni;
    private JComboBox cbFiltroAnni;
    private JScrollPane scrollPane;
    private JPopupMenu popupMenu;

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
        btnFiltraPrenotazione = new JButton("Filtra");
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

        // Genera il popup con il tasto destro
        tabellaPrenotazioni.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleRowClick(e);
                if (e.isPopupTrigger()) {
                    doPop(e);
                } else {
                    hidePop();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    doPop(e);
                }
            }

            private void handleRowClick(MouseEvent e) {
                ListSelectionModel selectionModel = tabellaPrenotazioni.getSelectionModel();
                Point contextMenuOpenedAt = e.getPoint();
                int clickedRow = tabellaPrenotazioni.rowAtPoint(contextMenuOpenedAt);
                int clickedColumn = tabellaPrenotazioni.columnAtPoint(contextMenuOpenedAt);

                if (clickedRow < 0 || clickedColumn < 0) {
                    // Nessuna cella selezionata
                    selectionModel.clearSelection();
                } else {
                    // Cella selezionata
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // Click destro
                        selectionModel.setSelectionInterval(clickedRow, clickedRow);
                        tabellaPrenotazioni.setColumnSelectionInterval(clickedColumn, clickedColumn);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        // Click sinistro
                        selectionModel.setSelectionInterval(clickedRow, clickedRow);
                    }
                }
            }

            private void doPop(MouseEvent e) {
                if (tabellaPrenotazioni.getSelectedRowCount() == 0) {
                    return;
                }

                // Mostra il popupMenu
                popupMenu = new JPopupMenu();
                setupPopUpMenu();
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

            private void hidePop() {
                popupMenu.setVisible(false);
            }
        });

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
        btnFiltraPrenotazione.setFocusPainted(false);
        btnAggiungiPiazzola.setFocusPainted(false);
        btnRimuoviPiazzola.setFocusPainted(false);
        btnSalva.setToolTipText("Salva sul drive");
        btnAggiungiPrenotazione.setToolTipText("Aggiungi prenotazione");
        btnFiltraPrenotazione.setToolTipText("Filtra prenotazione");
        btnAggiungiPiazzola.setToolTipText("Aggiungi piazzola ");
        btnRimuoviPiazzola.setToolTipText("Rimuovi piazzola");

        // Azione: aggiunta di una nuova prenotazione
        btnAggiungiPrenotazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addPrenotazioneDialog();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Azione: filtra prenotazioni
        btnFiltraPrenotazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    filtraPrenotazioniDialog();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
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
        toolBar.add(btnFiltraPrenotazione);
        toolBar.add(btnAggiungiPiazzola);
        toolBar.add(btnRimuoviPiazzola);
        toolBar.add(horizontalStrut);

        // Setting combobox
        cbFiltroAnni.setFocusable(false);
        toolBar.add(new JLabel("Filtra per anno: "));
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

        // Aggiungi -> aggiunge la nuova piazzola
        btnAggiungi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomePiazzola = tfNomePiazzola.getText();

                // Controllo che la piazzola sia selezionata
                if(Objects.equals(nomePiazzola, "")){
                    MessageController.getErrorMessage(aggiungiPiazzolaDialog, "Inserire il nome della piazzola");
                } else {
                    String query = "INSERT INTO Piazzole (Nome) VALUES (?)";
                    try {
                        new Gateway().execUpdateQuery(query, nomePiazzola);
                        aggiungiPiazzolaDialog.dispose();
                        MessageController.getInfoMessage(aggiungiPiazzolaDialog, String.format("Piazzola %s aggiunta correttamente!", nomePiazzola));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        MessageController.getErrorMessage(aggiungiPiazzolaDialog, "Impossibile aggiungere la piazzola");
                    }
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

        tablePrenotazioniController.setListaPiazzole();
        JComboBox cbPiazzole = new JComboBox(tablePrenotazioniController.getListaPiazzole().toArray());
        cbPiazzole.setFocusable(false);
        cbPiazzole.setSelectedItem(null);
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

        // Elimina -> elimina la piazzola
        btnElimina.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPiazzola = Objects.requireNonNull(cbPiazzole.getSelectedItem()).toString();
                String query = "DELETE FROM Piazzole WHERE Nome = ?";

                // Controllo che la piazzola sia selezionata
                if(Objects.equals(selectedPiazzola, null)){
                    MessageController.getErrorMessage(rimuoviPiazzolaDialog, "Scegliere la piazzola!");
                } else {
                    try {
                        new Gateway().execUpdateQuery(query, selectedPiazzola);
                        tablePrenotazioniController.removePiazzolaFromList(selectedPiazzola);
                        rimuoviPiazzolaDialog.dispose();

                        MessageController.getInfoMessage(rimuoviPiazzolaDialog, String.format("Piazzola %s rimossa correttamente!", selectedPiazzola));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        MessageController.getErrorMessage(rimuoviPiazzolaDialog, "Impossibile rimuovere la piazzola!");
                    }
                }
            }
        });

        pnlButtons.add(btnElimina);
        pnlButtons.add(btnAnnulla);
        /* --------------------------------------- */

        rimuoviPiazzolaDialog.add(pnlForm, BorderLayout.CENTER);
        rimuoviPiazzolaDialog.add(pnlButtons, BorderLayout.SOUTH);
        rimuoviPiazzolaDialog.pack();
        rimuoviPiazzolaDialog.setVisible(true);
    }

    // Setting dialog di aggiunta prenotazione
    private void addPrenotazioneDialog() throws SQLException {
        JDialog dialogNuovaPrenotazione = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Aggiungi nuova prenotazione", true);
        dialogNuovaPrenotazione.setLayout(new BorderLayout());
        dialogNuovaPrenotazione.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialogNuovaPrenotazione.setResizable(false);

        /* Panel dedicato agli elementi del form */
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                pnlForm.getBorder()
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Label arrivo
        JLabel lblArrivo = new JLabel("Arrivo:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblArrivo, gbc);

        // DatePicker arrivo
        DatePicker datePickerArrivo = new DatePicker();
        DatePickerSettings dateSettingsArrivo = new DatePickerSettings();
        dateSettingsArrivo.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerArrivo.setSettings(dateSettingsArrivo);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(datePickerArrivo, gbc);

        // Spaziatura orizzontale tra i datepickers
        Component horizontalStrut1 = Box.createHorizontalStrut(DIALOG_SEPARATOR_WIDTH);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlForm.add(horizontalStrut1, gbc);

        // Label partenza
        JLabel lblPartenza = new JLabel("Partenza:");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(lblPartenza, gbc);

        // DatePicker partenza
        DatePicker datePickerPartenza = new DatePicker();
        DatePickerSettings dateSettingsPartenza = new DatePickerSettings();
        dateSettingsPartenza.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerPartenza.setSettings(dateSettingsPartenza);
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(datePickerPartenza, gbc);

        // Controlla che la data di partenza sia successiva a quella di arrivo
        datePickerPartenza.addDateChangeListener((dateChangeEvent) -> {
            LocalDate partenza = dateChangeEvent.getNewDate();
            LocalDate arrivo = datePickerArrivo.getDate();

            if (arrivo != null && arrivo.isAfter(partenza)) {
                datePickerPartenza.closePopup();
                MessageController.getErrorMessage(dialogNuovaPrenotazione, "La data di partenza deve essere successiva alla data di arrivo");
                datePickerPartenza.clear();
            }
        });

        datePickerArrivo.addDateChangeListener((dateChangeEvent) -> {
            LocalDate arrivo = dateChangeEvent.getNewDate();
            LocalDate partenza = datePickerPartenza.getDate();

            if (partenza != null && partenza.isBefore(arrivo)) {
                datePickerArrivo.closePopup();
                MessageController.getErrorMessage(dialogNuovaPrenotazione, "La data di partenza deve essere successiva alla data di arrivo");
                datePickerArrivo.clear();
            }
        });

        // Label scelta piazzola
        JLabel lblPiazzola = new JLabel("Piazzola:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblPiazzola, gbc);

        // ComboBox scelta piazzola
        tablePrenotazioniController.setListaPiazzole();
        JComboBox cbSceltaPiazzola = new JComboBox<>(tablePrenotazioniController.getListaPiazzole().toArray());
        cbSceltaPiazzola.setPreferredSize(datePickerArrivo.getPreferredSize());
        cbSceltaPiazzola.setFocusable(false);
        cbSceltaPiazzola.setSelectedItem(null);
        ((JLabel) cbSceltaPiazzola.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(cbSceltaPiazzola, gbc);

        // Spazio
        Component horizontalStrut2 = Box.createHorizontalStrut(DIALOG_SEPARATOR_WIDTH);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlForm.add(horizontalStrut2, gbc);

        // Label nome della prenotazione
        JLabel lblNome = new JLabel("Nome:");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblNome, gbc);

        // TextField nome della prenotazione
        JTextField tfNome = new JTextField();
        tfNome.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfNome, gbc);

        // Label info
        JLabel lblInfo = new JLabel("Info:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblInfo, gbc);

        // TextField info
        JTextField tfInfo = new JTextField();
        tfInfo.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfInfo, gbc);

        // Label acconto
        JLabel lblAcconto = new JLabel("Acconto:");
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblAcconto, gbc);

        // TextField acconto
        JTextField tfAcconto = new JTextField();
        tfAcconto.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfAcconto, gbc);

        // Label telefono
        JLabel lblTelefono = new JLabel("Telefono:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblTelefono, gbc);

        // TextField telefono
        JTextField tfTelefono = new JTextField();
        tfTelefono.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfTelefono, gbc);

        // Label Email
        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblEmail, gbc);

        // TextField Email
        JTextField tfEmail = new JTextField();
        tfEmail.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfEmail, gbc);

        // Imposta i vincoli sulle textFields
        TextFieldsController.setupTextFieldsInteger(tfTelefono);
        TextFieldsController.setupTextFieldsFloat(tfAcconto);
        TextFieldsController.setupTextFieldsString(tfNome);
        /* --------------------------------------- */

        /* Panel dedicato ai buttons */
        JPanel pnlButtons = new JPanel(new FlowLayout());
        JButton btnAggiungiPrenotazioneDialog = new JButton("Aggiungi");
        JButton btnAnnulla = new JButton("Annulla");
        btnAggiungiPrenotazioneDialog.setFocusPainted(false);
        btnAnnulla.setFocusPainted(false);

        // Annulla -> chiude il dialog
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogNuovaPrenotazione.dispose();
            }
        });

        // Aggiungi -> aggiunge la prenotazione nel db
        btnAggiungiPrenotazioneDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Controllo sulle date non nulle
                if ((!datePickerArrivo.getText().isEmpty() && datePickerPartenza.getText().isEmpty()) ||
                    (datePickerArrivo.getText().isEmpty() && !datePickerPartenza.getText().isEmpty())) {
                    MessageController.getErrorMessage(dialogNuovaPrenotazione,"Inserire entrambe le date!");

                // Controllo di aver inserito i valori obbligatori (nome, piazzola)
                } else if (tfNome.getText().isEmpty()) {
                    MessageController.getErrorMessage(dialogNuovaPrenotazione, "Inserire il nome!");
                } else if(cbSceltaPiazzola.getSelectedItem() == null) {
                    MessageController.getErrorMessage(dialogNuovaPrenotazione, "Inserire la piazzola!");
                }

                // Ricavo tutte le info inserite
                String nomePrenotazione = "";
                String piazzolaScelta = "";
                String dataArrivo = "";
                String dataPartenza = "";
                String info = "";
                String telefono = "";
                String email = "";
                String acconto = "";
                if(!Objects.equals(tfNome.getText(), ""))
                    nomePrenotazione = tfNome.getText();
                if(!Objects.equals(cbSceltaPiazzola.getSelectedItem().toString(), ""))
                    piazzolaScelta = cbSceltaPiazzola.getSelectedItem().toString();
                if(!Objects.equals(datePickerArrivo.getText(), ""))
                    dataArrivo = datePickerArrivo.getText();
                if(!Objects.equals(datePickerPartenza.getText(), ""))
                    dataPartenza = datePickerPartenza.getText();
                if(!Objects.equals(tfInfo.getText(), ""))
                    info = tfInfo.getText();
                if(!Objects.equals(tfTelefono.getText(), ""))
                    telefono = tfTelefono.getText();
                if(!Objects.equals(tfEmail.getText(), ""))
                    email = tfEmail.getText();
                if(!Objects.equals(tfAcconto.getText(), ""))
                    acconto = tfAcconto.getText();

                // Controllo che non ci siano già altre prenotazioni nelle date scelte per quella piazzola!
                try {
                    if(tablePrenotazioniController.isAlreadyBooked(dataArrivo, dataPartenza, piazzolaScelta)){
                        MessageController.getErrorMessage(dialogNuovaPrenotazione, String.format("La piazzola %s è già prenotata per le date selezionate", piazzolaScelta));
                        datePickerArrivo.setText("");
                        datePickerPartenza.setText("");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Eseguo la query di inserimento della prenotazione
                String query = "INSERT INTO Prenotazioni (Piazzola, Arrivo, Partenza, Nome, Acconto, Info, Telefono, Email) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

                try {
                    if(!Objects.equals(acconto, ""))
                        new Gateway().execUpdateQuery(query, piazzolaScelta, dataArrivo, dataPartenza, nomePrenotazione, "€ " + acconto, info, telefono, email);
                    else
                        new Gateway().execUpdateQuery(query, piazzolaScelta, dataArrivo, dataPartenza, nomePrenotazione, null, info, telefono, email);

                    // Inserisce le info nella tabella SaldoAcconti
                    if (!Objects.equals(acconto, "")) {
                        String insertSaldoQuery = "INSERT INTO SaldoAcconti (Nome, Arrivo, Partenza, Acconto, Saldato) VALUES (?, ?, ?, ?, 'non saldato')";
                        new Gateway().execUpdateQuery(insertSaldoQuery, nomePrenotazione, dataArrivo, dataPartenza, "€ " + acconto);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Ricarico la tabella prenotazioni
                tablePrenotazioniController.refreshTable(tabellaPrenotazioni);

                // Controlla che la nuova prenotazione sia stata inserita
                String checkQuery = "SELECT * FROM Prenotazioni WHERE Nome = ? AND Piazzola = ? AND Arrivo = ? AND Partenza = ?";

                try {
                    if(new Gateway().execSelectQuery(checkQuery) != null) {
                        MessageController.getInfoMessage(dialogNuovaPrenotazione, "Prenotazione aggiunta");
                    } else {
                        MessageController.getErrorMessage(dialogNuovaPrenotazione, "Impossibile inserire la nuova prenotazione");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                dialogNuovaPrenotazione.dispose();
            }
        });

        pnlButtons.add(btnAggiungiPrenotazioneDialog, CENTER_ALIGNMENT);
        pnlButtons.add(btnAnnulla, CENTER_ALIGNMENT);
        /* --------------------------------------- */

        dialogNuovaPrenotazione.add(pnlForm, BorderLayout.CENTER);
        dialogNuovaPrenotazione.add(pnlButtons, BorderLayout.SOUTH);
        dialogNuovaPrenotazione.pack();
        dialogNuovaPrenotazione.setVisible(true);
    }

    // Setting dialog filtraggio delle prenotazioni
    private void filtraPrenotazioniDialog() throws SQLException {
        JDialog dialogFiltraPrenotazione = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Filtra le prenotazioni", true);
        dialogFiltraPrenotazione.setLayout(new BorderLayout());
        dialogFiltraPrenotazione.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialogFiltraPrenotazione.setResizable(false);

        /* Panel scelta corrispondenze */
        JPanel pnlCorrispondenze = new JPanel(new FlowLayout());
        Border pnlBorder = BorderFactory.createTitledBorder("Filtra corrispondenze");
        pnlCorrispondenze.setBorder(pnlBorder);
        JLabel lblTutteCorrispondenze = new JLabel("TUTTI i valori inseriti");
        JLabel lblAlcuneCorrispondenze = new JLabel("ALMENO un valore inserito");
        JRadioButton rbTutteCorrispondenze = new JRadioButton();
        JRadioButton rbAlcuneCorrispondenze = new JRadioButton();

        // Di default si vuole filtrare per tutti i valori inseriti
        rbTutteCorrispondenze.setSelected(true);

        // Gestisce la mutua esclusione dei radiobutton
        rbTutteCorrispondenze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rbAlcuneCorrispondenze.setSelected(false);
            }
        });
        rbAlcuneCorrispondenze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rbTutteCorrispondenze.setSelected(false);
            }
        });

        pnlCorrispondenze.add(lblTutteCorrispondenze);
        pnlCorrispondenze.add(rbTutteCorrispondenze);
        pnlCorrispondenze.add(Box.createHorizontalStrut(DIALOG_SEPARATOR_WIDTH));
        pnlCorrispondenze.add(lblAlcuneCorrispondenze);
        pnlCorrispondenze.add(rbAlcuneCorrispondenze);

        pnlCorrispondenze.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                pnlCorrispondenze.getBorder()
        ));
        /* --------------------------------------- */


        /* Panel dedicato agli elementi del form */
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Label arrivo
        JLabel lblArrivo = new JLabel("Arrivo:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblArrivo, gbc);

        // DatePicker arrivo
        DatePicker datePickerArrivo = new DatePicker();
        DatePickerSettings dateSettingsArrivo = new DatePickerSettings();
        dateSettingsArrivo.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerArrivo.setSettings(dateSettingsArrivo);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(datePickerArrivo, gbc);

        // Spaziatura orizzontale tra i datepickers
        Component horizontalStrut1 = Box.createHorizontalStrut(DIALOG_SEPARATOR_WIDTH);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlForm.add(horizontalStrut1, gbc);

        // Label partenza
        JLabel lblPartenza = new JLabel("Partenza:");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(lblPartenza, gbc);

        // DatePicker partenza
        DatePicker datePickerPartenza = new DatePicker();
        DatePickerSettings dateSettingsPartenza = new DatePickerSettings();
        dateSettingsPartenza.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerPartenza.setSettings(dateSettingsPartenza);
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(datePickerPartenza, gbc);

        // Controlla che la data di partenza sia successiva a quella di arrivo
        datePickerPartenza.addDateChangeListener((dateChangeEvent) -> {
            LocalDate partenza = dateChangeEvent.getNewDate();
            LocalDate arrivo = datePickerArrivo.getDate();

            if (arrivo != null && arrivo.isAfter(partenza)) {
                datePickerPartenza.closePopup();
                MessageController.getErrorMessage(dialogFiltraPrenotazione, "La data di partenza deve essere successiva alla data di arrivo");
                datePickerPartenza.clear();
            }
        });

        datePickerArrivo.addDateChangeListener((dateChangeEvent) -> {
            LocalDate arrivo = dateChangeEvent.getNewDate();
            LocalDate partenza = datePickerPartenza.getDate();

            if (partenza != null && partenza.isBefore(arrivo)) {
                datePickerArrivo.closePopup();
                MessageController.getErrorMessage(dialogFiltraPrenotazione, "La data di partenza deve essere successiva alla data di arrivo");
                datePickerArrivo.clear();
            }
        });

        // Label scelta piazzola
        JLabel lblPiazzola = new JLabel("Piazzola:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblPiazzola, gbc);

        // ComboBox scelta piazzola
        tablePrenotazioniController.setListaPiazzole();
        JComboBox cbSceltaPiazzola = new JComboBox<>(tablePrenotazioniController.getListaPiazzole().toArray());
        cbSceltaPiazzola.setPreferredSize(datePickerArrivo.getPreferredSize());
        cbSceltaPiazzola.setFocusable(false);
        cbSceltaPiazzola.setSelectedItem(null);
        ((JLabel) cbSceltaPiazzola.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(cbSceltaPiazzola, gbc);

        // Spazio
        Component horizontalStrut2 = Box.createHorizontalStrut(DIALOG_SEPARATOR_WIDTH);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlForm.add(horizontalStrut2, gbc);

        // Label nome della prenotazione
        JLabel lblNome = new JLabel("Nome:");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblNome, gbc);

        //TODO: COMPLETER TextField nome della prenotazione
        JTextField tfNome = new JTextField();
        AutoCompleteDecorator.decorate(tfNome, tablePrenotazioniController.getAllNames(), false, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);
        tfNome.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfNome, gbc);

        // Label info
        JLabel lblInfo = new JLabel("Info:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblInfo, gbc);

        // TextField info
        JTextField tfInfo = new JTextField();
        tfInfo.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfInfo, gbc);

        // Label acconto
        JLabel lblAcconto = new JLabel("Acconto:");
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblAcconto, gbc);

        // TextField acconto
        JTextField tfAcconto = new JTextField();
        tfAcconto.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfAcconto, gbc);

        // Label telefono
        JLabel lblTelefono = new JLabel("Telefono:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblTelefono, gbc);

        // TextField telefono
        JTextField tfTelefono = new JTextField();
        tfTelefono.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfTelefono, gbc);

        // Label Email
        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblEmail, gbc);

        // TextField Email
        JTextField tfEmail = new JTextField();
        tfEmail.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfEmail, gbc);

        // Imposta i vincoli sulle textFields
        TextFieldsController.setupTextFieldsInteger(tfTelefono);
        TextFieldsController.setupTextFieldsFloat(tfAcconto);
        TextFieldsController.setupTextFieldsString(tfNome);
        /* --------------------------------------- */

        /* Panel dedicato ai buttons */
        JPanel pnlButtons = new JPanel(new FlowLayout());
        JButton btnFiltraPrenotazioni = new JButton("Filtra");
        JButton btnAnnulla = new JButton("Annulla");
        btnFiltraPrenotazioni.setFocusPainted(false);
        btnAnnulla.setFocusPainted(false);

        // Annulla -> chiude il dialog
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogFiltraPrenotazione.dispose();
            }
        });

        //TODO: Filtra -> applica la query filtrata (TUTTI oppure ALCUNI valori) e aggiorna la vista -> deve apparire un flag visivo che la tabella è filtrata
        // e quindi deve esserci anche un tasto che elimina il filtro!!

        pnlButtons.add(btnFiltraPrenotazioni, CENTER_ALIGNMENT);
        pnlButtons.add(btnAnnulla, CENTER_ALIGNMENT);
        /* --------------------------------------- */

        dialogFiltraPrenotazione.add(pnlCorrispondenze, BorderLayout.NORTH);
        dialogFiltraPrenotazione.add(pnlForm, BorderLayout.CENTER);
        dialogFiltraPrenotazione.add(pnlButtons, BorderLayout.SOUTH);
        dialogFiltraPrenotazione.pack();
        dialogFiltraPrenotazione.setVisible(true);
    }

    // Setup popupMenu sulla tabella
    private void setupPopUpMenu(){
        JMenuItem rimuoviItem = new JMenuItem("Rimuovi");
        JMenuItem saldaAccontoItem = new JMenuItem("Salda acconto");

        int selectedRow = tabellaPrenotazioni.getSelectedRow();
        int accontoIndex = 4;  //Indice colonna acconto

        if (selectedRow >= 0 && tablePrenotazioniController.isAcconto(tabellaPrenotazioni.getValueAt(selectedRow, accontoIndex))) {
            // Se è presente l'acconto mostra entrambi i menu
            popupMenu.add(saldaAccontoItem);
            popupMenu.add(rimuoviItem);
        } else {
            // Mostra solo il menu rimuoviItem se non è presente l'acconto
            popupMenu.add(rimuoviItem);
        }

        // Azione: rimuove la riga selezionata
        rimuoviItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabellaPrenotazioni.getSelectedRow();
                if (selectedRow < 0) {
                    return;
                }

                try {
                    String deleteQuery = "DELETE FROM Prenotazioni WHERE Piazzola = ? AND " +
                            "Arrivo = ? AND " +
                            "Partenza = ? AND " +
                            "Nome = ? AND " +
                            "(Acconto = ? OR Acconto IS NULL) AND " +
                            "Info = ? AND " +
                            "Telefono = ? AND " +
                            "Email = ?";

                    // Ottengo i valori dalla riga selezionata
                    String piazzola = (String) tabellaPrenotazioni.getValueAt(selectedRow, 0);
                    String arrivo = (String) tabellaPrenotazioni.getValueAt(selectedRow, 1);
                    String partenza = (String) tabellaPrenotazioni.getValueAt(selectedRow, 2);
                    String nome = (String) tabellaPrenotazioni.getValueAt(selectedRow, 3);
                    String acconto = (String) tabellaPrenotazioni.getValueAt(selectedRow, 4);
                    String info = (String) tabellaPrenotazioni.getValueAt(selectedRow, 5);
                    String telefono = (String) tabellaPrenotazioni.getValueAt(selectedRow, 6);
                    String email = (String) tabellaPrenotazioni.getValueAt(selectedRow, 7);

                    // Eseguo la query di eliminazione
                    new Gateway().execUpdateQuery(deleteQuery, piazzola, arrivo, partenza, nome, acconto, info, telefono, email);
                    tablePrenotazioniController.getListaNomi().remove(nome);

                    // Elimino anche dalla tabella SaldoAcconti
                    String deleteSaldoAccontiQuery = "DELETE FROM SaldoAcconti WHERE Nome = ? AND Arrivo = ? AND Partenza = ? AND Acconto = ?";
                    new Gateway().execUpdateQuery(deleteSaldoAccontiQuery, nome, arrivo, partenza, acconto);

                    // Aggiorno la tabella dopo l'eliminazione
                    tablePrenotazioniController.refreshTable(tabellaPrenotazioni);
                } catch (SQLException ex) {
                    MessageController.getErrorMessage(MenuPrenotazioni.this, "Errore durante l'eliminazione della riga: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Azione: cambia il colore dell'acconto
        saldaAccontoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Ricavo i valori dell'acconto selezionato dalla tabella SaldoAcconti
                ArrayList<String> valoriAcconto = new ArrayList<>();
                for(int i = 0; i < tabellaPrenotazioni.getColumnCount(); i++){
                    valoriAcconto.add((String) tabellaPrenotazioni.getValueAt(selectedRow, i));
                }

                String arrivo = valoriAcconto.get(1);
                String partenza = valoriAcconto.get(2);
                String nome = valoriAcconto.get(3);
                String acconto = valoriAcconto.get(4);

                // Imposto l'acconto saldato sul database
                String updateAcconto = "UPDATE SaldoAcconti SET Saldato = ? WHERE Nome = ? AND Arrivo = ? AND Partenza = ? AND Acconto = ?";
                try {
                    System.out.println(new Gateway().execUpdateQuery(updateAcconto, "saldato", nome, arrivo, partenza, acconto));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Aggiorna anche il colore della cella dell'acconto
                int accontoColumnIndex = 4; // L'indice della colonna dell'acconto (partendo da 0)
                TableCellRenderer renderer = tabellaPrenotazioni.getCellRenderer(selectedRow, accontoColumnIndex);
                Component component = tabellaPrenotazioni.prepareRenderer(renderer, selectedRow, accontoColumnIndex);
                component.setForeground(Color.green);

                // Ricarico la visualizzazione
                tabellaPrenotazioni.repaint(selectedRow);
            }
        });
    }
}
