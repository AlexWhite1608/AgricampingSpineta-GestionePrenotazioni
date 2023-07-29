package utils;

import javax.swing.*;
import java.awt.*;

class UploadProgressDialog extends JDialog {
    private JProgressBar progressBar;

    public UploadProgressDialog(Frame parent) {
        super(parent, "Caricamento in corso...", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 30));

        add(progressBar, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(parent);
    }

    public void setProgress(int percent) {
        progressBar.setValue(percent);
    }
}
