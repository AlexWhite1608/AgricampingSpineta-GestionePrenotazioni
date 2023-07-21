package views;

import javax.swing.*;
import java.awt.*;

public class MenuPrenotazioni extends JPanel {

    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JPanel pnlTable;
    private JToolBar toolbar;
    private JButton btnAggiungiPrenotazione;
    private JButton btnRimuoviPrenotazione;


    public MenuPrenotazioni() {

        createUIComponents();

        setupToolbar();

        setupTable();

        setLayout(new BorderLayout());
        add(mainPanelPrenotazioni, BorderLayout.CENTER);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {

        // Main panel
        mainPanelPrenotazioni = new JPanel();
        mainPanelPrenotazioni.setLayout(new BorderLayout());

        // Panel toolbar
        pnlToolbar = new JPanel(new BorderLayout());

        // Panel tabella
        pnlTable = new JPanel(new BorderLayout());

        // Toolbar
        toolbar = new JToolBar();
        btnAggiungiPrenotazione = new JButton("Aggiungi");
        btnRimuoviPrenotazione = new JButton("Rimuovi");
        btnAggiungiPrenotazione.setFocusPainted(false);
        btnRimuoviPrenotazione.setFocusPainted(false);

    }

    // Setup della tabella delle prenotazioni
    private void setupTable(){

    }

    // Setup toolbar
    private void setupToolbar(){
        toolbar.add(btnAggiungiPrenotazione);
        toolbar.add(btnRimuoviPrenotazione);
        toolbar.setFloatable(false);

        pnlToolbar.add(toolbar, BorderLayout.CENTER);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.NORTH);
    }
}
