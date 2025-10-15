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
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//import org.vn.radius.cc.server.entities.EN;
//
//import ois.radius.gravity.service.exception.GravityIllegalArgumentException;
//
//
///**
// *
// * @author manoj
// * @since Feb 21, 2018
// */
//public class AdminQuery extends EntityQuery
//{
//
//    public AdminQuery()
//    {
//        super(EN.Admin);
//    }
//
//    public AdminQuery filterByName(String name)
//    {
//        AppendWhere("And Admin.Name =: name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public AdminQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(Admin.Name) like :name ");
//        _params.put("name", "%" + name + "%");
//
//        return this;
//    }
//
//    public AdminQuery filterByPhone(String phone)
//    {
//        AppendWhere("And Admin.PhMobile =:phone or Admin.PhWork =:phone or Admin.PhHome =:phone");
//        _params.put("phone", phone);
//
//        return this;
//    }
//
//    public AdminQuery filterByEmail(String email)
//    {
//        AppendWhere("And Admin.EmailId =: email");
//        _params.put("email", email);
//
//        return this;
//    }
//
//    public AdminQuery filterByOSId(String osid)
//    {
//        AppendWhere("And Admin.OSId =: osid");
//        _params.put("osid", osid);
//
//        return this;
//    }
//
//    public AdminQuery filterByLoginId(String loginid)
//    {
//        AppendWhere("And Admin.LoginId=: loginid");
//        _params.put("loginid", loginid);
//
//        return this;
//    }
//
//    public AdminQuery filterByLoginIdLike(String loginid)
//    {
//        AppendWhere("And Lower(Admin.LoginId) Like :loginid");
//        _params.put("loginid", "%" + loginid + "%");
//
//        return this;
//    }
//
//    public AdminQuery filterByEmpId(String empid)
//    {
//        AppendWhere("And Admin.EmpId =: empid");
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
//                    filterByNameLike(filters.get(name).get(0).toLowerCase());
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
//    private AdminQuery orderByLoginId(Boolean get)
//    {
//        setOrederBy("LoginId", get);
//        return this;
//    }
//
//    private AdminQuery orderByOSId(Boolean get)
//    {
//        setOrederBy("OSId", get);
//        return this;
//    }
//
//    private AdminQuery orderByEmpId(Boolean get)
//    {
//        setOrederBy("EmpId", get);
//        return this;
//    }
//
//    private AdminQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}
