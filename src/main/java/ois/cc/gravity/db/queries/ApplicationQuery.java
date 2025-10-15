///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
// package ois.radius.gravity.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//import org.vn.radius.cc.server.entities.EN;
//
//import ois.radius.gravity.service.exception.GravityIllegalArgumentException;
//
//
//public class ApplicationQuery extends EntityQuery
//{
//
//    public ApplicationQuery()
//    {
//        super(EN.Application);
//    }
//
//    public ApplicationQuery filterByCode(String code)
//    {
//        AppendWhere("And Application.Code=:code");
//        _params.put("code", code == null ? null : code.toUpperCase());  //Code will always be in upper case.
//
//        return this;
//    }
//
//    public ApplicationQuery filterByName(String name)
//    {
//        AppendWhere("And Application.Name=:name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public ApplicationQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(Application.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
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
//                case "bycode":
//                    filterByCode(filters.get(name).get(0));
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(filters.get(name).get(0).toLowerCase());
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
//    private ApplicationQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private ApplicationQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//}
