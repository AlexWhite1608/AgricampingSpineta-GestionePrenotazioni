package utils;

import controllers.TablePrenotazioniController;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class CustomCellEditorPrenotazioni extends AbstractCellEditor implements TableCellEditor {

    private JTextField textField;

    public CustomCellEditorPrenotazioni() {
        textField = new JTextField();
    }

    @Override
    public Object getCellEditorValue() {
        return textField.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textField.setText((String) value);
        return textField;
    }
}
