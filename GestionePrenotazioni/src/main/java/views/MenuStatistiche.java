package views;

import observer.PrenotazioniObservers;
import stats_controllers.PresenzePlotController;
import stats_controllers.TablePresenzeController;
import utils.TimeManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class MenuStatistiche extends JPanel implements PrenotazioniObservers {

    private JPanel mainPanelStatistiche;
    private JPanel pnlPresenze;
    private JPanel pnlMezzi;
    private JPanel pnlNazioni;
    private JPanel pnlToolbar;
    private JPanel pnlPlotPresenze;
    private JToolBar toolBar;
    private JTable tblPresenze;
    private JTable tblMezzi;
    private JTable tblNazioni;
    private JComboBox cbPlotYears;

    // Lista degli anni
    private ArrayList<String> YEARS = TimeManager.getPlotYears();

    public MenuStatistiche() throws SQLException {

        createUIComponents();
        setupToolbar();
        setupPlotPresenze();
        setupTablesPresenze();
        setupTableMezzi();
        setupTableNazioni();

        setLayout(new BorderLayout());
        add(pnlToolbar, BorderLayout.NORTH);
        add(mainPanelStatistiche, BorderLayout.CENTER);
        setVisible(true);

        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelStatistiche = new JPanel();
        mainPanelStatistiche.setLayout(new GridLayout(3, 1));

        // Panel presenze
        pnlPresenze = new JPanel(new GridLayout(1, 2));

    }

    // Setup toolbar
    private void setupToolbar() {

        // Inizializza la toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
        toolBar.setLayout(new BorderLayout());
        JPanel pnlButtonsToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // ComboBox per la scelta dell'anno di visualizzazione nei grafici
        JLabel lblPlotYears = new JLabel("Mostra grafici per l'anno: ");
        cbPlotYears = new JComboBox<>(YEARS.toArray());
        cbPlotYears.setFocusable(false);
        ((JLabel) cbPlotYears.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        pnlButtonsToolbar.add(lblPlotYears);
        pnlButtonsToolbar.add(cbPlotYears);

        toolBar.setFloatable(false);
        toolBar.add(pnlButtonsToolbar, BorderLayout.CENTER);
        pnlToolbar.add(toolBar, BorderLayout.CENTER);
    }

    // Setup tabelle presenze
    private void setupTablesPresenze() throws SQLException {

        // Imposta il bordo
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "Presenze", TitledBorder.LEFT, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlPresenze.setBorder(titledBorder);

        JPanel pnlTablesPresenze = new JPanel(new BorderLayout());

        // Setting controller tabella Presenze
        tblPresenze = new JTable();
        tblPresenze.setGridColor(Color.BLACK);
        tblPresenze.getTableHeader().setReorderingAllowed(false);
        TablePresenzeController tablePresenzeController = new TablePresenzeController(tblPresenze);
        TablePresenzeController.setTableModel();
//        if(tblPresenze.getColumnCount() > 10)
//            tblPresenze.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Imposta il renderer
        TablePresenzeController.createTableRenderer();

        pnlTablesPresenze.add(new JScrollPane(tblPresenze, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        pnlPresenze.add(pnlTablesPresenze);
        mainPanelStatistiche.add(pnlPresenze);

    }

    // Setup del plot Presenze
    private void setupPlotPresenze() throws SQLException {
        pnlPlotPresenze = new JPanel(new BorderLayout());
        PresenzePlotController presenzePlotController = new PresenzePlotController(pnlPlotPresenze, cbPlotYears.getSelectedItem().toString());

        // Crea il grafico
        presenzePlotController.createPlot();

        // Aggiungo il panel del plot
        pnlPresenze.add(pnlPlotPresenze);
        mainPanelStatistiche.add(pnlPresenze);
    }

    // Setup tabella mezzi
    private void setupTableMezzi() {
        pnlMezzi = new JPanel(new FlowLayout());

        // Imposta il bordo
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "Mezzi", TitledBorder.LEFT, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlMezzi.setBorder(titledBorder);

        JPanel pnlPlotMezzi = new JPanel(new BorderLayout());
        JPanel pnlTableMezzi = new JPanel(new BorderLayout());

        pnlMezzi.add(pnlPlotMezzi);
        pnlMezzi.add(pnlTableMezzi);
        mainPanelStatistiche.add(pnlMezzi);
    }

    // Setup tabella nazioni
    private void setupTableNazioni() {
        pnlNazioni = new JPanel(new FlowLayout());

        // Imposta il bordo
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "Nazioni", TitledBorder.LEFT, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlNazioni.setBorder(titledBorder);

        JPanel pnlPlotNazioni = new JPanel(new BorderLayout());
        JPanel pnlTableNazioni = new JPanel(new BorderLayout());

        pnlNazioni.add(pnlPlotNazioni);
        pnlNazioni.add(pnlTableNazioni);
        mainPanelStatistiche.add(pnlNazioni);
    }

    // Ricarica la cb degli anni
    @Override
    public void refreshView() throws SQLException {
        YEARS = TimeManager.getPlotYears();

        cbPlotYears.removeAllItems();

        for (String year : YEARS) {
            cbPlotYears.addItem(year);
        }
    }

    @Override
    public void refreshPiazzola() throws SQLException {

    }
}
