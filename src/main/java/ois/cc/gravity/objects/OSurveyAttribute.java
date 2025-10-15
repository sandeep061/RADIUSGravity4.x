package ois.cc.gravity.objects;

import CrsCde.CODE.Common.Enums.DataType;
import jakarta.persistence.*;
import ois.radius.cc.entities.tenant.cc.Survey;
import ois.radius.cc.entities.tenant.cc.SurveyForm;

public class OSurveyAttribute extends AObject{

    private String Code;

    private DataType DataType;

    private String Validation;

    private SurveyForm SurveyForm;

    private Survey Survey;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public DataType getDataType() {
        return DataType;
    }

    public void setDataType(DataType dataType) {
        DataType = dataType;
    }

    public String getValidation() {
        return Validation;
    }

    public void setValidation(String validation) {
        Validation = validation;
    }

    public SurveyForm getSurveyForm() {
        return SurveyForm;
    }

    public void setSurveyForm(SurveyForm surveyForm) {
        SurveyForm = surveyForm;
    }

    public Survey getSurvey() {
        return Survey;
    }

    public void setSurvey(Survey survey) {
        Survey = survey;
    }
}
