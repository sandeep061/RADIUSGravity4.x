/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import java.util.ArrayList;
import java.util.HashMap;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;


/**
 *
 * @author manoj
 * @since Feb 21, 2018
 */
public class AgentSkillQuery extends EntityQuery
{

    public AgentSkillQuery()
    {
        super(EN.AgentSkill);
    }

    public AgentSkillQuery filterByAgent(String Id)
    {
        AppendWhere("And AgentSkill.Agent.UserId =: agid");
        _params.put("agid", Id);

        return this;
    }

    public AgentSkillQuery filterBySkill(Long Id)
    {
        AppendWhere("And AgentSkill.Skill.Id =: skid");
        _params.put("skid", Id);
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
                case "byagent":
                    filterByAgent(filters.get(name).get(0));
                    break;
                case "byskill":
                    filterBySkill(Long.valueOf(filters.get(name).get(0)));
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
                    case "agent":
                        orderByAgent(hm.get(hm));
                        break;
                    case "Skill":
                        orderBySkill(hm.get(hm));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private AgentSkillQuery orderByAgent(Boolean get)
    {
        setOrederBy("Agent.Id", get);
        return this;
    }

    private AgentSkillQuery orderBySkill(Boolean get)
    {
        setOrederBy("Skill.Id", get);
        return this;
    }

}
