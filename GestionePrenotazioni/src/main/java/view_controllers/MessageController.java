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
}


