/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import org.vn.radius.cc.platform.xspi.ProviderID;

import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;

/**
 *
 * @author manoj
 * @since Jul 9, 2018
 */
public class XServerQuery extends EntityQuery
{
    
    public XServerQuery()
    {
        super(EN.XServer);
    }
    
    public XServerQuery filterByCode(String code)
    {
        AppendWhere("And XServer.Code =: code");
        _params.put("code", code);
        
        return this;
    }
    
    public XServerQuery filterByName(String name)
    {
        AppendWhere("And XServer.Name =: name");
        _params.put("name", name);
        
        return this;
    }

    /**
     * argument must in lowercase as discussed.
     * @param name
     * @return
     */
    public XServerQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(XServer.Name) like : name ");
        _params.put("name", "%" + name + "%");
        
        return this;
    }
    
    public XServerQuery filterByProviderID(ProviderID provid)
    {
        AppendWhere("And XServer.ProviderID =: provid ");
        _params.put("provid", provid);
        
        return this;
    }
    
    public XServerQuery filterByChannel(Channel... channels)
    {

        AppendWhere("And XServer.Channel in (:channels)");
        _params.put("channels", List.of(channels));

        return this;
    }

    /**
     * Though this method is not for filters (due to list in argument) , but is frequent used by processors.
     *
     * @param channels
     * @return
     */
    public XServerQuery filterByChannels(List<Channel> channels)
    {
        AppendWhere("And XServer.Channel in (:channels)");
        _params.put("channels", channels);
        
        return this;
    }

    /**
     * This not for filters , but is for server internal uses
     * @param ids
     * @return
     */
    public XServerQuery filterByIds(Set<Long> ids)
    {
        AppendWhere("And XServer.Id in (:ids)");
        _params.put("ids", ids);
        
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
                    filterByCode(filters.get(name).get(0));
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "bychannel":
                    List<Channel> chnls = filters.get(name).stream()
                            .map((c) -> Channel.valueOf(c)).collect(Collectors.toList());
                    filterByChannel(chnls.toArray(new Channel[chnls.size()]));
                    break;
                case "byproviderid":
                    filterByProviderID(ProviderID.valueOf(filters.get(name).get(0)));
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
    
    private XServerQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }
    
    private XServerQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }
    
}
