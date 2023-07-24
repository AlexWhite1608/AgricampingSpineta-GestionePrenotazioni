package controllers;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class TextFieldsController {

    // Imposta solo valori interi nella textfield fornita
    public static void setupTextFieldsInteger(JTextField textField){
        DocumentFilter numberFilter = new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(numberFilter);
    }

    // Imposta solo valori stringa nella textfield fornita
    public static void setupTextFieldsString(JTextField textField){
        DocumentFilter stringFilter = new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                super.replace(fb, offset, length, text, attrs);
            }
        };

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(stringFilter);
    }

    // Imposta solo valori float nella textfield fornita
    public static void setupTextFieldsFloat(JTextField textField) {
        DocumentFilter floatFilter = new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("^[+-]?\\d*\\.?\\d*$")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("^[+-]?\\d*\\.?\\d*$")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(floatFilter);
    }

}
