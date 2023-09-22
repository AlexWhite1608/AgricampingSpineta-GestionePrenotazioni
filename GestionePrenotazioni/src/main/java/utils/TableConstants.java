package utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TableConstants {

    public static final Font CELL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font HEADER_FONT_PRENOTAZIONI = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font HEADER_FONT_CALENDARIO = new Font("Segoe UI", Font.BOLD, 12);

    public static final Color ALTERNATE_CELL_COLOR = new Color(220, 232, 234);
    public static final Color SELECTION_COLOR = new Color(255, 255, 102);
    public static final Color ACCONTO_SALDATO_COLOR = new Color(14, 129, 60);
    public static final Color CALENDARIO_FIRST_DAY_COLOR = new Color(166, 197, 245);
    public static final Color CALENDARIO_WEEKEND_COLOR = new Color(208, 208, 208);
    public static final Color CALENDARIO_STARSBOX_COLOR = new Color(245, 237, 86);
    public static final Color CALENDARIO_G_COLOR = new Color(117, 232, 104);
    public static final Color CALENDARIO_MARGHERITA_COLOR = new Color(252, 163, 53);
    public static final Color CALENDARIO_BBQ_COLOR = new Color(247, 89, 87);
    public static final Color BORDER_CELL_SELECTED = Color.blue;
    public static final Color CALENDARIO_PRENOTAZIONE_COLOR =  new Color(54, 70, 225, 255);
    public static final Color ARRIVI_PARTENZE_CONFERMA_COLOR = new Color(208, 247, 208);
    public static final Color ARRIVI_CONTORNO_COLOR = new Color(63, 227, 48);
    public static final Color TABELLA_PRESENZE_COLOR = new Color(249, 83, 53);
    public static final Color TABELLA_MEZZI_COLOR = new Color(252, 175, 56);
    public static final Color TABELLA_PERCENTUALE_CRESCITA = new Color(208, 247, 208);
    public static final Color TABELLA_PERCENTUALE_DECRESCITA = new Color(252, 167, 167);
    public static final Color BUTTON_ANNULLA_FILTRO_COLORE = new Color(250, 175, 170);

    public static final int COLUMNS_WIDTH_CALENDARIO = 45;
    public static final int SEPARATOR_BORDER_WIDTH = 3;
    public static final int DAYS_BEFORE_DELETE = 30;

    // Lista dei mezzi
    public static final ArrayList<String> listaMezzi = new ArrayList<>(List.of(new String[]{"Camper", "Caravan", "Van", "Piedi + Tenda", "Auto + Tenda", "Bici + Tenda", "Moto + Tenda", "StarsBox", "Casa Mobile", "Nessuno"}));
}
