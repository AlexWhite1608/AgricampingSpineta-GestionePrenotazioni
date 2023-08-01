package views;

import controllers.TableCalendarioController;

import javax.swing.*;
import java.awt.*;

public class MenuCalendario extends JPanel {

    // Controller della tabella
    TableCalendarioController tableCalendarioController;

    private JPanel mainPanelCalendario;
    private JTable tabellaCalendario;

    public MenuCalendario() {
        tableCalendarioController = new TableCalendarioController(tabellaCalendario);

        createUIComponents();
        setupTable();

        setLayout(new BorderLayout());
        add(mainPanelCalendario, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelCalendario = new JPanel();
        mainPanelCalendario.setLayout(new BorderLayout());

        //TODO: istanzia la tabella col controller
    }

    // Setup tabella calendario
    private void setupTable() {
        tabellaCalendario.getTableHeader().setReorderingAllowed(false);
        tabellaCalendario.setCellSelectionEnabled(false);
        tabellaCalendario.setRowSelectionAllowed(true);
        tabellaCalendario.setDefaultEditor(Object.class, null);

        // Dimensione righe tabella
        int rowHeight = 40;
        tabellaCalendario.setRowHeight(rowHeight);

        JScrollPane scrollPane = new JScrollPane(tabellaCalendario);
        mainPanelCalendario.add(scrollPane, BorderLayout.CENTER);
    }
}
