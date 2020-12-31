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
    @Expose(serialize = true)
    public String name;

    @MaxSize(5000)
    @Expose(serialize = true)
    public String description;

    @Required
    @Expose(serialize = true)
    public Date date;

    @Required
    @Expose(serialize = true)
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
