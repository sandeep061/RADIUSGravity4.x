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
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;

/**
 *
 * @author Manoj-PC
 * @since Oct 18, 2023
 */
public class ProfileQuery extends EntityQuery
{

    public ProfileQuery()
    {
        super(EN.Profile);
    }

    public ProfileQuery filterByCode(String code)
    {
        AppendWhere("And Profile.Code=:code");
        _params.put("code", code);

        return this;
    }
    public ProfileQuery filterById(Long id)
    {
        AppendWhere("And Profile.Id=:code");
        _params.put("code", id);

        return this;
    }

    public ProfileQuery filterByName(String name)
    {
        AppendWhere("And Profile.Name=:code");
        _params.put("code", name);

        return this;
    }

    public ProfileQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(Profile.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    @Override
	public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "byid":
                      filterById(Long.valueOf(filters.get(name).get(0)));
                      break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0));
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
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private ProfileQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private ProfileQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

}
