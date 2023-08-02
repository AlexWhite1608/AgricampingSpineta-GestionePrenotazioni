package views;

import controllers.MessageController;
import data_access.CloudUploader;
import utils.DeleteOldBackups;

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
import java.util.PrimitiveIterator;

public class HomePage extends JFrame{

    private final static String MENU_CALENDARIO = "Calendario";
    private final static String MENU_PRENOTAZIONI = "Prenotazioni";
    private final static String MENU_ARRIVI_PARTENZE = "Arrivi/Partenze";

    private final static int DAYS_BEFORE_DELETE = 0;

    private final static boolean DEBUG_MODE = false;     //TODO: imposta false!!

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

        //Aggiunge i menu al tabbed layout
        tabbedPane.addTab(MENU_CALENDARIO, menuCalendario);
        tabbedPane.addTab(MENU_PRENOTAZIONI, menuPrenotazioni);
        tabbedPane.addTab(MENU_ARRIVI_PARTENZE, menuArriviPartenze);

        this.add(tabbedPane, BorderLayout.CENTER);

        this.setTitle("Gestione prenotazioni");
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        this.pack();
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        if(!DEBUG_MODE) {

            // Cancella i vecchi backup
            new DeleteOldBackups(LocalDate.now().minusDays(DAYS_BEFORE_DELETE)).start();

            // Salva sul drive quando si chiude l'applicazione
//            addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent windowEvent) {
//                    try {
//                        setCursor(Cursor.WAIT_CURSOR);
//                        CloudUploader.uploadDatabaseFile();
//                        setCursor(Cursor.DEFAULT_CURSOR);
//                    } catch (IOException | GeneralSecurityException | URISyntaxException e) {
//                        e.printStackTrace();
//                        MessageController.getErrorMessage(HomePage.this, "Errore nel salvataggio del backup sul Drive");
//                    }
//                }
//            });
        }
    }
}
