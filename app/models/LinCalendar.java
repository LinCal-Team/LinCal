package models;

import play.db.jpa.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.persistence.*;

@Entity
public class LinCalendar extends Model{

    //Constructor
    public LinCalendar(User owner, String calName, boolean isPublic){
        this.owner = owner;
        this.calName = calName;
        this.isPublic = isPublic;
        this.createdAt = new Date();
        this.tasks = new ArrayList<CalTask>();
        this.events = new ArrayList<CalEvent>();
        this.subscriptions = new ArrayList<Subscription>();

        this.publicLink = "testlink";
    }

    //Atributs
    public String calName;
    public boolean isPublic;
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

    @ManyToOne
    public User owner;

    //TODO: Afegir altres atributs rellevants

}
