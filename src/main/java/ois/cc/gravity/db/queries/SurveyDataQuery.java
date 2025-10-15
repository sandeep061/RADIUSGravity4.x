package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyDataQuery extends EntityQuery{
    public SurveyDataQuery() {
        super(EN.SurveyData);
    }

    public SurveyDataQuery filterBySurveyFormCode(String code)
    {
        AppendWhere("And SurveyData.SurveyForm.Code=:code");
        _params.put("code", code);
        return this;
    }

    public SurveyDataQuery filterBySurveyUSUID(String id)
    {
        AppendWhere("And SurveyData.USUID=:id");
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
                case "bysurveycode":
                    filterBySurveyFormCode(filters.get(name).get(0));
                    break;
                case "byusuid":
                    filterBySurveyUSUID(filters.get(name).get(0));
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
