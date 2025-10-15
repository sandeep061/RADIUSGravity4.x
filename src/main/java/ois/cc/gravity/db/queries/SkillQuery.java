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

public class SkillQuery extends EntityQuery
{

    public SkillQuery()
    {
        super(EN.Skill);
    }

    public SkillQuery filterByCode(String code)
    {
        AppendWhere("And Skill.Code=:code");
        _params.put("code", code);

        return this;
    }

    public SkillQuery filterByName(String name)
    {
        AppendWhere("And Skill.Name=:name");
        _params.put("name", name);

        return this;
    }

    public SkillQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(Skill.Name) Like : name ");
        _params.put("name", "%" + name + "%");

        return this;
    }

    public SkillQuery filterByAops(Long campid)
    {
        AppendWhere("And Skill.AOPs.Id=:campid");
        _params.put("campid", campid);

        return this;
    }
    public SkillQuery filterByAopsCode(String code)
    {
        AppendWhere("And Skill.AOPs.Code=:code");
        _params.put("code", code);

        return this;
    }


    public SkillQuery filterByQueue(Long qid)
    {
        AppendWhere("And Skill.Queue.Id=:qid");
        _params.put("qid", qid);

        return this;
    }

    public SkillQuery filterByChannel(Channel chn)
    {
        AppendWhere("And Skill.Channel=:chn");
        _params.put("chn", chn);

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
                case "byaops":
                    filterByAops (Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAopsCode(filters.get(name).get(0));
                    break;
                case "byqueue":
                    filterByQueue(Long.valueOf(filters.get(name).get(0)));
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

    private SkillQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private SkillQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

}
