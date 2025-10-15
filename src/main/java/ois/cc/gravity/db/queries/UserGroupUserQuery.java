package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class UserGroupUserQuery extends EntityQuery
{
    public UserGroupUserQuery()
    {
        super(EN.UserGroupUser);
    }

    public UserGroupUserQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(UserGroupAOPs.Name) Like : name ");
        _params.put("name", "%" + name + "%");

        return this;
    }

    public UserGroupUserQuery filterByLoginIdLike(String name)
    {
        AppendWhere("And Lower(UserGroupAOPs.AOPs.LoginId) Like : loginid ");
        _params.put("loginid", "%" + name + "%");

        return this;
    }

    public UserGroupUserQuery filterByUserId(String uid)
    {
        AppendWhere("And UserGroupUser.User.UserId =: campid");
        _params.put("campid", uid);

        return this;
    }
    public UserGroupUserQuery filterByUserGroup(Long ugid)
    {
        AppendWhere("And UserGroupUser.UserGroup.Id =: Usergrp");
        _params.put("Usergrp", ugid);

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
                case "byuser":
                    filterByUserId(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byloginidlike":
                    filterByLoginIdLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byusergroup":
                    filterByUserGroup(Long.valueOf(filters.get(name).get(0)));
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
