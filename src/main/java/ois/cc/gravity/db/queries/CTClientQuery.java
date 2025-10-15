///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.vn.radius.cc.server.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//import org.vn.radius.cc.server.entities.EN;
//import org.vn.radius.cc.server.exceptions.RADIllegalArgumentException;
//
//import ois.radius.gravity.AppConst;
//
///**
// *
// * @author Suman
// * @since 27 Feb, 2018
// */
//public class CTClientQuery extends EntityQuery
//{
//
//    public CTClientQuery()
//    {
//        super(EN.CTClient);
//    }
//
//    public CTClientQuery filterByCTClientDB(Long id)
//    {
//        AppendWhere("And CTClient.CTClientDB.Id=:code");
//        _params.put("code", id);
//
//        return this;
//    }
//    public CTClientQuery filterByCode(String code)
//    {
//        AppendWhere("And CTClient.Code=:code");
//        _params.put("code", code == null ? null : code.toUpperCase());
//
//        return this;
//    }
//
//    public CTClientQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(CTClient.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
//
//        return this;
//    }
//
//    public CTClientQuery filterByDefAdminLoginId(String name)
//    {
//        AppendWhere("And CTClient.DefAdminLoginId =: name ");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public CTClientQuery excludeSYSClient()
//    {
//        AppendWhere("And CTClient.Code <>: syscode");
//        _params.put("syscode", AppConst.SYS_CLIENT_CODE);
//
//        return this;
//    }
//
//    public CTClientQuery excludeClient(Long id)
//    {
//        AppendWhere("And CTClient.Id <>: id");
//        _params.put("id", id);
//
//        return this;
//    }
//
//    @Override
//    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws RADIllegalArgumentException
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
//                case "byctclientdb":
//                    filterByCTClientDB(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                default:
//                    throw new RADIllegalArgumentException("filter{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//    }
//
//    @Override
//    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws RADIllegalArgumentException
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
//                        throw new RADIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//    private CTClientQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private CTClientQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}
