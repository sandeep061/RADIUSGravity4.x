package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCDNAddressQuery;
import ois.cc.gravity.db.queries.AOPsCallerIdAddressQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCDN;
import ois.radius.cc.entities.tenant.cc.AOPsCDNAddress;

import java.util.ArrayList;

public class RequestAOPsCDNDeleteService extends ARequestEntityService
{

    public RequestAOPsCDNDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;
        ArrayList<AEntity> entities=new ArrayList<>();
        AOPsCDN aopsCdn = _tctx.getDB().FindAssert(EN.AOPsCDN.getEntityClass(), req.getEntityId());
        ArrayList<AOPsCDNAddress>alcdnadds= _tctx.getDB().Select(new AOPsCDNAddressQuery().filterByAOPsCDN(aopsCdn.getId()));
         alcdnadds.forEach(entity->entities.add(entity));
         entities.add(aopsCdn);
        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), entities);
        EventEntityDeleted ev = new EventEntityDeleted(req, req.getEntityId().toString(), req.getEntityName().name());
        return ev;
    }
}
