package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.CLISelectionStrategy;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;


import java.util.ArrayList;
import java.util.HashMap;

public class AOPsCallerIdQuery extends EntityQuery
{

    public AOPsCallerIdQuery()
    {
        super(EN.AOPsCallerId);
    }

  
    public AOPsCallerIdQuery filterByAOPs(Long id)
    {
        AppendWhere("And AOPsCallerId.AOPs.Id =: id");
        _params.put("id", id);
        return this;
    }
    public AOPsCallerIdQuery filterByCode(String code)
    {
        AppendWhere("And AOPsCallerId.Code =: code");
        _params.put("code", code);
        return this;
    }

    public AOPsCallerIdQuery filterByAOPsCode(String code)
    {
        AppendWhere("And AOPsCallerId.AOPs.Code =: code");
        _params.put("code", code);
        return this;
    }

    public AOPsCallerIdQuery filterByChannel(Channel Channel)
    {
        AppendWhere("And AOPsCallerId.Channel =: Channel");
        _params.put("Channel", Channel);
        return this;
    }

    public AOPsCallerIdQuery filterByXplatform(Long id)
    {
        AppendWhere("And AOPsCallerId.XPlatform.Id =: id");
        _params.put("id", id);
        return this;
    }

    public AOPsCallerIdQuery filterByXplatformSID(XPlatformSID sid)
    {
        AppendWhere("And AOPsCallerId.XPlatformSID =: sid");
        _params.put("sid", sid);
        return this;
    }

    public AOPsCallerIdQuery filterByCLISelectionStrategy(CLISelectionStrategy clist)
    {
        AppendWhere("And AOPsCallerId.CLISelectionStrategy =: clist");
        _params.put("clist", clist);
        return this;
    }

    public AOPsCallerIdQuery filterByXplatformUA(Long id)
    {
        AppendWhere("And AOPsCallerId.DefXPlatformUA.Id =: id");
        _params.put("id", id);
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
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAOPsCode(filters.get(name).get(0));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatform":
                    filterByXplatform(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformsid":
                    filterByXplatformSID(XPlatformSID.valueOf(filters.get(name).get(0)));
                    break;
                case "bycliselectionstrategy":
                    filterByCLISelectionStrategy(CLISelectionStrategy.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformua":
                    filterByXplatformUA(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bycode":
                    filterByCode(filters.get(name).get(0));
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
    
    private AOPsCallerIdQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    
}
