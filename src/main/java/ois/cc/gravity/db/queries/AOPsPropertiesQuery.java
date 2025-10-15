/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 package ois.cc.gravity.db.queries;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.tenant.cc.AOPsProperties;

import java.util.ArrayList;
import java.util.HashMap;



/**
 *
 * @author Manoj
 * @since 12 Aug, 2019
 */
public class AOPsPropertiesQuery extends EntityQuery
{

    public AOPsPropertiesQuery()
    {
        super(EN.AOPsProperties);
    }

    public AOPsPropertiesQuery filterByAOPs(Long aopsid)
    {
        AppendWhere("And AOPsProperties.AOPs.Id =: aopsid");
        _params.put("aopsid", aopsid);

        return this;
    }

    public AOPsPropertiesQuery filterByConfKey(AOPsProperties.Keys key)
    {
        AppendWhere("And AOPsProperties.ConfKey =: key");
        _params.put("key", key.name());

        return this;
    }

    public AOPsPropertiesQuery filterByConfValue(String value)
    {
        AppendWhere("And AOPsProperties.ConfValue =: value");
        _params.put("value", value);

        return this;
    }

    public AOPsPropertiesQuery filterByAOPsCode(String code)
    {
        AppendWhere("And AOPsProperties.AOPs.Code=:code");
        _params.put("code", code);

        return this;
    }
    
    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAOPsCode(filters.get(name).get(0));
                    break;
                case "byconfkey":
                    filterByConfKey(AOPsProperties.Keys.valueOf(filters.get(name).get(0)));
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
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
