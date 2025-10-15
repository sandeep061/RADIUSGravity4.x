///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package ois.radius.gravity.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import ois.radius.gravity.entites.EN;
//import ois.radius.gravity.entities.tenant.Campaign;
//import ois.radius.gravity.entities.tenant.Contact;
//import ois.radius.gravity.entities.util.EntitiesUtil;
//import ois.radius.gravity.service.exception.GravityException;
//
//
///**
// *
// * @author Deepak
// */
//public class ContactRecordQuery extends EntityQuery
//{
//    String _tblName;
//
//    public ContactRecordQuery(Campaign camp)
//    {
//        super(EN.ContactRecord);
//        _tblName = EntitiesUtil.getEntityTableName(EN.ContactRecord, camp);
//    }
//    
//    public ContactRecordQuery filterByContact(Contact cs)
//    {
//        AppendWhere("And " + _tblName + ".Contact.Id=:id");
//        _params.put("id", cs.getId());
//
//        return this;
//    }
//
//    @Override
//    protected void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
//    {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
//
//    @Override
//    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityException
//    {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
//    
//    
//    
//    @Override
//    protected String getTableName()
//    {
//        return _tblName;
//    }
//}
