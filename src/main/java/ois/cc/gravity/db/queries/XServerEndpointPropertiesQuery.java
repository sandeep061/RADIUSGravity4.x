package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
public class XServerEndpointPropertiesQuery   extends EntityQuery
{

    public XServerEndpointPropertiesQuery()
    {
        super(EN.XServerEndpointProperties);
    }

    public XServerEndpointPropertiesQuery filterByKey(String name)
    {
        AppendWhere("And XServerEndpointProperties.ConfKey =: name ");
        _params.put("name", name);
        return this;
    }

    public XServerEndpointPropertiesQuery filterByCode(String code)
    {
        AppendWhere("And XServerEndpointProperties.Code =: code ");
        _params.put("code", code);
        return this;
    }

    public XServerEndpointPropertiesQuery filterByKeyLike(String name)
    {
        AppendWhere("And Lower(XServerEndpointProperties.ConfKey) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public XServerEndpointPropertiesQuery filterByXServer(Long xsid)
    {
        AppendWhere("And XServerEndpointProperties.XServer.Id =: xsid");
        _params.put("xsid", xsid);

        return this;
    }

    public XServerEndpointPropertiesQuery filterByChannel(Channel channel)
    {
        AppendWhere("And XServerEndpointProperties.Channel =: chn");
        _params.put("chn", channel);

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
                    filterByKey(filters.get(name).get(0));
                    break;
                case "bykeylike":
                    filterByKeyLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byxserver":
                    filterByXServer(Long.valueOf(filters.get(name).get(0)));
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





