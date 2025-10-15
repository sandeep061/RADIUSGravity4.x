///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package org.vn.radius.cc.server.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//import org.vn.radius.cc.server.entities.EN;
//import org.vn.radius.cc.server.exceptions.RADIllegalArgumentException;
//
///**
// *
// * @author Sandeepkumar.Sahoo
// * @since 30 Jan, 2024
// */
//public class TenantPropertiesQuery extends EntityQuery
//{
//
//    public TenantPropertiesQuery()
//    {
//        super(EN.TenantProperties);
//    }
//
//    public TenantPropertiesQuery filterByTenant(Long ctclientId)
//    {
//        AppendWhere("And TenantProperties.Tenant.Id =:ctclientId");
//        _params.put("ctclientId", ctclientId);
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
//                case "bytenant":
//                    filterByTenant(Long.valueOf(filters.get(name).get(0)));
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
//                    default:
//                        throw new RADIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//}
//
//
//
//
