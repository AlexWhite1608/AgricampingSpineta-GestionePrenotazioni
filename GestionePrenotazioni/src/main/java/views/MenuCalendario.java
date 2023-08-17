package views;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import view_controllers.ControllerDatePrenotazioni;
import view_controllers.MessageController;
import view_controllers.TableCalendarioController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MenuCalendario extends JPanel {

    // Controller della tabella
    TableCalendarioController tableCalendarioController;

    private JPanel mainPanelCalendario;
    private JTable tabellaCalendario;
    private JPanel pnlToolbar;
    private JToolBar toolBar;

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
        tableCalendarioController.setCalendarioTableModel();

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
        tableCalendarioController.createCellRenderer();

        // Renderer per l'header
        tableCalendarioController.createHeaderRenderer();

        JScrollPane scrollPane = new JScrollPane(tabellaCalendario, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanelCalendario.add(scrollPane, BorderLayout.CENTER);
    }

    // Setup della toolbar
    private void setupToolbar() {

        // Impostazione del layout
        toolBar.setLayout(new BorderLayout());
        JPanel pnlButtonsToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Creazione degli elementi della toolbar
        JButton btnScegliGiorno = new JButton("Giorno");
        JButton btnResetGiorno = new JButton("Reset");
        JLabel lblGiornoSelezionato = new JLabel();

        // Impostazioni
        btnScegliGiorno.setFocusPainted(false);
        btnResetGiorno.setFocusPainted(false);
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

        toolBar.setFloatable(false);
        toolBar.add(pnlButtonsToolbar, BorderLayout.CENTER);
        pnlToolbar.add(toolBar, BorderLayout.CENTER);
        mainPanelCalendario.add(pnlToolbar, BorderLayout.NORTH);
    }
}
