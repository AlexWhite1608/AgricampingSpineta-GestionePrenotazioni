package controllers;

import data_access.Gateway;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class TableCalendarioController {

    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private final Color HEADER_BACKGROUND = Color.LIGHT_GRAY;

    // Lista delle date
    private List<String> listaDate;

    // Lista delle piazzole
    private List<String> listaPiazzole;

    private JTable tabellaCalendario;
    private final Gateway gateway;

    public TableCalendarioController(JTable tabellaCalendario) throws SQLException {
        this.tabellaCalendario = tabellaCalendario;
        this.gateway = new Gateway();

        // Inizializzo la lista delle date per l'header dal giorno corrente
        listaDate = ControllerDatePrenotazioni.getDatesFromCurrentDate();

        // Inizializzo la lista delle piazzole disponibili
        ControllerPiazzole.setListaPiazzole();
        listaPiazzole = ControllerPiazzole.getListaPiazzole();

    }

    public void setCalendarioTableModel() {

        // Imposta le colonne (Piazzole seguite dalle date)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Piazzole");
        columnNames.addAll(ControllerDatePrenotazioni.getDatesFromCurrentDate());

        // Imposta i dati di default del modello
        Vector<Vector<Object>> data = new Vector<>();
        for (int i = 0; i < listaPiazzole.size(); i++) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(listaPiazzole.get(i)); // Inserisci il nome della piazzola nella prima colonna
            for (int j = 1; j < listaDate.size() + 1; j++) {
                rowData.add(0); // Inizializza le celle con valori vuoti
            }
            data.add(rowData);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaCalendario.setModel(model);
    }

//    public void updateTableModel(List<Prenotazione> nuovePrenotazioni) {
//        // Supponiamo che tu abbia un elenco di oggetti Prenotazione, ciascuno contenente la piazzola e la data di prenotazione.
//
//        // Cicla attraverso le nuove prenotazioni e aggiorna i dati nel modello tableModel
//        for (Prenotazione prenotazione : nuovePrenotazioni) {
//            // Recupera i nuovi dati delle prenotazioni dal database
//
//            // Trova l'indice della riga associata alla piazzola e l'indice della colonna associata alla data di prenotazione
//            int row = listaPiazzole.indexOf(piazzola);
//            int column = listaDate.indexOf(dataPrenotazione.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//
//            // Aggiorna il valore nella cella corrispondente con le nuove informazioni della prenotazione
//            tableModel.setValueAt(VALUE, row, column);
//        }
//
//        // Aggiorna la tabella Calendario
//        tabellaCalendario.setModel(tableModel);
//        ((AbstractTableModel) tabellaCalendario.getModel()).fireTableDataChanged();
//
//    }

    // Ricarica le piazzole
    public void refreshPiazzole(JTable tabellaCalendario) {

    }

    // Imposta il renderer per le celle
    public DefaultTableCellRenderer createCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                return c;
            }
        };
    }

    // Imposta il renderer per l'header
    public DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(HEADER_FONT);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setBackground(HEADER_BACKGROUND);

                // Adatta la larghezza della cella al testo
                if (value != null) {
                    String text = value.toString();
                    int width = table.getFontMetrics(HEADER_FONT).stringWidth(text) + 10;
                    Dimension preferredSize = new Dimension(width, c.getPreferredSize().height);
                    setPreferredSize(preferredSize);
                }

                return c;
            }
        };
    }

}
