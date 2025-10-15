package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import java.util.ArrayList;
import java.util.HashMap;

public class EntityQueryImpl extends EntityQuery
{

    public EntityQueryImpl(EN en)
    {
        super(en);
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;

                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityIllegalArgumentException {
        for (HashMap<String, Boolean> hm : orderby)
        {
            for (String name : hm.keySet())
            {
                Boolean isAsc = hm.get(name);
                switch (name.toLowerCase())
                {
                    case "id":
                        orderById(isAsc);
                        break;
                    case "entitystate":
                        orderByEntityState(isAsc);
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
