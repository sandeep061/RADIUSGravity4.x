package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsPropertiesQuery;
import ois.cc.gravity.db.queries.XPlatformUAQuery;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.requests.xs.RequestXPlatformDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.AEntity_cces;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsProperties;
import ois.radius.cc.entities.tenant.cc.XPlatform;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

import java.util.ArrayList;

public class RequestXPlatformDeleteService extends ARequestEntityService
{

    public RequestXPlatformDeleteService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestXPlatformDelete reqdel = (RequestXPlatformDelete) request;

        if (!reqdel.getForceDelete())
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.EntityNotDeletedYet, "Mapped " + EN.XPlatformUA.name() + " Exists");
        }
        XPlatformUAQuery query = new XPlatformUAQuery().filterByXplatform(reqdel.getEntityId());

        ArrayList<XPlatformUA> xPlatformUA = _tctx.getDB().Select(query);
        for (XPlatformUA ua : xPlatformUA)
        {
            deleteUAAiDisposition(ua);
        }
        entities.add(new NameValuePair(ENActionList.Action.Delete.name(), xPlatformUA.toArray(new AEntity_cces[0])));

        XPlatform platform = _tctx.getDB().FindAssert(EN.XPlatform.getEntityClass(), reqdel.getEntityId());
        deletePlatformAiDisposition(platform);
        entities.add(new NameValuePair(ENActionList.Action.Delete.name(), platform));

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
        return new EventEntityDeleted(reqdel, platform);
    }

    private void deleteUAAiDisposition(XPlatformUA ua) throws CODEException, GravityException, Exception
    {

        ArrayList<AOPsProperties> props = _tctx.getDB().Select(new AOPsPropertiesQuery().filterByConfKey(AOPsProperties.Keys.Global_AIDisposeXPlatformUA).filterByConfValue(String.valueOf(ua.getId())));

        for (AOPsProperties aoPsProperties : props)
        {
            AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aoPsProperties.getAOPs());
            entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aoPsProperties));
        }
    }

    private void deletePlatformAiDisposition(XPlatform xPlatform) throws CODEException, GravityException, Exception
    {

        ArrayList<AOPsProperties> props = _tctx.getDB().Select(new AOPsPropertiesQuery().filterByConfKey(AOPsProperties.Keys.Global_AIDisposeXPlatform).filterByConfValue(String.valueOf(xPlatform.getId())));

        for (AOPsProperties aoPsProperties : props)
        {
            AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aoPsProperties.getAOPs());
            entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aoPsProperties));
        }
    }
}
