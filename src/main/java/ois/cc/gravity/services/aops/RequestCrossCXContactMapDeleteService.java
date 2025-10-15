package ois.cc.gravity.services.aops;

import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.CrossCXContactMapQuery;
import ois.cc.gravity.framework.requests.aops.RequestCrossCXContactMapDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.CrossCXContactMap;

public class RequestCrossCXContactMapDeleteService extends ARequestEntityService
{

    public RequestCrossCXContactMapDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestCrossCXContactMapDelete reqcrossDeleteReq = (RequestCrossCXContactMapDelete) request;
        String entityid = reqcrossDeleteReq.getUCXConMapId();
        CrossCXContactMap crosscx = _tctx.getDB().FindAssert(new CrossCXContactMapQuery().filterByUCXConMapId(entityid));
        //Delete Entity
        _tctx.getDB().DeleteEntity(_uac.getUserSession().getUser(), crosscx);
        EventEntityDeleted event = new EventEntityDeleted(reqcrossDeleteReq, crosscx);
        return event;
    }
}
