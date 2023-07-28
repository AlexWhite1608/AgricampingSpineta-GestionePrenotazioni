package controllers;

import data_access.Gateway;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ControllerPiazzole {
    static ArrayList<String> listaPiazzole = new ArrayList<>();

    // Carica tutte le piazzole
    public static void setListaPiazzole() throws SQLException {
        ResultSet piazzoleRs = new Gateway().execSelectQuery("SELECT * FROM Piazzole");
        while (piazzoleRs.next()) {
            if(!listaPiazzole.contains(piazzoleRs.getString("Nome")))
                listaPiazzole.add(piazzoleRs.getString("Nome"));
        }
        piazzoleRs.close();
    }

    public static ArrayList<String> getListaPiazzole() {
        return listaPiazzole;
    }

    // Rimuove le piazzole dalla lista
    public static void removePiazzolaFromList(String value){
        listaPiazzole.remove(value);
    }
}
