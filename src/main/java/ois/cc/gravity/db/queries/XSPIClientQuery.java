package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class XSPIClientQuery extends EntityQuery
{
    public XSPIClientQuery()
    {
        super(EN.XSPIClient);
    }

    public XSPIClientQuery filterByTenantCode(String name)
    {
        AppendWhere("And XSPIClient.TenantCode =:name");
        _params.put("name", name);
        return this;
    }
    public XSPIClientQuery filterByXServerCode(String name)
    {
        AppendWhere("And XSPIClient.XServerCode=:code");
        _params.put("code", name);
        return this;
    }
//    public XSPIClientQuery filterByProviderId(String name)
//    {
//        AppendWhere("And XSPIConnection.ProviderId=:name");
//        _params.put("name", ProviderID.valueOf(name));
//        return this;
//    }
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
                case "bytenantcode":
                    filterByTenantCode(filters.get(name).get(0));
                    break;
                case "byxservercode":
                    filterByXServerCode(filters.get(name).get(0));
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
//                    case "code":
//                        orderByCode(hm.get(name));
//                        break;
//                    case "name":
//                        orderByName(hm.get(name));
//                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private XSPIClientQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private XSPIClientQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }
}
