package views;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import utils.TableConstants;
import view_controllers.*;
import data_access.CloudUploader;
import data_access.Gateway;
import observer.StopTableEditObservers;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;
import utils.CustomCellEditorPrenotazioni;
import utils.TimeManager;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import observer.PrenotazioniObservers;
import utils.ListOfNations;

public class MenuPrenotazioni extends JPanel implements StopTableEditObservers {

    // Valori per modifiche estetiche
    private final int SEPARATOR_WIDTH = 1270;
    private final int DIALOG_SEPARATOR_WIDTH = 30;

    // Anni contenuti nella cbFiltroAnni
    private final ArrayList<String> YEARS = TimeManager.getPrenotazioniYears();

    // Controller della tabella
    TablePrenotazioniController tablePrenotazioniController;

    // Lista dei controller observer di MenuCalenario e MenuArriviPartenze
    private static ArrayList<PrenotazioniObservers> prenotazioniObserversList = new ArrayList<>();

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolBar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnFiltraPrenotazione;
    private JButton btnSalva;
    private JButton btnImportaDrive;
    private JButton btnAggiungiPiazzola;
    private JButton btnRimuoviPiazzola;
    private JTable tabellaPrenotazioni;
    private JLabel lblFiltro;
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
        btnImportaDrive = new JButton("Importa");
        btnAggiungiPrenotazione = new JButton("Aggiungi");
        btnFiltraPrenotazione = new JButton("Filtra");
        btnAggiungiPiazzola = new JButton("Aggiungi piazzola");
        btnRimuoviPiazzola = new JButton("Rimuovi piazzola");

        // ComboBox filtraggio anni
        lblFiltro = new JLabel("Filtra per anno: ");
        cbFiltroAnni = new JComboBox(YEARS.toArray());

