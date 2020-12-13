package controllers;


//import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
//import jdk.internal.event.Event;
import org.hibernate.Hibernate;
import play.mvc.*;
import java.util.*;
import models.*;
import play.data.validation.*;


public class AndroidController extends Controller
{
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
}
