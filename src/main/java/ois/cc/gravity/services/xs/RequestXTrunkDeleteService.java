package ois.cc.gravity.services.xs;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsMediaTrunkQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsMediaTrunk;
import ois.radius.cc.entities.tenant.cc.XTrunk;

import java.util.ArrayList;

public class RequestXTrunkDeleteService extends ARequestEntityService
{
    
    public RequestXTrunkDeleteService(UAClient uac)
    {
        super(uac);
    }
    
    ArrayList<AEntity> entities = new ArrayList<>();
    
    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;
        
        XTrunk trunk = _tctx.getDB().FindAssert(EN.XTrunk.getEntityClass(), req.getEntityId());
        getAllAOPsMediaTrunkByXTrunk(trunk.getId());
        entities.add(trunk);
        
        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), entities);
        
        EventEntityDeleted event = new EventEntityDeleted(req, trunk);
        return event;
    }
    
    private void getAllAOPsMediaTrunkByXTrunk(long id) throws CODEException, GravityException
    {
        ArrayList<AOPsMediaTrunk> listmediatrunks = _tctx.getDB().Select(new AOPsMediaTrunkQuery().filterByXTrunk(id));
        entities.addAll(listmediatrunks);
    }
}