        // Mostra la query in base al valore della comboBox
        cbFiltroAnni.setSelectedItem("Tutto");
        tabellaPrenotazioni = tablePrenotazioniController.initView(cbFiltroAnni, null);

    }

    // Setup della tabella delle prenotazioni
    private void setupTable() {

        tabellaPrenotazioni.getTableHeader().setReorderingAllowed(false);
        tabellaPrenotazioni.setCellSelectionEnabled(false);
        tabellaPrenotazioni.setRowSelectionAllowed(true);
        tabellaPrenotazioni.setDefaultEditor(Object.class, null);
        tabellaPrenotazioni.removeColumn(tabellaPrenotazioni.getColumnModel().getColumn(0));
        tabellaPrenotazioni.setGridColor(Color.BLACK);

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
                if(popupMenu != null)
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

        //Azione: doppio click sulla cella ne seleziona il contenuto
        tabellaPrenotazioni.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateTableSettings(e);
            }
        });

        // Quando si aggiunge una nuova prenotazione si scorre sempre verso l'ultima
        tabellaPrenotazioni.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int lastIndex = tabellaPrenotazioni.getRowCount() - 1;
                tabellaPrenotazioni.changeSelection(lastIndex, 0,false,false);
            }
        });

        scrollPane = new JScrollPane(tabellaPrenotazioni);
        mainPanelPrenotazioni.add(scrollPane, BorderLayout.CENTER);
    }

    // Setup toolbar
    private void setupToolbar() {

        // Layout toolbar
        pnlToolbar.setLayout(new BorderLayout());

        // Crea il pannello per i bottoni
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnSalva);
        buttonPanel.add(btnImportaDrive);
        buttonPanel.add(btnAggiungiPrenotazione);
        buttonPanel.add(btnFiltraPrenotazione);
        toolBar.add(buttonPanel, BorderLayout.WEST);

        // Panel spazio orizzontale
        JPanel pnlHorizontalStrut = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHorizontalStrut.setPreferredSize(new Dimension(SEPARATOR_WIDTH, 1));
        toolBar.add(pnlHorizontalStrut, BorderLayout.CENTER);

        // Panel comboBox filtro anni + bottoni piazzole
        JPanel pnlFiltroAnni = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFiltroAnni.add(btnAggiungiPiazzola);
        pnlFiltroAnni.add(btnRimuoviPiazzola);
        pnlFiltroAnni.add(lblFiltro);
        pnlFiltroAnni.add(cbFiltroAnni);
        toolBar.add(pnlFiltroAnni, BorderLayout.EAST);

        // Setting buttons
        btnSalva.setFocusPainted(false);
        btnImportaDrive.setFocusPainted(false);
        btnAggiungiPrenotazione.setFocusPainted(false);
        btnFiltraPrenotazione.setFocusPainted(false);
        btnAggiungiPiazzola.setFocusPainted(false);
        btnRimuoviPiazzola.setFocusPainted(false);
        btnSalva.setToolTipText("Salva sul drive");
        btnImportaDrive.setToolTipText("Importa prenotazioni dal backup");
        btnAggiungiPrenotazione.setToolTipText("Aggiungi prenotazione");
        btnFiltraPrenotazione.setToolTipText("Filtra prenotazione");
        btnAggiungiPiazzola.setToolTipText("Aggiungi piazzola ");
        btnRimuoviPiazzola.setToolTipText("Rimuovi piazzola");

        // Setting combobox
        cbFiltroAnni.setFocusable(false);
        ((JLabel) cbFiltroAnni.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Azione: salvataggio del database sul drive
        btnSalva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmResult = JOptionPane.showConfirmDialog(MenuPrenotazioni.this,
                        "Salvare le prenotazioni sul drive?",
                        "Salva prenotazioni",
                        JOptionPane.YES_NO_OPTION);

                if(confirmResult == JOptionPane.YES_OPTION){
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    setupSalva();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        // Azione: importa il database dal backup sul drive
        btnImportaDrive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int confirmResult = JOptionPane.showConfirmDialog(MenuPrenotazioni.this,
                        "Importare l'ultimo backup eseguito?",
                        "Importa backup",
                        JOptionPane.YES_NO_OPTION);

                if(confirmResult == JOptionPane.YES_OPTION){
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    // Si connette all'ultima versione del database importato dal drive
                    boolean result = false;
                    try {
                        result = CloudUploader.importFileFromDrive("database.db");
                    } catch (IOException | GeneralSecurityException ex) {
                        MessageController.getErrorMessage(null, "Errore: " + ex.getMessage());
                    }

                    // Ricarica la vista della tabella
                    tablePrenotazioniController.getGateway().connect();
                    tablePrenotazioniController.refreshTable(tabellaPrenotazioni);

                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    if(result)
                        MessageController.getInfoMessage(null, "Backup importato correttamente!");
                    else
                        MessageController.getErrorMessage(null, "Non sono presenti backup");
                    try {
                        notifyPrenotazioneChanged();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

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

        // Azione: cambia anni nel filtro per la visualizzazione
        cbFiltroAnni.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Reimposta la label lblFiltro se è applicato il filtro e si rimuove scegliendo un valore della cb
                lblFiltro.setText("FIltra per anno: ");
                lblFiltro.setForeground(Color.black);

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

                        // Notifica aggiunta piazzola
                        notifyPiazzolaChanged();

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
        aggiungiPiazzolaDialog.setLocationRelativeTo(null);
        aggiungiPiazzolaDialog.setVisible(true);
    }

    // Setup form rimuovi piazzola
    private void setupRimuoviPiazzola() throws SQLException {
        JDialog rimuoviPiazzolaDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Rimuovi piazzola", true);
        rimuoviPiazzolaDialog.setLayout(new BorderLayout());

        /* Panel dedicato agli elementi del form */
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Scegli piazzola:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(nameLabel, gbc);

        ControllerPiazzole.setListaPiazzole();
        JComboBox cbPiazzole = new JComboBox(ControllerPiazzole.getListaPiazzole().toArray());
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
                        ControllerPiazzole.removePiazzolaFromList(selectedPiazzola);

                        // Notifica della rimozione della piazzola
                        notifyPiazzolaChanged();

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
        rimuoviPiazzolaDialog.setLocationRelativeTo(null);
        rimuoviPiazzolaDialog.setResizable(false);
        rimuoviPiazzolaDialog.setVisible(true);
    }

    // Setting dialog di aggiunta prenotazione
    private void addPrenotazioneDialog() throws SQLException {
        JDialog dialogNuovaPrenotazione = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuPrenotazioni.this), "Aggiungi nuova prenotazione", true);
        dialogNuovaPrenotazione.setLayout(new BorderLayout());

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

            if(arrivo != null && partenza != null)
                ControllerDatePrenotazioni.checkOrdineDate(arrivo, arrivo.isAfter(partenza), datePickerPartenza, dialogNuovaPrenotazione);
        });

        datePickerArrivo.addDateChangeListener((dateChangeEvent) -> {
            LocalDate arrivo = dateChangeEvent.getNewDate();
            LocalDate partenza = datePickerPartenza.getDate();

            if(partenza != null && arrivo != null)
                ControllerDatePrenotazioni.checkOrdineDate(partenza, partenza.isBefore(arrivo), datePickerArrivo, dialogNuovaPrenotazione);
        });

        // Label scelta piazzola
        JLabel lblPiazzola = new JLabel("Piazzola:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblPiazzola, gbc);

        // ComboBox scelta piazzola
        ControllerPiazzole.setListaPiazzole();
        JComboBox cbSceltaPiazzola = new JComboBox<>(ControllerPiazzole.getListaPiazzole().toArray());
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

        // Label numero persone
        JLabel lblNPersone = new JLabel("N° Persone:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblNPersone, gbc);

        // TextField numero persone
        JTextField tfNPersone = new JTextField();
        tfNPersone.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfNPersone, gbc);

        // Label mezzo
        JLabel lblMezzo = new JLabel("Mezzo:");
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblMezzo, gbc);

        // ComboBox mezzo
        JComboBox<String> cbMezzo = new JComboBox(TableConstants.listaMezzi.toArray());
        cbMezzo.setPreferredSize(datePickerArrivo.getPreferredSize());
        cbMezzo.setFocusable(false);
        cbMezzo.setSelectedItem(null);
        ((JLabel) cbMezzo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(cbMezzo, gbc);

        // Label nazione
        JLabel lblNazione = new JLabel("Nazione:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblNazione, gbc);

        // TextField nazione
        JTextField tfNazione = new JTextField();
        tfNazione.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfNazione, gbc);

        // Imposta i vincoli sulle textFields
        TextFieldsController.setupTextFieldsInteger(tfTelefono);
        TextFieldsController.setupTextFieldsFloat(tfAcconto);
        TextFieldsController.setupTextFieldsString(tfNome);
        TextFieldsController.setupTextFieldsInteger(tfNPersone);

        // Implementa il completer per le nazioni
        AutoCompleteDecorator.decorate(tfNazione, ListOfNations.getListaNazioni(), false, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);
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

                // Controllo di aver inserito i valori obbligatori (nome, piazzola, mezzo, numero persone, nazione)
                } else if (tfNome.getText().isEmpty()) {
                    MessageController.getErrorMessage(dialogNuovaPrenotazione, "Inserire il nome!");
                    return;
                } else if(cbSceltaPiazzola.getSelectedItem() == null) {
                    MessageController.getErrorMessage(dialogNuovaPrenotazione, "Inserire la piazzola!");
                    return;
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
                String nPersone = "";
                String mezzo = "";
                String nazione = "";
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
                if(!Objects.equals(tfNPersone.getText(), ""))
                    nPersone = tfNPersone.getText();
                if(cbMezzo.getSelectedItem() != null)
                    mezzo = cbMezzo.getSelectedItem().toString();
                else
                    mezzo = "";
                if(!Objects.equals(tfNazione.getText(), ""))
                    nazione = tfNazione.getText();

                // Controllo che non ci siano già altre prenotazioni nelle date scelte per quella piazzola!
                try {
                    if(ControllerDatePrenotazioni.isAlreadyBooked(dataArrivo, dataPartenza, piazzolaScelta, null)){
                        MessageController.getErrorMessage(dialogNuovaPrenotazione, String.format("La piazzola %s è già prenotata per le date selezionate", piazzolaScelta));
                        datePickerArrivo.setText("");
                        datePickerPartenza.setText("");
                        return;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Eseguo la query di inserimento della prenotazione
                String query = "INSERT INTO Prenotazioni (Piazzola, Arrivo, Partenza, Nome, Acconto, Info, Telefono, Email, Persone, Mezzo, Nazione) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

                try {
                    if(!Objects.equals(acconto, ""))
                        new Gateway().execUpdateQuery(query, piazzolaScelta, dataArrivo, dataPartenza, nomePrenotazione, "€ " + acconto, info, telefono, email, nPersone, mezzo, nazione);
                    else
                        new Gateway().execUpdateQuery(query, piazzolaScelta, dataArrivo, dataPartenza, nomePrenotazione, null, info, telefono, email, nPersone, mezzo, nazione);

                    // Inserisce le info nella tabella SaldoAcconti
                    if (!Objects.equals(acconto, "")) {
                        String insertSaldoQuery = "INSERT INTO SaldoAcconti (Nome, Arrivo, Partenza, Acconto, Saldato) VALUES (?, ?, ?, ?, 'non saldato')";
                        new Gateway().execUpdateQuery(insertSaldoQuery, nomePrenotazione, dataArrivo, dataPartenza, "€ " + acconto);
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Ricarico la tabella prenotazioni e notifico gli observers
                tablePrenotazioniController.refreshTable(tabellaPrenotazioni);
                try {
                    notifyPrenotazioneChanged();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Controlla che la nuova prenotazione sia stata inserita
                String checkQuery = "SELECT * FROM Prenotazioni WHERE Nome = ? AND Piazzola = ? AND Arrivo = ? AND Partenza = ?";

                try {
                    if(new Gateway().execSelectQuery(checkQuery) != null) {
                        dialogNuovaPrenotazione.dispose();
                        MessageController.getInfoMessage(MenuPrenotazioni.this, "Prenotazione aggiunta");

                        // Inserisce la prenotazione nella tabella ArriviPartenze
                        String insertArriviPartenze = "INSERT INTO ArriviPartenze (Id, Arrivo, Partenza, Nome, Arrivato, Partito) VALUES (?, ?, ?, ?, ?, ?);";

                        // Ricavo l'id della prenotazione appena inserita
                        String id = "";
                        if (tabellaPrenotazioni.getRowCount() > 0) {
                            int lastRowIndex = tabellaPrenotazioni.getRowCount() - 1;
                            Object idValue = tabellaPrenotazioni.getModel().getValueAt(lastRowIndex, 0);
                            if (idValue != null) {
                                id = idValue.toString();
                            }
                        }
                        new Gateway().execUpdateQuery(insertArriviPartenze, id, dataArrivo, dataPartenza, nomePrenotazione, "no", "no");

                    } else {
                        dialogNuovaPrenotazione.dispose();
                        MessageController.getErrorMessage(MenuPrenotazioni.this, "Impossibile inserire la nuova prenotazione");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        pnlButtons.add(btnAggiungiPrenotazioneDialog, CENTER_ALIGNMENT);
        pnlButtons.add(btnAnnulla, CENTER_ALIGNMENT);
        /* --------------------------------------- */

        dialogNuovaPrenotazione.add(pnlForm, BorderLayout.CENTER);
        dialogNuovaPrenotazione.add(pnlButtons, BorderLayout.SOUTH);
        dialogNuovaPrenotazione.pack();
        dialogNuovaPrenotazione.setLocationRelativeTo(null);
        dialogNuovaPrenotazione.setResizable(false);
        dialogNuovaPrenotazione.setVisible(true);
    }

    // Setting dialog filtraggio delle prenotazioni
    private void filtraPrenotazioniDialog() throws SQLException {
        JDialog dialogFiltraPrenotazione = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Filtra le prenotazioni", true);
        dialogFiltraPrenotazione.setLayout(new BorderLayout());

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

            if(arrivo != null && partenza != null)
                ControllerDatePrenotazioni.checkOrdineDate(arrivo, arrivo.isAfter(partenza), datePickerPartenza, dialogFiltraPrenotazione);
        });

        datePickerArrivo.addDateChangeListener((dateChangeEvent) -> {
            LocalDate arrivo = dateChangeEvent.getNewDate();
            LocalDate partenza = datePickerPartenza.getDate();

            if(partenza != null && arrivo != null)
                ControllerDatePrenotazioni.checkOrdineDate(partenza, partenza.isBefore(arrivo), datePickerArrivo, dialogFiltraPrenotazione);
        });

        // Label scelta piazzola
        JLabel lblPiazzola = new JLabel("Piazzola:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblPiazzola, gbc);

        // ComboBox scelta piazzola
        ControllerPiazzole.setListaPiazzole();
        JComboBox cbSceltaPiazzola = new JComboBox<>(ControllerPiazzole.getListaPiazzole().toArray());
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

        // Label mezzo
        JLabel lblMezzo = new JLabel("Mezzo:");
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblMezzo, gbc);

        // ComboBox mezzo
        JComboBox<String> cbMezzo = new JComboBox(TableConstants.listaMezzi.toArray());
        cbMezzo.setPreferredSize(datePickerArrivo.getPreferredSize());
        cbMezzo.setFocusable(false);
        cbMezzo.setSelectedItem(null);
        ((JLabel) cbMezzo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(cbMezzo, gbc);

        // Label nazione
        JLabel lblNazione = new JLabel("Nazione:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblNazione, gbc);

        // TextField nazione
        JTextField tfNazione = new JTextField();
        tfNazione.setPreferredSize(datePickerArrivo.getPreferredSize());
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(tfNazione, gbc);

        // Imposta i vincoli sulle textFields
        TextFieldsController.setupTextFieldsInteger(tfTelefono);
        TextFieldsController.setupTextFieldsFloat(tfAcconto);
        TextFieldsController.setupTextFieldsString(tfNome);

        // Implementa il completer per le nazioni
        AutoCompleteDecorator.decorate(tfNazione, ListOfNations.getListaNazioni(), false, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);

        /* Panel dedicato ai buttons */
        JPanel pnlButtons = new JPanel(new FlowLayout());
        JButton btnFiltra = new JButton("Filtra");
        JButton btnAnnulla = new JButton("Annulla");
        btnFiltra.setFocusPainted(false);
        btnAnnulla.setFocusPainted(false);

        // Annulla -> chiude il dialog
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogFiltraPrenotazione.dispose();
            }
        });

        // Filtra -> applica il filtro alla visualizzazione della tabella
        btnFiltra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Ricavo tutte le info inserite
                String nomePrenotazione = "";
                String piazzolaScelta = "";
                String dataArrivo = "";
                String dataPartenza = "";
                String info = "";
                String telefono = "";
                String email = "";
                String acconto = "";
                String mezzo = "";
                String nazione = "";
                if(!Objects.equals(tfNome.getText(), ""))
                    nomePrenotazione = tfNome.getText();
                if(!Objects.equals(cbSceltaPiazzola.getSelectedItem(), null))
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
                if(cbMezzo.getSelectedItem() != null)
                    mezzo = cbMezzo.getSelectedItem().toString();
                else
                    mezzo = "";
                if(!Objects.equals(tfNazione.getText(), ""))
                    nazione = tfNazione.getText();

                // Eseguo la query del filtro (in base al radio button selezionato) con eventuale messaggio se non esiste nessun valore per il filtro scelto
                String filterQuery = "SELECT * FROM Prenotazioni WHERE ";
                String conjunction = "AND";

                ArrayList<String> conditions = new ArrayList<>();

                // Aggiungi le condizioni alla lista solo se i valori non sono vuoti
                if (!nomePrenotazione.isEmpty()) {
                    conditions.add("Nome = '" + nomePrenotazione + "'");
                }
                if (!piazzolaScelta.isEmpty()) {
                    conditions.add("Piazzola = '" + piazzolaScelta + "'");
                }
                if (!dataArrivo.isEmpty()) {
                    conditions.add("Arrivo = '" + dataArrivo + "'");
                }
                if (!dataPartenza.isEmpty()) {
                    conditions.add("Partenza = '" + dataPartenza + "'");
                }
                if (!info.isEmpty()) {
                    conditions.add("Info = '" + info + "'");
                }
                if (!telefono.isEmpty()) {
                    conditions.add("Telefono = '" + telefono + "'");
                }
                if (!email.isEmpty()) {
                    conditions.add("Email = '" + email + "'");
                }
                if (!acconto.isEmpty()) {
                    conditions.add("Acconto = '" + acconto + "'");
                }
                if (!mezzo.isEmpty()) {
                    conditions.add("Mezzo = '" + mezzo + "'");
                }
                if (!nazione.isEmpty()) {
                    conditions.add("Nazione = '" + nazione + "'");
                }

                // Unisco le condizioni utilizzando l'operatore AND oppure OR a seconda del radio button
                filterQuery += String.join(" " + conjunction + " ", conditions);

                try {
                    ResultSet rs = new Gateway().execSelectQuery(filterQuery);

                    if(!rs.next()) {
                        MessageController.getErrorMessage(dialogFiltraPrenotazione, "Non esistono prenotazioni che soddisfano i filtri inseriti");
                        return;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Aggiorno la tabella con la vista filtrata
                tablePrenotazioniController.refreshTable(tabellaPrenotazioni, filterQuery);
                dialogFiltraPrenotazione.dispose();

                lblFiltro.setText("Filtro applicato ");
                lblFiltro.setForeground(Color.red);
            }
        });

        pnlButtons.add(btnFiltra, CENTER_ALIGNMENT);
        pnlButtons.add(btnAnnulla, CENTER_ALIGNMENT);
        /* --------------------------------------- */

        dialogFiltraPrenotazione.add(pnlForm, BorderLayout.CENTER);
        dialogFiltraPrenotazione.add(pnlButtons, BorderLayout.SOUTH);
        dialogFiltraPrenotazione.pack();
        dialogFiltraPrenotazione.setLocationRelativeTo(null);
        dialogFiltraPrenotazione.setResizable(false);
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
                    String deleteQuery = "DELETE FROM Prenotazioni WHERE Id = ?";

                    // Ottengo i valori dalla riga selezionata
                    String id = tabellaPrenotazioni.getModel().getValueAt(selectedRow, 0).toString();
                    String arrivo = (String) tabellaPrenotazioni.getValueAt(selectedRow, 1);
                    String partenza = (String) tabellaPrenotazioni.getValueAt(selectedRow, 2);
                    String nome = (String) tabellaPrenotazioni.getValueAt(selectedRow, 3);
                    String acconto = (String) tabellaPrenotazioni.getValueAt(selectedRow, 4);

                    // Messaggio di conferma di eliminazione della prenotazione
                    int confirmResult = JOptionPane.showConfirmDialog(MenuPrenotazioni.this,
                            "Vuoi eliminare la prenotazione?",
                            "Elimina prenotazione",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmResult == JOptionPane.YES_OPTION) {
                        // Eseguo la query di eliminazione
                        new Gateway().execUpdateQuery(deleteQuery, id);
                        TablePrenotazioniController.getListaNomi().remove(nome);

                        // Elimino anche dalla tabella SaldoAcconti
                        String idEliminazione = tabellaPrenotazioni.getModel().getValueAt(selectedRow, 0).toString();
                        String deleteSaldoAccontiQuery = "DELETE FROM SaldoAcconti WHERE Id = ?";
                        new Gateway().execUpdateQuery(deleteSaldoAccontiQuery, idEliminazione);

                        // Elimino anche dalla tabella ArriviPartenze
                        String deleteArriviPartenzequery = "DELETE FROM ArriviPartenze WHERE Id = ?";
                        new Gateway().execUpdateQuery(deleteArriviPartenzequery, idEliminazione);

                        // Aggiorno la tabella dopo l'eliminazione e notifico gli observers
                        tablePrenotazioniController.refreshTable(tabellaPrenotazioni);
                        notifyPrenotazioneChanged();
                    }
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

    // Setup del button del salvataggio dei file su drive
    private void setupSalva() {
        if(CloudUploader.uploadDatabaseFile())
            MessageController.getInfoMessage(MenuPrenotazioni.this, "Backup eseguito correttamente!");
        else
            MessageController.getErrorMessage(MenuPrenotazioni.this, "Impossibile eseguire il backup");
    }

    // Setting della modifica dinamica della tabella (doppio click)
    private void updateTableSettings(MouseEvent e){
        if (e.getClickCount() == 2) {
            int row = tabellaPrenotazioni.rowAtPoint(e.getPoint());
            int column = tabellaPrenotazioni.columnAtPoint(e.getPoint());

            // Rimuovi l'editor di default per consentire l'utilizzo dell'editor personalizzato
            tabellaPrenotazioni.removeEditor();

            // Ottieni l'editor della cella selezionata
            TableCellEditor cellEditor = tabellaPrenotazioni.getCellEditor(row, column);

            // Se l'editor è nullo o non è già un editor personalizzato, crea un nuovo editor
            if (cellEditor == null || !(cellEditor instanceof CustomCellEditorPrenotazioni)) {
                CustomCellEditorPrenotazioni customCellEditor = new CustomCellEditorPrenotazioni(tablePrenotazioniController);

                // Si iscrive come observer all'evento di terminazione dell'edit della tabella
                CustomCellEditorPrenotazioni.getObservers().add(this);

                tabellaPrenotazioni.getColumnModel().getColumn(column).setCellEditor(customCellEditor);
            }
        }
    }

    // Notifica i controllers observer della modifica della prenotazione
    private void notifyPrenotazioneChanged() throws SQLException {
        for (PrenotazioniObservers listener : prenotazioniObserversList) {
            listener.refreshView();
        }
    }

    // Notifica i controllers observer della modifica della modifica della piazzola
    private void notifyPiazzolaChanged() throws SQLException {
        for (PrenotazioniObservers listener : prenotazioniObserversList) {
            listener.refreshPiazzola();
        }
    }

    // Notifica i controllers quando termina la modifica (da popup) della tabella Prenotazioni
    @Override
    public void stopEditNotify() throws SQLException {
        for (PrenotazioniObservers listener : prenotazioniObserversList) {
            listener.refreshView();
        }
    }

    public static ArrayList<PrenotazioniObservers> getPrenotazioniObserversList() {
        return prenotazioniObserversList;
    }

    public static ArrayList<String> getListaMezzi() {
        return TableConstants.listaMezzi;
    }
}
