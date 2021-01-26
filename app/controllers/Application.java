package controllers;
import play.db.jpa.Model;
import java.util.concurrent.TimeUnit;


import org.hibernate.Hibernate;
import play.db.jpa.JPA;
import play.mvc.*;
import java.util.*;
import models.*;
import play.data.validation.*;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PessimisticLockScope;

public class Application extends Controller {

    @Before
    static void updateTemplateArgs ()
    {
       User connectedUser = connectedUser();
       if (connectedUser != null)
       {
           renderArgs.put("user", connectedUser);

           List<LinCalendar> editableCalendars = new ArrayList<>();

           for (LinCalendar cal : connectedUser.ownedCalendars)
           {
               editableCalendars.add(cal);
           }
           for (Subscription sub : connectedUser.subscriptions)
           {
               if (sub.isEditor)
               {
                   editableCalendars.add(sub.calendar);
               }
           }
           renderArgs.put("editableCalendars", editableCalendars);

           List<CalEvent> events = new ArrayList<>();
           List<CalEvent> editableEvents = new ArrayList<>();
           List<CalEvent> nonEditableEvents = new ArrayList<>();
           User user = User.findById(connectedUser.id);

           for (LinCalendar cal : user.ownedCalendars)
           {
               for (CalEvent event : cal.events)
               {
                   editableEvents.add(event);
                   events.add(event);
               }
           }
           for (Subscription s : user.subscriptions)
           {
               LinCalendar cal = s.calendar;
               if (s.isEditor)
               {
                   for (CalEvent event : cal.events)
                   {
                       editableEvents.add(event);
                       events.add(event);
                   }
               }
               else
               {
                   for (CalEvent event : cal.events)
                   {
                       nonEditableEvents.add(event);
                       events.add(event);
                   }
               }
           }

           List<CalTask> tasks = new ArrayList<>();
           List<CalTask> editableTasks = new ArrayList<>();
           List<CalTask> nonEditableTasks = new ArrayList<>();
           for (LinCalendar cal : user.ownedCalendars)
           {
               for (CalTask task : cal.tasks)
               {
                   tasks.add(task);
                   editableTasks.add(task);
               }
           }
           for (Subscription s : user.subscriptions)
           {
               LinCalendar cal = s.calendar;
               if (s.isEditor)
               {
                   for (CalTask task : cal.tasks)
                   {
                       tasks.add(task);
                       editableTasks.add(task);
                   }
               }
               else
               {
                   for (CalTask task : cal.tasks)
                   {
                       tasks.add(task);
                       nonEditableTasks.add(task);
                   }
               }

           }
           renderArgs.put("events", events);
           renderArgs.put("tasks", tasks);
           renderArgs.put("editableEvents", editableEvents);
           renderArgs.put("editableTasks", editableTasks);
           renderArgs.put("nonEditableEvents", nonEditableEvents);
           renderArgs.put("nonEditableTasks", nonEditableTasks);
       }
    }

    static User connectedUser ()
    {
        User connectedUser;
        String username = session.get("username");
        if (username == null)
        {
            return null;
        }
        else
        {
            /*
            connectedUser = renderArgs.get("user", User.class);

            if (connectedUser == null)
            {
                connectedUser = User.find("byUsername", username).first();
            }
            return connectedUser;
            */

            connectedUser = User.find("byUsername", username).first();
            return connectedUser;
        }
    }


