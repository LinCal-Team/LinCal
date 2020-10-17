package controllers;

import play.*;
import play.mvc.*;
import java.util.*;
import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    // Per executar servei des del navegador
    // localhost:9000/Application/inicialitzarBaseDades

    public static void inicialitzarBaseDades(){

        // Accio Sign-Up
        //-----------------------------------------
        User usuari1 = new User("Ixion", "blueixion@gmail.com", "Joaquim", "blue");
        usuari1.save();

        usuari1 = new User("BaboRulez", "babo@gmail.com", "Babo", "8480");
        usuari1.save();
        // ------------------------------------------


        // Accio Crear Calendari
        // -------------------------------------------
        LinCalendar calendari1 = new LinCalendar(usuari1.fullName, "UPC", false);
        calendari1.save();

        Subscription subscription = new Subscription(usuari1, calendari1, true, true);
        subscription.save();
        usuari1.subscriptions.add(subscription);
        usuari1.save();
        calendari1.subscriptions.add(subscription);
        calendari1.save();
        // -------------------------------------------


        // Accio Crear tasques i esdeveniments
        CalTask tasca1 = new CalTask(calendari1, "Mart 2024", "Elon Musk envia l'home al planteta roig", "31/12/2024", false);
        tasca1.save();
        calendari1.tasks.add(tasca1);
        calendari1.save();

        CalEvent esdeveniment1 = new CalEvent(calendari1, "PES", "Classe setmanal de PES",
                "13/10/2020", "13/10/2020", "A casa", "www.google.com");
        esdeveniment1.save();
        calendari1.events.add(esdeveniment1);
        calendari1.save();


        List<User> llistaUsers = User.findAll();
        List<LinCalendar> llistaCalendars = LinCalendar.findAll();
        List<CalEvent> llistaEvents = CalEvent.findAll();
        List<CalTask> llistaTasks = CalTask.findAll();



        String nomUsuari = llistaUsers.get(0).fullName;
        String nomUsuari2 = llistaUsers.get(1).fullName;
        String nomCalendari1 = llistaCalendars.get(0).calName;
        String descripcioTasca1 = llistaTasks.get(0).description;
        String nomEsdeveniment1 = llistaEvents.get(0).name;


        render(nomUsuari, nomUsuari2, nomCalendari1, descripcioTasca1, nomEsdeveniment1);
    }
}