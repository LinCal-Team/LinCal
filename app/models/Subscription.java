package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


@Entity
public class Subscription extends Model{

    // Atributs
    public Boolean isOwner;
    public Boolean isEditor;

    // Constructor
    public Subscription (User user, LinCalendar cal, Boolean isOwner, Boolean isEditor)
    {
        this.isOwner = isOwner;
        this.isEditor = isEditor;
        this.calendar = cal;
        this.user = user;
    }

    // Relacions
    @ManyToOne
    public User user;
    @ManyToOne
    public LinCalendar calendar;

}
