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
 * @author Suman
 * @since 23 Feb, 2018
 */
public class UserQuery extends EntityQuery
{

    public UserQuery()
    {
        super(EN.User);
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
    {
        for (String name : filters.keySet()) {
            switch (name.toLowerCase()) {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byloginid":
                    filterByLoginId(filters.get(name).get(0));
                    break;
                case "byuserid":
                    filterByUserId(filters.get(name).get(0));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    public UserQuery filterByLoginId(String loginid)
    {
        AppendWhere("And " + getEn().name() + ".LoginId=:loginid");
        _params.put("loginid", loginid);

        return this;
    }

    public UserQuery filterByUserId(String userid)
    {
        AppendWhere("And " + getEn().name() + ".UserId=:userid");
        _params.put("userid", userid);

        return this;
    }
    private UserQuery orderByLoginId(Boolean get)
    {
        setOrederBy("LoginId", get);
        return this;
    }

    private UserQuery orderByOSId(Boolean get)
    {
        setOrederBy("OSId", get);
        return this;
    }

    private UserQuery orderByEmpId(Boolean get)
    {
        setOrederBy("EmpId", get);
        return this;
    }

    private UserQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
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
                    case "name":
                        orderByName(hm.get(name));
                        break;
                    case "loginid":
                        orderByLoginId(hm.get(name));
                        break;
                    case "osid":
                        orderByOSId(hm.get(name));
                        break;
                    case "empid":
                        orderByEmpId(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
