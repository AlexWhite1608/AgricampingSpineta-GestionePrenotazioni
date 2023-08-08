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
import java.util.*;
import java.util.List;

public class TableCalendarioController implements PrenotazioniObservers {

    // Lista delle date
    private static List<String> listaDate;

    // Lista delle piazzole
    private static List<String> listaPiazzole;

    private static JTable tabellaCalendario;
    private static Gateway gateway;

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
    public static void setCalendarioTableModel() throws SQLException {

        // Imposta le colonne (Piazzole seguite dalle date)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Piazzole");
        columnNames.addAll(ControllerDatePrenotazioni.getDatesFromCurrentDate());

        // Ottengo la lista delle prenotazioni
        ArrayList<String[]> infoPrenotazioni = getInfoPrenotazioni();

        // Ottengo le date comprese tra Arrivo e Partenza
        ArrayList<ArrayList<String>> daysFromDates = ControllerDatePrenotazioni.getDaysFromDates(infoPrenotazioni);

        // Imposta i dati del modello (0 --> nessuna prenotazione, 1 --> prenotazione)
        Vector<Vector<Object>> data = new Vector<>();
        String currentBookingId = "-1";
        int currentCellValue = 0;

        for (int i = 0; i < listaPiazzole.size(); i++) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(listaPiazzole.get(i)); // Inserisce il nome della piazzola nella prima colonna

            for (int j = 0; j < listaDate.size(); j++) {
                String completeColumnValue = listaDate.get(j);
                String onlyDateColumnValue = completeColumnValue.substring(0, completeColumnValue.length() - 4);
                boolean hasPrenotazione = false;

                for (ArrayList<String> prenotazione : daysFromDates) {
                    if (rowData.get(0).equals(prenotazione.get(1))) {
                        if (prenotazione.contains(onlyDateColumnValue)) {
                            hasPrenotazione = true;
                            //FIXME: ricava id prenotazione dal db!
                            String bookingId = prenotazione.get(0);
                            if (!Objects.equals(bookingId, currentBookingId)) {
                                currentBookingId = bookingId;
                                currentCellValue++;
                            }
                            break;
                        }
                    }
                }

                rowData.add(hasPrenotazione ? currentCellValue : 0);
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
    private static ArrayList<String[]> getInfoPrenotazioni() throws SQLException {
        ArrayList<String[]> infoPrenotazioni = new ArrayList<>();

        String query = "SELECT Id, Piazzola, Arrivo, Partenza FROM Prenotazioni " +
                "WHERE strftime('%Y-%m-%d', substr(Arrivo, 7, 4) || '-' || substr(Arrivo, 4, 2) || '-' || substr(Arrivo, 1, 2)) >= date('now') " +
                "OR strftime('%Y-%m-%d', substr(Partenza, 7, 4) || '-' || substr(Partenza, 4, 2) || '-' || substr(Partenza, 1, 2)) >= date('now') " +
                "OR date('now') BETWEEN strftime('%Y-%m-%d', substr(Arrivo, 7, 4) || '-' || substr(Arrivo, 4, 2) || '-' || substr(Arrivo, 1, 2)) " +
                "AND strftime('%Y-%m-%d', substr(Partenza, 7, 4) || '-' || substr(Partenza, 4, 2) || '-' || substr(Partenza, 1, 2));";

        ResultSet result = gateway.execSelectQuery(query);

        while (result.next()) {
            String id = result.getString("Id");
            String piazzola = result.getString("Piazzola");
            String arrivo = result.getString("Arrivo");
            String partenza = result.getString("Partenza");

            infoPrenotazioni.add(new String[]{id, piazzola, arrivo, partenza});
        }

        result.close();

        return infoPrenotazioni;
    }

    // Ricarica la tabella a seguito di modifiche delle prenotazioni
    @Override
    public void refreshView() throws SQLException {

        // Ricarico il tableModel
        setCalendarioTableModel();

        // Ricarico il renderer per le celle
        createCellRenderer();

        // Ricarico il renderer per l'header
        createHeaderRenderer();
    }

    // Ricarica la tabella a seguito di inserimento/rimozione piazzole
    @Override
    public void refreshPiazzola() throws SQLException {

        // Ricarico la lista delle piazzole
        ControllerPiazzole.setListaPiazzole();
        listaPiazzole = ControllerPiazzole.getListaPiazzole();

        // Ricarico il tableModel
        setCalendarioTableModel();

        // Ricarico il renderer per le celle
        createCellRenderer();

        // Ricarico il renderer per l'header
        createHeaderRenderer();
    }

    // Viene richiamato quando si intende cambiare il primo giorno di visualizzazione del calendario
    public static void refreshDate() throws SQLException {
        listaDate = ControllerDatePrenotazioni.getDatesFromCurrentDate();

        // Ricarico il tableModel
        setCalendarioTableModel();

        // Ricarico il renderer per le celle
        createCellRenderer();

        // Ricarico il renderer per l'header
        createHeaderRenderer();
    }

    // Imposta il renderer per le celle
    public static void createCellRenderer() {
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
    public static void createHeaderRenderer() {
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
    private static DefaultTableCellRenderer createHeaderPiazzole() {
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

    public static JTable getTabellaCalendario() {
        return tabellaCalendario;
    }
}
