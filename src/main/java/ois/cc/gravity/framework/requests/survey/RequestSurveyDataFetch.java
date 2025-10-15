package ois.cc.gravity.framework.requests.survey;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestSurveyDataFetch extends Request
{

    @Param(Optional = false)
    private String USUID;

    public RequestSurveyDataFetch(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.SurveyDataFetch);
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
