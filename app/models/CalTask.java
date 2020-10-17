package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
public class CalTask extends Model{

    // Constructor
    public CalTask(LinCalendar calendar, String taskName, String taskDescription, String taskDate, boolean taskfinished)
    {
        this.name = taskName;
        this.description = taskDescription;
        this.date = taskDate;
        this.completed = taskfinished;
        this.calendar = calendar;
    }

    // Atributs
    public String name;
    public String description;
    public String date;
    public boolean completed;

    //Atributs
    @ManyToOne
    public LinCalendar calendar;

    //TODO: Afegir atributs i constructor
}
