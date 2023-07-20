package views;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class HomePage extends JFrame{

    private final static String MENU_CALENDARIO = "Calendario";
    private final static String MENU_PRENOTAZIONI = "Prenotazioni";
    private final static String MENU_ARRIVI_PARTENZE = "Arrivi/Partenze";

    public HomePage() throws IOException {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Imposta l'icona
        //Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/favicon-32x32.png")));
        //setIconImage(icon);

        //Crea i menu
        MenuCalendario menuCalendario = new MenuCalendario();
        MenuPrenotazioni menuPrenotazioni = new MenuPrenotazioni();
        MenuArriviPartenze menuArriviPartenze = new MenuArriviPartenze();

        //Aggiunge i menu al tabbed layout
        tabbedPane.addTab(MENU_CALENDARIO, menuCalendario);
        tabbedPane.addTab(MENU_PRENOTAZIONI, menuPrenotazioni);
        tabbedPane.addTab(MENU_ARRIVI_PARTENZE, menuArriviPartenze);

        this.add(tabbedPane, BorderLayout.CENTER);

        //Imposta parametri visualizzazione HomePage
        this.setTitle("Gestore prenotazioni");
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
