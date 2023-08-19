package stats_controllers;

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

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class PresenzePlotController implements PlotController, PrenotazioniObservers {

    private final JPanel pnlPlot;
    private final String PLOT_TYPE = "Presenze ";
    private String YEAR = "";

    public PresenzePlotController(JPanel pnlPlot, String year) {
        this.pnlPlot = pnlPlot;
        this.YEAR = year;

        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
    }

    // Costruisce il grafico
    @Override
    public void createPlot() throws SQLException {
        JFreeChart barChart = ChartFactory.createBarChart(
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
    public void changeTitlePlot(String newYear){
        this.YEAR = newYear;
    }

    @Override
    public void refreshView() throws SQLException {
        //TODO: implementa ricarica del grafico
    }

    @Override
    public void refreshPiazzola() throws SQLException {

    }
}
