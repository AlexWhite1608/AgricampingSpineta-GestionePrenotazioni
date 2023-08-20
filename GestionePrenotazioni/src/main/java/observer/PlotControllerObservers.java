package observer;

import java.sql.SQLException;

public interface PlotControllerObservers {
    void refreshPlot() throws SQLException;

    void setSelectedYear(String year);
}
