package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyAlertQuery extends EntityQuery{
    public SurveyAlertQuery() {
        super(EN.SurveyAlert);
    }

    public SurveyAlertQuery filterByUSUID(String id)
    {
        AppendWhere("And SurveyAlert.USUID=:usuid");
        _params.put("usuid", id);

        return this;
    }

    public SurveyAlertQuery filterByUAltID(String id)
    {
        AppendWhere("And SurveyAlert.UAltID=:ultid");
        _params.put("ultid", id);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet()) {
            switch (name.toLowerCase()) {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byusuid":
                    filterByUSUID(filters.get(name).get(0));
                    break;
                case "byualtid":
                    filterByUAltID(filters.get(name).get(0));
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
