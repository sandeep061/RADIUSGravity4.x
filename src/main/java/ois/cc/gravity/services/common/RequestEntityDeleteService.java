package ois.cc.gravity.services.common;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.entities.AEntity;
import code.entities.AEntity_ad;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.events.EventInvalidEntity;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.AEntity_cces;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;

public class RequestEntityDeleteService extends ARequestEntityService
{

    public RequestEntityDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected final Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete reqDelete = (RequestEntityDelete) request;
        EN en = reqDelete.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqDelete.getEntityName().name());
            return ev;
        }

        //Find entity by Id supplied.
        AEntity entity = (AEntity) _tctx.getDB().Find(en.getEntityClass(), reqDelete.getEntityId());

        if (entity == null)
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, reqDelete.getEntityName().name());
            ev.setCondition("Id", OPRelational.Eq, String.valueOf(reqDelete.getEntityId()));
            return ev;
        }

        doPreProcessDelete(reqDelete, entity);

//        if (entity instanceof AEntity_ad)
//        {
//            AEntity_ad entityad = (AEntity_ad) entity;
//            _tctx.getDB().DeleteUpdate(_uac.getUserSession().getUser(), entity);
//        }
//        else
//        {
//            AEntity_cces entityes = (AEntity_cces) entity;
            _tctx.getDB().DeleteEntity(_uac.getUserSession().getUser(), entity);
//        }

        EventSuccess evs = new EventSuccess(request);
        return evs;

    }

    /**
     * Facilitate any required pre-delete processing by specific(derived) entity add processor.
     *
     * @param req
     * @param entity
     * @throws Throwable
     */
    protected void doPreProcessDelete(RequestEntityDelete req, AEntity entity) throws Throwable
    {
        //do nothing. to be imple by derived class.
        //not abstract becaue we want this generic RequestEntityDelete class to be non-abstract class.
    }

}
