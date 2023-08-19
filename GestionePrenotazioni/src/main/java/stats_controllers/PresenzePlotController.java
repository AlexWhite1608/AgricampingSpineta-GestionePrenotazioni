package stats_controllers;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

import javax.swing.*;
import java.sql.SQLException;

public class PresenzePlotController implements PlotController{

    private final JPanel pnlPlot;
    private final String PLOT_TYPE = "Presenze ";
    private String YEAR = "";

    public PresenzePlotController(JPanel pnlPlot, String year) {
        this.pnlPlot = pnlPlot;
        this.YEAR = year;
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
        //plot.getRenderer().setSeriesPaint(0, new Color(0,255,0));

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); //X-Axis Labels will be inclined at 45degree
        xAxis.setLabel("Mesi");

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(true); // Y-Axis range will be set automatically based on the supplied data
        rangeAxis.setLabel("Presenze");

        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setMaximumBarWidth(.1); //making sure that if there is only one bar, it does not occupy the entire available width

        // Collega il plot al JPanel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setMouseWheelEnabled(true);
        pnlPlot.add(chartPanel);
    }

    @Override
    public void refreshPlot() {
    }

    // Richiamata per modificare l'anno del plot quando modificato
    public void changeTitlePlot(String newYear){
        this.YEAR = newYear;
    }
}
