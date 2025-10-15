package ois.cc.gravity.services.si.aopscsat;

import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCSATConf;

import java.util.ArrayList;
import ois.cc.gravity.db.queries.XAlertIDQuery;
import ois.radius.cc.entities.tenant.cc.XAlertID;

public class RequestAOPsCSATConfDeleteService extends ARequestEntityService
{

    public RequestAOPsCSATConfDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;
        ArrayList<AEntity> deleteEntities = new ArrayList<>();

        //delete entity
        AOPsCSATConf aopsCSATConf = _tctx.getDB().FindAssert(EN.AOPsCSATConf.getEntityClass(), req.getEntityId());
        ArrayList<XAlertID> xAlertID = _tctx.getDB().Select(new XAlertIDQuery().filterByAOPsCSATConf(aopsCSATConf.getId()));
        deleteEntities.add(aopsCSATConf);
        deleteEntities.addAll(xAlertID);

        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), deleteEntities);

        return new EventEntityDeleted(req, aopsCSATConf);
    }

}
