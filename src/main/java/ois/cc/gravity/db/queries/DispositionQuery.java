/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.db.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import code.db.jpa.JPAQuery;
import code.ua.events.EventFailedCause;
import ois.radius.ca.enums.xsess.XSessStatus;
import ois.radius.cc.entities.EN;

public class DispositionQuery extends EntityQuery
{

    public DispositionQuery(EN en) throws GravityIllegalArgumentException
    {
        super(en);
        if (!(en.equals(EN.Disposition) || en.equals(EN.Disposition0) || en.equals(EN.Disposition1)))
        {
            throw new GravityIllegalArgumentException("Entity must be a subclass of Disposition only.");
        }
        this._en = en;
    }

    public DispositionQuery filterByCode(String code)
    {
        AppendWhere("And " + _en.name() + ".Code =: code");
        _params.put("code", code);

        return this;
    }

    public DispositionQuery filterByChannel(Channel channel)
    {
        AppendWhere("And  :Channel in (" + _en.name() + ".Channels)");
        _params.put("Channel", channel);

        return this;
    }

    public JPAQuery filterByChannels(List<Channel> channel)
    {
        JPAQuery qry = new JPAQuery("Select " + _en.name() + " from " + _en.name() + " "
                + "" + _en.name() + " JOIN " + _en.name() + ".Channels chn "
                + "Where chn in : chns ");
        _params.put("Channel", List.of(channel));

        return qry;
    }

    public DispositionQuery filterByCategory(XSessStatus.Category ctgry)
    {
        AppendWhere("And " + _en.name() + ".Category =: ctgry");
        _params.put("ctgry", ctgry);

        return this;
    }

    public DispositionQuery filterByAops(Long campid)
    {
        AppendWhere("And " + _en.name() + ".AOPs.Id =:camp ");
        _params.put("camp", campid);

        return this;
    }
    public DispositionQuery filterByAopsCode(String code)
    {
        AppendWhere("And " + _en.name() + ".AOPs.Code =:code ");
        _params.put("code", code);

        return this;
    }

    public DispositionQuery filterBySuper(Long supid)
    {
        AppendWhere("And " + _en.name() + ".Super.Id =:supid");
        _params.put("supid", supid);

        return this;
    }

    public DispositionQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(" + _en.name() + ".Name) Like : name ");
        _params.put("name", "%" + name + "%");

        return this;
    }

    public DispositionQuery filterByName(String name)
    {
        AppendWhere("And " + _en.name() + ".Name=:name");
        _params.put("name", name);

        return this;
    }

    public void forNonAOPs()
    {
        AppendWhere("And " + _en.name() + ".AOPs Is Null");
    }

    public void forNotCreatedBy(Long userid)
    {
        AppendWhere("And CreatedBy <>: userid");
        _params.put("userid", userid);
    }

    public void byNonCategory()
    {
        AppendWhere("And " + _en.name() + ".Category IS NULL");
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
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "bychannels":
                    List<Channel> chnlList = filters.get(name).stream()
                            .map((c) -> Channel.valueOf(c)).collect(Collectors.toList());
                    filterByChannels(chnlList);
                    break;
                case "bycategory":
                    filterByCategory(XSessStatus.Category.valueOf(filters.get(name).get(0)));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "byaops":
                    filterByAops(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAopsCode(filters.get(name).get(0));
                    break;
                case "bysuper":
                    filterBySuper(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byonlyparent":
                    //@since V:070723 
                    //For this filter we don't any need impl method. This is handled in processor layer,based on entity we will return the records.
                    break;
                case "bynonaops":
                    forNonAOPs();
                    break;

                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    public DispositionQuery orderByDispSeq(Boolean isasc)
    {
        setOrederBy("DispSeq", isasc);
        return this;
    }

    private DispositionQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    public DispositionQuery orderByName(Boolean get)
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
                    case "code":
                        orderByCode(hm.get(name));
                        break;
                    case "name":
                        orderByName(hm.get(name));
                        break;
                    case "dispseq":
                        orderByDispSeq(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
