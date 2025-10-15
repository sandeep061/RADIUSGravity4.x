//package ois.cc.gravity.services.aops;
//import code.entities.AEntity;
//import code.ua.events.Event;
//import code.ua.events.EventEntityDeleted;
//import code.ua.requests.Request;
//import ois.cc.gravity.services.ARequestEntityService;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.radius.cc.entities.tenant.cc.DNCAddress;
//import ois.radius.cc.entities.tenant.cc.DNCList;
//import ois.cc.gravity.db.queries.DNCAddressQuery;
//import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
//import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
//import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
//
//import java.util.ArrayList;
//
//public class RequestDNCListDeleteService extends ARequestEntityService
//{
//
//    public RequestDNCListDeleteService(UAClient uac)
//    {
//        super(uac);
//    }
//
//    @Override
//    protected Event DoProcessEntityRequest(Request request) throws Throwable
//    {
//        RequestEntityDelete req = (RequestEntityDelete) request;
//
//        DNCList dnclist = _tctx.getDB().FindAssert(EN.DNCList.getEntityClass(), req.getEntityId());
//
//        //All campaign must be unmapped before deleting the DNCList.
//        if (dnclist.getAOPs() != null && !dnclist.getAOPs().isEmpty())
//        {
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.Delete_NotAllowed_MappedEntity_Still_Exist, "DNCList.Campaigns");
//        }
//
//        DNCAddressQuery qry = new DNCAddressQuery().filterByDNCList(dnclist.getId());
//        ArrayList<DNCAddress> dncAddrList = _tctx.getDB().Select(qry);
//
//        ArrayList<AEntity> entities = new ArrayList<>();
//        entities.add(dnclist);
//        entities.addAll(dncAddrList);
//
//        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(),entities);
//
//        return  new EventEntityDeleted(req,dnclist.getId().toString(),EN.DNCList.name());
//    }
//
//}
