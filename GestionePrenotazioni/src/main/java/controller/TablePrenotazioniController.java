package controller;

import data_access.Gateway;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TablePrenotazioniController {

    private JTable tblPrenotazioni;
    private final Gateway gateway;

    public TablePrenotazioniController(JTable tblPrenotazioni) {
        this.tblPrenotazioni = tblPrenotazioni;
        this.gateway  = new Gateway();
    }

    // Mostra la visualizzazione iniziale della tabella (con il filtro dell'anno)
    public JTable initView() throws SQLException {
        String initialQuery = "SELECT * FROM Prenotazioni";

        //TODO: nella query devi filtrare gli anni della combobox!!
        ResultSet resultSet = this.gateway.execSelectQuery(initialQuery);
        tblPrenotazioni = new JTable(gateway.buildCustomTableModel(resultSet));

        return tblPrenotazioni;
    }

    // Ricarica la visualizzazione della tabella con la query fornita
    public JTable refreshTable(String query) throws SQLException {
        ResultSet resultSet = gateway.execSelectQuery(query);
        tblPrenotazioni = new JTable(gateway.buildCustomTableModel(resultSet));

        return tblPrenotazioni;
    }
}
