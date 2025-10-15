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



public class QueueQuery extends EntityQuery
{

    public QueueQuery()
    {
        super(EN.Queue);
    }

    public QueueQuery filterByName(String name)
    {
        AppendWhere("And Queue.Name =: name ");
        _params.put("name", name);
        return this;
    }

    public QueueQuery filterByCode(String code)
    {
        AppendWhere("And Queue.Code =: code ");
        _params.put("code", code);
        return this;
    }

    public QueueQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(Queue.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public QueueQuery filterByXServer(Long xsid)
    {
        AppendWhere("And Queue.XServer.Id =: xsid");
        _params.put("xsid", xsid);

        return this;
    }

    public QueueQuery filterByChannel(Channel channel)
    {
        AppendWhere("And Queue.Channel =: chn");
        _params.put("chn", channel);

        return this;
    }

    /**
     * This method used for internal propose.
     * @param addrs
     * @return
     */
    public QueueQuery filterByAddress(String addrs)
    {
        AppendWhere("And Queue.Address =: addrs");
        _params.put("addrs", addrs);

        return this;
    }

    public QueueQuery filterByAddressLike(String addrs)
    {
        AppendWhere("And Lower(Queue.Address) Like : addrs");
        _params.put("addrs", "%" + addrs + "%");

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
                    filterByCode(filters.get(name).get(0).toUpperCase());
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byxserver":
                    filterByXServer(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaddress":
                    filterByAddress(filters.get(name).get(0));
                    break;
                case "byaddresslike":
                    filterByAddressLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
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

    private QueueQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private QueueQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }
}
