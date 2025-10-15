package ois.cc.gravity.framework.requests.survey;

import code.ua.requests.Param;
import code.ua.requests.Request;
import java.util.HashMap;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestSurveyDataConfig extends Request
{

    @Param(Optional = false)
    private String FormCode;

    @Param(Optional = false)
    private String USUID;

    private HashMap<String, String> SurveyData;

    private Integer Score;

    public RequestSurveyDataConfig(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.SurveyDataConfig);
    }

    public HashMap<String, String> getSurveyData()
    {
        return SurveyData;
    }

    public String getUSUID()
    {
        return USUID;
    }

    public void setUSUID(String USUID)
    {
        this.USUID = USUID;
    }

    public void setSurveyData(HashMap<String, String> SurveyData)
    {
        this.SurveyData = SurveyData;
    }

    public Integer getScore()
    {
        return Score;
    }

    public void setScore(Integer score)
    {
        Score = score;
    }

    public String getFormCode()
    {
        return FormCode;
    }

    public void setFormCode(String formCode)
    {
        FormCode = formCode;
    }
}
