package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyAttributeQuery extends EntityQuery{

    public SurveyAttributeQuery() {
        super(EN.SurveyAttribute);
    }

    public SurveyAttributeQuery filterByCode(String code)
    {
        AppendWhere("And SurveyAttribute.Code=:code");
        _params.put("code", code);

        return this;
    }

    public SurveyAttributeQuery filterBySurveyForm(Long id)
    {
        AppendWhere("And SurveyAttribute.SurveyForm.Id=:id");
        _params.put("id", id);

        return this;
    }
//    
    public SurveyAttributeQuery filterBySurveyFormCode(String code)
    {
        AppendWhere("And SurveyAttribute.SurveyForm.Code=:code");
        _params.put("code", code);

        return this;
    }
    
    public SurveyAttributeQuery filterBySurvey(Long id)
    {
        AppendWhere("And SurveyAttribute.Survey.Id=:id");
        _params.put("id", id);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet())
        {
            String fltrKey = name.toLowerCase();

            switch (fltrKey)
            {
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "bysurveyform":
                    filterBySurveyForm(Long.valueOf(filters.get(name).get(0)));
                    break;
                    case "bysurvey":
                    filterBySurvey(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bysurveycode":
                    filterBySurveyFormCode(filters.get(name).get(0));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }


    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException {

    }
}
