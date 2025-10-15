package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserMedia;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestService;

import java.util.ArrayList;

public class RequestUserMediaDeleteService extends ARequestEntityService
{

    public RequestUserMediaDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        RequestEntityDelete reqDelete = (RequestEntityDelete) request;

        UserMedia agmd = _tctx.getDB().FindAssert(EN.UserMedia.getEntityClass(), reqDelete.getEntityId());

        ArrayList<NameValuePair> entities = new ArrayList<>();
        entities.add(new NameValuePair<>("Delete", agmd));

        if (!agmd.getAgentMediaMaps().isEmpty())
        {
            entities.add(new NameValuePair<>("Delete", agmd.getAgentMediaMaps().toArray(new AEntity[0])));
        }

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        EventEntityDeleted ev = new EventEntityDeleted(reqDelete,agmd.getId().toString(),EN.UserMedia.name());
        return ev;
    }

}

