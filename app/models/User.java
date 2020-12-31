package models;


import com.google.gson.annotations.Expose;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Email;
import play.data.validation.MinSize;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends Model {

    // Constructor
    public User(String userName, String email, String fullName, String password)
    {
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.subscriptions = new ArrayList<Subscription>();
        this.ownedCalendars = new ArrayList<LinCalendar>();
    }

    // Atributs

    @Required
    @MaxSize(30)
    @Expose(serialize = true)
    public String userName;

    @Required
    @Email
    @Expose(serialize = true)
    public String email;

    @Required
    @MaxSize(100)
    public String fullName;

    @Required
    @MinSize(4)
    public String password;

    //Relacions
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Subscription> subscriptions;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    public List<LinCalendar> ownedCalendars;

    //Nom dels elements del CRUD
    public String toString()
    {
        String s = userName;
        return s;
    }
}
