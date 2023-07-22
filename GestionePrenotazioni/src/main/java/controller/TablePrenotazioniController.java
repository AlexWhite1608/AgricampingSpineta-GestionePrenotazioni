package controller;

import data_access.Gateway;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TablePrenotazioniController {

    private JTable tblPrenotazioni;
    private final Gateway gateway;

    public TablePrenotazioniController(JTable tblPrenotazioni) {
        this.tblPrenotazioni = tblPrenotazioni;
        this.gateway  = new Gateway();
    }

    // Mostra la visualizzazione iniziale della tabella (con il filtro dell'anno)
    public JTable initView(JComboBox cbFiltro) throws SQLException {
        //TODO: nella query devi filtrare gli anni della combobox!!

        // Ottiene il valore selezionato nella comboBox
        String selectedFilter = Objects.requireNonNull(cbFiltro.getSelectedItem()).toString();

        String initialQuery = "";
        if(Objects.equals(selectedFilter, "Tutto")) {
            initialQuery = "SELECT * FROM Prenotazioni";
        } else {
            initialQuery = String.format("SELECT * FROM Prenotazioni WHERE strftime('%%Y', Arrivo) >= '%s-01-01' AND strftime('%%Y', Partenza) <= '%s-12-31';", selectedFilter, selectedFilter);
        }

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
