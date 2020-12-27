package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
public class CalTask extends Model{

    // Constructor
    public CalTask(LinCalendar calendar, String taskName, String taskDescription, Date taskDate, boolean taskfinished)
    {
        this.name = taskName;
        this.description = taskDescription;
        this.date = taskDate;
        this.completed = taskfinished;
        this.calendar = calendar;
    }

    // Atributs
    @Required
    @MaxSize(18)
    public String name;

    @MaxSize(5000)
    public String description;

    @Required
    public Date date;

    @Required
    public boolean completed;

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
