package observer;

import java.sql.SQLException;

public interface StopTableEditObservers {
    void stopEditNotify() throws SQLException;
}
