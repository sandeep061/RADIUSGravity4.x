package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.cc.entities.EN;


import java.util.ArrayList;
import java.util.HashMap;

public class XPlatformQuery extends EntityQuery
{
    public XPlatformQuery()
    {
        super(EN.XPlatform);
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
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byxplatformid":
                    filterByPlatformID(XPlatformID.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }

    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException
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

    private XPlatformQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private XPlatformQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

    public XPlatformQuery filterByCode(String code)
    {
        AppendWhere("And XPlatform.Code =: code");
        _params.put("code", code);
        return this;
    }
    public XPlatformQuery filterByName(String name)
    {
        AppendWhere("And XPlatform.Name =: name");
        _params.put("name", name);
        return this;
    }
    public XPlatformQuery filterByPlatformID(XPlatformID PlatformID)
    {
        AppendWhere("And XPlatform.PlatformID =: PlatformID");
        _params.put("PlatformID", PlatformID);
        return this;
    }
    public XPlatformQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(XPlatform.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

}
