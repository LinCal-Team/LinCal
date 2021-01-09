package controllers;

import com.google.gson.annotations.Expose;
import models.LinCalendar;

import java.util.ArrayList;
import java.util.List;

public class AndroidSentCalendars {

    @Expose(serialize = true)
    List<LinCalendar> ownedCalendars = new ArrayList<>();

    @Expose(serialize = true)
    List<LinCalendar> editableCalendars = new ArrayList<>();

    @Expose(serialize = true)
    List<LinCalendar> nonEditableCalendars = new ArrayList<>();

    public AndroidSentCalendars(List<LinCalendar> ownedCalendars, List<LinCalendar> editableCalendars, List<LinCalendar> nonEditableCalendars)
    {
        this.ownedCalendars = ownedCalendars;
        this.editableCalendars = editableCalendars;
        this.nonEditableCalendars = nonEditableCalendars;
    }


}
