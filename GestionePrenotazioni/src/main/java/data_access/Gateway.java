package data_access;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class Gateway {

    private Connection connection;
    private final String dbName = "database";

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
            System.out.println("Connesso al database");

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
    private DefaultTableModel setTableModelParams(Vector<Vector<Object>> data, Vector<String> columnNames){
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            //TODO: quali celle sono editabili??
            public boolean isCellEditable(int row, int column){
                return true;
            }
        };

        return model;
    }
}
