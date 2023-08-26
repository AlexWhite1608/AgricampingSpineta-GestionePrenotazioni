package plot_stats_controllers;

import datasets.DatasetPresenzeController;
import observer.PlotControllerObservers;
import observer.PrenotazioniObservers;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import plot_stats_controllers.PlotController;
import utils.TableConstants;
import views.MenuPrenotazioni;
import views.MenuStatistiche;

import javax.swing.*;
import java.sql.SQLException;

public class PresenzePlotController implements PlotController, PrenotazioniObservers, PlotControllerObservers {

    private final JPanel pnlPlot;
    private JFreeChart barChart;
    private final String PLOT_TYPE = "Presenze ";
    private static String YEAR = "";

    public PresenzePlotController(JPanel pnlPlot, String year) {
        this.pnlPlot = pnlPlot;
        YEAR = year;

        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
        MenuStatistiche.getPlotControllersObserversList().add(this);
    }

    // Costruisce il grafico
    @Override
    public void createPlot() throws SQLException {
        DefaultCategoryDataset dataset = DatasetPresenzeController.getPlotDataset(YEAR);
        barChart = ChartFactory.createBarChart(
                PLOT_TYPE + YEAR,
                "Mese",
                "Presenze",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, TableConstants.TABELLA_PRESENZE_COLOR);
        barChart.setBackgroundPaint(pnlPlot.getBackground());

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(true); //

        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setMaximumBarWidth(0.1);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());

        // Collega il plot al JPanel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setMouseWheelEnabled(false);
        pnlPlot.add(chartPanel);
    }

    // Richiamata per modificare l'anno del plot quando modificato
    @Override
    public void changeTitlePlot(String newYear) throws SQLException {
        YEAR = newYear;

        refreshView();
    }

    // Viene chiamato quando si aggiunge/modifica/elimina una nuova prenotazione
    @Override
    public void refreshView() throws SQLException {

        // Ricostruisce il grafico con i nuovi valori
        barChart.getCategoryPlot().setDataset(DatasetPresenzeController.getPlotDataset(YEAR));
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setMouseWheelEnabled(false);
        pnlPlot.removeAll();
        pnlPlot.add(chartPanel);
        pnlPlot.revalidate();
        pnlPlot.repaint();
    }

    // Viene chiamato quando si modifica l'anno di visualizzazione dei grafici
    @Override
    public void refreshPlot() throws SQLException {

        // Ricostruisce il grafico con i nuovi valori
        barChart.getCategoryPlot().setDataset(DatasetPresenzeController.getPlotDataset(YEAR));
        barChart.setTitle(PLOT_TYPE + YEAR);
        ChartPanel chartPanel = new ChartPanel(barChart);
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

    // Non utilizzato!
    @Override
    public void refreshPiazzola() throws SQLException {

    }
}
