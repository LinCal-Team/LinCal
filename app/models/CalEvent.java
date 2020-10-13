package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
public class CalEvent extends Model{

    // Constructor
    public CalEvent(String eventName, String eventDescription, String starteventDate, String finishedeventDate)
    {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.starteventDate = starteventDate;
        this.finishedeventDate= finishedeventDate;
    }

    // Atributs
    public String eventName;
    public String eventDescription;
    public String starteventDate;
    public String finishedeventDate;

    //Atributs
    //@ManyToOne
    //public LinCalendar calendar;

    //TODO: Afegir atributs i constructor
}
