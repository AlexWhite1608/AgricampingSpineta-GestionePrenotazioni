package views;

import controllers.TableCalendarioController;

import javax.swing.*;
import java.awt.*;

public class MenuCalendario extends JPanel {

    // Controller della tabella
    TableCalendarioController tableCalendarioController;

    private JPanel mainPanelCalendario;
    private JTable tblCalendario;

    public MenuCalendario() {
        tableCalendarioController = new TableCalendarioController();

        createUIComponents();
        setupTable();

        setLayout(new BorderLayout());
        add(mainPanelCalendario, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

    }

    // Setup tabella calendario
    private void setupTable() {

    }
}
