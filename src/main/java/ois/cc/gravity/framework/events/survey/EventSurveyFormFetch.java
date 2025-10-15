package ois.cc.gravity.framework.events.survey;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OSurveyForm;

import java.util.ArrayList;

public class EventSurveyFormFetch extends EventOK
{

    private ArrayList<OSurveyForm> SurveyForm;

    public EventSurveyFormFetch(Request request)
    {
        super(request, EventCode.SurveyFormFetch);
    }

    public ArrayList<OSurveyForm> getSurveyForm()
    {
        return SurveyForm;
    }

    public void setSurveyForm(ArrayList<OSurveyForm> surveyForm)
    {
        SurveyForm = surveyForm;
    }
}
