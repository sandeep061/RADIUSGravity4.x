/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import java.util.ArrayList;
import java.util.HashMap;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;

/**
 *
 * @author manoj
 * @since Jul 4, 2018
 */
public class AOPsMediaQuery extends EntityQuery
{

    public AOPsMediaQuery()
    {
        super(EN.AOPsMedia);
    }

    public AOPsMediaQuery filterByAOPs(Long campid)
    {
        AppendWhere("And AOPsMedia.AOPs.Id=:campid");
        _params.put("campid", campid);

        return this;
    }
    public AOPsMediaQuery filterByAOPsCode(String Code)
    {
        AppendWhere("And AOPsMedia.AOPs.Code=:Code");
        _params.put("Code", Code);

        return this;
    }

    public AOPsMediaQuery filterByXServer(Long xid)
    {
        AppendWhere("And AOPsMedia.XServer.Id=:xid");
        _params.put("xid", xid);

        return this;
    }

    public AOPsMediaQuery filterByChannel(Channel channel)
    {
        AppendWhere("And AOPsMedia.Channel =: Channel");
        _params.put("Channel", channel);

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
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAOPsCode(filters.get(name).get(0));
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
                        throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
