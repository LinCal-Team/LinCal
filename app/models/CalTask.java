package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ManyToOne;
import java.util.Date;
@Entity
public class CalTask extends Model{

    //Constructor

    //Atributs
    @ManyToOne
    public Calendar calendar;

    //TODO: Afegir atributs i constructor
}
