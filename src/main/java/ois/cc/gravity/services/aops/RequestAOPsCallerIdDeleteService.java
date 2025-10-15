package ois.cc.gravity.services.aops;


import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCallerIdAddressQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCallerId;
import ois.radius.cc.entities.tenant.cc.AOPsCallerIdAddress;

import java.util.ArrayList;

public class RequestAOPsCallerIdDeleteService extends ARequestEntityService {

    public RequestAOPsCallerIdDeleteService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityDelete req = (RequestEntityDelete) request;
        ArrayList<AEntity> entities = new ArrayList<>();
        AOPsCallerId aopsCallerid = _tctx.getDB().FindAssert(EN.AOPsCallerId.getEntityClass(), req.getEntityId());
        ArrayList<AOPsCallerIdAddress> aladdress = _tctx.getDB().Select(new AOPsCallerIdAddressQuery().filterByAOPsCallerId(aopsCallerid.getId()));
        entities.add(aopsCallerid);
        aladdress.forEach(entity->entities.add(entity));
         _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), entities);
        EventEntityDeleted ev = new EventEntityDeleted(req, req.getEntityId().toString(), req.getEntityName().name());
        return ev;

    }
}
