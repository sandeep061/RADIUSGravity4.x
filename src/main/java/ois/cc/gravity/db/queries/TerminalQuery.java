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


/**
 *
 * @author Manoj
 * @since 22 Jan, 2020
 */
public class TerminalQuery extends EntityQuery
{

    public TerminalQuery()
    {
        super(EN.Terminal);
    }

    public TerminalQuery filterByCode(String name)
    {
        AppendWhere("And Terminal.Code=:code");
        _params.put("code", name);

        return this;
    }

    public TerminalQuery filterByName(String name)
    {
        AppendWhere("And Terminal.Name=:name");
        _params.put("name", name);

        return this;
    }


    public TerminalQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(Terminal.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public TerminalQuery filterByLoginId(String loginid)
    {
        AppendWhere("And Terminal.LoginId=:loginid");
        _params.put("loginid", loginid);

        return this;
    }
    
    public TerminalQuery filterByPassword(String pass)
    {
        AppendWhere("And Terminal.Password=:pass");
        _params.put("pass", pass);

        return this;
    }

    public TerminalQuery filterByAddress(String addr)
    {
        AppendWhere("And Terminal.Address=:addr");
        _params.put("addr", addr);

        return this;
    }

    public TerminalQuery filterByChannel(Channel chn)
    {
        AppendWhere("And Terminal.Channel=:chn");
        _params.put("chn", chn);

        return this;
    }

    public TerminalQuery filterByXServer(Long xsid)
    {
        AppendWhere("And Terminal.XServer.Id=:id");
        _params.put("id", xsid);

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
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byaddress":
                    filterByAddress(filters.get(name).get(0));
                    break;
                case "byxserver":
                    filterByXServer(Long.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    private TerminalQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private TerminalQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

    private TerminalQuery orderByAddress(Boolean get)
    {
        setOrederBy("Address", get);
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
                    case "code":
                        orderByCode(hm.get(name));
                        break;
                    case "name":
                        orderByName(hm.get(name));
                        break;
                    case "address":
                        orderByAddress(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
