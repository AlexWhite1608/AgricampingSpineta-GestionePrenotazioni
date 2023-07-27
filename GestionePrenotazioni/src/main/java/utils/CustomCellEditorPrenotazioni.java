package utils;

import controllers.TablePrenotazioniController;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class CustomCellEditorPrenotazioni extends AbstractCellEditor implements TableCellEditor {
    private JComboBox<String> comboBox;
    private JTextField textField;
    private Object originalValue;
    private int editingColumn;

    public CustomCellEditorPrenotazioni() {
        comboBox = new JComboBox<>();
        comboBox.addActionListener(e -> stopCellEditing());

        textField = new JTextField();
        textField.addActionListener(e -> stopCellEditing());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        editingColumn = column;
        originalValue = value;

        if (column == 0) {
            // TODO: Popola la combobox con i valori delle piazzole
            comboBox.removeAllItems();

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
            }
        } else {
            String newValue = textField.getText();
            originalValue = newValue.isEmpty() ? null : newValue;
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

