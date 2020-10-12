package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ManyToOne;
import java.util.Date;
import javax.persistence.CascadeType;

@Entity
public class Calendar extends Model{

    //Constructor
    public Calendar(User author, String calName, boolean isPublic){
        this.author = author;
        this.calName = calName;
        this.isPublic = isPublic;
        this.createdAt = new Date();
        this.tasks = new ArrayList<CalTask>();
        this.events = new ArrayList<CalEvent>();

    }

    //Atributs
    public String calName;
    public boolean isPublic;
    public Date createdAt;

    public User author;

    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<CalEvent> events;

    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<CalTask> tasks;

    //TODO: Afegir altres atributs rellevants

}
