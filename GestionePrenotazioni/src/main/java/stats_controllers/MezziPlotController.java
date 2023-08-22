package stats_controllers;

import observer.PlotControllerObservers;
import observer.PrenotazioniObservers;

import java.sql.SQLException;

public class MezziPlotController implements PlotController, PrenotazioniObservers, PlotControllerObservers {


    @Override
    public void refreshPlot() throws SQLException {

    }

    @Override
    public void setSelectedYear(String year) {

    }

    @Override
    public void refreshView() throws SQLException {

    }

    @Override
    public void refreshPiazzola() throws SQLException {

    }

    @Override
    public void createPlot() throws SQLException {

    }

    @Override
    public void changeTitlePlot(String newYear) throws SQLException {

    }
}
