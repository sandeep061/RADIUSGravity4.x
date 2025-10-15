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
///**
// *
// * @author Manoj
// * @since 14 Apr, 2020
// */
//public class AgentQuery extends EntityQuery
//{
//
//    public AgentQuery()
//    {
//        super(EN.Agent);
//    }
//
//    public AgentQuery filterByName(String name)
//    {
//        AppendWhere("And Agent.Name =: name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public AgentQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(Agent.Name) like :name ");
//        _params.put("name", "%" + name.toLowerCase() + "%");
//
//        return this;
//    }
//
//    public AgentQuery filterByPhone(String phone)
//    {
//        AppendWhere("And Agent.PhMobile =:phone or Agent.PhWork =:phone or Agent.PhHome =:phone");
//        _params.put("phone", phone);
//
//        return this;
//    }
//
//    public AgentQuery filterByEmail(String email)
//    {
//        AppendWhere("And Agent.EmailId =: email");
//        _params.put("email", email);
//
//        return this;
//    }
//
//    public AgentQuery filterByOSId(String osid)
//    {
//        AppendWhere("And Agent.OSId =:osid");
//        _params.put("osid", osid);
//
//        return this;
//    }
//
//    public AgentQuery filterByLoginId(String loginid)
//    {
//        AppendWhere("And Agent.LoginId =:loginid");
//        _params.put("loginid", loginid);
//
//        return this;
//    }
//
//    public AgentQuery filterByLoginIdLike(String loginid)
//    {
//        AppendWhere("And Lower(Agent.LoginId) Like :loginid");
//        _params.put("loginid", "%" + loginid + "%");
//
//        return this;
//    }
//
//    public AgentQuery filterByEmpId(String empid)
//    {
//        AppendWhere("And Agent.EmpId =: empid");
//        _params.put("empid", empid);
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
//                case "byosid":
//                    filterByOSId(filters.get(name).get(0));
//                    break;
//                case "byloginid":
//                    filterByLoginId(filters.get(name).get(0));
//                    break;
//                case "byloginidlike":
//                    filterByLoginIdLike(filters.get(name).get(0).toLowerCase());
//                    break;
//                case "byempid":
//                    filterByEmpId(filters.get(name).get(0));
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(filters.get(name).get(0).split(" ")[0].toLowerCase());
//                    break;
//                case "byphone":
//                    filterByPhone(filters.get(name).get(0));
//                    break;
//                case "byemail":
//                    filterByEmail(filters.get(name).get(0));
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
//                    case "name":
//                        orderByName(hm.get(name));
//                        break;
//                    case "loginid":
//                        orderByLoginId(hm.get(name));
//                        break;
//                    case "osid":
//                        orderByOSId(hm.get(name));
//                        break;
//                    case "empid":
//                        orderByEmpId(hm.get(name));
//                        break;
//                    default:
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//    private AgentQuery orderByLoginId(Boolean get)
//    {
//        setOrederBy("LoginId", get);
//        return this;
//    }
//
//    private AgentQuery orderByOSId(Boolean get)
//    {
//        setOrederBy("OSId", get);
//        return this;
//    }
//
//    private AgentQuery orderByEmpId(Boolean get)
//    {
//        setOrederBy("EmpId", get);
//        return this;
//    }
//
//    private AgentQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}
