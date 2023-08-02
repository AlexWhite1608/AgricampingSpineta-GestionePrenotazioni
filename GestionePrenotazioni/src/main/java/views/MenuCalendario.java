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

        int padding = 20; // Aggiungi un valore di padding desiderato
        for (int column = 0; column < tabellaCalendario.getColumnCount(); column++) {
            TableColumn tableColumn = tabellaCalendario.getColumnModel().getColumn(column);
            TableCellRenderer headerRenderer = tabellaCalendario.getTableHeader().getDefaultRenderer();
            Object headerValue = tableColumn.getHeaderValue();
            Component headerComp = headerRenderer.getTableCellRendererComponent(tabellaCalendario, headerValue, false, false, 0, column);
            int headerWidth = headerComp.getPreferredSize().width;
            int maxCellWidth = 0;

            for (int row = 0; row < tabellaCalendario.getRowCount(); row++) {
                TableCellRenderer cellRenderer = tabellaCalendario.getCellRenderer(row, column);
                Component cellComp = tabellaCalendario.prepareRenderer(cellRenderer, row, column);
                int cellWidth = cellComp.getPreferredSize().width + tabellaCalendario.getIntercellSpacing().width;
                maxCellWidth = Math.max(maxCellWidth, cellWidth);
            }

            int preferredWidth = Math.max(headerWidth, maxCellWidth) + padding; // Aggiungi il padding
            tableColumn.setPreferredWidth(preferredWidth);
        }


        // Renderer per il testo delle celle

        // Renderer per l'header
        DefaultTableCellRenderer headerRenderer = tableCalendarioController.createHeaderRenderer();

        // Assegna i renderer
        tabellaCalendario.getTableHeader().setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(tabellaCalendario);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainPanelCalendario.add(scrollPane, BorderLayout.CENTER);
    }
}
