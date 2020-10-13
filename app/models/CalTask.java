package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
public class CalTask extends Model{

    // Constructor
    public CalTask(String taskName, String taskDescription, String taskDate, boolean taskfinished)
    {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
        this.taskfinished = taskfinished;
    }

    // Atributs
    public String taskName;
    public String taskDescription;
    public String taskDate;
    public boolean taskfinished;

    //Atributs
    //@ManyToOne
    //public LinCalendar calendar;

    //TODO: Afegir atributs i constructor
}
