package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AOPsCDNAddressQuery extends EntityQuery
{
    public AOPsCDNAddressQuery()
    {
        super(EN.AOPsCDNAddress);
    }

    public AOPsCDNAddressQuery filterByAddress(String address)
    {
        AppendWhere("And AOPsCDNAddress.Address =: address");
        _params.put("address", address);
        return this;
    }

    public AOPsCDNAddressQuery filterByChannel(Channel channel)
    {
        AppendWhere("And AOPsCDNAddress.Channel =: channel");
        _params.put("channel", channel);
        return this;
    }

    public AOPsCDNAddressQuery filterByAOPsCDN(Long id)
    {
        AppendWhere("And AOPsCDNAddress.AOPsCDN.Id =: id");
        _params.put("id", id);
        return this;
    }


    public AOPsCDNAddressQuery filterByXplatformSid(XPlatformSID sid)
    {
        AppendWhere("And AOPsCDNAddress.XPlatformSID =:sid");
        _params.put("sid", sid);
        return this;
    }

    public AOPsCDNAddressQuery filterByXplatformid(XPlatformID sid)
    {
        AppendWhere("And AOPsCDNAddress.XPlatformID =:sid");
        _params.put("sid", sid);
        return this;
    }

    public AOPsCDNAddressQuery filterByXplatformua(long uaid)
    {
        AppendWhere("And AOPsCDNAddress.XPlatformUA.Id =:uid");
        _params.put("uid", uaid);
        return this;
    }
    public AOPsCDNAddressQuery filterByAOPs(long aopid)
    {
        AppendWhere("And AOPsCDNAddress.AOPsCDN.AOPs.Id =:uid");
        _params.put("uid", aopid);
        return this;
    }
    public AOPsCDNAddressQuery forNotAOPsBy(Long aopsid)
    {
        AppendWhere("And AOPsCDN.AOPs.Id <>: aopsid");
        _params.put("aopsid", aopsid);
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
                case "byaddress":
                    filterByAddress(filters.get(name).get(0));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscdn":
                    filterByAOPsCDN(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformsid":
                    filterByXplatformSid(XPlatformSID.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformid":
                    filterByXplatformid(XPlatformID.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformua":
                    filterByXplatformua(Long.valueOf(filters.get(name).get(0)));
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
                    default:
                        throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }
}
