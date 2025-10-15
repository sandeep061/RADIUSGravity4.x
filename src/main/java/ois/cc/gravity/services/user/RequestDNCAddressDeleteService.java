//package ois.cc.gravity.services.user;
//
//import code.ua.events.Event;
//import CrsCde.CODE.Common.Classes.NameValuePair;
//import code.db.jpa.ENActionList;
//import code.ua.events.EventEntityDeleted;
//import code.ua.requests.Request;
//import ois.cc.gravity.db.MySQLDB;
//import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
//import ois.cc.gravity.services.ARequestEntityService;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//
//
//import java.util.ArrayList;
//
//public class RequestDNCAddressDeleteService extends ARequestEntityService
//{
//
//    public RequestDNCAddressDeleteService(UAClient uac)
//    {
//        super(uac);
//    }
//
//    @Override
//    protected Event DoProcessEntityRequest(Request request) throws Throwable
//    {
//        RequestEntityDelete req = (RequestEntityDelete) request;
//
//        MySQLDB db = _tctx.getDB();
//
//        /**
//         * Following points will performed on delete DNCAddress. <br>
//         *
//         * -Update associate contact address to non dnc. <br>
//         * -Update the contact list. <br>
//         */
//        DNCAddress dncAddr = db.FindAssert(EN.DNCAddress.getEntityClass(), req.getEntityId());
//
//        DNCList list = dncAddr.getDNCList();
//        list.setNoOfRecs(list.getNoOfRecs() == 0 ? 0 //checking for null and negavite value.
//                : list.getNoOfRecs() - 1);
//
//        ArrayList<NameValuePair> entities = new ArrayList<>();
//        entities.add(new NameValuePair(ENActionList.Action.Update.name(), list));
//        entities.add(new NameValuePair(ENActionList.Action.Delete.name(), dncAddr));
//
//        db.Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
//
//        EventEntityDeleted ev = new EventEntityDeleted(req,dncAddr.getId().toString(),dncAddr.getClass().getName());
////        return new EventOK(req);
//        return  ev;
//    }
//
//}
//
