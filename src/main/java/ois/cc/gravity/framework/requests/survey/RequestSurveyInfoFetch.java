package ois.cc.gravity.framework.requests.survey;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestSurveyInfoFetch extends Request
{

    private String USUID;

    public RequestSurveyInfoFetch(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.SurveyInfoFetch);
    }

    public String getUSUID()
    {
        return USUID;
    }

    public void setUSUID(String USUID)
    {
        this.USUID = USUID;
    }

}
