/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import java.util.ArrayList;
import java.util.HashMap;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsBFProperties;

/**
 *
 * @author Sandeepkumar.Sahoo
 * @since Jun 17, 2025
 */
public class AOPsBFPropertiesQuery extends EntityQuery
{

    public AOPsBFPropertiesQuery() {
        super(EN.AOPsBFProperties);
    }

    public AOPsBFPropertiesQuery filterByAOPsBF(Long id)
    {
        AppendWhere("And AOPsBFProperties.AOPsBF.Id =:id");
        _params.put("id",id);
        return this;
    }
    public AOPsBFPropertiesQuery filterByAOPs(Long id)
    {
        AppendWhere("And AOPsBFProperties.AOPsBF.AOPs.Id =:id");
        _params.put("id",id);
        return this;
    }
    public AOPsBFPropertiesQuery filterByConfKey(AOPsBFProperties.Key key)
    {
        AppendWhere("And AOPsBFProperties.ConfKey =: key");
        _params.put("key", key.name());

        return this;
    }
    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {

        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byaopsbf":
                    filterByAOPsBF(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byconfkey":
                    filterByConfKey(AOPsBFProperties.Key.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException
    {

    }

}
