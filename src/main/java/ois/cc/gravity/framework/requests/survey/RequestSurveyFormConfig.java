/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.framework.requests.survey;

import code.ua.requests.Param;
import code.ua.requests.Request;
import java.util.ArrayList;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.cc.entities.tenant.cc.SurveyAttribute;

/**
 *
 * @author Sandeepkumar.Sahoo
 * @since Aug 12, 2025
 */
public class RequestSurveyFormConfig extends Request
{

    @Param(Optional = false)
    private Long Survey;

    @Param(Optional = true)
    private Boolean IsPublished;

    @Param(Optional = false)
    private String FormCode;

    @Param(Optional = false)
    private ArrayList<SurveyAttribute> Attributes;

    public RequestSurveyFormConfig(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.SurveyFormConfig);
    }

    public Long getSurvey()
    {
        return Survey;
    }

    public void setSurvey(Long Survey)
    {
        this.Survey = Survey;
    }

    public Boolean getIsPublished()
    {
        return IsPublished;
    }

    public void setIsPublished(Boolean IsPublished)
    {
        this.IsPublished = IsPublished;
    }

    public ArrayList<SurveyAttribute> getAttributes()
    {
        return Attributes;
    }

    public void setAttributes(ArrayList<SurveyAttribute> Attributes)
    {
        this.Attributes = Attributes;
    }

    public Boolean getPublished()
    {
        return IsPublished;
    }

    public void setPublished(Boolean published)
    {
        IsPublished = published;
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
