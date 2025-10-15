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


/**
 *
 * @author rumana.begum
 * @since 8 Nov, 2023
 */
public class UserProfileQuery extends EntityQuery
{

    public UserProfileQuery()
    {
        super(EN.UserProfile);
    }
    public UserProfileQuery filterByUser(String id)
    {
        AppendWhere("And UserProfile.User.UserId=:id");
        _params.put("id", id);

        return this;
    }
    public UserProfileQuery filterByProfile(Long profileid)
    {
        AppendWhere("And UserProfile.Profile.Id =: id");
        _params.put("id", profileid);

        return this;
    }

    @Override
	public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byprofile":
                    filterByProfile(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byuser":
                    filterByUser(filters.get(name).get(0));
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
