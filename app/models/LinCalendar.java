package models;

import play.db.jpa.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.persistence.*;

@Entity
public class LinCalendar extends Model{

    //Constructor
    public LinCalendar(String author, String calName, boolean isPublic){
        this.author = author;
        this.calName = calName;
        this.isPublic = isPublic;
        this.createdAt = new Date();
        /*
        this.tasks = new ArrayList<CalTask>();
        this.events = new ArrayList<CalEvent>();

         */

    }

    //Atributs
    public String calName;
    public boolean isPublic;
    public Date createdAt;

    public String author;

    /*
    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<CalEvent> events;

    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<CalTask> tasks;
*/
    //TODO: Afegir altres atributs rellevants

}
