package utils;

import controllers.ControllerPiazzole;
import controllers.TablePrenotazioniController;
import data_access.Gateway;
import observer.PrenotazioniObservers;
import observer.StopTableEditObservers;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventObject;

public class CustomCellEditorPrenotazioni extends AbstractCellEditor implements TableCellEditor {
    private JComboBox<String> comboBox;
    private JTextField textField;
    private Object originalValue;
    private int editingColumn;
    private int editingRow;
    private JTable tabellaPrenotazioni;
    private final TablePrenotazioniController tablePrenotazioniController;

    // Lista degli observers di fine edit tabella
    private static ArrayList<StopTableEditObservers> observers = new ArrayList<>();

    public CustomCellEditorPrenotazioni(TablePrenotazioniController tablePrenotazioniController) {
        this.tablePrenotazioniController = tablePrenotazioniController;

        comboBox = new JComboBox<>();
        comboBox.addActionListener(e -> stopCellEditing());

        textField = new JTextField();
        textField.addActionListener(e -> stopCellEditing());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        editingColumn = column;
        editingRow = row;
        originalValue = value;
        tabellaPrenotazioni = table;

        if (column == 0) {
            comboBox.removeAllItems();

            // Aggiunge tutti i valori delle piazzole
            try {
                ControllerPiazzole.setListaPiazzole();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for(String piazzola : ControllerPiazzole.getListaPiazzole()){
                comboBox.addItem(piazzola);
            }

            // Seleziona il valore della cella o imposta un valore di default
            if (value != null) {
                comboBox.setSelectedItem(value);
            }

            return comboBox;
        } else {
            // Imposta il valore della cella
            textField.setText(value != null ? value.toString() : "");

            return textField;
        }
    }

    @Override
    public Object getCellEditorValue() {
        return editingColumn == 0 ? comboBox.getSelectedItem() : textField.getText();
    }

    @Override
    public boolean stopCellEditing() {
        if (editingColumn == 0) {
            Object selectedValue = comboBox.getSelectedItem();
            if (selectedValue != null) {

                // Salva le modifiche nel database nel caso della piazzola (comboBox)
                try {
                    int result = 0;
                    if(originalValue != null)
                        result = new Gateway().updateCellData(tabellaPrenotazioni, editingRow, editingColumn, selectedValue.toString(), originalValue.toString());
                    else
                        result = new Gateway().updateCellData(tabellaPrenotazioni, editingRow, editingColumn, selectedValue.toString(), null);
                    if(result == 0)
                        System.err.println("Impossibile modificare il valore");
                    else if (result == -1) {
                        tablePrenotazioniController.refreshTable(tabellaPrenotazioni);
                        tabellaPrenotazioni.repaint();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        } else {
            String newValue = textField.getText();

            // Salva le modifiche nel database
            try {
                int result = 0;
                if(originalValue != null)
                    result = new Gateway().updateCellData(tabellaPrenotazioni, editingRow, editingColumn, newValue, originalValue.toString());
                else
                    result = new Gateway().updateCellData(tabellaPrenotazioni, editingRow, editingColumn, newValue, null);
                if(result == 0)
                    System.err.println("Impossibile modificare il valore");
                else if (result == -1) {
                    tablePrenotazioniController.refreshTable(tabellaPrenotazioni);
                    tabellaPrenotazioni.repaint();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        fireEditingStopped();

        // Notifica gli observers che l'editing della tabella è concluso
        for(StopTableEditObservers observer : observers)
            observer.stopEditNotify();

        return true;
    }

    @Override
    public void cancelCellEditing() {
        super.cancelCellEditing();
        if (editingColumn == 0) {
            comboBox.setSelectedItem(originalValue);
        } else {
            String newValue = textField.getText();
            originalValue = newValue.isEmpty() ? null : newValue;
            textField.setText(originalValue != null ? originalValue.toString() : "");
        }
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return ((MouseEvent) e).getClickCount() >= 2;
        }
        return false;
    }

    public static ArrayList<StopTableEditObservers> getObservers() {
        return observers;
    }
}

