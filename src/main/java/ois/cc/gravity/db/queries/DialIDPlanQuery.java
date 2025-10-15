/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import java.util.ArrayList;
import java.util.HashMap;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;

public class DialIDPlanQuery extends EntityQuery
{

    public DialIDPlanQuery()
    {
        super(EN.DialIDPlan);
    }

    public DialIDPlanQuery filterByAops(Long campid)
    {
        AppendWhere("And DialIDPlan.AOPs.Id =: campid");
        _params.put("campid", campid);

        return this;
    }
    public DialIDPlanQuery filterByAopsCode(String code)
    {
        AppendWhere("And DialIDPlan.AOPs.Code =: code");
        _params.put("code", code);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byaops":
                    filterByAops(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAopsCode(filters.get(name).get(0));
                    break;
                case "byid":
                        filterById(Long.valueOf(filters.get(name).get(0)));
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
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
