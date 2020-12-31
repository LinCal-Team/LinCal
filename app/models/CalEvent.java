package models;

import com.google.gson.annotations.Expose;
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
    public CalEvent(LinCalendar calendar, String eventName, String eventDescription, Date starteventDate,
                    Date finishedeventDate, String addressPhysical, String addressOnline)
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
    @MaxSize(18)
    @Expose(serialize = true)
    public String name;

    @MaxSize(5000)
    @Expose(serialize = true)
    public String description;

    @Required
    @Expose(serialize = true)
    public Date startDate;

    @Required
    @Expose(serialize = true)
    public Date endDate;

    @Expose(serialize = true)
    public String addressPhysical;

    @Expose(serialize = true)
    public String addressOnline;


    //Atributs
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
