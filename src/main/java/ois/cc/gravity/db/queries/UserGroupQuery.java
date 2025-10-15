/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserGroupType;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author Suman
 * @since 16 Feb, 2018
 */
public class UserGroupQuery extends EntityQuery
{

    public UserGroupQuery()
    {
        super(EN.UserGroup);
    }

    public UserGroupQuery filterByAgent(String id)
    {
        AppendWhere("And element(UserGroup.Users).UserId =: id");
        _params.put("id", id);

        return this;
    }
    public UserGroupQuery filterByUserGroupType(String type)
    {
        AppendWhere("And UserGroup.UserGroupType =: type");
        _params.put("type", UserGroupType.valueOf(type));

        return this;
    }

    public UserGroupQuery filterByCode(String code)
    {
        AppendWhere("And UserGroup.Code=:code");
        _params.put("code", code);

        return this;
    }

    public UserGroupQuery filterByName(String name)
    {
        AppendWhere("And UserGroup.Name=:code");
        _params.put("code", name);

        return this;
    }

    public UserGroupQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(UserGroup.Name) Like : name ");
        _params.put("name", "%" + name + "%");

        return this;
    }

    public UserGroupQuery filterByLoginIdLike(String name)
    {
        AppendWhere("And Lower(UserGroup.Users.LoginId) Like : loginid ");
        _params.put("loginid", "%" + name + "%");

        return this;
    }

    public UserGroupQuery filterByCampaign(Long campid)
    {
        AppendWhere("And element(UserGroup.AOPs).Id =: campid");
        _params.put("campid", campid);

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
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byloginidlike":
                    filterByLoginIdLike(filters.get(name).get(0).toLowerCase());
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

    private UserGroupQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private UserGroupQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }
}
