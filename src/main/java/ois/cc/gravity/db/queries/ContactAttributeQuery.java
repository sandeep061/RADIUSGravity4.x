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
//import ois.radius.gravity.entities.tenant.ContactAttributeType;
//import ois.radius.gravity.service.exception.GravityIllegalArgumentException;
//
///**
// *
// * @author Deepak
// */
//public class ContactAttributeQuery extends EntityQuery
//{
//
//    public ContactAttributeQuery()
//    {
//        super(EN.ContactAttribute);
//    }
//
//    public ContactAttributeQuery filterByAops(Long id)
//    {
//        AppendWhere("And ContactAttribute.Campaign.Id=:id");
//        _params.put("id", id);
//        return this;
//    }
//
//    public ContactAttributeQuery filterByType(ContactAttributeType type)
//    {
//        AppendWhere("And ContactAttribute.Type=:type");
//        _params.put("type", type);
//        return this;
//    }
//
//    public ContactAttributeQuery filterByCode(String code)
//    {
//        AppendWhere("And ContactAttribute.Code =: code");
//        _params.put("code", code);
//        return this;
//    }
//    
//    public ContactAttributeQuery filterByAttributeType(ContactAttributeType type)
//    {
//        AppendWhere("And ContactAttribute.Type =:type");
//        _params.put("type", type);
//        return this;
//    }
//
//    public ContactAttributeQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(ContactAttribute.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
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
//                case "bycampaign":
//                    filterByAops(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycode":
//                    filterByCode(String.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(String.valueOf(filters.get(name).get(0)).toLowerCase());
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
//    private ContactAttributeQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private ContactAttributeQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}
