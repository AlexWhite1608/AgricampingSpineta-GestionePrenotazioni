package observer;

import java.sql.SQLException;

public interface PrenotazioniObservers {
    void refreshView() throws SQLException;

    void refreshPiazzola();
}
