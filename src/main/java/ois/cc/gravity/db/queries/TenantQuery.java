/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.db.queries;

import java.util.ArrayList;
import java.util.HashMap;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.AppConst;
import ois.cc.gravity.db.queries.EntityQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;


/**
 *
 * @author Suman
 * @since 27 Feb, 2018
 */
public class TenantQuery extends EntityQuery
{

    public TenantQuery()
    {
        super(EN.Tenant);
    }


    public TenantQuery filterByCode(String code)
    {
        AppendWhere("And Tenant.Code=:code");
        _params.put("code", code == null ? null : code.toUpperCase());

        return this;
    }

    public TenantQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(Tenant.Name) Like : name ");
        _params.put("name", "%" + name + "%");

        return this;
    }

    public TenantQuery filterByDefAdminLoginId(String name)
    {
        AppendWhere("And Tenant.DefAdminLoginId =: name ");
        _params.put("name", name);

        return this;
    }

    public TenantQuery excludeSYSClient()
    {
        AppendWhere("And Tenant.Code <>: syscode");
        _params.put("syscode", AppConst.SYS_CLIENT_CODE);

        return this;
    }

    public TenantQuery excludeClient(Long id)
    {
        AppendWhere("And Tenant.Id <>: id");
        _params.put("id", id);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
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

    private TenantQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private TenantQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

}
