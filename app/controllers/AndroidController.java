package controllers;


//import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
//import jdk.internal.event.Event;
//import jdk.nashorn.internal.objects.NativeJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.hibernate.Hibernate;
import play.db.jpa.JPA;
import play.mvc.*;
import java.util.*;
import models.*;
import play.data.validation.*;


public class AndroidController extends Controller
{

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Funció SingUp
    //Crear un usuari a través de l'aplicació d'Android
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void SignUp () {
        String fullname = params.get("fullname");
        String email = params.get("email");
        String username = params.get("user");
        String password = params.get("password");

        User u = User.find("byUsername",username).first();

        if (u!=null)
        {
            renderText("FAIL: EXISTING USER");
        }
        else
        {
            u = User.find("byEmail", email).first();
            if (u == null)
            {
                User newUser = new User(username,email,fullname, password);
                newUser.save();
                renderText("OK");
            }
            else
            {
                renderText("FAIL: EXISTING EMAIL");
            }
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Funció LogIn
    //Verifica si un usuari és correcte o no
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void LogIn () {
        String username = params.get("user");
        String password = params.get("password");

        User u = User.find("byUsername", username).first();

        if (u == null) {

            renderText("FAIL: NO USER");
        } else {
            if (u.password.equals(password)) {
                renderText("OK");
            } else {
                renderText("FAIL: WRONG PASSWORD");
            }
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Funció getCalendarList
    //Obté la llista de calendaris d'un usuari
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void getCalendarList()
    {
        String username = params.get("user");

        User u = User.find("byUsername", username).first();

        //Gson gson = new Gson().excludeFieldsWithoutExposeAnnotation().create();
        Gson gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        List<LinCalendar> ownedCalendars = u.ownedCalendars;
        List<LinCalendar> editableCalendars = new ArrayList<>();
        List<LinCalendar> nonEditableCalendars = new ArrayList<>();

        for (Subscription s : u.subscriptions)
        {
            LinCalendar cal = s.calendar;
            if (s.isEditor)
            {
                editableCalendars.add(cal);
            }
            else
            {
                nonEditableCalendars.add(cal);
            }
        }

        AndroidSentCalendars sentCalendars = new AndroidSentCalendars(ownedCalendars,editableCalendars,nonEditableCalendars);
        String jsonObject = gsonBuilder.toJson(sentCalendars);
        renderText(jsonObject);

    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Funció CreateCalendar
    //Crear un calendari a través de l'aplicació
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void CreateCalendar()
    {
        String username = params.get("user");
        String password = params.get("password");
        String calName = params.get("CalName");
        String description = params.get("description");
        Boolean isPublic = Boolean.valueOf(params.get("isPublic"));

        User u = User.find("byUsername", username).first();

        if (u == null) {

            renderText("FAIL: NO USER");
        } else {
            if (u.password.equals(password))
            {
                Boolean exist = false;

                for(LinCalendar cal: u.ownedCalendars)
                {
                    if(cal.calName.equals(calName))
                        exist = true;
                }

                if(!exist) {
                    LinCalendar calendar = new LinCalendar(u, calName, description, isPublic);
                    calendar.save();
                    u.ownedCalendars.add(calendar);
                    u.save();
                    renderText("OK");
                }
                else
                    renderText("EXIST");

            } else {
                renderText("FAIL: WRONG PASSWORD");
            }
        }
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //Funció CreateEvent
    //Crear un esdeveniment a través de l'aplicació d'Android
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void CreateEvent()
    {
        String username = params.get("user");
        String password = params.get("password");
        String name = params.get("name");
        String description = params.get("description");
        Date startDate = new Date(params.get("startDate"));
        Date endDate = new Date(params.get("endDate"));
        String addressPhysical = params.get("addressPhysical");
        String addressOnline = params.get("addressOnline");
        String calName = params.get("calName");

        User u = User.find("byUsername", username).first();

        if (u == null) {

            renderText("FAIL: NO USER");
        } else {
            if (u.password.equals(password))
            {

                //Obtenim les dates en el format correcte i evitem errors de dates
                try {

                    if(name.isEmpty()||params.get("startDate").isEmpty()||params.get("endDate").isEmpty()||calName.isEmpty())
                    {
                        renderText("EMPTY-VARIABLES");
                    }
                    else if (startDate.getTime() >= endDate.getTime()) //Evita dates on la data d'inici i final estàn canviats o són iguals
                    {
                        renderText("NO-TIME-EVENT");
                    }
                    else if(startDate.getTime() + 84400000 < endDate.getTime())
                    {
                        renderText("MORE-THAN-1DAY");
                    }
                    else
                        {
                            //Busquem el calendari i afegim l'esdeveniment en el calendari
                            LinCalendar calendar;
                            for (LinCalendar cal : u.ownedCalendars) {
                                if (cal.calName.equals(calName)) {
                                    calendar = cal;
                                    CalEvent event= new CalEvent(calendar, name, description, startDate, endDate, addressPhysical, addressOnline );
                                    event.save();
                                    calendar.events.add(event);
                                    calendar.save();
                                    JPA.em().flush();
                                    JPA.em().clear();

                                    renderText("OK");
                                }
                            }

                            for (Subscription subscription : u.subscriptions) {
                                if (subscription.calendar.calName.equals(calName)) {
                                    calendar = subscription.calendar;
                                    CalEvent event= new CalEvent(calendar, name, description, startDate, endDate, addressPhysical, addressOnline );
                                    event.save();
                                    calendar.events.add(event);
                                    calendar.save();
                                    JPA.em().flush();
                                    JPA.em().clear();

                                    renderText("OK");
                                }
                            }
                    }
                }
                catch(Exception e)
                {
                    renderText("INCORRECT-FORMAT");
                }

            } else {
                renderText("FAIL: WRONG PASSWORD");
            }
        }
    }
}

