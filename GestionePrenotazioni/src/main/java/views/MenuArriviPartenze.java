package views;

import controllers.TableArriviController;
import controllers.TableCalendarioController;
import controllers.TablePartenzeController;

import javax.swing.*;
import java.awt.*;

public class MenuArriviPartenze extends JPanel {

    // I due controller per le due tabelle
    TableArriviController tableArriviController;
    TablePartenzeController tablePartenzeController;

    private JPanel mainPanelArriviPartenze;
    private JTable tabellaArrivi;
    private JTable tabellaPartenze;
    private JPanel pnlToolbar;
    private JToolBar toolBar;

    public MenuArriviPartenze() {

        createUIComponents();
        setupToolbar();
        setupTableArrivi();
        setupTablePartenze();

        setLayout(new BorderLayout());
        add(mainPanelArriviPartenze, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelArriviPartenze = new JPanel();
        mainPanelArriviPartenze.setLayout(new BorderLayout());

        // Inizializzazione delle tabelle e relativi controllers
        tabellaArrivi = new JTable();
        tabellaPartenze = new JTable();
        tableArriviController = new TableArriviController(tabellaArrivi);
        tablePartenzeController = new TablePartenzeController(tabellaPartenze);

        // Inizializza la toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
    }

    // Setup tabella arrivi
    private void setupTableArrivi() {
    }

    // Setup tabella arrivi
    private void setupTablePartenze() {
    }

    // Setup della toolbar
    private void setupToolbar() {

    }
}
