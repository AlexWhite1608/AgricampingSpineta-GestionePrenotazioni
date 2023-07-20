package views;

import javax.swing.*;
import java.awt.*;

public class MenuPrenotazioni extends JPanel {


    private JPanel mainPanelPrenotazioni;
    private JPanel pnlToolbar;
    private JToolBar toolbar;
    private JButton btnAggiungiPrenotazione;

    public MenuPrenotazioni() {

        createUIComponents();

        setupToolbar();

        setupTable();

        add(mainPanelPrenotazioni);
        setVisible(true);
    }

    // Inizializzazione degli elementi di UI
    private void createUIComponents() {
        mainPanelPrenotazioni = new JPanel(new BorderLayout());
        pnlToolbar = new JPanel(new FlowLayout());

        toolbar = new JToolBar();
        btnAggiungiPrenotazione = new JButton("Aggiungi");
    }

    // Setup della tabella delle prenotazioni
    private void setupTable(){

    }

    // Setup toolbar
    private void setupToolbar(){
        toolbar.add(btnAggiungiPrenotazione);
        toolbar.setFloatable(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER));

        pnlToolbar.add(toolbar);
        mainPanelPrenotazioni.add(pnlToolbar, BorderLayout.PAGE_START);
    }
}
