package data_access;

import controllers.ControllerDatePrenotazioni;
import controllers.MessageController;
import controllers.TablePrenotazioniController;
import views.HomePage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.nio.file.FileSystems;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class Gateway {

    private Connection connection;
    private final String dbName = "database.db";

    public Gateway() {
        connect();
    }

    // Esegue connessione al database
    public void connect() {
        try {
            // Verifica se la connessione esiste già
            if (connection != null && !connection.isClosed()) {
                disconnect();   //FIXME
            }

            // Carica il driver JDBC per SQLite
            Class.forName("org.sqlite.JDBC");

            // Cartella di destinazione del database scaricato
            String destinationFolder = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "GestionePrenotazioni" + FileSystems.getDefault().getSeparator() + "backup";

            // Verifica se il file "scaricato.db" esiste nella cartella di destinazione
            java.io.File downloadedDbFile = new java.io.File(destinationFolder, dbName);
            if (downloadedDbFile.exists()) {
                // Se il file "scaricato.db" esiste, connettiti a quel database
                connection = DriverManager.getConnection("jdbc:sqlite:" + downloadedDbFile.getAbsolutePath());
            } else {
                // Altrimenti, connettiti al database interno "database.db"
                connection = DriverManager.getConnection("jdbc:sqlite::resource:" + dbName);
            }

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
    public int updateCellData(JTable table, int row, int column, Object newValue, Object originalValue) throws SQLException {
        String updateQuery = "UPDATE Prenotazioni SET ";
        String queryValue = "";
        String updateSaldoAcconti = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataArrivo;
        LocalDate dataPartenza;
        String nomeAcconto = "";
        String acconto = "";

        switch (column){
            case 0:
                queryValue = "Piazzola = ?";

                // Verifica se è già presente una prenotazione la piazzola modificata
                dataArrivo = LocalDate.parse(table.getValueAt(row, 1).toString().toString(), dtf);
                dataPartenza = LocalDate.parse(table.getValueAt(row, 2).toString(), dtf);

                try {
                    if(ControllerDatePrenotazioni.isAlreadyBooked(dataArrivo.toString(), dataPartenza.toString(), newValue.toString())){
                        MessageController.getErrorMessage(HomePage.getFrames()[0], String.format("La piazzola %s è già prenotata per le date selezionate", newValue.toString()));

                        return -1;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                break;

            case 1:
                queryValue = "Arrivo = ?";

                // Controlla che la data sia corretta e che non ci siano già prenotazioni (considera piazzola)
                dataArrivo = LocalDate.parse(newValue.toString(), dtf);
                dataPartenza = LocalDate.parse(table.getValueAt(row, 2).toString(), dtf);

                if (dataArrivo != null && dataPartenza.isBefore(dataArrivo)) {
                    MessageController.getErrorMessage(HomePage.getFrames()[0], "La data di partenza deve essere successiva alla data di arrivo");

                    return -1;
                }

                // Verifica se è già presente una prenotazione
                String piazzolaScelta = table.getValueAt(row, 0).toString();
                try {
                    if(ControllerDatePrenotazioni.isAlreadyBooked(dataArrivo.toString(), dataPartenza.toString(), piazzolaScelta)){
                        MessageController.getErrorMessage(HomePage.getFrames()[0], String.format("La piazzola %s è già prenotata per le date selezionate", piazzolaScelta));

                        return -1;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Aggiorna anche la tabella SaldoAcconti con la nuova data di arrivo (se non è nullo l'acconto)
                acconto = (String) table.getValueAt(row, 4);
                String dataPartenzaString = table.getValueAt(row, 2).toString();
                nomeAcconto = table.getValueAt(row, 3).toString();

                if(!Objects.equals(acconto, null)){
                    updateSaldoAcconti = "UPDATE SaldoAcconti SET Arrivo = ? WHERE Nome = ? AND Acconto = ? AND Partenza = ?";

                    if(new Gateway().execUpdateQuery(updateSaldoAcconti, (String) newValue, nomeAcconto, acconto, dataPartenzaString) == 0)
                        return -1;
                }


                break;

            case 2:
                queryValue = "Partenza = ?";

                // Controlla che la data sia corretta e che non ci siano già prenotazioni (considera piazzola)
                dataPartenza = LocalDate.parse(newValue.toString(), dtf);
                dataArrivo = LocalDate.parse(table.getValueAt(row, 1).toString(), dtf);

                if (dataPartenza != null && dataPartenza.isBefore(dataArrivo)) {
                    MessageController.getErrorMessage(HomePage.getFrames()[0], "La data di partenza deve essere successiva alla data di arrivo");

                    return -1;
                }

                // Verifica se è già presente una prenotazione
                piazzolaScelta = table.getValueAt(row, 0).toString();
                try {
                    if(ControllerDatePrenotazioni.isAlreadyBooked(dataArrivo.toString(), dataPartenza.toString(), piazzolaScelta)){
                        MessageController.getErrorMessage(HomePage.getFrames()[0], String.format("La piazzola %s è già prenotata per le date selezionate", piazzolaScelta));

                        return -1;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                // Aggiorna anche la tabella SaldoAcconti con la nuova data di partenza (se non è nullo l'acconto)
                acconto = (String) table.getValueAt(row, 4);
                String dataArrivoString = table.getValueAt(row, 1).toString();
                nomeAcconto = table.getValueAt(row, 3).toString();

                if(!Objects.equals(acconto, null)) {
                    updateSaldoAcconti = "UPDATE SaldoAcconti SET Partenza = ? WHERE Nome = ? AND Arrivo = ? AND Acconto = ?";

                    if(new Gateway().execUpdateQuery(updateSaldoAcconti, (String) newValue, nomeAcconto, dataArrivoString, acconto) == 0)
                        return -1;
                }

                break;

            case 3:
                queryValue = "Nome = ?";

                // Aggiorna anche la tabella SaldoAcconti con la nuova data di partenza (se non è nullo l'acconto)
                dataPartenzaString = table.getValueAt(row, 2).toString();
                dataArrivoString = table.getValueAt(row, 1).toString();
                acconto = (String) table.getValueAt(row, 4);

                if(!Objects.equals(acconto, null)) {
                    updateSaldoAcconti = "UPDATE SaldoAcconti SET Nome = ? WHERE Arrivo = ? AND Partenza = ? AND Acconto = ?";

                    if(new Gateway().execUpdateQuery(updateSaldoAcconti, (String) newValue, dataArrivoString, dataPartenzaString, acconto) == 0)
                        return -1;
                }

                // Sostituisce il vecchio nome con quello nuovo
                TablePrenotazioniController.refreshAllNames();
                ArrayList<String> listaNomi = TablePrenotazioniController.getAllNames();

                for (int i = 0; i < listaNomi.size(); i++) {
                    if (Objects.equals(listaNomi.get(i), originalValue)) {
                        listaNomi.set(i, newValue.toString());
                        break;
                    }
                }

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
                    updateSaldoAcconti = "UPDATE SaldoAcconti SET Acconto = ?, Saldato = 'non saldato' WHERE Nome = ? AND Arrivo = ? AND Partenza = ?";

                    rs.close();

                    if(new Gateway().execUpdateQuery(updateSaldoAcconti, (String) newValue, nome, arrivo, partenza) == 0)
                        return -1;
                } else {
                    String insertNewAcconto = "INSERT INTO SaldoAcconti (Nome, Arrivo, Partenza, Acconto, Saldato) VALUES (?, ?, ?, ?, 'non saldato')";

                    if(new Gateway().execUpdateQuery(insertNewAcconto, nome, arrivo, partenza, (String) newValue) == 0)
                        return -1;
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

    //TODO: sposta i metodi di costruzione delle tabelle nei rispettivi controller per non intasare il gateway!

    // Costruisce il table model della tabella Prenotazioni passando il result set della query
    public DefaultTableModel buildPrenotazioniTableModel(ResultSet rs) throws SQLException {
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

        DefaultTableModel tableModel = setPrenotazioniTableModelParams(data, columnNames);
        return tableModel;
    }

    // Modifica i metodi del DefaultTableModel per la modifica della tabella delle prenotazioni
    private DefaultTableModel setPrenotazioniTableModelParams(Vector<Vector<Object>> data, Vector<String> columnNames){
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        return model;
    }

    public boolean isConnectedToDatabase(String databasePath) {
        try {
            // Esegui una query per verificare la connessione al database specificato
            String checkQuery = "SELECT 1 FROM Prenotazioni WHERE 1 = 0";
            connect();
            PreparedStatement statement = connection.prepareStatement(checkQuery);
            statement.executeQuery();

            // Se l'esecuzione della query non ha causato errori, siamo connessi al database
            return true;
        } catch (SQLException e) {
            // Se si verifica un errore, non siamo connessi al database
            return false;
        }
    }
}
