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
//import ois.radius.ca.enums.Channel;
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//import org.vn.radius.cc.server.entities.EN;
//import org.vn.radius.cc.server.exceptions.RADIllegalArgumentException;
//
///**
// *
// * @author rumana.begum
// * @since 31 May, 2023
// */
//public class LicenseQuery extends EntityQuery
//{
//
//    public LicenseQuery()
//    {
//        super(EN.License);
//    }
//
//    public LicenseQuery filterByCTClientId(Long ctclientid)
//    {
//        AppendWhere("And License.CTClient.Id=:ctclient");
//        _params.put("ctclient", ctclientid);
//
//        return this;
//    }
//
//    public LicenseQuery fiterByChannel(Channel chn)
//    {
//        AppendWhere("And License.Channels.containsKey(:chn)");
//        _params.put("chn", chn);
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
//                case "byctclient":
//                    filterByCTClientId(Long.valueOf(filters.get(name).get(0)));
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
//                 Boolean isAsc = hm.get(name);
//                switch (name.toLowerCase())
//                {
//                    case "id":
//                        orderById(hm.get(name));
//                        break;
//                    case "createdon":
//                        orderByCreatedOn(isAsc);
//                        break;
//                    default:
//                        throw new RADIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//
//                }
//            }
//        }
//    }
//}
