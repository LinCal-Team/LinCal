package controllers;


//import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
//import jdk.internal.event.Event;
//import jdk.nashorn.internal.objects.NativeJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.hibernate.Hibernate;
import play.mvc.*;
import java.util.*;
import models.*;
import play.data.validation.*;


public class AndroidController extends Controller
{
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
                User newUser = new User(fullname,email,username, password);
                newUser.save();
                renderText("OK");
            }
            else
            {
                renderText("FAIL: EXISTING EMAIL");
            }
        }
    }
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

    public static void getCalendarList()
    {
        String username = params.get("user");

        User u = User.find("byUsername", username).first();

        /*
        String JSONstring = "userCalendars = [";

        for(int i=0; i<u.ownedCalendars.size(); i++)
        {
            JSONstring += "{" +
                    " \"id\" : \"" + u.ownedCalendars.get(i).id + "\"," +
                    " \"name\" : \"" + u.ownedCalendars.get(i).calName + "\"," +
                    " \"description\" : \"" + u.ownedCalendars.get(i).description + "\"," +
                    " \"createdAt\" : \"" + u.ownedCalendars.get(i).createdAt + "\"," +
                    " \"creator\" : \"" + u.ownedCalendars.get(i).owner.userName + "\",";

            JSONstring += " events : [";

            for(int j=0; j<u.ownedCalendars.get(i).events.size(); j++)
            {
                JSONstring += "{" +
                        " \"id\" : \"" + u.ownedCalendars.get(i).events.get(j).id + "\"," +
                        " \"name\" : \"" + u.ownedCalendars.get(i).events.get(j).name + "\"," +
                        " \"description\" : \"" + u.ownedCalendars.get(i).events.get(j).description + "\"," +
                        " \"startDate\" : \"" + u.ownedCalendars.get(i).events.get(j).startDate.toString() + "\"," +
                        " \"endDate\" : \"" + u.ownedCalendars.get(i).events.get(j).endDate.toString() + "\"," +
                        " \"addressOnline\" : \"" + u.ownedCalendars.get(i).events.get(j).addressOnline + "\"," +
                        " \"addressPhysical\" : \"" + u.ownedCalendars.get(i).events.get(j).addressPhysical + "\"" +
                        "}";

                if(j<(u.ownedCalendars.get(i).events.size()-1))
                {
                    JSONstring += ",";
                }
            }

            JSONstring += " ]," +
                    " tasks : [";

            for(int k=0; k<u.ownedCalendars.get(i).tasks.size(); k++)
            {
                JSONstring += "{" +
                        " \"id\" : \"" + u.ownedCalendars.get(i).tasks.get(k).id + "\"," +
                        " \"name\" : \"" + u.ownedCalendars.get(i).tasks.get(k).name + "\"," +
                        " \"description\" : \"" + u.ownedCalendars.get(i).tasks.get(k).description + "\"," +
                        " \"endDate\" : \"" + u.ownedCalendars.get(i).tasks.get(k).date.toString() + "\"," +
                        " \"completed\" : \"" + u.ownedCalendars.get(i).tasks.get(k).completed + "\"" +
                        "}";

                if(k<(u.ownedCalendars.get(i).tasks.size()-1))
                {
                    JSONstring += ",";
                }
            }

            JSONstring += " ]";

            JSONstring += "}";

            if(i<(u.ownedCalendars.size()-1))
            {
                JSONstring += ",";
            }

        }

        JSONstring += "]";

        renderText(JSONstring);
        */

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
}

