package views;

import observer.PlotControllerObservers;
import observer.PrenotazioniObservers;
import plot_stats_controllers.MezziPlotController;
import plot_stats_controllers.PresenzePlotController;
import table_stats_controllers.TableMezziController;
import table_stats_controllers.TablePresenzeController;
import utils.TimeManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class MenuStatistiche extends JPanel implements PrenotazioniObservers {

    private JPanel mainPanelStatistiche;
    private JPanel pnlPresenze;
    private JPanel pnlMezzi;
    private JPanel pnlNazioni;
    private JPanel pnlToolbar;
    private JPanel pnlPlotPresenze;
    private JPanel pnlPlotMezzi;
    private JToolBar toolBar;
    private JTable tblPresenze;
    private JTable tblMezzi;
    private JTable tblNazioni;
    private JComboBox cbPlotYears;

    // Lista degli anni
    private ArrayList<String> YEARS = TimeManager.getPlotYears();

    // Lista dei controller observer dei plotControllers
    private static ArrayList<PlotControllerObservers> plotControllersObserversList = new ArrayList<>();

    public MenuStatistiche() throws SQLException {

        createUIComponents();
        setupToolbar();
        setupPlotPresenze();
        setupTablesPresenze();
        setupPlotMezzi();
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

        // Panel mezzi
        pnlMezzi = new JPanel(new GridLayout(1, 2));

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

        // Implementa aggiornamento del grafico quando si cambia l'anno della cb
        cbPlotYears.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                for(PlotControllerObservers observer : plotControllersObserversList){
                    observer.setSelectedYear(cbPlotYears.getSelectedItem().toString());
                    try {
                        observer.refreshPlot();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });

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

        JPanel pnlTablePresenze = new JPanel(new BorderLayout());

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

        pnlTablePresenze.add(new JScrollPane(tblPresenze, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        pnlPresenze.add(pnlTablePresenze);
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
    private void setupTableMezzi() throws SQLException {

        // Imposta il bordo
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "Mezzi", TitledBorder.LEFT, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlMezzi.setBorder(titledBorder);

        JPanel pnlTableMezzi = new JPanel(new BorderLayout());

        // Setting controller tabella Presenze
        tblMezzi = new JTable();
        tblMezzi.setGridColor(Color.BLACK);
        tblMezzi.getTableHeader().setReorderingAllowed(false);
        TableMezziController tableMezziController = new TableMezziController(tblMezzi);
        TableMezziController.setTableModel();

        TableMezziController.createTableRenderer();

        pnlTableMezzi.add(new JScrollPane(tblMezzi, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        pnlMezzi.add(pnlTableMezzi);
        mainPanelStatistiche.add(pnlMezzi);
    }

    // Setup del plot Mezzi
    private void setupPlotMezzi() throws SQLException {
        pnlPlotMezzi = new JPanel(new BorderLayout());
        MezziPlotController mezziPlotController = new MezziPlotController(pnlPlotMezzi, cbPlotYears.getSelectedItem().toString());

        // Crea il grafico
        mezziPlotController.createPlot();

        // Aggiungo il panel del plot
        pnlMezzi.add(pnlPlotMezzi);
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
        ArrayList<String> currentItems = new ArrayList<>();
        YEARS = TimeManager.getPlotYears();

        // Ottieni gli elementi attualmente presenti nella combobox
        for (int i = 0; i < cbPlotYears.getItemCount(); i++) {
            currentItems.add((String) cbPlotYears.getItemAt(i));
        }

        // Rimuovi gli elementi non presenti in YEARS dalla combobox
        for (String currentItem : currentItems) {
            if (!YEARS.contains(currentItem)) {
                cbPlotYears.removeItem(currentItem);
            }
        }

        // Aggiungi gli elementi presenti in YEARS alla combobox se non già presenti
        for (String year : YEARS) {
            if (!currentItems.contains(year)) {
                cbPlotYears.addItem(year);
            }
        }
    }

    @Override
    public void refreshPiazzola() throws SQLException {

    }

    public static ArrayList<PlotControllerObservers> getPlotControllersObserversList() {
        return plotControllersObserversList;
    }
}
