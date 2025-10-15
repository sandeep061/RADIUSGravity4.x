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
//import org.vn.radius.cc.server.exceptions.RADIllegalArgumentException;
//
///**
// *
// * @author Manoj
// * @since 10 Jun, 2020
// */
//public class TextTemplateQuery extends EntityQuery
//{
//
//    public TextTemplateQuery()
//    {
//        super(EN.TextTemplate);
//    }
//
//    public TextTemplateQuery filterByCode(String code)
//    {
//        AppendWhere("And TextTemplate.Code=:code");
//        _params.put("code", code);
//
//        return this;
//    }
//
//    public TextTemplateQuery filterByName(String name)
//    {
//        AppendWhere("And TextTemplate.Name=:code");
//        _params.put("code", name);
//
//        return this;
//    }
//
//    public TextTemplateQuery filterByAops(Long campid)
//    {
//        AppendWhere("And TextTemplate.Campaign.Id=:campid");
//        _params.put("campid", campid);
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
//                case "bycampaign":
//                    filterByAops(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "bycode":
//                    filterByCode(filters.get(name).get(0));
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
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
//}
