package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;
import org.vn.radius.cc.platform.xspi.ProviderID;

import java.util.ArrayList;
import java.util.HashMap;

public class XSPIConnectQuery extends EntityQuery
{
    public XSPIConnectQuery()
    {
        super(EN.XSPIConnect);
    }

    public XSPIConnectQuery filterByTenantCode(String name)
    {
        AppendWhere("And XSPIConnect.TenantCode =:name");
        _params.put("name", name);
        return this;
    }
    public XSPIConnectQuery filterByXServerCode(String name)
    {
        AppendWhere("And XSPIConnect.XServerCode=:code");
        _params.put("code", name);
        return this;
    }
    public XSPIConnectQuery filterByProviderId(String name)
    {
        AppendWhere("And XSPIConnect.ProviderId=:name");
        _params.put("name", ProviderID.valueOf(name));
        return this;
    }
    //XSPIConnId
    public XSPIConnectQuery filterByXSPIConnId(String xspicid)
    {
        AppendWhere("And XSPIConnect.XSPIConnId=:xspicid");
        _params.put("xspicid", xspicid);
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
                case "bytenantcode":
                    filterByTenantCode(filters.get(name).get(0));
                    break;
                case "byxservercode":
                    filterByXServerCode(filters.get(name).get(0));
                    break;
                case "byproviderid":
                    filterByProviderId(filters.get(name).get(0));
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

    private XSPIConnectQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private XSPIConnectQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }
}
