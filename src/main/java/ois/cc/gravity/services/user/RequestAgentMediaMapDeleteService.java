package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.UserMedia;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;

import java.util.ArrayList;

public class RequestAgentMediaMapDeleteService extends ARequestEntityService
{

    public RequestAgentMediaMapDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete reqDel = (RequestEntityDelete) request;

        AgentMediaMap agmdmap = _tctx.getDB().FindAssert(EN.AgentMediaMap.getEntityClass(), reqDel.getEntityId());

        ArrayList<NameValuePair> entities = new ArrayList<>();
        entities.add(new NameValuePair<>("Delete", agmdmap));

        JPAQuery qry = new JPAQuery("Select md from UserMedia md where element(AgentMediaMaps).Id =: id");
        qry.setParam("id", agmdmap.getId());

        UserMedia agmd = _tctx.getDB().Find(EN.UserMedia, qry);

         if(agmd!=null)
         {

             if (agmd.getAutoRegAgentMedia() != null && agmd.getAutoRegAgentMedia().equals(agmdmap))
             {
                 agmd.setAutoRegister(Boolean.FALSE);
                 agmd.setAutoRegAgentMedia(null);
             }

             agmd.getAgentMediaMaps().remove(agmdmap);
             entities.add(new NameValuePair("Update", agmd));
         }
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(),entities);

        EventEntityDeleted ev = new EventEntityDeleted(request,agmdmap.getId().toString(),agmdmap.getEN().name());
        return ev;
    }

}
