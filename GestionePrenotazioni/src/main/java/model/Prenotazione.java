package model;

import utils.IDGenerator;

import java.time.LocalDate;
import java.util.Date;

public class Prenotazione {

    private final String id = IDGenerator.generateRandomId();
    private Piazzola piazzola;
    private Date dataArrivo;
    private Date dataPartenza;
    private String nome;
    private String acconto = "";
    private String info = "";
    private String telefono = "";
    private String email = "";

    public Prenotazione(Piazzola piazzola, Date dataArrivo, Date dataPartenza, String nome, String acconto, String info, String telefono, String email) {
        this.piazzola = piazzola;
        this.dataArrivo = dataArrivo;
        this.dataPartenza = dataPartenza;
        this.nome = nome;
        this.acconto = acconto;
        this.info = info;
        this.telefono = telefono;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public Piazzola getPiazzola() {
        return piazzola;
    }

    public Date getDataArrivo() {
        return dataArrivo;
    }

    public Date getDataPartenza() {
        return dataPartenza;
    }

    public String getNome() {
        return nome;
    }

    public String getAcconto() {
        return acconto;
    }

    public String getInfo() {
        return info;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
