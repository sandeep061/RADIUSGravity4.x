package ois.cc.gravity.framework.events.survey;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.radius.cc.entities.tenant.cc.SurveyData;

public class EventSurveyDataFetch extends EventOK
{

    private SurveyData SurveyData;

    public EventSurveyDataFetch(Request request)
    {
        super(request, EventCode.SurveyDataFetch);
    }

    public SurveyData getSurveyData()
    {
        return SurveyData;
    }

    public void setSurveyData(SurveyData surveyData)
    {
        SurveyData = surveyData;
    }
}
