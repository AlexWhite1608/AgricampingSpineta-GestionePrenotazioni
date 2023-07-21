package model;

import java.util.ArrayList;
import java.util.Objects;

public class GestorePiazzole {
    private static ArrayList<Piazzola> listaPiazzole = new ArrayList<>();

    public static Piazzola getPiazzolaFromNome(String nome){
        for(Piazzola piazzola : listaPiazzole){
            if(Objects.equals(piazzola.getNome(), nome))
                return piazzola;
        }

        return null;
    }

    public static void aggiungiPiazzola(Piazzola piazzola){
        listaPiazzole.add(piazzola);
    }

    public static void rimuoviPiazzola(String nome){
        if(getPiazzolaFromNome(nome) == null)
            //TODO: aggiungi dialog se la piazzola non è presente
            System.err.println("Piazzola non esistente!");

        for(Piazzola piazzola : listaPiazzole){
            if(Objects.equals(piazzola.getNome(), nome))
                listaPiazzole.remove(piazzola);
        }
    }

    public static ArrayList<Piazzola> getListaPiazzole() {
        return listaPiazzole;
    }
}
