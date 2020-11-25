package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.persistence.*;

@Entity
public class LinCalendar extends Model{

    //Constructor
    public LinCalendar(User owner, String calName, String description, boolean isPublic){
        this.owner = owner;
        this.calName = calName;
        this.description = description;
        this.isPublic = isPublic;
        this.createdAt = new Date();
        this.tasks = new ArrayList<CalTask>();
        this.events = new ArrayList<CalEvent>();
        this.subscriptions = new ArrayList<Subscription>();

        this.publicLink = "testlink";
    }

    //Atributs

    @Required
    @MaxSize(30)
    public String calName;

    @MaxSize(5000)
    public String description;

    @Required
    public boolean isPublic;

    @Required
    public Date createdAt;
    //public String owner;
    public String publicLink;

    // Relacio amb la llista de subscriptors
    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<Subscription> subscriptions;

    // Relacions amb els esdeveniments i tasques
    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<CalEvent> events;

    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<CalTask> tasks;

    @Required
    @ManyToOne
    public User owner;

    //TODO: Afegir altres atributs rellevants

    //Nom dels elements del CRUD
    public String toString()
    {
        String s = owner.userName + "->" + calName;
        return s;
    }

}
