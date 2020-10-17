package models;


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
    }

    // Atributs
    public String userName;
    public String email;
    public String fullName;
    public String password;

    //Relacions
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Subscription> subscriptions;
}
