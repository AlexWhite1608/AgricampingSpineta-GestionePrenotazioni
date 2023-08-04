package controllers;

import data_access.Gateway;
import observer.PrenotazioniObservers;
import renderers_calendario.CalendarioCellRenderer;
import renderers_calendario.VerticalTableHeaderCellRenderer;
import utils.TableConstants;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class TableCalendarioController implements PrenotazioniObservers {

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

        // Si iscrive alle notifiche del MenuPrenotazioni
        MenuPrenotazioni.getPrenotazioniObserversList().add(this);
    }

    // Imposta il tableModel iniziale della tabella
    public void setCalendarioTableModel() throws SQLException {

        // Imposta le colonne (Piazzole seguite dalle date)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Piazzole");
        columnNames.addAll(ControllerDatePrenotazioni.getDatesFromCurrentDate());

        // Ottengo la lista delle prenotazioni
        ArrayList<String[]> infoPrenotazioni = getInfoPrenotazioni();

        // Ottengo le date comprese tra Arrivo e Partenza
        ArrayList<ArrayList<String>> daysFromDates = getDaysFromDates(infoPrenotazioni);

        // Imposta i dati del modello (0 --> nessuna prenotazione, 1 --> prenotazione)
        Vector<Vector<Object>> data = new Vector<>();
        for (int i = 0; i < listaPiazzole.size(); i++) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(listaPiazzole.get(i)); // Inserisce il nome della piazzola nella prima colonna

            for (int j = 0; j < listaDate.size(); j++) {
                String completeColumnValue = listaDate.get(j);
                String onlyDateColumnValue = completeColumnValue.substring(0, completeColumnValue.length() - 4);
                boolean hasPrenotazione = false;

                for (ArrayList<String> prenotazione : daysFromDates) {
                    if (rowData.get(0).equals(prenotazione.get(0))) {
                        if (prenotazione.contains(onlyDateColumnValue)) {
                            hasPrenotazione = true;
                            break;
                        }
                    }
                }

                rowData.add(hasPrenotazione ? 1 : 0);
            }

            data.add(rowData);
        }


        // Genera il DefaultTableModel con i dati ricavati
        DefaultTableModel model = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaCalendario.setModel(model);
    }

    // Ricava le info sulle prenotazioni come lista di (Piazzola, Arrivo, Partenza)
    private ArrayList<String[]> getInfoPrenotazioni() throws SQLException {
        ArrayList<String[]> infoPrenotazioni = new ArrayList<>();

        String query = "SELECT Piazzola, Arrivo, Partenza FROM Prenotazioni " +
                "WHERE strftime('%Y-%m-%d', substr(Arrivo, 7, 4) || '-' || substr(Arrivo, 4, 2) || '-' || substr(Arrivo, 1, 2)) >= date('now') " +
                "OR strftime('%Y-%m-%d', substr(Partenza, 7, 4) || '-' || substr(Partenza, 4, 2) || '-' || substr(Partenza, 1, 2)) >= date('now') " +
                "OR date('now') BETWEEN strftime('%Y-%m-%d', substr(Arrivo, 7, 4) || '-' || substr(Arrivo, 4, 2) || '-' || substr(Arrivo, 1, 2)) " +
                "AND strftime('%Y-%m-%d', substr(Partenza, 7, 4) || '-' || substr(Partenza, 4, 2) || '-' || substr(Partenza, 1, 2));";

        ResultSet result = gateway.execSelectQuery(query);

        while (result.next()) {
            String piazzola = result.getString("Piazzola");
            String arrivo = result.getString("Arrivo");
            String partenza = result.getString("Partenza");

            infoPrenotazioni.add(new String[]{piazzola, arrivo, partenza});
        }

        result.close();

        return infoPrenotazioni;
    }

    // Ricava i giorni tra le date di arrivo e di partenza fornite (il primo elemento è sempre il nome della piazzola)
    private ArrayList<ArrayList<String>> getDaysFromDates(ArrayList<String[]> prenotazioni) {
        ArrayList<ArrayList<String>> listaGiorni = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (String[] info : prenotazioni) {
            String piazzola = info[0];
            LocalDate arrivo = LocalDate.parse(info[1], formatter);
            LocalDate partenza = LocalDate.parse(info[2], formatter);

            LocalDate currentDate = arrivo;
            ArrayList<String> days = new ArrayList<>();

            days.add(piazzola);
            while (!currentDate.isAfter(partenza)) {
                days.add(currentDate.format(formatter));
                currentDate = currentDate.plusDays(1);
            }

            listaGiorni.add(days);
        }

        return listaGiorni;
    }

    // Ricarica la tabella a seguito di modifiche delle prenotazioni
    @Override
    public void refreshView() {
        //TODO: implementa il refresh
        System.out.println("NOTIFICA RICEVUTA!");
    }

    // Ricarica la tabella a seguito di inserimento/rimozione piazzole
    @Override
    public void refreshPiazzola() {
        //TODO: implementa il refresh
        System.out.println("PIAZZOLA MODIFICATA!");
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
    public void createCellRenderer() {
        DefaultTableCellRenderer cellRenderer = new CalendarioCellRenderer();
        for(int columnIndex = 0; columnIndex < tabellaCalendario.getColumnCount(); columnIndex++) {
            tabellaCalendario.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }

        // Imposta la larghezza delle colonne (tranne Piazzole)
        TableColumnModel columnModel = tabellaCalendario.getColumnModel();
        for (int i = 1; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(TableConstants.COLUMNS_WIDTH_CALENDARIO);
        }
    }

    // Imposta il renderer per l'header (verticale)
    public void createHeaderRenderer() {
        TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer(false, false);
        Enumeration<TableColumn> columns = tabellaCalendario.getColumnModel().getColumns();
        int columnIndex = 0; // Contatore per tenere traccia dell'indice della colonna corrente

        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            if (columnIndex > 0) { // Imposta il renderer dell'header solo per le colonne con indice maggiore di 0
                column.setHeaderRenderer(headerRenderer);
                String columnHeaderText = column.getHeaderValue().toString();

                if(columnHeaderText.contains("(S)") || columnHeaderText.contains("(D)")) {
                    column.setHeaderRenderer(new VerticalTableHeaderCellRenderer(true, false));
                }

                if(columnIndex == 1) {
                    column.setHeaderRenderer(new VerticalTableHeaderCellRenderer(false, true));
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
                setFont(TableConstants.HEADER_FONT_CALENDARIO);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setOpaque(false);

                // Aggiunge il bordo solo sulla parte destra dell'header Piazzole
                Border headerBorder = BorderFactory.createMatteBorder(0, 0, 0, TableConstants.SEPARATOR_BORDER_WIDTH + 1, Color.BLACK);
                setBorder(BorderFactory.createCompoundBorder(getBorder(), headerBorder));

                return c;
            }
        };
    }

}
