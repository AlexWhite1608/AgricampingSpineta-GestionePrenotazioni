package views;

import stats_controllers.TablePresenzeController;
import utils.TimeManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class MenuStatistiche extends JPanel {

    private JPanel mainPanelStatistiche;
    private JPanel pnlPresenze;
    private JPanel pnlMezzi;
    private JPanel pnlNazioni;
    private JPanel pnlToolbar;
    private JToolBar toolBar;
    private JTable tblPresenze;
    private JTable tblMezzi;
    private JTable tblNazioni;

    // Lista degli anni
    private final ArrayList<String> YEARS = TimeManager.getPlotYears();

    public MenuStatistiche() {

        createUIComponents();
        setupToolbar();
        setupTablesPresenze();
        setupTableMezzi();
        setupTableNazioni();

        setLayout(new BorderLayout());
        add(pnlToolbar, BorderLayout.NORTH);
        add(mainPanelStatistiche, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelStatistiche = new JPanel();
        mainPanelStatistiche.setLayout(new GridLayout(3, 1));

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
        JComboBox cbPlotYears = new JComboBox<>(YEARS.toArray());
        cbPlotYears.setFocusable(false);
        ((JLabel) cbPlotYears.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        pnlButtonsToolbar.add(lblPlotYears);
        pnlButtonsToolbar.add(cbPlotYears);

        toolBar.setFloatable(false);
        toolBar.add(pnlButtonsToolbar, BorderLayout.CENTER);
        pnlToolbar.add(toolBar, BorderLayout.CENTER);
    }

    // Setup tabelle presenze
    private void setupTablesPresenze() {
        pnlPresenze = new JPanel(new GridLayout(1, 2));

        // Imposta il bordo
        Border blackline = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "Presenze", TitledBorder.LEFT, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        pnlPresenze.setBorder(titledBorder);

        JPanel pnlPlotPresenze = new JPanel(new BorderLayout());
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

        pnlPresenze.add(pnlPlotPresenze);
        pnlPresenze.add(pnlTablesPresenze);
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
}
