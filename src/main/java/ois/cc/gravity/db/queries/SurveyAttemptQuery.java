package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyAttemptQuery extends EntityQuery
{

    public SurveyAttemptQuery()
    {
        super(EN.SurveyAttempt);
    }

    public SurveyAttemptQuery filterByUSurId(String id)
    {
        AppendWhere("And SurveyAttempt.USUID=:id");
        _params.put("id", id);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {

        for (String name : filters.keySet())
        {
            String fltrKey = name.toLowerCase();

            switch (fltrKey)
            {
                case "byusuid":
                    filterByUSurId(filters.get(name).get(0));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException
    {

    }
}
