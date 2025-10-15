package ois.cc.gravity.objects;

import ois.radius.cc.entities.tenant.cc.Survey;

import java.util.ArrayList;

public class OSurveyForm extends AObject{

    private String Code;

    private Survey Survey;

    private Boolean IsPublished;

    private ArrayList<OSurveyAttribute> SurveyAttribute;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public Survey getSurvey() {
        return Survey;
    }

    public void setSurvey(Survey survey) {
        Survey = survey;
    }

    public Boolean getPublished() {
        return IsPublished;
    }

    public void setPublished(Boolean published) {
        IsPublished = published;
    }

    public ArrayList<OSurveyAttribute> getSurveyAttribute() {
        return SurveyAttribute;
    }

    public void setSurveyAttribute(ArrayList<OSurveyAttribute> surveyAttribute) {
        SurveyAttribute = surveyAttribute;
    }
}
