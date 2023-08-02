package views;

import controllers.TableCalendarioController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        tabellaCalendario.setRowSelectionAllowed(true);
        tabellaCalendario.setDefaultEditor(Object.class, null);
        tabellaCalendario.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Adatta la larghezza delle colonne!
        tableCalendarioController.adaptColumnsWidthToHeader();

        //TODO: Renderer per il testo delle celle

        // Renderer per l'header
        DefaultTableCellRenderer headerRenderer = tableCalendarioController.createHeaderRenderer();

        // Assegna i renderer
        tabellaCalendario.getTableHeader().setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(tabellaCalendario);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainPanelCalendario.add(scrollPane, BorderLayout.CENTER);
    }
}
