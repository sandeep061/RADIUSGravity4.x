package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;


import java.util.ArrayList;
import java.util.HashMap;

public class AOPsCDNQuery extends EntityQuery
{
    public AOPsCDNQuery()
    {
        super(EN.AOPsCDN);
    }

    public AOPsCDNQuery filterByCode(String code)
    {
        AppendWhere("And AOPsCDN.Code =: code");
        _params.put("code", code);
        return this;
    }
    public AOPsCDNQuery filterByAddress(String address)
    {
        AppendWhere("And AOPsCDN.Address =: address");
        _params.put("address", address);
        return this;
    }
    public AOPsCDNQuery filterByChannel(Channel channel)
    {
        AppendWhere("And AOPsCDN.Channel =: channel");
        _params.put("channel", channel);
        return this;
    }
    public AOPsCDNQuery filterByAOPs(Long id)
    {
        AppendWhere("And AOPsCDN.AOPs.Id =: id");
        _params.put("id", id);
        return this;
    }
    public AOPsCDNQuery filterByAOPsCode(String code)
    {
        AppendWhere("And AOPsCDN.AOPs.Code =: code");
        _params.put("code", code);
        return this;
    }

    public AOPsCDNQuery filterByXplatformSid(XPlatformSID sid)
    {
        AppendWhere("And AOPsCDN.XPlatformSID =:sid");
        _params.put("sid", sid);
        return this;
    }

    public AOPsCDNQuery filterByWorkFlow(long wid)
    {
        AppendWhere("And AOPsCDN.WorkFlow.Id =:wid");
        _params.put("wid", wid);
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
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAOPsCode(filters.get(name).get(0));
                    break;
                case "byworkflow":
                    filterByWorkFlow(Long.valueOf(filters.get(name).get(0)));
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

                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }
    private AOPsCDNQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }


}
