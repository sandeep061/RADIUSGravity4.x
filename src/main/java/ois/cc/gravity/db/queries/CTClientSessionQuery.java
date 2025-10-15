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
//import org.vn.radius.cc.server.entities.sys.CTClient;
//import org.vn.radius.cc.server.exceptions.RADIllegalArgumentException;
//
///**
// *
// * @author manoj
// * @since Apr 25, 2018
// */
//public class CTClientSessionQuery extends EntityQuery
//{
//
//    public CTClientSessionQuery()
//    {
//        super(EN.CTClientSession);
//    }
//
//    public CTClientSessionQuery filterByCTClientId(Long Id)
//    {
//        AppendWhere("And CTClientSession.CTClient.Id=:Id");
//        _params.put("Id", Id);
//
//        return this;
//    }
//
//    public CTClientSessionQuery openSession(CTClient client)
//    {
//        AppendWhere("CTClientSession.CTClient.Id=:clientid "
//                + "And CTClientSession.StopAt IS NULL");
//
//        _params.put("clientid", client.getId());
//        _params.put("Date", null);
//
//        AppendOrderby("CTClientSession.Id DESC");
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
//                case "byid":
//                    filterById(Long.valueOf(filters.get(name).get(0)));
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
//                    default:
//                        throw new RADIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//}
