package models;

import com.google.gson.annotations.Expose;
import net.sf.oval.constraint.Range;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.persistence.*;

import java.util.Random;
import java.nio.charset.*;

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

        byte[] array = new byte[16]; // length is bounded by 16
        Random random = new Random();

        for (int i = 0; i < array.length; i++)
        {
            int r = random.nextInt(26) + 97;
            array[i] = (byte)r;
        }

        String randString = new String(array, Charset.forName("ASCII"));

        this.publicLink = randString;
    }

    //Atributs

    @Required
    @MaxSize(18)
    @Expose(serialize = true)
    public String calName;

    @MaxSize(5000)
    @Expose(serialize = true)
    public String description;

    @Required
    @Expose(serialize = true)
    public boolean isPublic;

    @Required
    @Expose(serialize = true)
    public Date createdAt;
    //public String owner;

    public String publicLink;

    // Relacio amb la llista de subscriptors
    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    public List<Subscription> subscriptions;

    // Relacions amb els esdeveniments i tasques
    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    @Expose(serialize = true)
    public List<CalEvent> events;

    @OneToMany (mappedBy="calendar", cascade=CascadeType.ALL)
    @Expose(serialize = true)
    public List<CalTask> tasks;

    @Required
    @ManyToOne
    @Expose(serialize = true)
    public User owner;

    //TODO: Afegir altres atributs rellevants

    //Nom dels elements del CRUD
    public String toString()
    {
        String s = owner.userName + "->" + calName;
        return s;
    }

}
