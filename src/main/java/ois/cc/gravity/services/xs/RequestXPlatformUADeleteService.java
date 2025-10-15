package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.db.jpa.JPAQuery;
import code.entities.EntityState;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCDNAddressQuery;
import ois.cc.gravity.db.queries.AOPsCallerIdAddressQuery;
import ois.cc.gravity.db.queries.AOPsPropertiesQuery;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;

import java.util.ArrayList;

public class RequestXPlatformUADeleteService extends ARequestEntityService
{

    public RequestXPlatformUADeleteService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete reqdel = (RequestEntityDelete) request;

        XPlatformUA platformua = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), reqdel.getEntityId());

        validateDefXPlatformUAWithCallerIDAddress(platformua);
        validateDefXPlatformUAWithCDNAddress(platformua);
        deleteAiDisposition(platformua);

        entities.add(new NameValuePair(ENActionList.Action.Delete.name(), platformua));
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        return new EventEntityDeleted(reqdel, platformua);
    }

    private void validateDefXPlatformUAWithCallerIDAddress(XPlatformUA xplatformua) throws CODEException, GravityException
    {
        AOPsCallerIdAddressQuery query = new AOPsCallerIdAddressQuery();
        query.filterByXplatformua(xplatformua.getId());
        AOPsCallerIdAddress dbaopclid = _tctx.getDB().Find(query);
        if (dbaopclid != null)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.EntityNotDeletedYet, "Mapped " + EN.XPlatformUA.name() + " Exists in " + EN.AOPsCallerId.name());
        }
    }

    private void validateDefXPlatformUAWithCDNAddress(XPlatformUA xplatformua) throws CODEException, GravityException
    {
        AOPsCDNAddressQuery query = new AOPsCDNAddressQuery();
        query.filterByXplatformua(xplatformua.getId());
        AOPsCDNAddress dbcdnadd = _tctx.getDB().Find(query);
        if (dbcdnadd != null)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.EntityNotDeletedYet, "Mapped " + EN.XPlatformUA.name() + " Exists in " + EN.AOPsCallerId.name());
        }
    }

    private void validateXPlatformUASWithCallerID(XPlatformUA xplatformua) throws CODEException, GravityException, Exception
    {
        JPAQuery query = new JPAQuery("SELECT a FROM AOPsCallerId a JOIN a.XPlatformUAs x WHERE x.id IN :xPlatformUAIds AND a.EntityState=:entitystate");
        query.setParam("xPlatformUAIds", xplatformua.getId());
        query.setParam("entitystate", EntityState.Active);
        ArrayList<AOPsCallerId> dbaopclids = _tctx.getDB().Select(EN.AOPsCallerId, query);
        if (!dbaopclids.isEmpty())
        {
            for (AOPsCallerId callerID : dbaopclids)
            {
                AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), callerID.getAOPs());
//                callerID.getXPlatformUAs().remove(xplatformua);
//                entities.add(new NameValuePair(ENActionList.Action.Update.name(), callerID));
            }

        }
    }

    private void validateXPlatformUASWithAOPsCDN(XPlatformUA xplatformua) throws CODEException, GravityException, Exception
    {
        JPAQuery query = new JPAQuery("SELECT a FROM AOPsCDN a JOIN a.XPlatformUAs x WHERE x.id IN :xPlatformUAIds AND a.EntityState=:entitystate");
        query.setParam("xPlatformUAIds", xplatformua.getId());
        query.setParam("entitystate", EntityState.Active);
        ArrayList<AOPsCDN> dbaopCDN = _tctx.getDB().Select(EN.AOPsCallerId, query);
        if (!dbaopCDN.isEmpty())
        {
            for (AOPsCDN cdn : dbaopCDN)
            {
                AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), cdn.getAOPs());
//                cdn.getXPlatformUAs().remove(xplatformua);
//                entities.add(new NameValuePair(ENActionList.Action.Update.name(), cdn));
            }

        }
    }

    private void deleteAiDisposition(XPlatformUA ua) throws CODEException, GravityException, Exception
    {

        ArrayList<AOPsProperties> props = _tctx.getDB().Select(new AOPsPropertiesQuery().filterByConfKey(AOPsProperties.Keys.Global_AIDisposeXPlatformUA).filterByConfValue(String.valueOf(ua.getId())));

        for (AOPsProperties aoPsProperties : props)
        {
            AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aoPsProperties.getAOPs());
            entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aoPsProperties));
        }
    }
}
