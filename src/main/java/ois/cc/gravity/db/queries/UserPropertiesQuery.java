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
import ois.radius.cc.entities.tenant.cc.UserProperties;


/**
 *
 * @author Manoj
 * @since 20 May, 2021
 */
public class UserPropertiesQuery extends EntityQuery
{

    public UserPropertiesQuery()
    {
        super(EN.UserProperties);
    }

    public UserPropertiesQuery filterByUserId(String agid)
    {
        AppendWhere("And UserProperties.User.UserId =: agid");
        _params.put("agid", agid);

        return this;
    }
    public UserPropertiesQuery filterByConfKey(UserProperties.Keys key)
    {
        AppendWhere("And UserProperties.ConfKey =: key");
        _params.put("key", key.name());

        return this;
    }

    public UserPropertiesQuery filterByUser(Long agid)
    {
        AppendWhere("And UserProperties.User.Id =: agid");
        _params.put("agid", agid);

        return this;
    }
    private UserPropertiesQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
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
                case "byuser":
                    filterByUserId(filters.get(name).get(0));
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
