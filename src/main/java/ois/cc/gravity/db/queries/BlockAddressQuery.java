///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
// package ois.cc.gravity.db.queries;
//
//import code.ua.events.EventFailedCause;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
//import ois.radius.ca.enums.Channel;
//import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
//
//import ois.radius.cc.entities.EN;
//
//
///**
// *
// * @author Toshalin.Dhal
// * @since Sep 9, 2022
// */
//public class BlockAddressQuery extends EntityQuery
//{
//
//    public BlockAddressQuery()
//    {
//        super(EN.BlockAddress);
//    }
//
//    public BlockAddressQuery filterByBlockList(Long id)
//    {
//        AppendWhere("And BlockAddress.BlockList.Id =: id");
//        _params.put("id", id);
//
//        return this;
//    }
//
//    public BlockAddressQuery filterByAddress(String addr)
//    {
//        AppendWhere("And BlockAddress.Address =: addr");
//        _params.put("addr", addr);
//
//        return this;
//    }
//
//    public BlockAddressQuery filterByAddressLike(String addrs)
//    {
//        AppendWhere("And Lower(BlockAddress.Address) Like : addrs");
//        _params.put("addrs", "%" + addrs + "%");
//
//        return this;
//    }
//
//    public BlockAddressQuery filterByChannel(Channel chnl)
//    {
//        AppendWhere("And BlockAddress.Channel =: chnl");
//        _params.put("chnl", chnl);
//
//        return this;
//    }
//
//    @Override
//	public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
//    {
//        for (String name : filters.keySet())
//        {
//            switch (name.toLowerCase())
//            {
//
//                case "byid":
//                    filterById(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byblocklist":
//                    filterByBlockList(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byaddress":
//                    filterByAddress(filters.get(name).get(0));
//                    break;
//                case "byaddresslike":
//                    filterByAddressLike(filters.get(name).get(0).toLowerCase());
//                    break;
//                case "bychannel":
//                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
//                    break;
//
//            }
//
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
//                    default:
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
//                }
//
//            }
//        }
//    }
//}
