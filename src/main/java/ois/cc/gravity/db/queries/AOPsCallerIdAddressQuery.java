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

public class AOPsCallerIdAddressQuery extends EntityQuery
{
    public AOPsCallerIdAddressQuery()
    {
        super(EN.AOPsCallerIdAddress);
    }


    public AOPsCallerIdAddressQuery filterByAddress(String address)
    {
        AppendWhere("And AOPsCallerIdAddress.Address =: address");
        _params.put("address", address);
        return this;
    }

    public AOPsCallerIdAddressQuery filterByChannel(Channel channel)
    {
        AppendWhere("And AOPsCallerIdAddress.Channel =: channel");
        _params.put("channel", channel);
        return this;
    }

    public AOPsCallerIdAddressQuery filterByAOPsCallerId(Long id)
    {
        AppendWhere("And AOPsCallerIdAddress.AOPsCallerId.Id =: id");
        _params.put("id", id);
        return this;
    }


    public AOPsCallerIdAddressQuery filterByXplatformSid(XPlatformSID sid)
    {
        AppendWhere("And AOPsCallerIdAddress.XPlatformSID =:sid");
        _params.put("sid", sid);
        return this;
    }

    public AOPsCallerIdAddressQuery filterByXplatformid(XPlatformID sid)
    {
        AppendWhere("And AOPsCallerIdAddress.XPlatformID =:sid");
        _params.put("sid", sid);
        return this;
    }

    public AOPsCallerIdAddressQuery filterByXplatformua(long uaid)
    {
        AppendWhere("And AOPsCallerIdAddress.XPlatformUA.Id =:uid");
        _params.put("uid", uaid);
        return this;
    }
    public AOPsCallerIdAddressQuery forNotAOPsBy(Long aopsid)
    {
        AppendWhere("And AOPsCallerId.AOPs.Id <>: aopsid");
        _params.put("aopsid", aopsid);
        return this;
    }
    public AOPsCallerIdAddressQuery fetchAOPsCallerIdByAOPs(long id)
    {
        AppendWhere("And AOPsCallerIdAddress.AOPsCallerId.AOPs.Id =:id");
        _params.put("id", id);
        return this;
    }

    public AOPsCallerIdAddressQuery filterByIsDefault(boolean isdefault)
    {
        AppendWhere("And AOPsCallerIdAddress.IsDefault =:isdefault");
        _params.put("isdefault", isdefault);
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
                case "byaopscallerid":
                    filterByAOPsCallerId(Long.valueOf(filters.get(name).get(0)));
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
