///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ois.radius.gravity.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import ois.radius.cc.entities.EN;
//import ois.radius.core.gravity.db.queries.EntityQuery;
//import ois.radius.core.gravity.framework.events.common.EvCauseRequestValidationFail;
//import ois.radius.core.gravity.services.exceptions.GravityIllegalArgumentException;
//
//
///**
// *
// * @author Suman
// * @since 16 Feb, 2018
// */
//public class AdminGroupQuery extends EntityQuery
//{
//
//    public AdminGroupQuery()
//    {
//        super(EN.AdminGroup);
//    }
//
//    public AdminGroupQuery filterByAdmin(Long admid)
//    {
//        AppendWhere("And :admid member of AdminGroup.Admins.Id");
//        _params.put("admid", admid);
//
//        return this;
//    }
//
//    public AdminGroupQuery filterByCode(String code)
//    {
//        AppendWhere("And AdminGroup.Code=:code");
//        _params.put("code", code);
//
//        return this;
//    }
//
//    public AdminGroupQuery filterByName(String name)
//    {
//        AppendWhere("And AdminGroup.Name=:name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public AdminGroupQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(AdminGroup.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
//
//        return this;
//    }
//
//    public AdminGroupQuery filterByAops(Long campid)
//    {
//        AppendWhere("And :campid member of AdminGroup.Campaigns.Id");
//        _params.put("campid", campid);
//
//        return this;
//    }
//
//    @Override
//    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
//    {
//        for (String name : filters.keySet())
//        {
//            switch (name.toLowerCase())
//            {
//                case "byid":
//                    filterById(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byadmin":
//                    filterByAdmin(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycode":
//                    filterByCode(filters.get(name).get(0));
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(filters.get(name).get(0).toLowerCase());
//                    break;
//                case "bycampaign":
//                    filterByAops(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                default:
//                    throw new GravityIllegalArgumentException("filter{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//    }
//
//    @Override
//    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityIllegalArgumentException
//    {
//        for (HashMap<String, Boolean> hm : orderby)
//        {
//            for (String name : hm.keySet())
//            {
//                switch (name.toLowerCase())
//                {
//                    case "id":
//                        orderById(hm.get(name));
//                        break;
//                    case "code":
//                        orderByCode(hm.get(name));
//                        break;
//                    case "name":
//                        orderByName(hm.get(name));
//                        break;
//                    default:
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//    private AdminGroupQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private AdminGroupQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//}
