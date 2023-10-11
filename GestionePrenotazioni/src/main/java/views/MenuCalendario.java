package views;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import data_access.Gateway;
import observer.NewPrenotazioneObservers;
import observer.PrenotazioniObservers;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;
import table_stats_controllers.TableAdvancedStatsController;
import utils.ListOfNations;
import utils.TableConstants;
import view_controllers.*;

import javax.print.attribute.standard.JobHoldUntil;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class MenuCalendario extends JPanel {

    // Controller della tabella
    TableCalendarioController tableCalendarioController;

    // Lista dei controller observer per inserire nuova prenotazione da Calendario
    private static ArrayList<NewPrenotazioneObservers> nuovaPrenotazioneCalendarioObserver = new ArrayList<>();

    private JPanel mainPanelCalendario;
    private JTable tabellaCalendario;
    private JPanel pnlToolbar;
    private JToolBar toolBar;
    private JLabel lblPresenzeGiornaliere;

    public MenuCalendario() throws SQLException {

        createUIComponents();
        setupToolbar();
        setupTable();

        setLayout(new BorderLayout());
        add(mainPanelCalendario, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() throws SQLException {

        // Main panel
        mainPanelCalendario = new JPanel();
        mainPanelCalendario.setLayout(new BorderLayout());

        // Inizializza il TableModel per il calendario
        tabellaCalendario = new JTable();
        tableCalendarioController = new TableCalendarioController(tabellaCalendario);
        TableCalendarioController.setCalendarioTableModel();

        // Inizializza la toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
    }

    // Setup tabella calendario
    private void setupTable() {

        // Impostazioni di base, sempre fisse
        tabellaCalendario.getTableHeader().setReorderingAllowed(false);
        tabellaCalendario.setCellSelectionEnabled(true);
        tabellaCalendario.setRowSelectionAllowed(false);
        tabellaCalendario.setColumnSelectionAllowed(false);
        tabellaCalendario.setDefaultEditor(Object.class, null);
        tabellaCalendario.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabellaCalendario.setGridColor(Color.BLACK);

        // Renderer per le celle
        TableCalendarioController.createCellRenderer();

        // Renderer per l'header
        TableCalendarioController.createHeaderRenderer();

        JScrollPane scrollPane = new JScrollPane(tabellaCalendario, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanelCalendario.add(scrollPane, BorderLayout.CENTER);
    }

    // Setup della toolbar
    private void setupToolbar() throws SQLException {

        // Impostazione del layout
        toolBar.setLayout(new BorderLayout());
        JPanel pnlButtonsToolbar = new JPanel(new FlowLayout());

        // Creazione degli elementi della toolbar
        JButton btnScegliGiorno = new JButton("Giorno");
        JButton btnResetGiorno = new JButton("Reset");
        JButton btnAggiungiPrenotazione = new JButton("Aggiungi");
        JLabel lblGiornoSelezionato = new JLabel();

        // Panel box delle presenze giornaliere
        JPanel pnlPresenzeGiornaliere = new JPanel(new FlowLayout());
        lblPresenzeGiornaliere = new JLabel("Attualmente presenti: " + tableCalendarioController.getAttualmentePresenti());
        tableCalendarioController.setPresenzeLabel(lblPresenzeGiornaliere);
        lblPresenzeGiornaliere.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font font = new Font(lblPresenzeGiornaliere.getFont().getName(), Font.BOLD, 15);
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        Border emptyBorder = new EmptyBorder(5, 10, 5, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);
        lblPresenzeGiornaliere.setBorder(compoundBorder);
        lblPresenzeGiornaliere.setFont(font);
        lblPresenzeGiornaliere.setForeground(Color.BLACK);
        lblPresenzeGiornaliere.setBorder(compoundBorder);
        lblPresenzeGiornaliere.setFont(font);
        lblPresenzeGiornaliere.setForeground(Color.BLACK);
        pnlPresenzeGiornaliere.add(lblPresenzeGiornaliere);

        // Impostazioni
        btnAggiungiPrenotazione.setFocusPainted(false);
        btnScegliGiorno.setFocusPainted(false);
        btnResetGiorno.setFocusPainted(false);
        btnAggiungiPrenotazione.setToolTipText("Aggiungi nuova prenotazione");
        btnScegliGiorno.setToolTipText("Scegli il giorno iniziale");
        btnResetGiorno.setToolTipText("Reimposta la data odierna");

        // Imposta il giorno corrente (di default) alla label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedTodayDate = ControllerDatePrenotazioni.getCurrentDate().format(formatter);
        lblGiornoSelezionato.setText("Mostra prenotazioni dal: " + formattedTodayDate);

        // Aggiunta degli elementi
        pnlButtonsToolbar.add(btnScegliGiorno);
        pnlButtonsToolbar.add(btnResetGiorno);
        pnlButtonsToolbar.add(Box.createHorizontalStrut(10));
        pnlButtonsToolbar.add(lblGiornoSelezionato);
        pnlButtonsToolbar.add(Box.createHorizontalStrut(10));
        pnlButtonsToolbar.add(btnAggiungiPrenotazione);

        btnScegliGiorno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Creazione del dialog con il datepicker
                JDialog newDateDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuCalendario.this), "Scegli la data", true);
                newDateDialog.setLayout(new BorderLayout());

                // Creazione del panel per il layout
                JPanel pnlNewDate = new JPanel(new FlowLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                JLabel nameLabel = new JLabel("Scegli la data iniziale:");
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.WEST;
                pnlNewDate.add(nameLabel, gbc);

                DatePicker datePickerNewDate = new DatePicker();
                DatePickerSettings dateSettingsArrivo = new DatePickerSettings();
                dateSettingsArrivo.setFormatForDatesCommonEra("dd/MM/yyyy");
                datePickerNewDate.setSettings(dateSettingsArrivo);
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.WEST;
                pnlNewDate.add(datePickerNewDate, gbc);

                // Panel buttons
                JPanel pnlButtons = new JPanel(new FlowLayout());
                JButton btnConferma = new JButton("Conferma");
                JButton btnAnnulla = new JButton("Annulla");
                btnConferma.setFocusPainted(false);
                btnAnnulla.setFocusPainted(false);
                pnlButtons.add(btnConferma, CENTER_ALIGNMENT);
                pnlButtons.add(btnAnnulla, CENTER_ALIGNMENT);

                btnAnnulla.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        newDateDialog.dispose();
                    }
                });

                // Realizza l'aggiornamento della visualizzazione
                btnConferma.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        LocalDate selectedDate = datePickerNewDate.getDate();
                        ControllerDatePrenotazioni.setCurrentDate(selectedDate);

                        // aggiornare il tableModel con le nuove date?
                        try {
                            // Ricarica la visualizzazione dell'intera tabella
                            TableCalendarioController.refreshDate();

                            // Modifica la label con il nuovo giorno selezionato
                            lblGiornoSelezionato.setForeground(Color.red);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            String formattedTodayDate = selectedDate.format(formatter);
                            lblGiornoSelezionato.setText("Mostra prenotazioni dal: " + formattedTodayDate);

                            newDateDialog.dispose();
                        } catch (SQLException ex) {
                            MessageController.getErrorMessage(newDateDialog, "Impossibile cambiare la data");
                        }
                    }
                });

                newDateDialog.add(pnlNewDate, BorderLayout.CENTER);
                newDateDialog.add(pnlButtons, BorderLayout.SOUTH);
                newDateDialog.pack();
                newDateDialog.setLocationRelativeTo(null);
                newDateDialog.setResizable(false);
                newDateDialog.setVisible(true);
            }
        });

        btnResetGiorno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControllerDatePrenotazioni.setCurrentDate(LocalDate.now());

                // aggiornare il tableModel con le nuove date?
                try {
                    // Ricarica la visualizzazione dell'intera tabella
                    TableCalendarioController.refreshDate();

                    // Modifica la label con il nuovo giorno selezionato (oggi)
                    lblGiornoSelezionato.setForeground(Color.black);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedTodayDate = LocalDate.now().format(formatter);
                    lblGiornoSelezionato.setText("Mostra prenotazioni dal: " + formattedTodayDate);

                } catch (SQLException ex) {
                    MessageController.getErrorMessage(null, "Impossibile resettare la data");
                }
            }
        });

        // Azione -> Aggiunge nuova prenotazione dal Calendario
        btnAggiungiPrenotazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addPrenotazioneDialog();
                } catch (SQLException ex) {
                    System.err.println("Impossibile aggiungere prenotazione da Calendario: " + ex.getMessage());
                }
            }
        });

        toolBar.setFloatable(false);
        toolBar.add(pnlButtonsToolbar, BorderLayout.WEST);
        toolBar.add(lblPresenzeGiornaliere, BorderLayout.EAST);
        pnlToolbar.add(toolBar, BorderLayout.CENTER);
        mainPanelCalendario.add(pnlToolbar, BorderLayout.NORTH);
    }

    // Setting dialog di aggiunta prenotazione
    private void addPrenotazioneDialog() throws SQLException {
        JDialog dialogNuovaPrenotazione = new JDialog((Frame) SwingUtilities.getWindowAncestor(MenuCalendario.this), "Aggiungi nuova prenotazione", true);
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
        Component horizontalStrut1 = Box.createHorizontalStrut(TableConstants.DIALOG_SEPARATOR_WIDTH);
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
        Component horizontalStrut2 = Box.createHorizontalStrut(TableConstants.DIALOG_SEPARATOR_WIDTH);
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
        cbMezzo.removeItem("Nessuno");
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

                //TODO: incapsula il codice in una funzione statica che puoi chiamare dal MenuCalendario!
                notifyAddPrenotazione(dataArrivo, dataPartenza, nomePrenotazione);

                dialogNuovaPrenotazione.dispose();
                MessageController.getInfoMessage(MenuCalendario.this, "Prenotazione aggiunta");


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

    // Notifica il MenuPrenotazioni che si è aggiunta una nuova prenotazione
    private void notifyAddPrenotazione(String arrivo, String partenza, String nome) {
        for (NewPrenotazioneObservers listener : nuovaPrenotazioneCalendarioObserver) {
            listener.addPrenotazione(arrivo, partenza, nome);
        }
    }

    public static ArrayList<NewPrenotazioneObservers> getNuovaPrenotazioneCalendarioObserver() {
        return nuovaPrenotazioneCalendarioObserver;
    }
}
