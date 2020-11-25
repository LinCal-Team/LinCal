package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
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
    @Required
    @MaxSize(100)
    public String name;

    @MaxSize(5000)
    public String description;

    @Required
    public String startDate;

    @Required
    public String endDate;

    public String addressPhysical;
    public String addressOnline;


    //Atributs
    @Required
    @ManyToOne
    public LinCalendar calendar;

    //TODO: Afegir atributs i constructor

    //Nom dels elements del CRUD
    public String toString()
    {
        String s = calendar.owner.userName + "->" + calendar.calName + "->" + name;
        return s;
    }
}
