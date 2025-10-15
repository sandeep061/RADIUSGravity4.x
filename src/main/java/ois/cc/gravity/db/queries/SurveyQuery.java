package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyQuery extends EntityQuery
{

    public SurveyQuery()
    {
        super(EN.Survey);
    }

    public SurveyQuery filterBySurveyForm(Long id)
    {
        AppendWhere("And Survey.SurveyForm.Id=:id");
        _params.put("id", id);

        return this;
    }

    public SurveyQuery filterByCode(String code)
    {
        AppendWhere("And Survey.Code=:code");
        _params.put("code", code);

        return this;
    }

    public SurveyQuery filterByName(String name)
    {
        AppendWhere("And Survey.Name=:name");
        _params.put("name", name);

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
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case"byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bysurveyform":
                    filterBySurveyForm(Long.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

     private SurveyQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private SurveyQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException {

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
                    case "name":
                        orderByName(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }
}
