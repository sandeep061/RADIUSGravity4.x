package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class XSessionStatusRedialQuery extends EntityQuery
{

    public XSessionStatusRedialQuery()
    {
        super(EN.XSessionStatusRedial);
    }

    public XSessionStatusRedialQuery filterBycategory(String category)
    {
        AppendWhere("And XSessionStatusRedial.Category=:category");
        _params.put("category", category);

        return this;
    }
    public XSessionStatusRedialQuery filterByAOPs(Long id)
    {
        AppendWhere("And XSessionStatusRedial.AOPs.Id =: id");
        _params.put("id", id);
        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byCategory":
                    filterBycategory(name);
                    break;
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);

            }

        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityIllegalArgumentException
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
                    case "category":
                        orderByCode(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private XSessionStatusRedialQuery orderByCode(Boolean get)
    {
        setOrederBy("Category", get);
        return this;
    }

}
