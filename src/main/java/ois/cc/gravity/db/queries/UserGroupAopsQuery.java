package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;
import java.util.ArrayList;
import java.util.HashMap;

public class UserGroupAopsQuery extends EntityQuery
{
    public UserGroupAopsQuery()
    {
        super(EN.UserGroupAops);
    }

    public UserGroupAopsQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(UserGroupAops.Name) Like : name ");
        _params.put("name", "%" + name + "%");

        return this;
    }

    public UserGroupAopsQuery filterByLoginIdLike(String name)
    {
        AppendWhere("And Lower(UserGroupAops.AOPs.LoginId) Like : loginid ");
        _params.put("loginid", "%" + name + "%");

        return this;
    }

    public UserGroupAopsQuery filterByAops(Long aopid)
    {
        AppendWhere("And UserGroupAops.AOPs.Id =: aopid");
        _params.put("aopid", aopid);

        return this;
    }

    public UserGroupAopsQuery filterByAopsCode(String code)
    {
        AppendWhere("And UserGroupAops.AOPs.Code =: code");
        _params.put("code", code);

        return this;
    }

    public UserGroupAopsQuery filterByUserGroup(Long ugid)
    {
        AppendWhere("And UserGroupAops.UserGroup.Id =: ugid");
        _params.put("ugid", ugid);

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
                case "byaops":
                    filterByAops(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAopsCode(filters.get(name).get(0));
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
