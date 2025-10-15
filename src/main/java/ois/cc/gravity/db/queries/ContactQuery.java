///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package ois.radius.core.gravity.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import ois.radius.cc.entities.EN;
//import ois.radius.cc.entities.tenant.cc.Campaign;
//import ois.radius.core.gravity.entities.util.EntitiesUtil;
//import ois.radius.core.gravity.framework.events.common.EvCauseRequestValidationFail;
//import ois.radius.core.gravity.services.exceptions.GravityIllegalArgumentException;
//import ois.radius.ca.enums.ContactState;
//
//
///**
// *
// * @author Deepak
// */
//public class ContactQuery extends EntityQuery
//{
//
//    String _tblName;
//
//    public ContactQuery(Campaign camp)
//    {
//        super(EN.Contact);
//        _tblName = EntitiesUtil.getEntityTableName(EN.Contact, camp);
//
//    }
//
//    public ContactQuery filterByContactState(ContactState cs)
//    {
//        AppendWhere("And " + _tblName + ".ContactState=:cs");
//        _params.put("cs", cs);
//
//        return this;
//    }
//
//    public ContactQuery filterByContactList(Long conlistid)
//    {
//        AppendWhere("And " + _tblName + ".ContactList.Id=:conlistid");
//        _params.put("conlistid", conlistid);
//
//        return this;
//    }
//
//    public ContactQuery filterByContactStates(ArrayList<ContactState> cs)
//    {
//        AppendWhere("And " + _tblName + ".ContactState in (: cs )");
//        _params.put("cs", cs);
//
//        return this;
//    }
//
//    public ContactQuery filterByContactAddress(String address)
//    {
//        AppendWhere("And " + ":address member of " + _tblName + ".ContactAddresses.Address");
//        _params.put("address", address);
//
//        return this;
//    }
//
//    public ContactQuery filterByActiveEntityState()
//    {
//        AppendWhere("And " + _tblName + ".ContactList.Deleted=: cles And " + _tblName + ".Deleted=:es");
//        _params.put("es", false);
//        _params.put("cles", false);
//
//        return this;
//    }
//
//    public ContactQuery filterByIsDNC(Boolean isdnc)
//    {
//        AppendWhere("And " + _tblName + ".IsDNC=:isdnc");
//        _params.put("isdnc", isdnc);
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
//                case "bycontactstate":
//                    filterByContactState(ContactState.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycontactstates":
//                    ArrayList<ContactState> alc = new ArrayList<>();
//                    filters.get(name).forEach((c) ->
//                    {
//                        alc.add(ContactState.valueOf(c));
//                    });
//                    filterByContactStates(alc);
//                    break;
//                case "byid":
//                    filterById(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycontactlist":
//                    filterByContactList(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycontactaddress":
//                    filterByContactAddress(filters.get(name).get(0));
//                    break;
//                default:
//                    throw new GravityIllegalArgumentException("filter{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//    }
//
//    public ContactQuery orderByCreatedOn(Boolean isasc)
//    {
//        setOrederBy("CreatedOn", isasc);
//        return this;
//    }
//
//    public ContactQuery orderByPriority(Boolean isasc)
//    {
//        setOrederBy("Priority", isasc);
//        return this;
//    }
//
//    public ContactQuery orderByDialSeq(Boolean isasc)
//    {
//        setOrederBy("DialSeq", isasc);
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
//                    case "createdon":
//                        orderByCreatedOn(isAsc);
//                        break;
//                    case "priority":
//                        orderByPriority(isAsc);
//                        break;
//                    case "dialseq":
//                        orderByDialSeq(isAsc);
//                        break;
//                    default:
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//
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
//}
