package views;

import view_controllers.MessageController;
import data_access.CloudUploader;
import loading_dialogs.DeleteOldBackups;
import utils.TableConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

public class HomePage extends JFrame{

    private final static String MENU_CALENDARIO = "Calendario";
    private final static String MENU_PRENOTAZIONI = "Prenotazioni";
    private final static String MENU_ARRIVI_PARTENZE = "Arrivi/Partenze";
    private final static String MENU_STATISTICHE = "Statistiche";

    private final static boolean DEBUG_MODE = true;     //TODO: imposta false per avere il funzionamento completo!!

    public HomePage() throws IOException, SQLException, GeneralSecurityException {

        // Se non è presente la cartella del backup, allora la creo e ci copio il file delle risorse (primo avvio)
        CloudUploader.copyResourceDBtoLocal();

        JTabbedPane tabbedPane = new JTabbedPane();

        //Imposta l'icona
        Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/favicon-32x32.png")));
        setIconImage(icon);

        //Crea i menu
        MenuCalendario menuCalendario = new MenuCalendario();
        MenuPrenotazioni menuPrenotazioni = new MenuPrenotazioni();
        MenuArriviPartenze menuArriviPartenze = new MenuArriviPartenze();
        MenuStatistiche menuStatistiche = new MenuStatistiche();

        //Aggiunge i menu al tabbed layout
        tabbedPane.addTab(MENU_CALENDARIO, menuCalendario);
        tabbedPane.addTab(MENU_PRENOTAZIONI, menuPrenotazioni);
        tabbedPane.addTab(MENU_ARRIVI_PARTENZE, menuArriviPartenze);
        tabbedPane.addTab(MENU_STATISTICHE, menuStatistiche);

        this.add(tabbedPane, BorderLayout.CENTER);

        this.setTitle("Agricamping Spineta");
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Imposta la finestra come schermo intero
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        if(!DEBUG_MODE) {

            // Cancella i vecchi backup
            new DeleteOldBackups(LocalDate.now().minusDays(TableConstants.DAYS_BEFORE_DELETE)).start();

            // Salva sul drive quando si chiude l'applicazione
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    setCursor(Cursor.WAIT_CURSOR);
                    CloudUploader.uploadDatabaseFile();
                    setCursor(Cursor.DEFAULT_CURSOR);
                }
            });
        }

        this.setVisible(true);
    }
}
