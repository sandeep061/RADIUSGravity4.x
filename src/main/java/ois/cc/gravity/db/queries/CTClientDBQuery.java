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
//import ois.radius.gravity.AppConst;
//import ois.radius.gravity.service.exception.GravityException;
//import ois.radius.gravity.service.exception.GravityIllegalArgumentException;
//
//
///**
// *
// * @author biswajit.rout
// * @since 16 Oct, 2023
// */
//public class CTClientDBQuery extends EntityQuery
//{
//
//    public CTClientDBQuery()
//    {
//        super(EN.CTClientDB);
//    }
//    
//    public CTClientDBQuery excludeSYSClientDB()
//    {
//        AppendWhere("And CTClientDB.Code <>: sysdbcode");
//        _params.put("sysdbcode", AppConst.SYS_CLIENT_DB_CODE);
//
//        return this;
//    }
//
//    public CTClientDBQuery filterByCode(String code)
//    {
//        AppendWhere("And CTClientDB.Code=:code");
//        _params.put("code", code == null ? null : code.toUpperCase());
//
//        return this;
//    }
//
//    public CTClientDBQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(CTClientDB.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
//
//        return this;
//    }
//
//    public CTClientDBQuery filterByDefAdminLoginId(String name)
//    {
//        AppendWhere("And CTClientDB.DefAdminLoginId =: name ");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    
//    @Override
//    protected void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
//    {
//        for (String name : filters.keySet())
//        {
//            switch (name.toLowerCase())
//            {
//                case "bycode":
//                    filterByCode(filters.get(name).get(0));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(filters.get(name).get(0).toLowerCase());
//                    break;
//                default:
//                    throw new GravityIllegalArgumentException("filter{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//    }
//    @Override
//    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityException
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
//    private CTClientDBQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private CTClientDBQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}
