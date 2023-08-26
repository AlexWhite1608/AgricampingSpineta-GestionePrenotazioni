package plot_stats_controllers;

import datasets.DatasetMezziController;
import datasets.DatasetNazioniController;
import observer.PlotControllerObservers;
import observer.PrenotazioniObservers;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import utils.CustomPieSectionLabelGenerator;
import utils.TableConstants;
import views.MenuPrenotazioni;
import views.MenuStatistiche;

import javax.swing.*;
import java.sql.SQLException;

public class NazioniPlotController implements PlotController, PrenotazioniObservers, PlotControllerObservers {

    private final JPanel pnlPlot;
    private JFreeChart pieChart;
    private final String PLOT_TYPE = "Nazioni ";
    private static String YEAR = "";

    public NazioniPlotController(JPanel pnlPlot, String year) {
        this.pnlPlot = pnlPlot;

        YEAR = year;

        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
        MenuStatistiche.getPlotControllersObserversList().add(this);
    }

    // Viene chiamato quando si modifica l'anno di visualizzazione dei grafici
    @Override
    public void refreshPlot() throws SQLException {

        // Ricostruisce il grafico con i nuovi valori
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setDataset(DatasetNazioniController.getPlotDataset(YEAR));
        plot.setLabelGenerator(new CustomPieSectionLabelGenerator(DatasetNazioniController.getPlotDataset(YEAR)));
        pieChart.setTitle(PLOT_TYPE + YEAR);
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setMouseWheelEnabled(false);
        pnlPlot.removeAll();
        pnlPlot.add(chartPanel);
        pnlPlot.revalidate();
        pnlPlot.repaint();
    }

    @Override
    public void setSelectedYear(String year) {
        YEAR = year;
    }

    // Viene chiamato quando si aggiunge/modifica/elimina una nuova prenotazione
    @Override
    public void refreshView() throws SQLException {

        // Ricostruisce il grafico con i nuovi valori
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setDataset(DatasetNazioniController.getPlotDataset(YEAR));
        plot.setLabelGenerator(new CustomPieSectionLabelGenerator(DatasetNazioniController.getPlotDataset(YEAR)));
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setMouseWheelEnabled(false);
        pnlPlot.removeAll();
        pnlPlot.add(chartPanel);
        pnlPlot.revalidate();
        pnlPlot.repaint();
    }

    // Non utilizzato!
    @Override
    public void refreshPiazzola() throws SQLException {

    }

    // Costruisce il grafico
    @Override
    public void createPlot() throws SQLException {
        DefaultPieDataset dataset = DatasetNazioniController.getPlotDataset(YEAR);
        pieChart = ChartFactory.createPieChart(
                PLOT_TYPE + YEAR,
                dataset,
                false, true, false);

        // Aggiungi il generatore di etichette personalizzato per mostrare le percentuali nei tooltip
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new CustomPieSectionLabelGenerator(dataset));
        pieChart.setBackgroundPaint(pnlPlot.getBackground());

        // Collega il plot al JPanel
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setMouseWheelEnabled(false);
        pnlPlot.add(chartPanel);
    }

    // Richiamata per modificare l'anno del plot quando modificato
    @Override
    public void changeTitlePlot(String newYear) throws SQLException {
        YEAR = newYear;

        refreshView();
    }
}
