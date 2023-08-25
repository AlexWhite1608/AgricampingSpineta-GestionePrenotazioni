package views;

import view_controllers.*;
import data_access.Gateway;
import utils.TableConstants;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MenuArriviPartenze extends JPanel {

    // I due controller per le due tabelle
    TableArriviController tableArriviController;
    TablePartenzeController tablePartenzeController;

    private JPanel mainPanelArriviPartenze;
    private JPanel pnlToolbar;
    private JPanel pnlTables;
    private JTable tabellaArrivi;
    private JTable tabellaPartenze;
    private JToolBar toolBar;
    private JPopupMenu popupMenuArrivi;
    private JPopupMenu popupMenuPartenze;

    public MenuArriviPartenze() throws SQLException {

        createUIComponents();
        setupToolbar();
        setupTableArrivi();
        setupTablePartenze();

        setLayout(new BorderLayout());
        add(mainPanelArriviPartenze, BorderLayout.CENTER);
        setVisible(true);

        mainPanelArriviPartenze.add(pnlTables, BorderLayout.CENTER);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelArriviPartenze = new JPanel();
        mainPanelArriviPartenze.setLayout(new BorderLayout());
        pnlTables = new JPanel(new GridLayout(1, 2));

        // Inizializzazione delle tabelle e relativi controllers
        tabellaArrivi = new JTable();
        tabellaPartenze = new JTable();
        tableArriviController = new TableArriviController(tabellaArrivi);
        tablePartenzeController = new TablePartenzeController(tabellaPartenze);

        // Inizializza la toolbar
        pnlToolbar = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
    }

    // Setup tabella arrivi
    private void setupTableArrivi() throws SQLException {

        // Impostazioni di base della tabella
        tabellaArrivi.getTableHeader().setReorderingAllowed(false);
        tabellaArrivi.setCellSelectionEnabled(true);
        tabellaArrivi.setRowSelectionAllowed(false);
        tabellaArrivi.setColumnSelectionAllowed(false);
        tabellaArrivi.setDefaultEditor(Object.class, null);
        tabellaArrivi.setGridColor(Color.BLACK);
        tabellaArrivi.setRowHeight(40);

        // Impostazione del tableModel e dei renderer
        tableArriviController.setTableModel();
        tableArriviController.createCellRenderer();
        tableArriviController.createHeaderRenderer();

        // Panel per tabellaArrivi
        JPanel pnlTabellaArrivi = new JPanel(new BorderLayout());
        pnlTabellaArrivi.add(new JScrollPane(tabellaArrivi), BorderLayout.CENTER);

        // Imposta il bordo del panel
        Border blackline = BorderFactory.createLineBorder(TableConstants.ARRIVI_CONTORNO_COLOR);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "ARRIVI", TitledBorder.CENTER, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        titledBorder.setTitleColor(TableConstants.ARRIVI_CONTORNO_COLOR);
        pnlTabellaArrivi.setBorder(titledBorder);

        // Genera il popup con il tasto destro
        tabellaArrivi.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleRowClick(e);
                if (e.isPopupTrigger()) {
                    doPop(e);
                } else {
                    hidePop();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    doPop(e);
                }
            }

            private void handleRowClick(MouseEvent e) {
                ListSelectionModel selectionModel = tabellaArrivi.getSelectionModel();
                Point contextMenuOpenedAt = e.getPoint();
                int clickedRow = tabellaArrivi.rowAtPoint(contextMenuOpenedAt);
                int clickedColumn = tabellaArrivi.columnAtPoint(contextMenuOpenedAt);

                if (clickedRow < 0 || clickedColumn < 0) {
                    // Nessuna cella selezionata
                    selectionModel.clearSelection();
                } else {
                    // Cella selezionata
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // Click destro
                        selectionModel.setSelectionInterval(clickedRow, clickedRow);
                        tabellaArrivi.setColumnSelectionInterval(clickedColumn, clickedColumn);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        // Click sinistro
                        selectionModel.setSelectionInterval(clickedRow, clickedRow);
                    }
                }
            }

            private void doPop(MouseEvent e) {
                if (tabellaArrivi.getSelectedRowCount() == 0) {
                    return;
                }

                // Mostra il popupMenu
                popupMenuArrivi = new JPopupMenu();
                setupPopUpMenuArrivi();
                popupMenuArrivi.show(e.getComponent(), e.getX(), e.getY());
            }

            private void hidePop() {
                popupMenuArrivi.setVisible(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabellaArrivi);
        pnlTabellaArrivi.add(scrollPane);

        pnlTables.add(pnlTabellaArrivi, BorderLayout.WEST);
    }

    // Setup tabella arrivi
    private void setupTablePartenze() throws SQLException {

        // Impostazioni di base della tabella
        tabellaPartenze.getTableHeader().setReorderingAllowed(false);
        tabellaPartenze.setCellSelectionEnabled(true);
        tabellaPartenze.setRowSelectionAllowed(true);
        tabellaPartenze.setColumnSelectionAllowed(true);
        tabellaPartenze.setDefaultEditor(Object.class, null);
        tabellaPartenze.setGridColor(Color.BLACK);
        tabellaPartenze.setRowHeight(40);

        // Impostazione del tableModel e dei renderer
        tablePartenzeController.setTableModel();
        tablePartenzeController.createCellRenderer();
        tablePartenzeController.createHeaderRenderer();

        // Panel per tabellaPartenze
        JPanel pnlTabellaPartenze = new JPanel(new BorderLayout());
        pnlTabellaPartenze.add(new JScrollPane(tabellaPartenze), BorderLayout.CENTER);

        // Imposta il bordo del panel
        Border blackline = BorderFactory.createLineBorder(Color.RED);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "PARTENZE", TitledBorder.CENTER, TitledBorder.TOP);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 16));
        titledBorder.setTitleColor(Color.RED);
        pnlTabellaPartenze.setBorder(titledBorder);

        // Genera il popup con il tasto destro
        tabellaPartenze.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleRowClick(e);
                if (e.isPopupTrigger()) {
                    doPop(e);
                } else {
                    hidePop();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    doPop(e);
                }
            }

            private void handleRowClick(MouseEvent e) {
                ListSelectionModel selectionModel = tabellaPartenze.getSelectionModel();
                Point contextMenuOpenedAt = e.getPoint();
                int clickedRow = tabellaPartenze.rowAtPoint(contextMenuOpenedAt);
                int clickedColumn = tabellaPartenze.columnAtPoint(contextMenuOpenedAt);

                if (clickedRow < 0 || clickedColumn < 0) {
                    // Nessuna cella selezionata
                    selectionModel.clearSelection();
                } else {
                    // Cella selezionata
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // Click destro
                        selectionModel.setSelectionInterval(clickedRow, clickedRow);
                        tabellaPartenze.setColumnSelectionInterval(clickedColumn, clickedColumn);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        // Click sinistro
                        selectionModel.setSelectionInterval(clickedRow, clickedRow);
                    }
                }
            }

            private void doPop(MouseEvent e) {
                if (tabellaPartenze.getSelectedRowCount() == 0) {
                    return;
                }

                // Mostra il popupMenu
                popupMenuPartenze = new JPopupMenu();
                setupPopUpMenuPartenze();
                popupMenuPartenze.show(e.getComponent(), e.getX(), e.getY());
            }

            private void hidePop() {
                popupMenuPartenze.setVisible(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabellaPartenze);
        pnlTabellaPartenze.add(scrollPane);

        pnlTables.add(pnlTabellaPartenze, BorderLayout.EAST);
    }

    // Impostazioni popup menu Arrivi
    private void setupPopUpMenuArrivi() {
        JMenuItem annullaArrivo = new JMenuItem("Annulla arrivo");
        JMenuItem confermaArrivo = new JMenuItem("Conferma arrivo");

        popupMenuArrivi.add(confermaArrivo);
        popupMenuArrivi.add(annullaArrivo);

        int selectedRow = tabellaArrivi.getSelectedRow();
        String idPrenotazione = tabellaArrivi.getModel().getValueAt(selectedRow, 0).toString();

        // Azione: annulla l'arrivo -> imposta a "no" il valore di Arrivato in ArriviPartenze
        annullaArrivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String setArriviValue = "UPDATE ArriviPartenze SET Arrivato = ? WHERE Id = ?";
                try {
                    System.out.println(new Gateway().execUpdateQuery(setArriviValue, "no", idPrenotazione));
                } catch (SQLException ex) {
                    System.err.println("Impossibile impostare il valore Arrivato");;
                }

                // Ricarico la visualizzazione
                tabellaArrivi.repaint(selectedRow);
            }
        });

        // Azione: conferma l'arrivo -> imposta a "si" il valore di Arrivato in ArriviPartenze
        confermaArrivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String setArriviValue = "UPDATE ArriviPartenze SET Arrivato = ? WHERE Id = ?";
                try {
                    System.out.println(new Gateway().execUpdateQuery(setArriviValue, "si", idPrenotazione));
                } catch (SQLException ex) {
                    System.err.println("Impossibile impostare il valore Arrivato");;
                }

                // Aggiorna il colore della riga
                for(int i = 0; i < tabellaArrivi.getColumnCount(); i++) {
                    TableCellRenderer renderer = tabellaArrivi.getCellRenderer(selectedRow, i);
                    Component component = tabellaArrivi.prepareRenderer(renderer, selectedRow, i);
                    component.setBackground(Color.green);
                }

                // Ricarico la visualizzazione
                tabellaArrivi.repaint(selectedRow);
            }
        });
    }

    // Impostazioni popup menu Partenze
    private void setupPopUpMenuPartenze() {
        JMenuItem annullaPartenza = new JMenuItem("Annulla partenza");
        JMenuItem confermaPartenza = new JMenuItem("Conferma partenza");

        popupMenuPartenze.add(confermaPartenza);
        popupMenuPartenze.add(annullaPartenza);

        int selectedRow = tabellaPartenze.getSelectedRow();
        String idPrenotazione = tabellaPartenze.getModel().getValueAt(selectedRow, 0).toString();

        // Azione: annulla la partenza -> imposta a "no" il valore di Partito in ArriviPartenze
        annullaPartenza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String setPartitoValue = "UPDATE ArriviPartenze SET Partito = ? WHERE Id = ?";
                try {
                    System.out.println(new Gateway().execUpdateQuery(setPartitoValue, "no", idPrenotazione));
                } catch (SQLException ex) {
                    System.err.println("Impossibile impostare il valore Partito");;
                }

                // Aggiorna il colore della riga
                for(int i = 0; i < tabellaPartenze.getColumnCount(); i++) {
                    TableCellRenderer renderer = tabellaPartenze.getCellRenderer(selectedRow, i);
                    Component component = tabellaPartenze.prepareRenderer(renderer, selectedRow, i);
                    component.setBackground(Color.red);
                }

                // Ricarico la visualizzazione
                tabellaPartenze.repaint(selectedRow);
            }
        });

        // Azione: conferma la partenza -> imposta a "si" il valore di Partito in ArriviPartenze
        confermaPartenza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String setPartitoValue = "UPDATE ArriviPartenze SET Partito = ? WHERE Id = ?";
                try {
                    System.out.println(new Gateway().execUpdateQuery(setPartitoValue, "si", idPrenotazione));
                } catch (SQLException ex) {
                    System.err.println("Impossibile impostare il valore Arrivato");;
                }

                // Ricarico la visualizzazione
                tabellaPartenze.repaint(selectedRow);
            }
        });
    }

    // Setup della toolbar
    private void setupToolbar() {
        // Impostazione del layout
        toolBar.setLayout(new BorderLayout());
        JPanel pnlButtonsToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Creazione degli elementi della toolbar
        JButton btnDomani = new JButton("Domani");
        JButton btnResetGiorno = new JButton("Reset");
        JLabel lblGiornoSelezionato = new JLabel();

        // Impostazioni
        btnDomani.setFocusPainted(false);
        btnResetGiorno.setFocusPainted(false);
        btnDomani.setToolTipText("Visualizza arrivi/partenze di domani");
        btnResetGiorno.setToolTipText("Reimposta la data odierna");

        // Imposta il giorno corrente (di default) alla label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedTodayDate = ControllerDatePrenotazioni.getCurrentDate().format(formatter);
        lblGiornoSelezionato.setText("Mostra arrivi/partenze del: " + formattedTodayDate);

        // Aggiunta degli elementi
        pnlButtonsToolbar.add(btnDomani);
        pnlButtonsToolbar.add(btnResetGiorno);
        pnlButtonsToolbar.add(Box.createHorizontalStrut(10));
        pnlButtonsToolbar.add(lblGiornoSelezionato);
        pnlButtonsToolbar.add(Box.createHorizontalStrut(10));

        btnDomani.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TablePartenzeController.setTODAY(LocalDate.now().plusDays(1));
                TableArriviController.setTODAY(LocalDate.now().plusDays(1));

                try {
                    tablePartenzeController.setTableModel();
                    tableArriviController.setTableModel();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    // Ricarica la visualizzazione dell'intera tabella
                    tablePartenzeController.refreshView();
                    tableArriviController.refreshView();

                    // Modifica la label con il nuovo giorno selezionato (oggi)
                    lblGiornoSelezionato.setForeground(Color.red);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedTodayDate = TablePartenzeController.getTODAY().format(formatter);
                    lblGiornoSelezionato.setText("Mostra arrivi/partenze del: " + formattedTodayDate);

                } catch (SQLException ex) {
                    MessageController.getErrorMessage(null, "Impossibile resettare la data");
                }
            }
        });

        btnResetGiorno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TablePartenzeController.setTODAY(LocalDate.now());
                TableArriviController.setTODAY(LocalDate.now());

                try {
                    tablePartenzeController.setTableModel();
                    tableArriviController.setTableModel();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    // Ricarica la visualizzazione dell'intera tabella
                    tablePartenzeController.refreshView();
                    tableArriviController.refreshView();

                    // Modifica la label con il nuovo giorno selezionato (oggi)
                    lblGiornoSelezionato.setForeground(Color.black);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedTodayDate = TablePartenzeController.getTODAY().format(formatter);
                    lblGiornoSelezionato.setText("Mostra arrivi/partenze del: " + formattedTodayDate);

                } catch (SQLException ex) {
                    MessageController.getErrorMessage(null, "Impossibile resettare la data");
                }
            }
        });

        toolBar.setFloatable(false);
        toolBar.add(pnlButtonsToolbar, BorderLayout.CENTER);
        pnlToolbar.add(toolBar, BorderLayout.CENTER);
        mainPanelArriviPartenze.add(pnlToolbar, BorderLayout.NORTH);
    }
}
