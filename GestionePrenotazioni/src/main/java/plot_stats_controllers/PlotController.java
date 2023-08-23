package plot_stats_controllers;

import java.sql.SQLException;

public interface PlotController {

    void createPlot() throws SQLException;

    void changeTitlePlot(String newYear) throws SQLException;

}
