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

        User usuari1 = new User("Ixion", "blueixion@gmail.com", "Joaquim");
        usuari1.save();

        usuari1 = new User("BaboRulez", "babo@gmail.com", "Babo");
        usuari1.save();

        List<User> llistaUsers = User.findAll();
        String nomUsuari = llistaUsers.get(0).fullName;
        String nomUsuari2 = llistaUsers.get(1).fullName;
        render(nomUsuari, nomUsuari2);
    }
}