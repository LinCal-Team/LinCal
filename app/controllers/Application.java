package controllers;


import play.*;
import play.mvc.*;
import java.util.*;
import models.*;
import play.data.validation.*;
import play.mvc.results.RenderTemplate;

public class Application extends Controller {

    @Before
    static void updateTemplateArgs ()
    {
       User connectedUser = connectedUser();
       if (connectedUser != null)
       {
           renderArgs.put("user", connectedUser);
           List<CalEvent> events = new ArrayList<>();

           for (LinCalendar cal : connectedUser.ownedCalendars)
           {
               for (CalEvent event : cal.events)
               {
                   events.add(event);
               }
           }
           for (Subscription s : connectedUser.subscriptions)
           {
               LinCalendar cal = s.calendar;
               for (CalEvent event : cal.events)
               {
                   events.add(event);
               }
           }
           renderArgs.put("events", events);


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
            connectedUser = renderArgs.get("user", User.class);
            if (connectedUser == null)
            {
                connectedUser = User.find("byUsername", username).first();
            }
            return connectedUser;
        }
    }

    @Before //(only= {"index","LogIn","SingUp"})
    public static void setCalendarItems()
    {
        Date today = new Date();
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
                {"Dilluns",
                        "Dimarts",
                        "Dimecres",
                        "Dijous",
                        "Divendres",
                        "Dissabte",
                        "Diumenge"
                };

        String Monday;
        if((today.getDate()-today.getDay()+1)>0)
            Monday = (today.getDate()-today.getDay()+1) + " " + month[today.getMonth()];
        else
            Monday = (new Date(today.getYear(), today.getMonth() - 1,0).getDate()
                    + today.getDate()-today.getDay()+1) + " " + month[today.getMonth()];

        String Sunday;
        if((today.getDate()+7-today.getDay())>new Date(today.getYear(), today.getMonth(),0).getDate())
            Sunday = (today.getDate()+7-today.getDay() - new Date(today.getYear(), today.getMonth(),0).getDate())
                    + " " + month[today.getMonth()+1];
        else
            Sunday = (today.getDate()+7-today.getDay()) + " " + month[today.getMonth()];

        renderArgs.put("Month", Monday + " - " + Sunday);
        renderArgs.put("Today", today.toLocaleString());
        renderArgs.put("Week", week[today.getDay()-1]);

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
            flash.error("Aqest usuari ja existeix!");
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
                render(userN);
                // TODO: Afegir vista
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

    public static void CreateCalendarForm()
    {
        String username = session.get("username");
        render();
    }

    public static void CreateCalendar(String calName, String description)
    {
        // TODO: comprovar que l'usuari existeix
        User owner = User.find("byUsername", session.get("username")).first();
        LinCalendar calendar = new LinCalendar(owner, calName, description,false);
        calendar.save();
        owner.ownedCalendars.add(calendar);
        owner.save();

        flash.put("messageOK", "Calendari creat correctament.");
        String userN = session.get("username");
        render("Application/LogIn.html", userN);
    }


    // Encara no està accessible des de l'aplicació web
    public static void DeleteCalendar(@Required String calName)
    {
        /*if (validation.hasErrors())
        {
            flash.error("Falten camps per omplir.");
            render("Application/DeleteUserForm.html", username);
        }*/
        String username = session.get("username");
        User owner = User.find("byUsername", username).first();
        LinCalendar calendar;

        // TODO: no iterar quan ja trobem el calendari
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                for (Subscription subscription : calendar.subscriptions) {
                    subscription.user.subscriptions.remove(subscription);
                }
                calendar.delete();
            }
        }
    }

    public static void CreateTaskForm()
    {
        render();
    }

    public static void CreateTask(String taskName, String description, String taskDate, String calName)
    {
        User owner = User.find("byUsername", session.get("username")).first();

        LinCalendar calendar;
        // TODO: no iterar quan ja trobem el calendari
        // TODO: si no es trboa el calendari, misstage d'error
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                CalTask task = new CalTask(calendar, taskName, description, taskDate, false);
                task.save();
                calendar.tasks.add(task);
                calendar.save();
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

    public static void CreateEvent(String name, String description, String startDate, String endDate, String calName, String addressOnline, String addressPhysical)
    {
        User owner = User.find("byUsername", session.get("username")).first();

        LinCalendar calendar;
        // TODO: no iterar quan ja trobem el calendari
        // TODO: si no es trboa el calendari, misstage d'error
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                CalEvent event= new CalEvent(calendar, name, description, startDate, endDate, addressPhysical, addressOnline );
                event.save();
                calendar.events.add(event);
                calendar.save();
                updateTemplateArgs();

                flash.put("messageOK", "Esdeveniment creat correctament.");
                String userN = session.get("username");
                render("Application/LogIn.html", userN);
            }
        }
    }

    // Encara no està accessible des de l'aplicació web
    public static void DeleteEvent(String eventName, String calName)
    {
        String username = session.get("username");
        User owner = User.find("byUsername", username).first();
        LinCalendar calendar;

        // TODO: no iterar quan ja trobem el calendari
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                for (CalEvent event : cal.events) {
                    if (event.name.equals((eventName))) {
                        event.delete();
                    }
                }
            }
        }
    }

    // Encara no està accessible des de l'aplicació web
    public static void DeleteTask(String taskName, String calName)
    {
        String username = session.get("username");
        User owner = User.find("byUsername", username).first();
        LinCalendar calendar;

        // TODO: no iterar quan ja trobem el calendari
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                for (CalTask task : cal.tasks) {
                    if (task.name.equals((taskName))) {
                        task.delete();
                    }
                }
            }
        }
    }

    // Encara no està accessible des de l'aplicació web
    public static void EditEvent(String calName, String oldEventName, String newEventName, String description, String startDate,
                                 String endDate, String addressOnline, String addressPhysical)
    {
        String username = session.get("username");
        User owner = User.find("byUsername", username).first();
        LinCalendar calendar;

        // TODO: no iterar quan ja trobem el calendari
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                for (CalEvent event : cal.events) {
                    if (event.name.equals((oldEventName))) {
                        event.name = newEventName;
                        event.addressOnline = addressOnline;
                        event.addressPhysical = addressPhysical;
                        event.description = description;
                        event.startDate = startDate;
                        event.endDate = endDate;
                        event.save();
                    }
                }
            }
        }
    }

    // Encara no està accessible des de l'aplicació web
    public static void EditTask(String calName, String oldTaskName, String newTaskName, String description, String date)
    {
        String username = session.get("username");
        User owner = User.find("byUsername", username).first();
        LinCalendar calendar;

        // TODO: no iterar quan ja trobem el calendari
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                for (CalTask task : cal.tasks) {
                    if (task.name.equals((oldTaskName))) {
                        task.name = newTaskName;
                        task.description = description;
                        task.date = date;
                        task.save();
                    }
                }
            }
        }
    }

    // Encara no està accessible des de l'aplicació web
    public static void markTaskDone(String calName, String taskName)
    {
        String username = session.get("username");
        User owner = User.find("byUsername", username).first();
        LinCalendar calendar;

        // TODO: no iterar quan ja trobem el calendari
        for (LinCalendar cal : owner.ownedCalendars) {
            if (cal.calName.equals(calName)) {
                calendar = cal;
                for (CalTask task : cal.tasks) {
                    if (task.name.equals((taskName))) {
                        task.completed = true;
                        task.save();
                    }
                }
            }
        }
    }


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