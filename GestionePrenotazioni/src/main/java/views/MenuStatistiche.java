package views;

import javax.swing.*;
import java.awt.*;

public class MenuStatistiche extends JPanel {

    private JPanel mainPanelStatistiche;
    private JPanel pnlPresenze;
    private JPanel pnlMezzi;
    private JPanel pnlNazioni;
    private JPanel pnlToolbar;
    private JToolBar toolBar;
    private JTable tblPresenze;
    private JTable tblTotalePresenze;
    private JTable tblMezzi;
    private JTable tblNazioni;

    public MenuStatistiche() {

        createUIComponents();
        setupToolbar();
        setupTablesPresenze();
        setupTableMezzi();
        setupTableNazioni();

        setLayout(new BorderLayout());
        add(mainPanelStatistiche, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelStatistiche = new JPanel();
        mainPanelStatistiche.setLayout(new GridLayout(4, 1));

    }

    // Setup toolbar
    private void setupToolbar() {

        // Inizializza la toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
    }

    // Setup tabelle presenze
    private void setupTablesPresenze() {
        pnlPresenze = new JPanel(new FlowLayout());

        JPanel pnlPlotPresenze = new JPanel(new BorderLayout());
        JPanel pnlTablesPresenze = new JPanel(new GridLayout(2, 1));

        pnlPresenze.add(pnlPlotPresenze);
        pnlPresenze.add(pnlTablesPresenze);
        mainPanelStatistiche.add(pnlPresenze);

    }

    // Setup tabella mezzi
    private void setupTableMezzi() {
        pnlMezzi = new JPanel(new FlowLayout());

        JPanel pnlPlotMezzi = new JPanel(new BorderLayout());
        JPanel pnlTableMezzi = new JPanel(new BorderLayout());

        pnlMezzi.add(pnlPlotMezzi);
        pnlMezzi.add(pnlTableMezzi);
        mainPanelStatistiche.add(pnlMezzi);
    }

    // Setup tabella nazioni
    private void setupTableNazioni() {
        pnlNazioni = new JPanel(new FlowLayout());

        JPanel pnlPlotNazioni = new JPanel(new BorderLayout());
        JPanel pnlTableNazioni = new JPanel(new BorderLayout());

        pnlNazioni.add(pnlPlotNazioni);
        pnlNazioni.add(pnlTableNazioni);
        mainPanelStatistiche.add(pnlNazioni);
    }
}
