package views;

import data_access.CloudUploader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

public class HomePage extends JFrame{

    private final static String MENU_CALENDARIO = "Calendario";
    private final static String MENU_PRENOTAZIONI = "Prenotazioni";
    private final static String MENU_ARRIVI_PARTENZE = "Arrivi/Partenze";

    public HomePage() throws IOException, SQLException, GeneralSecurityException {

        // Se non è presente la cartella del backup, allora la creo e ci copio il file delle risorse (primo avvio)
        CloudUploader.copyResourceDBtoLocal();

        // TODO: cancella i backup della settimana scorsa
        CloudUploader.deleteFilesBeforeDate(LocalDate.now().minusDays(1));

        JTabbedPane tabbedPane = new JTabbedPane();

        //Imposta l'icona
        Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/favicon-32x32.png")));
        setIconImage(icon);

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
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        double width = screenSize.getWidth();
//        double height = screenSize.getHeight();
//        this.setSize((int) width, (int) height);

        this.setTitle("Gestione prenotazioni");
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        this.pack();
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
