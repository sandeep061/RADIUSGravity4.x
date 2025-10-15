///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package ois.radius.gravity.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//import org.vn.radius.cc.server.entities.EN;
//
//import ois.radius.gravity.entities.tenant.Campaign;
//import ois.radius.gravity.entities.tenant.Channel;
//import ois.radius.gravity.entities.util.EntitiesUtil;
//import ois.radius.gravity.service.exception.GravityIllegalArgumentException;
//
//
//
///**
// *
// * @author Deepak
// */
//public class ContactAddressQuery extends EntityQuery
//{
//
//    String _tblName;
//
//    public ContactAddressQuery(Campaign camp)
//    {
//        super(EN.ContactAddress);
//        _tblName = EntitiesUtil.getEntityTableName(EN.ContactAddress, camp);
//    }
//
//    public ContactAddressQuery filterByAddress(String addr)
//    {
//        AppendWhere("And " + _tblName + ".Address =: addr");
//        _params.put("addr", addr);
//        return this;
//    }
//
//    /**
//     * This is used for internal server use only. it should not applied as filter in fetch request.
//     *
//     * @param chn
//     * @return
//     */
//    public ContactAddressQuery filterByChannel(Channel chn)
//    {
//        AppendWhere("And " + _tblName + ".Channel =: chn");
//        _params.put("chn", chn);
//        return this;
//    }
//
//    public ContactAddressQuery filterByContact(Long conid)
//    {
//        AppendWhere("And " + _tblName + ".Contact.Id =: conid");
//        _params.put("conid", conid);
//        return this;
//    }
//
//    //Note:till now this is for internal uses.
//    public ContactAddressQuery filterByAops(Long campid)
//    {
//        AppendWhere("And " + _tblName + ".Contact.ContactList.Campaign.Id =: campid");
//        _params.put("campid", campid);
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
//                    filterByAddress(String.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycontact":
//                    filterByContact(Long.valueOf(filters.get(name).get(0)));
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
//                        orderById(isAsc);
//                        break;
//                    default:
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//    @Override
//    protected String getTableName()
//    {
//        return _tblName;
//    }
//
//}
