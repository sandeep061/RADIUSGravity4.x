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
//import ois.radius.cc.entities.EN;
//import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
//import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
//public class BlockListQuery extends EntityQuery
//{
//
//    public BlockListQuery()
//    {
//        super(EN.BlockList);
//    }
//
//    public BlockListQuery filterByCode(String code)
//    {
//        AppendWhere("And BlockList.Code =: code");
//        _params.put("code", code);
//
//        return this;
//    }
//
//    public BlockListQuery filterByName(String name)
//    {
//        AppendWhere("And BlockList.Name =: name");
//        _params.put("name", name);
//
//        return this;
//    }
//
//    public BlockListQuery filterByNameLike(String name)
//    {
//        AppendWhere("And Lower(BlockList.Name) Like : name ");
//        _params.put("name", "%" + name + "%");
//
//        return this;
//    }
//
//    public BlockListQuery filterByaops(Long campid)
//    {
//        AppendWhere("And element(BlockList.AOPs).Id = : id");
//        _params.put("id", campid);
//
//        return this;
//    }
//    public BlockListQuery filterByAopsCode(String code)
//    {
//        AppendWhere("And element(BlockList.AOPs).Code = : code");
//        _params.put("code", code);
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
//                case "byaops":
//                    filterByaops(Long.valueOf(filters.get(name).get(0)));
//                    break;
//                case "byname":
//                    filterByName(filters.get(name).get(0));
//                    break;
//                case "bynamelike":
//                    filterByNameLike(filters.get(name).get(0).toLowerCase());
//                    break;
//                case "bycode":
//                    filterByCode(filters.get(name).get(0));
//                    break;
//                case "byaopscode":
//                    filterByAopsCode(filters.get(name).get(0));
//                    break;
//                default:
//                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
//            }
//        }
//
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
//                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
//                }
//            }
//        }
//    }
//
//    private BlockListQuery orderByCode(Boolean get)
//    {
//        setOrederBy("Code", get);
//        return this;
//    }
//
//    private BlockListQuery orderByName(Boolean get)
//    {
//        setOrederBy("Name", get);
//        return this;
//    }
//
//}
