package views;

import controllers.TableCalendarioController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;


public class MenuCalendario extends JPanel {

    // Controller della tabella
    TableCalendarioController tableCalendarioController;

    private JPanel mainPanelCalendario;
    private JTable tabellaCalendario;

    public MenuCalendario() throws SQLException {

        createUIComponents();
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
    }

    // Setup tabella calendario
    private void setupTable() {

        // Impostazioni di base, sempre fisse
        tabellaCalendario.getTableHeader().setReorderingAllowed(false);
        tabellaCalendario.setCellSelectionEnabled(false);
        tabellaCalendario.setRowSelectionAllowed(false);
        tabellaCalendario.setColumnSelectionAllowed(false);
        tabellaCalendario.setDefaultEditor(Object.class, null);
        tabellaCalendario.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabellaCalendario.setGridColor(Color.BLACK);

        // TODO: chiama funzione che aggiorna i valori iniziali della tabella da zero --> uno

        //TODO: Renderer per il testo delle celle
        tableCalendarioController.createCellRenderer();

        // Renderer per l'header
        tableCalendarioController.createHeaderRenderer();

        JScrollPane scrollPane = new JScrollPane(tabellaCalendario, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanelCalendario.add(scrollPane, BorderLayout.CENTER);
    }
}
