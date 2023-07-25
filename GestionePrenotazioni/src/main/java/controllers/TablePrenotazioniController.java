package controllers;

import data_access.Gateway;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class TablePrenotazioniController {

    // Costanti per i renderer della tabella
    private final Font CELL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Color ALTERNATE_CELL_COLOR = new Color(220, 232, 234);
    private final Color SELECTION_COLOR = new Color(255, 255, 102);
    private final Color ACCONTO_SALDATO_COLOR = new Color(14, 129, 60);
    private final Color HEADER_BACKGROUND = Color.LIGHT_GRAY;
    private final Color BORDER_CELL_SELECTED = Color.blue;

    // Liste dei valori
    ArrayList<String> listaPiazzole = new ArrayList<>();
    ArrayList<String> listaNomi = new ArrayList<>();

    private JTable tblPrenotazioni;
    private JComboBox cbFiltro;
    private final Gateway gateway;

    public TablePrenotazioniController(JTable tblPrenotazioni) {
        this.tblPrenotazioni = tblPrenotazioni;
        this.gateway  = new Gateway();
    }

    // Mostra la visualizzazione iniziale della tabella (con il filtro dell'anno)
    public JTable initView(JComboBox cbFiltro) throws SQLException {
        this.cbFiltro = cbFiltro;

        // Ottiene il valore selezionato nella comboBox
        String selectedFilterYear = Objects.requireNonNull(cbFiltro.getSelectedItem()).toString();

        String initialQuery = "";
        if(Objects.equals(selectedFilterYear, "Tutto")) {
            initialQuery = "SELECT * FROM Prenotazioni";
        } else {
            initialQuery = String.format("SELECT * FROM Prenotazioni WHERE substr(Arrivo, 7, 4) = '%s' OR substr(Partenza, 7, 4) = '%s'", selectedFilterYear, selectedFilterYear);
        }

        ResultSet resultSet = this.gateway.execSelectQuery(initialQuery);
        tblPrenotazioni = new JTable(gateway.buildCustomTableModel(resultSet));

        return tblPrenotazioni;
    }

    // Ricarica la visualizzazione della tabella
    public void refreshTable(JTable tabellaPrenotazioni) {
        try {

            // Lancia evento di modifica della tabella
            tabellaPrenotazioni.setModel(this.initView(cbFiltro).getModel());
            ((AbstractTableModel) tabellaPrenotazioni.getModel()).fireTableDataChanged();

            // Riassegna tutti i renderer
            for(int columnIndex = 0; columnIndex < tabellaPrenotazioni.getColumnCount(); columnIndex++) {
                tabellaPrenotazioni.getColumnModel().getColumn(columnIndex).setCellRenderer(this.createCellRenderer());
            }
            tabellaPrenotazioni.getTableHeader().setDefaultRenderer(this.createHeaderRenderer());

            // Rimuove la colonna id
            tabellaPrenotazioni.removeColumn(tabellaPrenotazioni.getColumnModel().getColumn(0));

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Renderer estetica celle
    public DefaultTableCellRenderer createCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(CELL_FONT);

                // Colora le righe alternativamente
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(ALTERNATE_CELL_COLOR);
                }

                // Colora di giallo la riga selezionata
                if (isSelected) {
                    c.setBackground(SELECTION_COLOR);
                }

                // Imposta il bordo di selezione della cella (sia click sinistro che destro)
                if (isSelected && table.getSelectedColumn() == column && table.getSelectedRow() == row) {
                    setBorder(BorderFactory.createLineBorder(BORDER_CELL_SELECTED));
                } else {
                    setBorder(BorderFactory.createEmptyBorder());
                }

                // Ricavo le informazioni per l'acconto
                String acconto = (String) table.getModel().getValueAt(row, 5);
                String nome = (String) table.getModel().getValueAt(row, 4);
                String partenza = (String) table.getModel().getValueAt(row, 3);
                String arrivo = (String) table.getModel().getValueAt(row, 2);

                // Colora il testo dell'acconto (rosso -> non saldato, verde -> saldato)
                try {
                    if (column == 4 && !checkAccontoIsSaldato(nome, arrivo, partenza, acconto)) {
                        setForeground(Color.RED);
                    } else if (column == 4 && checkAccontoIsSaldato(nome, arrivo, partenza, acconto)) {
                        setForeground(ACCONTO_SALDATO_COLOR);
                    } else {
                        setForeground(Color.BLACK);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return c;
            }
        };
    }

    // Renderer estetica header
    public DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(HEADER_FONT);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setBackground(HEADER_BACKGROUND);

                return c;
            }
        };
    }

    // Carica tutte le piazzole
    public void setListaPiazzole() throws SQLException {
        ResultSet piazzoleRs = new Gateway().execSelectQuery("SELECT * FROM Piazzole");
        while (piazzoleRs.next()) {
            if(!listaPiazzole.contains(piazzoleRs.getString("Nome")))
                listaPiazzole.add(piazzoleRs.getString("Nome"));
        }
        piazzoleRs.close();
    }

    // Rimuove le piazzole dalla lista
    public void removePiazzolaFromList(String value){
        listaPiazzole.remove(value);
    }

    // Ritorna tutti i nomi delle prenotazioni
    public ArrayList<String> getAllNames() throws SQLException {
        ResultSet nomiRs = new Gateway().execSelectQuery("SELECT Nome FROM Prenotazioni");
        while (nomiRs.next()) {
            if(!listaNomi.contains(nomiRs.getString("Nome")))
                listaNomi.add(nomiRs.getString("Nome"));
        }
        nomiRs.close();

        return listaNomi;
    }

    // Verifica se nella riga selezionata è presente o meno l'acconto
    public boolean isAcconto(Object value) {
        if (value instanceof String) {
            return value != null;
        }
        return false;
    }

    // Controlla dal db se l'acconto è saldato o meno
    private boolean checkAccontoIsSaldato(String... values) throws SQLException {
        String checkQuery = "SELECT Saldato FROM SaldoAcconti WHERE Nome = ? AND Arrivo = ? AND Partenza = ? AND Acconto = ?";

        ResultSet rs = new Gateway().execSelectQuery(checkQuery, values);
        if(rs.next()){
            String isSaldato = rs.getString("Saldato");
            rs.close();
            return Objects.equals(isSaldato, "saldato");
        } else {
            rs.close();
            return false;
        }
    }

    public ArrayList<String> getListaPiazzole() {
        return listaPiazzole;
    }

}
