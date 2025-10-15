///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ois.radius.gravity.db.queries;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import ois.radius.ca.enums.Channel;
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//
//import ois.radius.cc.entities.EN;
//import static ois.radius.cc.entities.EN.DNCRequest;
//import ois.radius.gravity.service.exception.GravityIllegalArgumentException;
//
//
///**
// *
// * @author Manoj
// * @since 2 Apr, 2021
// */
//public class DNCRequestQuery extends EntityQuery
//{
//
//    public DNCRequestQuery()
//    {
//        super(EN.DNCRequest);
//    }
//
//    public DNCRequestQuery filterByAgent(Long agid)
//    {
//        AppendWhere("And DNCRequest.Agent.Id =: id");
//        _params.put("id", agid);
//
//        return this;
//    }
//
//    /**
//     * This for internal server use.
//     *
//     * @param conaddrid
//     * @return
//     */
//    public DNCRequestQuery filterByContactAddress(Long conaddrid)
//    {
//        AppendWhere("And DNCRequest.ContactAddressId =: id");
//        _params.put("id", conaddrid);
//
//        return this;
//    }
//
//    public DNCRequestQuery filterByAddress(String addr)
//    {
//        AppendWhere("And DNCRequest.Address =: addr");
//        _params.put("addr", addr);
//
//        return this;
//    }
//
//    public DNCRequestQuery filterByChannel(Channel chn)
//    {
//        AppendWhere("And DNCRequest.Channel =: chn");
//        _params.put("chn", chn);
//
//        return this;
//    }
//
//    public DNCRequestQuery filterByAops(Long campid)
//    {
//        AppendWhere("And DNCRequest.CampaignId =: campid");
//        _params.put("campid", campid);
//
//        return this;
//    }
//
//    public DNCRequestQuery filterByStatus(DNCRequest.Status status)
//    {
//        AppendWhere("And DNCRequest.Status =: status");
//        _params.put("status", status);
//
//        return this;
//    }
//
//    public DNCRequestQuery filterByReviewedBy(Long id)
//    {
//        AppendWhere("And DNCRequest.ReviewedBy.Id =: id");
//        _params.put("id", id);
//
//        return this;
//    }
//
//    public DNCRequestQuery filterByName(String name)
//    {
//        AppendWhere("And DNCRequest.Name=:name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public DNCRequestQuery filterByAddressLike(String addrs)
//    {
//        AppendWhere("And Lower(DNCRequest.Address) Like : addrs");
//        _params.put("addrs", "%" + addrs + "%");
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
//                case "byaddress":
//                    filterByAddress(filters.get(name).get(0));
//                    break;
//                case "bychannel":
//                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycampaign":
//                    filterByAops(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byagent":
//                    filterByAgent(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bystatus":
//                    filterByStatus(DNCRequest.Status.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byreviewedby":
//                    filterByReviewedBy(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
//                    break;
//                case "byaddresslike":
//                    filterByAddressLike(filters.get(name).get(0).toLowerCase());
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
//                Boolean isAsc = hm.get(name);
//                switch (name.toLowerCase())
//                {
//                    case "id":
//                        orderById(hm.get(name));
//                        break;
//                    case "createdon":
//                        orderByCreatedOn(isAsc);
//                        break;
//                    default:
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//}
