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
import ois.radius.ca.enums.AgentState;


import ois.radius.cc.entities.EN;


/**
 *
 * @author manoj
 * @since May 17, 2018
 */
public class AgentStateReasonQuery extends EntityQuery
{

    public AgentStateReasonQuery()
    {
        super(EN.AgentStateReason);
    }

    public AgentStateReasonQuery filterByCode(String code)
    {
        AppendWhere("And AgentStateReason.Code=:code");
        _params.put("code", code);

        return this;
    }

    public AgentStateReasonQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(AgentStateReason.Name) Like : name ");
        _params.put("name", "%" + name + "%");

        return this;
    }

    public AgentStateReasonQuery filterByAgentState(AgentState as)
    {
        AppendWhere("And AgentStateReason.AgentState=:as");
        _params.put("as", as);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException {
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
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byagentstate":
                    filterByAgentState(AgentState.valueOf(filters.get(name).get(0)));
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
                    case "code":
                        orderByCode(hm.get(name));
                        break;
                    case "name":
                        orderByName(hm.get(name));
                        break;
                    case "agentstate":
                        orderByAgentState(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private AgentStateReasonQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private AgentStateReasonQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

    private AgentStateReasonQuery orderByAgentState(Boolean get)
    {
        setOrederBy("AgentState", get);
        return this;
    }
}
