package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

// entitat que implementa les subscripcions a calendaris de tercers.
// la subscripció pot tenir permisos de visualització o d'edició segons l'atribut isEditor.
// els permisos d'edició permeten crear i modificar esdevemients i tasques, però no permeten esborrar
// el calendari. Tampoc permeten administrar els permisos dels altres subscriptors.
// Aquestes accions estan reservades al propietari del calendari (el seu creador).
@Entity
public class Subscription extends Model{

    // Atributs
    //public Boolean isOwner;
    @Required
    public Boolean isEditor;

    // Constructor
    public Subscription (User user, LinCalendar cal, Boolean isEditor)
    {
        //this.isOwner = isOwner;
        this.isEditor = isEditor;
        this.calendar = cal;
        this.user = user;
    }

    // Relacions
    @Required
    @ManyToOne
    public User user;

    @Required
    @ManyToOne
    public LinCalendar calendar;

    //Nom dels elements del CRUD
    public String toString()
    {
        String s = user.userName + ">" + calendar.calName;
        return s;
    }

}
