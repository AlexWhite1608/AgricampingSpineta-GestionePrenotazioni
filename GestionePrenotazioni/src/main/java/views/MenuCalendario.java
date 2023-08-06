package views;

import controllers.ControllerDatePrenotazioni;
import controllers.TableCalendarioController;
import utils.TableConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
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
        JPanel pnlButtonsToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Creazione degli elementi della toolbar
        JButton btnScegliGiorno = new JButton("Giorno");
        JButton btnResetGiorno = new JButton("Reset");
        JLabel lblGiornoSelezionato = new JLabel();

        // Impostazioni
        btnScegliGiorno.setFocusPainted(false);
        btnResetGiorno.setFocusPainted(false);

        // Imposta il giorno corrente (di default) alla label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedTodayDate = ControllerDatePrenotazioni.getCurrentDate().format(formatter);
        lblGiornoSelezionato.setText("Giorno selezionato: " + formattedTodayDate);

        // Aggiunta degli elementi
        pnlButtonsToolbar.add(btnScegliGiorno);
        pnlButtonsToolbar.add(btnResetGiorno);
        pnlButtonsToolbar.add(Box.createHorizontalStrut(10));
        pnlButtonsToolbar.add(lblGiornoSelezionato);
        pnlButtonsToolbar.add(Box.createHorizontalStrut(10));

        toolBar.setFloatable(false);
        toolBar.add(pnlButtonsToolbar, BorderLayout.WEST);
        pnlToolbar.add(toolBar, BorderLayout.CENTER);
        mainPanelCalendario.add(pnlToolbar, BorderLayout.NORTH);
    }
}
