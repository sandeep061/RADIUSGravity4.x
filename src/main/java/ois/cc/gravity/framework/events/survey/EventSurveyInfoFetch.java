package ois.cc.gravity.framework.events.survey;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OSurveyInfo;

public class EventSurveyInfoFetch extends EventOK
{

    private OSurveyInfo SurveyInfo;

    public EventSurveyInfoFetch(Request request)
    {
        super(request, EventCode.SurveyInfoFetch);
    }

    public OSurveyInfo getSurveyInfo()
    {
        return SurveyInfo;
    }

    public void setSurveyInfo(OSurveyInfo surveyInfo)
    {
        SurveyInfo = surveyInfo;
    }
}
