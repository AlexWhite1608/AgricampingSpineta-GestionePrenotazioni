package utils;

import controllers.TablePrenotazioniController;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

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
            comboBox.addItem("Piazzola 1");
            comboBox.addItem("Piazzola 2");

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
            originalValue = comboBox.getSelectedItem();
        } else {
            originalValue = textField.getText();
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
            textField.setText(originalValue != null ? originalValue.toString() : "");
        }
    }
}

