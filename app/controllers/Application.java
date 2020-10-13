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

        User usuari1 = new User("Ixion", "blueixion@gmail.com", "Joaquim", "blue");
        usuari1.save();

        usuari1 = new User("BaboRulez", "babo@gmail.com", "Babo", "8480");
        usuari1.save();

        CalTask Tasca1 = new CalTask("Mart 2024", "Elon Musk envia l'home al planteta roig", "31/12/2024", false);
        Tasca1.save();

        CalEvent Esdeveniment1 = new CalEvent("PES", "Classe setmanal de PES", "13/10/2020", "13/10/2020");
        Esdeveniment1.save();

        LinCalendar calendari1 = new LinCalendar("Babo", "UPC", false);
        calendari1.save();

        List<User> llistaUsers = User.findAll();
        List<LinCalendar> llistaCalendars = LinCalendar.findAll();
        List<CalEvent> llistaEvents = CalEvent.findAll();
        List<CalTask> llistaTasks = CalTask.findAll();


        String nomUsuari = llistaUsers.get(0).fullName;
        String nomUsuari2 = llistaUsers.get(1).fullName;
        String nomCalendari1 = llistaCalendars.get(0).calName;
        String descripcioTasca1 = llistaTasks.get(0).taskDescription;
        String nomEsdeveniment1 = llistaEvents.get(0).eventName;


        render(nomUsuari, nomUsuari2, nomCalendari1, descripcioTasca1, nomEsdeveniment1);
    }
}