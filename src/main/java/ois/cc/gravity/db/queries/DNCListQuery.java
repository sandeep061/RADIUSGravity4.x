///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ois.cc.gravity.db.queries;
//
//import code.ua.events.EventFailedCause;
//import java.util.ArrayList;
//import java.util.HashMap;
//import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
//
//
//import ois.radius.cc.entities.EN;
//import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
//
//
///**
// *
// * @author manoj
// * @since Dec 11, 2018
// */
//public class DNCListQuery extends EntityQuery
//{
//
//    public DNCListQuery()
//    {
//        super(EN.DNCList);
//    }
//
//    public DNCListQuery filterByCode(String code)
//    {
//        AppendWhere("And DNCList.Code =: code");
//        _params.put("code", code);
//
//        return this;
//    }
//
//    public DNCListQuery filterByName(String name)
//    {
//        AppendWhere("And DNCList.Name =: name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public DNCListQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(DNCList.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
//
//        return this;
//    }
//
//    public DNCListQuery filterByAOPs(Long campid)
//    {
//        AppendWhere("And element(DNCList.AOPs).Id =: id");
//        _params.put("id", campid);
//
//        return this;
//    }
//    public DNCListQuery filterByAopsCode(String code)
//    {
//        AppendWhere("And DialIDPlan.AOPs.Code =: code");
//        _params.put("code", code);
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
//                case "byaops":
//                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byaopscode":
//                    filterByAopsCode(filters.get(name).get(0));
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(filters.get(name).get(0).toLowerCase());
//                    break;
//                case "bycode":
//                    filterByCode(filters.get(name).get(0));
//                    break;
//                default:
//                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//
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
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//    private DNCListQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private DNCListQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}
