///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
// package ois.radius.gravity.db.queries;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.vn.radius.cc.platform.events.common.EvCauseRequestValidationFail;
//import org.vn.radius.cc.platform.exceptions.RADException;
//import org.vn.radius.cc.server.entities.EN;
//
//
//
///**
// *
// * @author rumana.begum
// * @since 30 Jan, 2024
// */
//public class AppConfigQuery extends EntityQuery
//{
//
//    public AppConfigQuery()
//    {
//        super(EN.AppConfig);
//    }
//
//    public AppConfigQuery filterByIdx0(String idx0)
//    {
//        AppendWhere("And AppConfig.Idx0=:idx0");
//        _params.put("idx0", idx0);
//
//        return this;
//    }
//
//    public AppConfigQuery filterByIdx1(String idx1)
//    {
//        AppendWhere("And AppConfig.Idx1=:idx1");
//        _params.put("idx1", idx1);
//
//        return this;
//    }
//
//    public AppConfigQuery filterByIdx2(String idx2)
//    {
//        AppendWhere("And AppConfig.Idx2=:idx2");
//        _params.put("idx2", idx2);
//
//        return this;
//    }
//
//    public AppConfigQuery filterByIdx3(String idx3)
//    {
//        AppendWhere("And AppConfig.Idx3=:idx3");
//        _params.put("idx3", idx3);
//
//        return this;
//    }
//
//    public AppConfigQuery filterByApplication(Long appid)
//    {
//        AppendWhere("And AppConfig.Application.Id=:id");
//        _params.put("id", appid);
//
//        return this;
//    }
//
//    public AppConfigQuery filterByConfigKey(String configkey)
//    {
//        AppendWhere("And AppConfig.ConfigKey=:configkey");
//        _params.put("configkey", configkey);
//
//        return this;
//    }
//
//    @Override
//    protected void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
//    {
//        for (String name : filters.keySet())
//        {
//            switch (name.toLowerCase())
//            {
//                case "byid":
//                    filterById(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byidx0":
//                    filterByIdx0(filters.get(name).get(0));
//                    break;
//                case "byidx1":
//                    filterByIdx1(filters.get(name).get(0));
//                    break;
//                case "byidx2":
//                    filterByIdx2(filters.get(name).get(0));
//                    break;
//                case "byidx3":
//                    filterByIdx3(filters.get(name).get(0));
//                    break;
//                case "byapplication":
//                    filterByApplication(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byconfigkey":
//                    filterByConfigKey(filters.get(name).get(0));
//                    break;
//                default:
//                    throw new RADIllegalArgumentException("filter{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//    }
//
//    @Override
//    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws RADException
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
//                    case "idx0":
//                        orderByIdx0(hm.get(name));
//                        break;
//                    case "idx1":
//                        orderByIdx1(hm.get(name));
//                        break;
//                    case "idx2":
//                        orderByIdx2(hm.get(name));
//                        break;
//                    case "idx3":
//                        orderByIdx3(hm.get(name));
//                        break;
//                    case "configkey":
//                        orderByConfigKey(hm.get(name));
//                        break;
//                    default:
//                        throw new RADIllegalArgumentException("orderby{" + name + "}", EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//    private AppConfigQuery orderByIdx0(Boolean get)
//    {
//        setOrederBy("Idx0", get);
//        return this;
//    }
//
//    private AppConfigQuery orderByIdx1(Boolean get)
//    {
//        setOrederBy("Idx1", get);
//        return this;
//    }
//
//    private AppConfigQuery orderByIdx2(Boolean get)
//    {
//        setOrederBy("Idx2", get);
//        return this;
//    }
//
//    private AppConfigQuery orderByIdx3(Boolean get)
//    {
//        setOrederBy("Idx3", get);
//        return this;
//    }
//
//    private AppConfigQuery orderByConfigKey(Boolean get)
//    {
//        setOrederBy("ConfigKey", get);
//        return this;
//    }
//
//}
