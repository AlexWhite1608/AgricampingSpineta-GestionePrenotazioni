package views;

import controllers.TableCalendarioController;
import vertical_header.VerticalTableHeaderCellRenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.util.Enumeration;


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
        tabellaCalendario.setRowSelectionAllowed(true);
        tabellaCalendario.setDefaultEditor(Object.class, null);

        //TODO: Renderer per il testo delle celle

        // Renderer per l'header
        tableCalendarioController.createHeaderRenderer();

        JScrollPane scrollPane = new JScrollPane(tabellaCalendario);
        mainPanelCalendario.add(scrollPane, BorderLayout.CENTER);
    }
}
