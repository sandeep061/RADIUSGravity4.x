/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import java.util.ArrayList;
import java.util.HashMap;



import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;


/**
 *
 * @author Manoj
 * @since 6 Aug, 2019
 */
public class AOPsScheduleQuery extends EntityQuery
{

    public AOPsScheduleQuery()
    {
        super(EN.AOPsSchedule);
    }

    public AOPsScheduleQuery filterByAops(Long campid)
    {
        AppendWhere("And AOPsSchedule.AOPs.Id=:campid");
        _params.put("campid", campid);

        return this;
    }
    public AOPsScheduleQuery filterByAopsCode(String code)
    {
        AppendWhere("And AOPsSchedule.AOPs.Code=:code");
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
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaops":
                    filterByAops(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAopsCode(filters.get(name).get(0));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }

    }

    private AOPsScheduleQuery orderByCampaign(Boolean isasc)
    {
        setOrederBy("AOPs.Id", isasc);
        return this;
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityIllegalArgumentException
    {
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
                    case "aops":
                        orderByCampaign(isAsc);
                        break;
                    default:
                        throw new  GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
