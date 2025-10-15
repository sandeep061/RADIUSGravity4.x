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
//
//import ois.radius.gravity.entites.EN;
//import ois.radius.gravity.service.exception.GravityIllegalArgumentException;
//
//
///**
// *
// * @author Deepak
// */
//public class ContactListQuery extends EntityQuery
//{
//
//    public ContactListQuery()
//    {
//        super(EN.ContactList);
//    }
//
//    public ContactListQuery filterByCode(String code)
//    {
//        AppendWhere("And ContactList.Code=:code");
//        _params.put("code", code);
//
//        return this;
//    }
//
//    public ContactListQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(ContactList.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
//
//        return this;
//    }
//     public ContactListQuery filterByName(String name)
//    {
//        AppendWhere("And ContactList.Name =: name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public ContactListQuery filterByAops(Long campId)
//    {
//        AppendWhere("And ContactList.Campaign.Id=:campid");
//        _params.put("campid", campId);
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
//                case "bycampaign":
//                    filterByAops(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycode":
//                    filterByCode(String.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(String.valueOf(filters.get(name).get(0)).toLowerCase());
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
//                    break;
//                default:
//                    throw new GravityIllegalArgumentException("filter{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//    }
//
//    private ContactListQuery orderByCampaign(Boolean isasc)
//    {
//        setOrederBy("Campaign.Id", isasc);
//        return this;
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
//                    case "campaign":
//                        orderByCampaign(isAsc);
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
//
//    private ContactListQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private ContactListQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}