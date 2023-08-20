package stats_controllers;

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

    // Metodo per verificare se un valore è presente nella cb degli anni
    public static boolean containsItem(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }

    // Costruisce il grafico
    @Override
    public void createPlot() throws SQLException {
        barChart = ChartFactory.createBarChart(
                PLOT_TYPE + YEAR,
                "Category",
                "Score",
                DatasetPresenzeController.getPlotDataset(YEAR),
                PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, TableConstants.TABELLA_PRESENZE_COLOR);
        barChart.setBackgroundPaint(pnlPlot.getBackground());

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); //X-Axis Labels will be inclined at 45degree
        xAxis.setLabel("Mese");

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(true); // Y-Axis range will be set automatically based on the supplied data
        rangeAxis.setLabel("Presenze");

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
        this.YEAR = newYear;

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

    @Override
    public void refreshPiazzola() throws SQLException {

    }
}
