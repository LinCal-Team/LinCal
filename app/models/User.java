package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
    }

    // Atributs
    public String userName;
    public String email;
    public String fullName;
    public String password;

    // TODO: Afegir altres atributs rellevants
}
