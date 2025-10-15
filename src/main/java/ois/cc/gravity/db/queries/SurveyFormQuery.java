package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyFormQuery extends EntityQuery
{

    public SurveyFormQuery()
    {
        super(EN.SurveyForm);
    }

    public SurveyFormQuery filterByCode(String code)
    {
        AppendWhere("And SurveyForm.Code=:code");
        _params.put("code", code);

        return this;
    }

    public SurveyFormQuery filterBySurvey(Long id)
    {
        AppendWhere("And SurveyForm.Survey.Id=:id");
        _params.put("id", id);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws CODEException, GravityIllegalArgumentException
    {

        try
        {
            for (String name : filters.keySet())
            {
                String fltrKey = name.toLowerCase();

                switch (fltrKey)
                {
                    case "bycode":
                        filterByCode(filters.get(name).get(0));
                        break;
                    case "bysurvey":
                        filterBySurvey(Long.valueOf(filters.get(name).get(0)));
                        break;
                    case "byid":
                        filterById(Long.valueOf(filters.get(name).get(0)));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException
    {

        for (HashMap<String, Boolean> hm : orderby)
        {
            for (String name : hm.keySet())
            {
                switch (name.toLowerCase())
                {
                    case "id":
                        orderById(hm.get(name));
                        break;
                    case "code":
                        orderByCode(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private SurveyFormQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }
}
