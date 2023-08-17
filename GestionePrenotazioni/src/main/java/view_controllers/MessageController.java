package view_controllers;

import javax.swing.*;
import java.awt.*;

public class MessageController {
    public static void getErrorMessage(Component component, String msg){
        JOptionPane.showMessageDialog(component,
                msg,
                "Errore",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void getInfoMessage(Component component, String msg){
        JOptionPane.showMessageDialog(component,
                msg,
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showSaveDialog(Component c) {
        JOptionPane optionPane = new JOptionPane("Salvataggio in corso...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        JDialog dialog = optionPane.createDialog(c, "Attendere");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }
}


