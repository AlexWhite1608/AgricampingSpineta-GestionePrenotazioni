package utils;

import data_access.CloudUploader;
import views.HomePage;

import javax.swing.*;
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
        JDialog loadingDialog = new JDialog(HomePage.getFrames()[0]);
        loadingDialog.setLocationRelativeTo(null);
        loadingDialog.setResizable(false);
        loadingDialog.setLayout(new FlowLayout());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        loadingDialog.add(progressBar);
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
