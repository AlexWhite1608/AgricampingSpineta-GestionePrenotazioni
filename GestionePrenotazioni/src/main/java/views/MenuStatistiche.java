package views;

import observer.PlotControllerObservers;
import observer.PrenotazioniObservers;
import plot_stats_controllers.MezziPlotController;
import plot_stats_controllers.NazioniPlotController;
import plot_stats_controllers.PresenzePlotController;
import table_stats_controllers.TableMezziController;
import table_stats_controllers.TableNazioniController;
import table_stats_controllers.TablePresenzeController;
import utils.TimeManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MenuStatistiche extends JPanel implements PrenotazioniObservers {

    private JPanel mainPanelStatistiche;
    private JPanel pnlPresenze;
    private JPanel pnlMezzi;
    private JPanel pnlNazioni;
    private JPanel pnlToolbar;
    private JPanel pnlPlotPresenze;
    private JPanel pnlPlotMezzi;
    private JPanel pnlPlotNazioni;
    private JToolBar toolBar;
    private JTable tblPresenze;
    private JTable tblMezzi;
    private JTable tblNazioni;
    private JComboBox cbPlotYears;
    private JButton btnFocusTables;

    // Lista degli anni
    private ArrayList<String> YEARS = TimeManager.getPlotYears();

    // Lista dei controller observer dei plotControllers
    private static ArrayList<PlotControllerObservers> plotControllersObserversList = new ArrayList<>();

    public MenuStatistiche() throws SQLException, IOException {

        createUIComponents();
        setupToolbar();
        setupPlotPresenze();
        setupTablesPresenze();
        setupPlotMezzi();
        setupTableMezzi();
        setupPlotNazioni();
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

        // Panel nazioni
        pnlNazioni = new JPanel(new GridLayout(1, 2));

    }

    // Setup toolbar
    private void setupToolbar() throws IOException {

        // Inizializza la toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
        toolBar.setLayout(new BorderLayout());
        JPanel pnlButtonsToolbar = new JPanel(new BorderLayout());

        // ComboBox per la scelta dell'anno di visualizzazione nei grafici
        JLabel lblPlotYears = new JLabel("Mostra grafici per l'anno: ");
        cbPlotYears = new JComboBox<>(YEARS.toArray());
        cbPlotYears.setFocusable(false);
        ((JLabel) cbPlotYears.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cbPlotYears.setSelectedItem(String.valueOf(LocalDate.now().getYear()));

        // Button per la visualizzazione del focus sulle tabelle
        btnFocusTables = new JButton();
        btnFocusTables.setFocusPainted(false);
        Icon icon = new ImageIcon((Objects.requireNonNull(getClass().getResource("/zoom-in-24x24.png"))));
        btnFocusTables.setIcon(icon);
        btnFocusTables.setToolTipText("Ingrandisci le tabelle");

        JPanel pnlChooseYears = new JPanel(new FlowLayout());
        pnlChooseYears.add(lblPlotYears);
        pnlChooseYears.add(cbPlotYears);

        pnlButtonsToolbar.add(pnlChooseYears, BorderLayout.WEST);
        pnlButtonsToolbar.add(btnFocusTables, BorderLayout.EAST);

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

        // Apre il frame di visualizzazione in full-screen delle tabelle
        btnFocusTables.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frameFocusTables = new JFrame("Statistiche");
                frameFocusTables.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                //TODO: aggiungi ulteriori statistiche tramite button

                // Toolbar
                JToolBar toolBarFocusTables = new JToolBar();
                toolBarFocusTables.setFloatable(false);
                toolBarFocusTables.setLayout(new FlowLayout(FlowLayout.LEFT));
                frameFocusTables.add(toolBarFocusTables, BorderLayout.NORTH);

                // CombBox con lista tabelle
                JComboBox<String> cbListaTabelle = new JComboBox<>(new String[] {"Presenze", "Mezzi", "Nazioni"});
                cbListaTabelle.setSelectedItem("Presenze");
                cbListaTabelle.setFocusable(false);
                ((JLabel) cbListaTabelle.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

                JPanel pnlTableFocusTables = new JPanel(new CardLayout()); // Utilizza CardLayout
                frameFocusTables.add(pnlTableFocusTables, BorderLayout.CENTER);

                // Crea copie delle tabelle
                JTable copyTablePresenze = new JTable(tblPresenze.getModel());
                JTable copyTableMezzi = new JTable(tblMezzi.getModel());
                JTable copyTableNazioni = new JTable(tblNazioni.getModel());

                TablePresenzeController controllerCopiaPresenze = new TablePresenzeController(copyTablePresenze);
                TableMezziController controllerCopiaMezzi = new TableMezziController(copyTableMezzi);
                TableNazioniController controllerCopiaNazioni = new TableNazioniController(copyTableNazioni);

                controllerCopiaPresenze.createTableRenderer();
                controllerCopiaMezzi.createTableRenderer();
                controllerCopiaNazioni.createTableRenderer();

                // Aggiungi JScrollPane per ciascuna tabella
                JScrollPane scrollPanePresenze = new JScrollPane(copyTablePresenze, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                JScrollPane scrollPaneMezzi = new JScrollPane(copyTableMezzi, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                JScrollPane scrollPaneNazioni = new JScrollPane(copyTableNazioni, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                pnlTableFocusTables.add(scrollPanePresenze, "Presenze");
                pnlTableFocusTables.add(scrollPaneMezzi, "Mezzi");
                pnlTableFocusTables.add(scrollPaneNazioni, "Nazioni");

                // Nascondi le JScrollPane inizialmente tranne quella di Presenze
                ((CardLayout) pnlTableFocusTables.getLayout()).show(pnlTableFocusTables, "Presenze");

                toolBarFocusTables.add(new JLabel("Seleziona la tabella: "));
                toolBarFocusTables.add(cbListaTabelle);

                // Implementa switch dinamico
                cbListaTabelle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedTable = cbListaTabelle.getSelectedItem().toString();

                        // Mostra la JScrollPane corrispondente alla tabella selezionata
                        ((CardLayout) pnlTableFocusTables.getLayout()).show(pnlTableFocusTables, selectedTable);
                    }
                });

                frameFocusTables.pack();
                frameFocusTables.setLocationRelativeTo(null);
                frameFocusTables.setResizable(true);
                frameFocusTables.setMinimumSize(new Dimension(1300, 800));
                frameFocusTables.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frameFocusTables.setVisible(true);
            }
        });

        toolBar.setFloatable(false);
        toolBar.add(pnlButtonsToolbar, BorderLayout.CENTER);
        pnlToolbar.add(toolBar, BorderLayout.CENTER);
    }

    // Setup tabelle Presenze
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

        TablePresenzeController.createTableRenderer();

        tblPresenze.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

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

    // Setup tabella Mezzi
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


        tblMezzi.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

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

    // Setup tabella Nazioni
    private void setupTableNazioni() throws SQLException {

        // Imposta il bordo
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "Nazioni", TitledBorder.LEFT, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlNazioni.setBorder(titledBorder);

        JPanel pnlTableNazioni = new JPanel(new BorderLayout());

        // Setting controller tabella Presenze
        tblNazioni = new JTable();
        tblNazioni.setGridColor(Color.BLACK);
        tblNazioni.getTableHeader().setReorderingAllowed(false);
        TableNazioniController tableNazioniController = new TableNazioniController(tblNazioni);
        TableNazioniController.setTableModel();

        TableNazioniController.createTableRenderer();

        tblNazioni.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        pnlTableNazioni.add(new JScrollPane(tblNazioni, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        pnlNazioni.add(pnlTableNazioni);
        mainPanelStatistiche.add(pnlNazioni);
    }

    // Setup plot Nazioni
    private void setupPlotNazioni() throws SQLException {
        pnlPlotNazioni = new JPanel(new BorderLayout());
        NazioniPlotController nazioniPlotController = new NazioniPlotController(pnlPlotNazioni, cbPlotYears.getSelectedItem().toString());

        // Crea il grafico
        nazioniPlotController.createPlot();

        // Aggiungo il panel del plot
        pnlNazioni.add(pnlPlotNazioni);
        mainPanelStatistiche.add(pnlNazioni);
    }

    // Ricarica la cb degli anni
    @Override
    public void refreshView() throws SQLException {
        ArrayList<String> currentItems = new ArrayList<>();
        YEARS = TimeManager.getPlotYears();
        YEARS.sort(Collections.reverseOrder());

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
