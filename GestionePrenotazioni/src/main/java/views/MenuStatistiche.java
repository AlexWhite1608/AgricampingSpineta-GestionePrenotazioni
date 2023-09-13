package views;

import observer.PlotControllerObservers;
import observer.PrenotazioniObservers;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;
import plot_stats_controllers.MezziPlotController;
import plot_stats_controllers.NazioniPlotController;
import plot_stats_controllers.PresenzePlotController;
import table_stats_controllers.TableAdvancedStatsController;
import table_stats_controllers.TableMezziController;
import table_stats_controllers.TableNazioniController;
import table_stats_controllers.TablePresenzeController;
import utils.ListOfNations;
import utils.TimeManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    private JButton btnAdvStats;

    // Specifica la larghezza desiderata per tutte le colonne
    private static final int larghezzaColonna = 200;

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
        btnAdvStats = new JButton();
        btnAdvStats.setFocusPainted(false);
        Icon icon = new ImageIcon((Objects.requireNonNull(getClass().getResource("/zoom-in-24x24.png"))));
        btnAdvStats.setIcon(icon);
        btnAdvStats.setToolTipText("Statistiche avanzate");

        JPanel pnlChooseYears = new JPanel(new FlowLayout());
        pnlChooseYears.add(lblPlotYears);
        pnlChooseYears.add(cbPlotYears);

        pnlButtonsToolbar.add(pnlChooseYears, BorderLayout.WEST);
        pnlButtonsToolbar.add(btnAdvStats, BorderLayout.EAST);

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

        // Apre il frame di visualizzazione delle statistiche avanzate
        btnAdvStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setupAdvancedStats();
                } catch (SQLException ex) {
                    System.err.println("Impossibile calcolare statistiche avanzate " + ex.getMessage());;
                }
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

        if (tblPresenze.getColumnCount() <= 6) {
            tblPresenze.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            tblPresenze.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        for (int i = 0; i < tblPresenze.getColumnCount(); i++) {
            tblPresenze.getColumnModel().getColumn(i).setPreferredWidth(larghezzaColonna);
        }

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

        if (tblMezzi.getColumnCount() <= 6) {
            tblMezzi.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            tblMezzi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        for (int i = 0; i < tblMezzi.getColumnCount(); i++) {
            tblMezzi.getColumnModel().getColumn(i).setPreferredWidth(larghezzaColonna);
        }

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

        if (tblNazioni.getColumnCount() <= 6) {
            tblNazioni.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            tblNazioni.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        for (int i = 0; i < tblNazioni.getColumnCount(); i++) {
            tblNazioni.getColumnModel().getColumn(i).setPreferredWidth(larghezzaColonna);
        }

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

    // Setup statistiche avanzate
    private void setupAdvancedStats() throws SQLException {
        JDialog dialogAdvStats = new JDialog();
        dialogAdvStats.setTitle("Statistiche avanzate");
        dialogAdvStats.setLayout(new BorderLayout());
        dialogAdvStats.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /* --- Panel statistiche di sinistra --- */
        JPanel pnlSxStats = new JPanel();
        dialogAdvStats.add(pnlSxStats, BorderLayout.NORTH);
        pnlSxStats.setLayout(new FlowLayout());

        // Labels
        JLabel lblMesePiuPresenze = new JLabel("Mese con più presenze: " + TableAdvancedStatsController.getMesePiuPresenze());
        JLabel lblMezzoPiuUsato = new JLabel("Mezzo più usato: " + TableAdvancedStatsController.getMezzoPiuUsato());
        JLabel durataMediaSoggiorno = new JLabel("Durata media del soggiorno: " + TableAdvancedStatsController.getDurataMediaSoggiorno());

        // Aggiungi un margine di 10 pixel a tutte le label
        lblMesePiuPresenze.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblMezzoPiuUsato.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        durataMediaSoggiorno.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font font = new Font(lblMesePiuPresenze.getFont().getName(), Font.BOLD, 15);
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        Border emptyBorder = new EmptyBorder(5, 10, 5, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);
        lblMesePiuPresenze.setBorder(compoundBorder);
        lblMesePiuPresenze.setFont(font);
        lblMesePiuPresenze.setForeground(Color.BLACK);

        lblMezzoPiuUsato.setBorder(compoundBorder);
        lblMezzoPiuUsato.setFont(font);
        lblMezzoPiuUsato.setForeground(Color.BLACK);

        durataMediaSoggiorno.setBorder(compoundBorder);
        durataMediaSoggiorno.setFont(font);
        durataMediaSoggiorno.setForeground(Color.BLACK);

        pnlSxStats.add(lblMesePiuPresenze);
        pnlSxStats.add(lblMezzoPiuUsato);
        pnlSxStats.add(durataMediaSoggiorno);
        /* ... ...*/

        /* --- Panel statistiche su tabella --- */
        JPanel pnlTableStats = new JPanel();
        pnlTableStats.setLayout(new BorderLayout());

        // Creazione della toolbar
        JToolBar toolbarAdvStats = new JToolBar();
        toolbarAdvStats.setLayout(new BorderLayout());
        toolbarAdvStats.setFloatable(false);
        pnlTableStats.add(toolbarAdvStats, BorderLayout.NORTH);

        // ComboBox scelta anno (senza "Tutto")
        ArrayList<String> listaAnni = TimeManager.getPrenotazioniYears();
        listaAnni.removeIf(el -> Objects.equals(el, "Tutto"));
        JComboBox<String> cbSceltaAnno = new JComboBox(listaAnni.toArray());
        cbSceltaAnno.setFocusable(false);
        ((JLabel) cbSceltaAnno.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // ComboBox scelta mese
        JComboBox<String> cbSceltaMese = new JComboBox(TimeManager.getYearMonths().toArray());
        cbSceltaMese.setFocusable(false);
        ((JLabel) cbSceltaMese.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // TextField scelta nazione
        ArrayList<String> listaNazioni = ListOfNations.getListaNazioni();
        JTextField tfSceltaNazione = new JTextField();
        tfSceltaNazione.setPreferredSize(cbSceltaMese.getPreferredSize());
        AutoCompleteDecorator.decorate(tfSceltaNazione, listaNazioni, false, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);

        // Panel scelta anno/mese
        JPanel pnlSceltaAnnoMese = new JPanel(new FlowLayout());
        pnlSceltaAnnoMese.add(cbSceltaMese);
        pnlSceltaAnnoMese.add(cbSceltaAnno);

        // Panel scelta nazione
        JPanel pnlSceltaNazione = new JPanel(new FlowLayout());
        pnlSceltaNazione.add(new JLabel("Scegli la nazione: "));
        pnlSceltaNazione.add(tfSceltaNazione);

        toolbarAdvStats.add(pnlSceltaAnnoMese, BorderLayout.WEST);
        toolbarAdvStats.add(pnlSceltaNazione, BorderLayout.EAST);

        // Panel nazioni
        JPanel pnlTableStatsNazioni = new JPanel(new BorderLayout());
        JTable tableStatsNazioni = new JTable();
        tableStatsNazioni.setGridColor(Color.BLACK);
        tableStatsNazioni.getTableHeader().setReorderingAllowed(false);

        // Imposta il tableModel
        String annoScelto = cbSceltaAnno.getSelectedItem().toString();
        String meseScelto = cbSceltaMese.getSelectedItem().toString();
        TableAdvancedStatsController.setTableModelNazioni(tableStatsNazioni, annoScelto, meseScelto);

        // Aggiorna tabella quando si cambia il mese
        cbSceltaMese.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Imposta il tableModel
                String annoScelto = cbSceltaAnno.getSelectedItem().toString();
                String meseScelto = cbSceltaMese.getSelectedItem().toString();

                try {
                    TableAdvancedStatsController.setTableModelNazioni(tableStatsNazioni, annoScelto, meseScelto);
                } catch (SQLException ex) {
                    System.err.println("Impossibile aggiornare tabella " + ex.getMessage());;
                }
            }
        });

        // Aggiorna tabella quando si cambia l'anno
        cbSceltaAnno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Imposta il tableModel
                String annoScelto = cbSceltaAnno.getSelectedItem().toString();
                String meseScelto = cbSceltaMese.getSelectedItem().toString();

                try {
                    TableAdvancedStatsController.setTableModelNazioni(tableStatsNazioni, annoScelto, meseScelto);
                } catch (SQLException ex) {
                    System.err.println("Impossibile aggiornare tabella " + ex.getMessage());;
                }
            }
        });

        // Aggiungi un margine esterno al pannello delle tabelle
        pnlTableStatsNazioni.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlTableStatsNazioni.add(new JScrollPane(tableStatsNazioni, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        // Panel mezzi per nazioni
        JPanel pnlTableStatsMezziNazioni = new JPanel(new BorderLayout());
        JTable tableStatsMezziNazioni = new JTable();
        tableStatsMezziNazioni.setGridColor(Color.BLACK);
        tableStatsMezziNazioni.getTableHeader().setReorderingAllowed(false);

        // Aggiungi un margine esterno al pannello delle tabelle
        pnlTableStatsMezziNazioni.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlTableStatsMezziNazioni.add(new JScrollPane(tableStatsMezziNazioni, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        // Crea un pannello che contiene entrambe le tabelle affiancate
        JPanel pnlAffiancate = new JPanel(new GridLayout(1, 2));
        pnlAffiancate.add(pnlTableStatsNazioni);
        pnlAffiancate.add(pnlTableStatsMezziNazioni);

        pnlTableStats.add(pnlAffiancate, BorderLayout.CENTER);
        dialogAdvStats.add(pnlTableStats, BorderLayout.CENTER);
        /* ... ... */


        // Implementa switch dinamico
//                cbListaTabelle.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        String selectedTable = cbListaTabelle.getSelectedItem().toString();
//
//                        // Mostra la JScrollPane corrispondente alla tabella selezionata
//                        ((CardLayout) pnlTableFocusTables.getLayout()).show(pnlTableFocusTables, selectedTable);
//                    }
//                });

        dialogAdvStats.pack();
        dialogAdvStats.setMinimumSize(new Dimension(1000, 600));
        dialogAdvStats.setLocationRelativeTo(null);
        dialogAdvStats.setResizable(false);
        dialogAdvStats.setModal(true);
        dialogAdvStats.setVisible(true);
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

        if (tblPresenze.getColumnCount() <= 6) {
            tblPresenze.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            tblPresenze.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        for (int i = 0; i < tblPresenze.getColumnCount(); i++) {
            tblPresenze.getColumnModel().getColumn(i).setPreferredWidth(larghezzaColonna);
        }

        if (tblNazioni.getColumnCount() <= 6) {
            tblNazioni.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            tblNazioni.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        for (int i = 0; i < tblNazioni.getColumnCount(); i++) {
            tblNazioni.getColumnModel().getColumn(i).setPreferredWidth(larghezzaColonna);
        }

        if (tblMezzi.getColumnCount() <= 6) {
            tblMezzi.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            tblMezzi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        for (int i = 0; i < tblMezzi.getColumnCount(); i++) {
            tblMezzi.getColumnModel().getColumn(i).setPreferredWidth(larghezzaColonna);
        }
    }

    @Override
    public void refreshPiazzola() throws SQLException {

    }

    public static ArrayList<PlotControllerObservers> getPlotControllersObserversList() {
        return plotControllersObserversList;
    }
}
