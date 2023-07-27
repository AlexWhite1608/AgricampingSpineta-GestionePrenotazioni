package utils;

import controllers.MessageController;
import controllers.TablePrenotazioniController;
import data_access.Gateway;
import views.HomePage;
import views.MenuPrenotazioni;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.EventObject;

public class CustomCellEditorPrenotazioni extends AbstractCellEditor implements TableCellEditor {
    private JComboBox<String> comboBox;
    private JTextField textField;
    private Object originalValue;
    private int editingColumn;
    private int editingRow;
    private JTable tabellaPrenotazioni;

    public CustomCellEditorPrenotazioni() {
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
                TablePrenotazioniController.setListaPiazzole();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            for(String piazzola : TablePrenotazioniController.getListaPiazzole()){
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
                originalValue = selectedValue;

                // Salva le modifiche nel database nel caso della piazzola (comboBox)
                try {
                    int result = new Gateway().updateCellData(tabellaPrenotazioni, editingRow, editingColumn, selectedValue.toString());
                    if(result == 0)
                        System.err.println("Impossibile modificare il valore");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        } else {
            String newValue = textField.getText();
            originalValue = newValue.isEmpty() ? null : newValue;

            // Salva le modifiche nel database
            try {
                int result = new Gateway().updateCellData(tabellaPrenotazioni, editingRow, editingColumn, newValue);
                if(result == 0)
                    System.err.println("Impossibile modificare il valore");
                else if (result == -1) {
                    tabellaPrenotazioni.setValueAt(originalValue, editingRow, editingColumn);   //FIXME: non funziona
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        fireEditingStopped();
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
}

