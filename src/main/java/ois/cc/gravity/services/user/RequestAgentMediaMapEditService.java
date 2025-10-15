package ois.cc.gravity.services.user;

import code.entities.AEntity;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.radius.ca.enums.EndPointType;

public class RequestAgentMediaMapEditService extends RequestEntityEditService
{

    public RequestAgentMediaMapEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
        AgentMediaMap agMdMap = (AgentMediaMap) thisentity;
        XServer xserver = agMdMap.getXserver();
        if(reqenedit.getAttributes().containsKey("Terminal"))
        {
            Long termId = reqenedit.getAttributeValueOf(Long.class, "Terminal");
            _tctx.getDB().FindAssert(EN.Terminal.getEntityClass(), termId);
        }

        if (reqenedit.getAttributes().containsKey("EndPointType"))
        {
            EndPointType endPoint = reqenedit.getAttributeValueOf(EndPointType.class, "EndPointType");
            if(!xserver.getProviderID().getEndPoints().contains(endPoint))
            {
                //TBD:throw a exception
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.InvalidEndPointForProvider);
            }
            agMdMap.setEndPointType(endPoint);
        }

    }




}

