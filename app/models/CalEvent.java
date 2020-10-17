package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
public class CalEvent extends Model{

    // Constructor
    public CalEvent(LinCalendar calendar, String eventName, String eventDescription, String starteventDate,
                    String finishedeventDate, String addressPhysical, String addressOnline)
    {
        this.name = eventName;
        this.description = eventDescription;
        this.startDate = starteventDate;
        this.endDate= finishedeventDate;
        this.calendar = calendar;
        this.addressOnline = addressOnline;
        this.addressPhysical = addressPhysical;
    }

    // Atributs
    public String name;
    public String description;
    public String startDate;
    public String endDate;
    public String addressPhysical;
    public String addressOnline;


    //Atributs
    @ManyToOne
    public LinCalendar calendar;

    //TODO: Afegir atributs i constructor
}
