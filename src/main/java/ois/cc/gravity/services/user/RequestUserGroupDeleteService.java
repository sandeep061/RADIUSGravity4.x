package ois.cc.gravity.services.user;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.UserGroup;
import ois.radius.cc.entities.tenant.cc.UserGroupAops;
import ois.radius.cc.entities.tenant.cc.UserGroupUser;

public class RequestUserGroupDeleteService extends ARequestEntityService
{

    public RequestUserGroupDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;

        UserGroup agGrp = _tctx.getDB().FindAssert(EN.UserGroup.getEntityClass(), req.getEntityId());

        /**
         * All agents and Campaigns must be unmapped before delete AgentGroup.
         */
        validateAops(agGrp.getId());
        validateUser(agGrp.getId());
        _tctx.getDB().DeleteEntity(_uac.getUserSession().getUser(), agGrp);

        EventEntityDeleted ev = new EventEntityDeleted(req, agGrp.getId().toString(), EN.UserGroup.name());

        return ev;
    }

    private void validateAops(Long id) throws GravityException, CODEException, Exception
    {
        JPAQuery query = new JPAQuery("SELECT uga FROM UserGroupAops uga WHERE uga.UserGroup.Id=:id");
        query.setParam("id", id);
        UserGroupAops ugaops = _tctx.getDB().Find(EN.UserGroupAops.getEntityClass(), query);
        if (ugaops != null)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.Delete_NotAllowed_MappedEntity_Still_Exist, ugaops.getAOPs().toString());
        }
    }

    private void validateUser(Long id) throws GravityException, CODEException, Exception
    {
        JPAQuery query = new JPAQuery("SELECT uga FROM UserGroupUser uga WHERE uga.UserGroup.Id=:id");
        query.setParam("id", id);
        UserGroupUser ugaops = _tctx.getDB().Find(EN.UserGroupUser.getEntityClass(), query);
        if (ugaops != null)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.Delete_NotAllowed_MappedEntity_Still_Exist, ugaops.getUser().toString());
        }
    }
}
