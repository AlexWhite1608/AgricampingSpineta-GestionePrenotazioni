package data_access;

import controllers.MessageController;
import controllers.TablePrenotazioniController;
import views.HomePage;
import views.MenuArriviPartenze;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class Gateway {

    private Connection connection;
    private final String dbName = "database.db";

    public Gateway() {
        connect();
    }

    // Esegue connessione al database
    private void connect(){
        try {
            // Verifica se la connessione esiste già
            if (connection != null && !connection.isClosed()) {
                return;
            }

            // Carica il driver JDBC per SQLite
            Class.forName("org.sqlite.JDBC");

            // Apre la connessione al database SQLite
            connection = DriverManager.getConnection("jdbc:sqlite::resource:" + dbName);

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC driver non trovato");
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println("Impossibile connettersi al database");
            e.printStackTrace();
        }

    }

    // Esegue disconnessione dal database
    public void disconnect() {
        try {
            // Verifica se la connessione esiste e chiude la connessione
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnesso dal database");
            }

        } catch (SQLException e) {
            System.out.println("Impossibile disconnettersi dal database");
            e.printStackTrace();
        }
    }

    // Esegue la query di select fornita ritornando il resultset
    public ResultSet execSelectQuery(String query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        return statement.executeQuery();
    }

    // Esegue la query di select fornita con parametri ritornando il resultset
    public ResultSet execSelectQuery(String query, String... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        // Imposta i parametri nella query
        for (int i = 0; i < params.length; i++) {
            statement.setString(i + 1, params[i]);
        }

        return statement.executeQuery();
    }

    // Esegue query di modifica della tabella
    public int execUpdateQuery(String query, String... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        // Imposta i parametri nella query
        for (int i = 0; i < params.length; i++) {
            statement.setString(i + 1, params[i]);
        }

        return statement.executeUpdate();
    }

    // Salva il valore modificato nella tabella all'interno del database
    public int updateCellData(JTable table, int row, int column, Object newValue) throws SQLException {
        String updateQuery = "UPDATE Prenotazioni SET ";
        String queryValue = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataArrivo;
        LocalDate dataPartenza;

        switch (column){
            case 0:
                queryValue = "Piazzola = ?";

                //TODO: controlla che non ci siano già prenotazioni per la nuova piazzola!
                dataArrivo = LocalDate.parse(table.getValueAt(row, 1).toString().toString(), dtf);
                dataPartenza = LocalDate.parse(table.getValueAt(row, 2).toString(), dtf);

                try {
                    if(TablePrenotazioniController.isAlreadyBooked(dataArrivo.toString(), dataPartenza.toString(), newValue.toString())){
                        MessageController.getErrorMessage(HomePage.getFrames()[0], String.format("La piazzola %s è già prenotata per le date selezionate", newValue.toString()));

                        return -1;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                break;

            case 1:
                queryValue = "Arrivo = ?";

                // TODO: controlla che la data sia corretta e che non ci siano già prenotazioni (considera piazzola)
                dataArrivo = LocalDate.parse(newValue.toString(), dtf);
                dataPartenza = LocalDate.parse(table.getValueAt(row, 2).toString(), dtf);

                if (dataArrivo != null && dataPartenza.isBefore(dataArrivo)) {
                    MessageController.getErrorMessage(HomePage.getFrames()[0], "La data di partenza deve essere successiva alla data di arrivo");

                    return -1;
                }

                // Verifica se è già presente una prenotazione
                String piazzolaScelta = table.getValueAt(row, 0).toString();
                try {
                    if(TablePrenotazioniController.isAlreadyBooked(dataArrivo.toString(), dataPartenza.toString(), piazzolaScelta)){
                        MessageController.getErrorMessage(HomePage.getFrames()[0], String.format("La piazzola %s è già prenotata per le date selezionate", piazzolaScelta));

                        return -1;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                break;

            case 2:
                queryValue = "Partenza = ?";

                // TODO: controlla che la data sia corretta e che non ci siano già prenotazioni (considera piazzola)
                dataPartenza = LocalDate.parse(newValue.toString(), dtf);
                dataArrivo = LocalDate.parse(table.getValueAt(row, 1).toString(), dtf);

                if (dataPartenza != null && dataPartenza.isBefore(dataArrivo)) {
                    MessageController.getErrorMessage(HomePage.getFrames()[0], "La data di partenza deve essere successiva alla data di arrivo");

                    return -1;
                }

                // Verifica se è già presente una prenotazione
                piazzolaScelta = table.getValueAt(row, 0).toString();
                try {
                    if(TablePrenotazioniController.isAlreadyBooked(dataArrivo.toString(), dataPartenza.toString(), piazzolaScelta)){
                        MessageController.getErrorMessage(HomePage.getFrames()[0], String.format("La piazzola %s è già prenotata per le date selezionate", piazzolaScelta));

                        return -1;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                break;

            case 3:
                queryValue = "Nome = ?";

                break;

            case 4:
                queryValue = "Acconto = ?";

                // Aggiunge il simbolo di euro per l'acconto
                if(!newValue.toString().contains("€ "))
                    newValue = "€ " + newValue;

                String nome = table.getValueAt(row, 3).toString();
                String arrivo = table.getValueAt(row, 1).toString();
                String partenza = table.getValueAt(row, 2).toString();

                // Se esisteva già l'acconto faccio update, altrimenti inserisco il nuovo valore!
                ResultSet rs = new Gateway().execSelectQuery("SELECT * FROM SaldoAcconti WHERE Nome = ? AND Arrivo = ? AND Partenza = ?", nome, arrivo, partenza);

                if(rs.next()){
                    String updateSaldoAcconti = "UPDATE SaldoAcconti SET Acconto = ?, Saldato = 'non saldato' WHERE Nome = ? AND Arrivo = ? AND Partenza = ?";

                    rs.close();

                    if(new Gateway().execUpdateQuery(updateSaldoAcconti, (String) newValue, nome, arrivo, partenza) == 0)
                        System.err.println("Impossibile aggiornare tabella SaldoAcconti");
                } else {
                    String insertNewAcconto = "INSERT INTO SaldoAcconti (Nome, Arrivo, Partenza, Acconto, Saldato) VALUES (?, ?, ?, ?, 'non saldato')";

                    if(new Gateway().execUpdateQuery(insertNewAcconto, nome, arrivo, partenza, (String) newValue) == 0)
                        System.err.println("Impossibile aggiornare tabella SaldoAcconti");
                }

                break;

            case 5:
                queryValue = "Info = ?";

                break;

            case 6:
                queryValue = "Telefono = ?";

                break;

            case 7:
                queryValue = "Email = ?";

                break;

            default:
                throw new RuntimeException("Errore modifica della tabella!");

        }

        updateQuery = updateQuery + queryValue + " WHERE Id = " + table.getModel().getValueAt(row, 0).toString();
        table.repaint();

        if(newValue != null)
            return execUpdateQuery(updateQuery, (String) newValue);
        else
            return 0;
    }

    // Costruisce il table model passando il result set della query
    public DefaultTableModel buildCustomTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Nome delle colonne
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Data
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        DefaultTableModel tableModel = setTableModelParams(data, columnNames);
        return tableModel;
    }

    // Modifica i metodi del DefaultTableModel per la modifica della tabella
    //TODO: implementare
    private DefaultTableModel setTableModelParams(Vector<Vector<Object>> data, Vector<String> columnNames){
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        return model;
    }
}
