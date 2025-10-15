package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class XPlatformUAQuery extends EntityQuery
{

    public XPlatformUAQuery()
    {
        super(EN.XPlatformUA);
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
                case "byaddress":
                    filterByAddress(filters.get(name).get(0));
                    break;
                case "byxplatform":
                    filterByXplatform(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformcode":
                    filterByXplatformCode(filters.get(name).get(0));
                    break;
                case "bychannel":
                    filterBychannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);

            }
        }
    }

    public XPlatformUAQuery filterByCode(String code)
    {
        AppendWhere("And XPlatformUA.Code =: code");
        _params.put("code", code);
        return this;
    }

    public XPlatformUAQuery filterByAddress(String address)
    {
        AppendWhere("And XPlatformUA.Address =: address");
        _params.put("address", address);
        return this;
    }

    public XPlatformUAQuery filterByXplatform(Long id)
    {
        AppendWhere("And XPlatformUA.XPlatform.Id =: id");
        _params.put("id", id);
        return this;
    }

    public XPlatformUAQuery filterByXplatformCode(String code)
    {
        AppendWhere("And XPlatformUA.XPlatform.Code =: code");
        _params.put("code", code);
        return this;
    }

    public XPlatformUAQuery filterBychannel(Channel channel)
    {
        AppendWhere("And XPlatformUA.Channel =: channel");
        _params.put("channel", channel);
        return this;
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

    private XPlatformUAQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

}
