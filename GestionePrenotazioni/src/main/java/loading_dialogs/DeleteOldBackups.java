package loading_dialogs;

import data_access.CloudUploader;
import views.HomePage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;

public class DeleteOldBackups extends Thread {
    private final LocalDate dateToDeleteBefore;

    public DeleteOldBackups(LocalDate dateToDeleteBefore) {
        this.dateToDeleteBefore = dateToDeleteBefore;
    }

    @Override
    public void start() {

        // Creo il dialog di caricamento
        JDialog loadingDialog = new JDialog();
        loadingDialog.setTitle("Caricamento");
        loadingDialog.setLocationRelativeTo(null);
        loadingDialog.setResizable(false);

        // Creo un JPanel come contenitore per il contenuto del dialog
        JPanel contentPanel = new JPanel(new BorderLayout());
        int margin = 20;
        contentPanel.setBorder(new EmptyBorder(margin, margin, margin, margin));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        contentPanel.add(new JLabel("Cancellazione dei vecchi backup..."), BorderLayout.NORTH);
        contentPanel.add(progressBar, BorderLayout.SOUTH);

        // Aggiungo il contentPanel al dialog
        loadingDialog.add(contentPanel);

        loadingDialog.pack();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                CloudUploader.deleteFilesBeforeDate(dateToDeleteBefore, progressBar);
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });

        worker.execute();

        loadingDialog.setVisible(true);
    }
}