    @Before(unless = {"ChangePage"})
    public static void setCalendarItems()
    {

        Date today = new Date();
        Date CalendarPage = new Date(today.getTime());
        session.put("DatePage",CalendarPage.getTime());

        String[] month =
                {"Gener",
                        "Febrer",
                        "Març",
                        "Abril",
                        "Maig",
                        "Juny",
                        "Juliol",
                        "Agost",
                        "Setembre",
                        "Octubre",
                        "Novembre",
                        "Decembre"};
        String[] week =
                {       "Diumenge",
                        "Dilluns",
                        "Dimarts",
                        "Dimecres",
                        "Dijous",
                        "Divendres",
                        "Dissabte"
                };

        /*
        String Monday;
        Date MondayDate;
        if((today.getDate()-today.getDay()+1)>0) {
            Monday = (today.getDate() - today.getDay() + 1) + " " + month[today.getMonth()];
            MondayDate = new Date(today.getYear(),today.getMonth(),today.getDate() - today.getDay() + 1,0,0);
        }
        else {
            Monday = (new Date(today.getYear(), today.getMonth() - 1, 0).getDate()
                    + today.getDate() - today.getDay() + 1) + " " + month[today.getMonth()-1];
            MondayDate = new Date(today.getYear(),today.getMonth()-1,(new Date(today.getYear(), today.getMonth() - 1, 0).getDate() + today.getDate() - today.getDay() + 1),0,0);
        }

        String Sunday;
        Date SundayDate;
        if((today.getDate()+7-today.getDay())>new Date(today.getYear(), today.getMonth(),0).getDate()) {
            Sunday = (today.getDate() + 7 - today.getDay() - new Date(today.getYear(), today.getMonth(), 0).getDate())
                    + " " + month[today.getMonth() + 1];
            SundayDate = new Date(today.getYear(),(today.getMonth() + 1),(today.getDate() + 7 - today.getDay() - new Date(today.getYear(), today.getMonth(), 0).getDate()),23,59,59);
        }
        else {
            Sunday = (today.getDate() + 7 - today.getDay()) + " " + month[today.getMonth()];
            SundayDate = new Date(today.getYear(),(today.getMonth()),(today.getDate() + 7 - today.getDay()),23,59,59);
        }
        */

        //Corrector del Diumenge mal indicat
        int WeekDayPageIndicator = 0;
        if (CalendarPage.getDay() == 0)
            WeekDayPageIndicator = 7;

        Date[] WeekDays = {
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 1)*24*3600*1000), //Dilluns
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 2)*24*60*60*1000), //Dimarts
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 3)*24*60*60*1000), //Dimecres
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 4)*24*60*60*1000), //Dijous
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 5)*24*60*60*1000), //Divendres
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 6)*24*60*60*1000), //Dissabte
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 7)*24*60*60*1000)  //Diumenge
        };

        Date[] WeekDaysStart = {
                new Date(WeekDays[0].getYear(),WeekDays[0].getMonth(),WeekDays[0].getDate(),0,0,0), //Dilluns
                new Date(WeekDays[1].getYear(),WeekDays[1].getMonth(),WeekDays[1].getDate(),0,0,0), //Dimarts
                new Date(WeekDays[2].getYear(),WeekDays[2].getMonth(),WeekDays[2].getDate(),0,0,0), //Dimecres
                new Date(WeekDays[3].getYear(),WeekDays[3].getMonth(),WeekDays[3].getDate(),0,0,0), //Dijous
                new Date(WeekDays[4].getYear(),WeekDays[4].getMonth(),WeekDays[4].getDate(),0,0,0), //Divendres
                new Date(WeekDays[5].getYear(),WeekDays[5].getMonth(),WeekDays[5].getDate(),0,0,0), //Dissabte
                new Date(WeekDays[6].getYear(),WeekDays[6].getMonth(),WeekDays[6].getDate(),0,0,0)  //Diumenge
        };
        Date[] WeekDaysEnd = {
                new Date(WeekDays[0].getYear(),WeekDays[0].getMonth(),WeekDays[0].getDate(),23,59,59), //Dilluns
                new Date(WeekDays[1].getYear(),WeekDays[1].getMonth(),WeekDays[1].getDate(),23,59,59), //Dimarts
                new Date(WeekDays[2].getYear(),WeekDays[2].getMonth(),WeekDays[2].getDate(),23,59,59), //Dimecres
                new Date(WeekDays[3].getYear(),WeekDays[3].getMonth(),WeekDays[3].getDate(),23,59,59), //Dijous
                new Date(WeekDays[4].getYear(),WeekDays[4].getMonth(),WeekDays[4].getDate(),23,59,59), //Divendres
                new Date(WeekDays[5].getYear(),WeekDays[5].getMonth(),WeekDays[5].getDate(),23,59,59), //Dissabte
                new Date(WeekDays[6].getYear(),WeekDays[6].getMonth(),WeekDays[6].getDate(),23,59,59)  //Diumenge
        };
        String Monday = WeekDays[0].getDate() + " " + month[WeekDays[0].getMonth()] + " " + (1900 + WeekDays[0].getYear());
        String Sunday = WeekDays[6].getDate() + " " + month[WeekDays[6].getMonth()] + " " + (1900 + WeekDays[6].getYear());

        //Fer un String que el DateTime-Local pugui llegir correctament
        String TodayDateTimeLocal = "0000-00-00T00:00";
        TodayDateTimeLocal = String.valueOf(1900+today.getYear()) + "-";
        if(String.valueOf(today.getMonth()).length() == 2)
            TodayDateTimeLocal += String.valueOf(today.getMonth()) + "-";
        else
            TodayDateTimeLocal += "0" + String.valueOf(today.getMonth()) + "-";
        if(String.valueOf(today.getDate()).length() == 2)
            TodayDateTimeLocal += String.valueOf(today.getDate()) + "T";
        else
            TodayDateTimeLocal += "0" + String.valueOf(today.getDate()) + "T";
        if(String.valueOf(today.getHours()).length() == 2)
            TodayDateTimeLocal += String.valueOf(today.getHours()) + ":";
        else
            TodayDateTimeLocal += "0" + String.valueOf(today.getHours()) + ":";
        if(String.valueOf(today.getMinutes()).length() == 2)
            TodayDateTimeLocal += String.valueOf(today.getMinutes());
        else
            TodayDateTimeLocal += "0" + String.valueOf(today.getMinutes());

        renderArgs.put("Month", Monday + " - " + Sunday);
        renderArgs.put("Today", today);
        renderArgs.put("TodayDateTimeLocal",TodayDateTimeLocal);
        renderArgs.put("Week", week[today.getDay()]);
        renderArgs.put("WeekDaysStart", WeekDaysStart);
        renderArgs.put("WeekDaysEnd", WeekDaysEnd);

        User connectedUser = connectedUser();
        if (connectedUser != null)
        {
            List<CalEvent> events = new ArrayList<>();

            for (LinCalendar cal : connectedUser.ownedCalendars) {
                for (CalEvent event : cal.events) {
                    if ((event.startDate.getTime() < WeekDaysEnd[6].getTime()) &&
                            (event.endDate.getTime() > WeekDaysStart[0].getTime()))
                        events.add(event);
                }
            }
            for (Subscription s : connectedUser.subscriptions) {
                LinCalendar cal = s.calendar;
                for (CalEvent event : cal.events) {
                    if ((event.startDate.getTime() < WeekDaysEnd[6].getTime()) && (event.endDate.getTime() > WeekDaysStart[0].getTime()))
                        events.add(event);
                }
            }


            renderArgs.put("eventsShow", events);
        }
    }

    public static void ChangePage(int page)
    {
        Date today = new Date();
        Date CalendarPage;

        if(page == 0)
        {
            CalendarPage = new Date(today.getTime());
        }
        else {
            try {
                CalendarPage = new Date(Long.valueOf(session.get("DatePage")));
                CalendarPage = new Date(CalendarPage.getTime() + (long) page * 7 * 24 * 3600 * 1000);
            } catch (Exception exception) {
                CalendarPage = new Date(today.getTime() + (long) page * 7 * 24 * 3600 * 1000);
            }
        }
        session.put("DatePage",CalendarPage.getTime());


        String[] month =
                {"Gener",
                        "Febrer",
                        "Març",
                        "Abril",
                        "Maig",
                        "Juny",
                        "Juliol",
                        "Agost",
                        "Setembre",
                        "Octubre",
                        "Novembre",
                        "Decembre"};
        String[] week =
                {       "Diumenge",
                        "Dilluns",
                        "Dimarts",
                        "Dimecres",
                        "Dijous",
                        "Divendres",
                        "Dissabte"
                };

        //Corrector del Diumenge mal indicat
        int WeekDayPageIndicator = 0;
        if (CalendarPage.getDay() == 0)
            WeekDayPageIndicator = 7;

        Date[] WeekDays = {
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 1)*24*3600*1000), //Dilluns
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 2)*24*60*60*1000), //Dimarts
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 3)*24*60*60*1000), //Dimecres
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 4)*24*60*60*1000), //Dijous
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 5)*24*60*60*1000), //Divendres
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 6)*24*60*60*1000), //Dissabte
                new Date(CalendarPage.getTime() - (WeekDayPageIndicator + CalendarPage.getDay() - 7)*24*60*60*1000)  //Diumenge
        };

        Date[] WeekDaysStart = {
                new Date(WeekDays[0].getYear(),WeekDays[0].getMonth(),WeekDays[0].getDate(),0,0,0), //Dilluns
                new Date(WeekDays[1].getYear(),WeekDays[1].getMonth(),WeekDays[1].getDate(),0,0,0), //Dimarts
                new Date(WeekDays[2].getYear(),WeekDays[2].getMonth(),WeekDays[2].getDate(),0,0,0), //Dimecres
                new Date(WeekDays[3].getYear(),WeekDays[3].getMonth(),WeekDays[3].getDate(),0,0,0), //Dijous
                new Date(WeekDays[4].getYear(),WeekDays[4].getMonth(),WeekDays[4].getDate(),0,0,0), //Divendres
                new Date(WeekDays[5].getYear(),WeekDays[5].getMonth(),WeekDays[5].getDate(),0,0,0), //Dissabte
                new Date(WeekDays[6].getYear(),WeekDays[6].getMonth(),WeekDays[6].getDate(),0,0,0)  //Diumenge
        };
        Date[] WeekDaysEnd = {
                new Date(WeekDays[0].getYear(),WeekDays[0].getMonth(),WeekDays[0].getDate(),23,59,59), //Dilluns
                new Date(WeekDays[1].getYear(),WeekDays[1].getMonth(),WeekDays[1].getDate(),23,59,59), //Dimarts
                new Date(WeekDays[2].getYear(),WeekDays[2].getMonth(),WeekDays[2].getDate(),23,59,59), //Dimecres
                new Date(WeekDays[3].getYear(),WeekDays[3].getMonth(),WeekDays[3].getDate(),23,59,59), //Dijous
                new Date(WeekDays[4].getYear(),WeekDays[4].getMonth(),WeekDays[4].getDate(),23,59,59), //Divendres
                new Date(WeekDays[5].getYear(),WeekDays[5].getMonth(),WeekDays[5].getDate(),23,59,59), //Dissabte
                new Date(WeekDays[6].getYear(),WeekDays[6].getMonth(),WeekDays[6].getDate(),23,59,59)  //Diumenge
        };
        String Monday = WeekDays[0].getDate() + " " + month[WeekDays[0].getMonth()] + " " + (1900 + WeekDays[0].getYear());
        String Sunday = WeekDays[6].getDate() + " " + month[WeekDays[6].getMonth()] + " " + (1900 + WeekDays[6].getYear());

        renderArgs.put("Month", Monday + " - " + Sunday);
        renderArgs.put("Today", today);
        renderArgs.put("Week", week[today.getDay()]);
        renderArgs.put("WeekDaysStart", WeekDaysStart);
        renderArgs.put("WeekDaysEnd", WeekDaysEnd);

        User connectedUser = connectedUser();
        if (connectedUser != null)
        {
            List<CalEvent> events = new ArrayList<>();

            for (LinCalendar cal : connectedUser.ownedCalendars) {
                for (CalEvent event : cal.events) {
                    if ((event.startDate.getTime() < WeekDaysEnd[6].getTime()) &&
                            (event.endDate.getTime() > WeekDaysStart[0].getTime()))
                        events.add(event);
                }
            }
            for (Subscription s : connectedUser.subscriptions) {
                LinCalendar cal = s.calendar;
                for (CalEvent event : cal.events) {
                    if ((event.startDate.getTime() < WeekDaysEnd[6].getTime()) && (event.endDate.getTime() > WeekDaysStart[0].getTime()))
                        events.add(event);
                }
            }

            renderArgs.put("eventsShow", events);
            String userN = connectedUser.userName;

            long calendarSelectedID = Long.valueOf(session.get("calendarSelectedID"));
            renderArgs.put("calendarSelectedID",calendarSelectedID);
            render("Application/LogIn.html",userN);
        }
    }

    public static Date dateFormatConverter (String date)
    {
        int yearDate = (int)Integer.valueOf(date.split("T")[0].split("-")[0])-1900;
        int monthDate = (int)Integer.valueOf(date.split("T")[0].split("-")[1])-1;
        int dayDate = (int)Integer.valueOf(date.split("T")[0].split("-")[2]);
        int hourDate = (int)Integer.valueOf(date.split("T")[1].split(":")[0]);
        int minuteDate = (int)Integer.valueOf(date.split("T")[1].split(":")[1]);

        Date dateRes = new Date(yearDate,monthDate,dayDate,hourDate,minuteDate);

        return dateRes;
    }

    public static boolean initUser (String username)
    {
        User user = User.find("byUsername", username).first();
        if (user == null)
        {
            return false;
        }
        else
        {
            session.put("username", username);
            renderArgs.put("user", user);
            return true;
        }
    }

    public static void index()
    {
        flash.clear();
        if (connectedUser() != null)
        {
            String userN = connectedUser().userName;
            renderTemplate("Application/LogIn.html", userN);
        }
        else
        {
            render();
        }
    }

    // Per executar servei des del navegador
    // localhost:9000/Application/inicialitzarBaseDades

    public static void LogIn(@Required String username,@Required String password){
         if (validation.hasErrors())
         {
             flash.error("Falten camps per omplir.");
             index();
         }

        User u = User.find("byUsername",username).first();

        if (u==null)
        {
            flash.error("Usuari inexistent");
            index();
        }
        else
        {
            if (u.password.equals(password))
            {
                String userN=u.userName;
                boolean initOk = initUser(userN);
                if (initOk)
                {
                    updateTemplateArgs();
                    render(userN);
                }
            }
            else
            {
                flash.error("Contrassenya incorrecta.");
                index();
            }
        }
    }

    public static void Logout ()
    {
        session.remove("username");
        renderTemplate("Application/index.html");
    }

    public static void SignUpForm()
    {
        render();
    }

    public static void SignUp(@Valid User user)
    {
        if (validation.hasErrors())
        {
            params.flash();
            Validation.keep();
            SignUpForm();
        }

        User u = User.find("byUsername",user.userName).first();

        if (u!=null)
        {
            flash.error("Aquest usuari ja existeix!");
            SignUpForm();
        }
        else
        {
            u = User.find("byEmail", user.email).first();
            if (u == null)
            {
                User newUser = user;
                newUser.save();
                String userN = newUser.userName;
                flash.put("messageOK", "Benvingut a LinCal, " + userN + ".");
                render("Application/index.html");
            }
            else
            {
                flash.error("Aquest correu electrònic ja s'ha utilitzat!");
                SignUpForm();
            }
        }
    }

    public static void DeleteUserForm()
    {
        String username = session.get("username");
        User u = User.find("byUsername", username).first();
        if (u == null)
        {
            renderText("Error al DeleteUserForm: l'usuari no existeix!, username =" + username);
        }
        else
        {
            render(username);
        }
    }

    public static void DeleteUser(@Required String username, @Required String password)
    {
        if (validation.hasErrors())
        {
            flash.error("Falten camps per omplir.");
            render("Application/DeleteUserForm.html", username);
        }

        User u = User.find("byUsername",username).first();

        if (u==null)
        {
            flash.error("Error al DeleteUser: L'usuari no existeix!");
            index();
        }
        else
        {
            if (!u.password.equals(password))
            {
                flash.error("Contrassenya incorrecta!");
                render("Application/DeleteUserForm.html", username);
            }
            else
            {
                LinCalendar cal;
                for (Subscription subscription : u.subscriptions)
                {
                    cal = subscription.calendar;
                    cal.subscriptions.remove(subscription);
                }
                for (LinCalendar calendar : u.ownedCalendars)
                {
                    for (Subscription subscription : calendar.subscriptions)
                    {
                        subscription.user.subscriptions.remove(subscription);
                    }
                }
                u.delete();
                renderText("Usuari esborrat!");
            }
        }
    }

    public static void ManageSubscriptionsForm()
    {
        List <LinCalendar> publicCalendars = LinCalendar.find("byIspublic", true).fetch();
        List <LinCalendar> publicCalendarsFiltered = new ArrayList<>();
        User user = connectedUser();
        //List <LinCalendar> calendars = LinCalendar.find("byOwner", session.get("username")).fetch();
        for (LinCalendar cal : publicCalendars)
        {
            if (cal.owner != user)
            {
                publicCalendarsFiltered.add(cal);
            }
        }

        List <LinCalendar> nonSubscribedCalendars = new ArrayList<>();
        List <LinCalendar> subscribedCalendars = new ArrayList<>();


        for (Subscription sub : user.subscriptions)
        {
            subscribedCalendars.add(sub.calendar);
        }

        for (LinCalendar cal : publicCalendarsFiltered)
        {
            if (!subscribedCalendars.contains(cal))
            {
                nonSubscribedCalendars.add(cal);
            }
        }

        renderArgs.put("subscriptions", user.subscriptions);
        renderArgs.put("nonSubscribedCalendars", nonSubscribedCalendars);
        render();
    }

    public static void AddCalendarSubscription(long id)
    {
        LinCalendar calendar = LinCalendar.findById(id);
        if(calendar == null)
        {
            flash.error("Error: No hem trobat el calendari!");
            Logout();
        }

        else
        {
            User user = connectedUser();
            Subscription subscription = new Subscription(user, calendar, false);
            user.subscriptions.add(subscription);
            user.save();
            updateTemplateArgs();
            ManageSubscriptionsForm();
        }
    }

    public static void RemoveCalendarSubscription(long id)
    {
        Subscription sub = Subscription.findById(id);
        if(sub == null)
        {
            flash.error("Error: No hem trobat la subscripció!");
            Logout();
        }

        else
        {
            sub.delete();
            JPA.em().flush();
            JPA.em().clear();
            updateTemplateArgs();
            setCalendarItems();
            ManageSubscriptionsForm();
        }
    }

    public static void SelectCalendar(long calendarId)
    {
        renderArgs.put("calendarSelectedID",calendarId);
        String userN = connectedUser().userName;
        session.put("calendarSelectedID",calendarId);
        render("Application/LogIn.html",userN);
    }

    public static void CreateCalendarForm()
    {
        String username = session.get("username");
        render();
    }

    public static void CreateCalendar(String calName, String description, boolean isPublic)
    {
        User owner = User.find("byUsername", session.get("username")).first();
        LinCalendar calendar = new LinCalendar(owner, calName, description, isPublic);
        calendar.save();
        owner.ownedCalendars.add(calendar);
        owner.save();

        flash.put("messageOK", "Calendari creat correctament.");
        String userN = session.get("username");
        render("Application/LogIn.html", userN);
    }


    // Encara no està accessible des de l'aplicació web
    public static void DeleteCalendar(long id, boolean check)
    {
        if (check)
        {
            LinCalendar calendar = LinCalendar.findById(id);
            if(calendar == null)
            {
                session.remove("editableEventId");
                flash.error("Error: No hem trobat l'esdeveniment!");
                Logout();
            }

            for (Subscription subscription : calendar.subscriptions) {
                subscription.user.subscriptions.remove(subscription);
            }
            calendar.delete();

            JPA.em().flush();
            JPA.em().clear();

            updateTemplateArgs();
            setCalendarItems();
            flash.put("messageOK", "Calendari esborrat.");
        }
        else
        {
            flash.put("messageOK", "El calendari no s'ha esborrat.");
        }
        render("Application/LogIn.html");
    }

    public static void DeleteCalendarForm()
    {
        render();
    }

    public static void CreateTaskForm()
    {
        render();
    }

    public static void CreateTask(@Required @MaxSize(18) String name, @MaxSize(5000) String description, @Required String date, @Required String calName)
    {
        User user = User.find("byUsername", session.get("username")).first();

        Date taskDate = dateFormatConverter(date);

        if(validation.hasErrors())
        {
            params.flash();
            //Validation.keep();
            render("Application/CreateTaskForm.html");
        }

        LinCalendar calendar;
        for (LinCalendar cal : user.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                CalTask task = new CalTask(calendar, name, description, taskDate, false);
                task.save();
                calendar.tasks.add(task);
                calendar.save();
                JPA.em().flush();
                JPA.em().clear();
                updateTemplateArgs();
                setCalendarItems();
                flash.put("messageOK", "Tasca creada correctament.");
                String userN = session.get("username");
                render("Application/LogIn.html", userN);

            }
        }
        for (Subscription subscription : user.subscriptions) {
            if (subscription.calendar.calName.equals(calName)) {
                calendar = subscription.calendar;
                CalTask task = new CalTask(calendar, name, description, taskDate, false);
                task.save();
                calendar.tasks.add(task);
                calendar.save();
                JPA.em().flush();
                JPA.em().clear();
                updateTemplateArgs();
                setCalendarItems();
                flash.put("messageOK", "Tasca creada correctament.");
                String userN = session.get("username");
                render("Application/LogIn.html", userN);
            }
        }
    }
    public static void CreateEventForm()
    {
        render();
    }

    public static void CreateEvent(@Required @MaxSize(18) String name, @MaxSize(5000) String description,
                                   @Required String startDate, @Required String endDate,
                                   String addressPhysical, String addressOnline, @Required String calName)
    {
        User user = User.find("byUsername", session.get("username")).first();
        //Obtenim les dates en el format correcte i evitem errors de dates

        if(Validation.hasErrors())
        {
            params.flash();
            //Validation.keep();
            render("Application/CreateEventForm.html");
        }

        //Obtenim les dates en el format correcte i evitem errors de dates
        try {
            Date startDateFormat = dateFormatConverter(startDate);
            Date endDateFormat = dateFormatConverter(endDate);
            if (startDateFormat.getTime() >= endDateFormat.getTime()) //Evita dates on la data d'inici i final estàn canviats o són iguals
            {
                flash.error("No pots posar la data inicial després de la data final o que l'esdeveniment comenci i acabi al mateix instant. Aquesta aplicació pel moment no contempla viatges al passat");
                params.flash();
                //Validation.keep();
                render("Application/CreateEventForm.html");
            }
            else if(startDateFormat.getTime() + 84400000 < endDateFormat.getTime())
            {
                flash.error("Els esdeveniments no poden durar més d'un dia sencer");
                params.flash();
                //Validation.keep();
                render("Application/CreateEventForm.html");

            }
        }
        catch(Exception e)
        {
            flash.error("Els formats de les dates són errònies");
            params.flash();
            //Validation.keep();
            render("Application/CreateEventForm.html");
        }

        LinCalendar calendar;
        for (LinCalendar cal : user.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                CalEvent event= new CalEvent(calendar, name, description, dateFormatConverter(startDate), dateFormatConverter(endDate), addressPhysical, addressOnline );
                event.save();
                calendar.events.add(event);
                calendar.save();
                JPA.em().flush();
                JPA.em().clear();
                updateTemplateArgs();
                setCalendarItems();

                flash.put("messageOK", "Esdeveniment creat correctament.");
                String userN = session.get("username");
                render("Application/LogIn.html", userN);
            }
        }

        for (Subscription subscription : user.subscriptions) {
            if (subscription.calendar.calName.equals(calName)) {
                calendar = subscription.calendar;
                CalEvent event= new CalEvent(calendar, name, description, dateFormatConverter(startDate), dateFormatConverter(endDate), addressPhysical, addressOnline );
                event.save();
                calendar.events.add(event);
                calendar.save();
                JPA.em().flush();
                JPA.em().clear();
                updateTemplateArgs();
                setCalendarItems();

                flash.put("messageOK", "Esdeveniment creat correctament.");
                String userN = session.get("username");
                render("Application/LogIn.html", userN);
            }
        }
    }

    // Encara no està accessible des de l'aplicació web
    public static void DeleteEvent(long id)
    {
        CalEvent event = CalEvent.find("byId", id).first();

        if(event == null)
        {
            session.remove("editableEventId");
            flash.error("Error: No hem trobat l'esdeveniment!");
            Logout();
        }
        event.delete();
        JPA.em().flush();
        JPA.em().clear();

        updateTemplateArgs();
        setCalendarItems();
        flash.put("messageOK", "Esdeveniment esborrat.");
        render("Application/LogIn.html");
    }

    // Encara no està accessible des de l'aplicació web
    public static void DeleteTask(long id)
    {
        CalTask task = CalTask.findById(id);
        if(task == null)
        {
            session.remove("editableTaskId");
            flash.error("Error: No hem trobat la tasca!!");
            Logout();
        }
        task.delete();
        JPA.em().flush();
        JPA.em().clear();
        updateTemplateArgs();
        setCalendarItems();
        flash.put("messageOK", "Tasca esborrada.");
        render("Application/LogIn.html");
    }

    public static void EditEventForm(long id)
    {
        CalEvent event = CalEvent.findById(id);
        session.put("editableEventId", event.id);
        render("Application/EditEventForm.html", event);
    }

    public static void EditTaskForm(long id)
    {
        CalTask task = CalTask.findById(id);
        session.put("editableTaskId", task.id);
        render("Application/EditTaskForm.html", task);
    }


    // Encara no està accessible des de l'aplicació web
    public static void EditEvent(@Required @MaxSize(18) String name,
                                 @Required @MaxSize(5000) String description,
                                 @Required String startDate,
                                 @Required String endDate,
                                 @Required String addressPhysical,
                                 @Required String addressOnline)
    {
        long id = Long.parseLong(session.get("editableEventId"));
        CalEvent event = CalEvent.findById(id);

        if(event == null)
        {
            session.remove("editableEventId");
            flash.error("Error: No hem trobat l'esdeveniment!");
            Logout();
        }
        if(Validation.hasErrors())
        {
            params.flash();
            render("Application/EditEventForm.html", event);
        }
       event.addressPhysical = addressPhysical;
       event.addressOnline = addressOnline;
       event.description = description;
       event.name = name;
       event.startDate = dateFormatConverter(startDate);
       event.endDate = dateFormatConverter(endDate);
       event.save();

       session.remove("editableEventId");
       flash.put("messageOK", "Esdeveniment modificat!");
       updateTemplateArgs();
       setCalendarItems();
       render("Application/LogIn.html");
    }



    // Encara no està accessible des de l'aplicació web
    public static void EditTask(@Required @MaxSize(18) String name,
                                @MaxSize(5000) String description,
                                @Required String date)
    {
        //renderText(dateFormatConverter(date));
        long id = Long.parseLong(session.get("editableTaskId"));
        CalTask task = CalTask.findById(id);

        if(task == null)
        {
            session.remove("editableTaskId");
            flash.error("Error: No hem trobat la tasca!");
            Logout();
        }
        if(Validation.hasErrors())
        {
            params.flash();
            //Validation.keep();
            render("Application/EditTaskForm.html", task);
        }

        task.name = name;
        task.description = description;
        task.date = dateFormatConverter(date);
        task.save();

        session.remove("editableTaskId");
        flash.put("messageOK", "Tasca modificada!");
        updateTemplateArgs();
        setCalendarItems();
        render("Application/LogIn.html");
    }

    // Encara no està accessible des de l'aplicació web
    public static void markTaskDone(long id) {
        {
            CalTask task = CalTask.findById(id);

            if (task == null) {
                session.remove("editableTaskId");
                flash.error("Error: No hem trobat la tasca!");
                Logout();
            }

            task.completed = true;
            task.save();
            updateTemplateArgs();
            setCalendarItems();
            render("Application/LogIn.html");
        }
    }

    // Encara no està accessible des de l'aplicació web
    public static void markTaskPending(long id) {
        {
            CalTask task = CalTask.findById(id);

            if (task == null) {
                session.remove("editableTaskId");
                flash.error("Error: No hem trobat la tasca!");
                Logout();
            }

            task.completed = false;
            task.save();
            updateTemplateArgs();
            setCalendarItems();
            render("Application/LogIn.html");
        }
    }


    public static void AdminCalendarForm()
    {
        render();
    }

    public static void EditCalendar(@Required long id,
                                    @Required String calName,
                                    @Required String description,
                                    boolean goPublic,
                                    boolean goPrivate)
    {
        LinCalendar cal = LinCalendar.findById(id);
        if(cal == null)
        {
            flash.error("Error: No hem trobat el calendari!");
            Logout();
        }
        if(Validation.hasErrors())
        {
            params.flash();
            //Validation.keep();
            render("Application/AdminCalendarForm.html");
        }

        cal.calName = calName;
        cal.description = description;

        if (goPublic)
        {
            cal.isPublic = true;
        }
        else if (goPrivate)
        {
            cal.isPublic = false;
        }

        cal.save();
        flash.put("messageOK", "Calendari modificat!");
        updateTemplateArgs();
        setCalendarItems();
        AdminCalendarForm();
    }

    public static void MakeEditor(long id)
    {
        Subscription sub = Subscription.findById(id);
        sub.isEditor = true;
        sub.save();
        render("Application/AdminCalendarForm.html");
    }

    public static void RevokeEditor(long id)
    {
        Subscription sub = Subscription.findById(id);
        sub.isEditor = false;
        sub.save();
        render("Application/AdminCalendarForm.html");
    }

    /*
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
        LinCalendar calendari1 = new LinCalendar(usuari1, "UPC","Descripcio de prova", false);
        calendari1.save();
        usuari1.ownedCalendars.add(calendari1);
        usuari1.save();
        // -------------------------------------------


        // Accio afegir subscriptor
        // -------------------------------------------
        User user = User.find("byUserName", "Ixion").first();
        Subscription subscription = new Subscription(user, calendari1, true);
        subscription.save();
        user.subscriptions.add(subscription);
        user.save();
        calendari1.subscriptions.add(subscription);
        calendari1.save();
        // -------------------------------------------

        // Accio Crear tasques i esdeveniments
        CalTask tasca1 = new CalTask(calendari1, "Mart 2024", "Elon Musk envia l'home al planteta roig", new Date(2024,12,31), false);
        tasca1.save();
        calendari1.tasks.add(tasca1);
        calendari1.save();

        CalEvent esdeveniment1 = new CalEvent(calendari1, "PES", "Classe setmanal de PES",
                new Date(2020,10,13), new Date(2020,10,13), "A casa", "www.google.com");
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
   */
}