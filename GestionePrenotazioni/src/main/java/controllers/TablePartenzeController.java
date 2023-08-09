package controllers;

import data_access.Gateway;
import observer.PrenotazioniObservers;

import javax.swing.*;
import java.sql.SQLException;

public class TablePartenzeController implements PrenotazioniObservers{

    private static JTable tabellaPartenze;
    private static Gateway gateway;

    public TablePartenzeController(JTable tabellaPartenze) {

        TablePartenzeController.tabellaPartenze = tabellaPartenze;
        TablePartenzeController.gateway = new Gateway();
    }


    // Metodo che crea la vista per la tabella Partenze
    private void createPartenzeView() throws SQLException {
        //TODO: non puoi mettere i parametri nella definizione della vista ricordatelo!
        String viewQueryPartenze = "DROP VIEW IF EXISTS Partenze; " +
                                   "CREATE VIEW Partenze AS " +
                                   "SELECT *, DATEDIFF(STR_TO_DATE(Partenza, '%d/%m/%Y'), STR_TO_DATE(Arrivo, '%d/%m/%Y')) AS `N° Notti` " +
                                   "FROM Prenotazioni WHERE Partenza = DATE_FORMAT(NOW(), '%d/%m/%Y');";

        // Crea la vista nel database
        gateway.execUpdateQuery(viewQueryPartenze);
    }

    @Override
    public void refreshView() throws SQLException {
        //TODO: implementa refresh!
    }

    @Override
    public void refreshPiazzola() throws SQLException {}
}
