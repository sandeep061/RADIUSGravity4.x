package ois.cc.gravity.services.user;

import code.entities.AEntity;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.Terminal;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.radius.ca.enums.EndPointType;

public class RequestAgentMediaMapAddService extends RequestEntityAddService
{

    public RequestAgentMediaMapAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {

        if (!reqenadd.getAttributes().containsKey("XServer"))
        {
            throw new GravityIllegalArgumentException("XServer", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if(!reqenadd.getAttributes().containsKey("EndPointType"))
        {
            throw new GravityIllegalArgumentException("EndPointType", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    @Override
    protected void DoPostBuildProcess(RequestEntityAdd reqenadd, AEntity entity) throws Throwable
    {
        AgentMediaMap agMdMap = (AgentMediaMap) entity;

        EndPointType endPoint = agMdMap.getEndPointType();
        XServer xserver = agMdMap.getXserver();
        if (!xserver.getProviderID().getEndPoints().contains(endPoint))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.InvalidEndPointForProvider);
        }

        Terminal terminal = agMdMap.getTerminal();
        if (terminal != null && !terminal.getXServer().equals(xserver))
        {
            throw new GravityIllegalArgumentException(terminal + " is not a valid terminal for " + xserver);
        }

    }

}
