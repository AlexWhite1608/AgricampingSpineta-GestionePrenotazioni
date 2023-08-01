package controllers;

import data_access.Gateway;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;

public class TableCalendarioController {
    private JTable tabellaCalendario;
    private final Gateway gateway;

    public TableCalendarioController(JTable tabellaCalendario) {
        this.tabellaCalendario = tabellaCalendario;
        this.gateway = new Gateway();
    }

    // Imposta la visualizzazione iniziale della tabella Calendario
    public JTable initView() {
        return null;
    }

    // Ricarica la tabella Calendario per aggiornamenti
    public void refreshTable(JTable tabellaCalendario) {

    }

    // Imposta il renderer per le celle
    public DefaultTableCellRenderer createCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                return c;
            }
        };
    }


    // Imposta il renderer per l'header
    public DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                return c;
            }
        };
    }

}
