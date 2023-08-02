package controllers;

import data_access.Gateway;
import vertical_headers.VerticalTableHeaderCellRenderer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Enumeration;
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

    // Imposta il renderer per l'header (verticale)
    public void createHeaderRenderer() {
        TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer(false);
        Enumeration<TableColumn> columns = tabellaCalendario.getColumnModel().getColumns();
        int columnIndex = 0; // Contatore per tenere traccia dell'indice della colonna corrente

        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            if (columnIndex > 0) { // Imposta il renderer dell'header solo per le colonne con indice maggiore di 0
                column.setHeaderRenderer(headerRenderer);
                String columnHeaderText = column.getHeaderValue().toString();

                if(columnHeaderText.contains("(S)") || columnHeaderText.contains("(D)")) {
                    column.setHeaderRenderer(new VerticalTableHeaderCellRenderer(true));
                }
            } else 
                column.setHeaderRenderer(createHeaderPiazzole());
            columnIndex++;
        }
    }

    // Crea il renderer per la colonna delle Piazzole
    private DefaultTableCellRenderer createHeaderPiazzole() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(HEADER_FONT);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setOpaque(false);
                return c;
            }
        };
    }

}
