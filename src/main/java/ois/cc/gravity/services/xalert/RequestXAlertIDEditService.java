package ois.cc.gravity.services.xalert;

import code.entities.AEntity;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XAlertID;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

public class RequestXAlertIDEditService extends RequestEntityEditService
{

    public RequestXAlertIDEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
        XAlertID alertid = (XAlertID) thisentity;
        if (reqenedit.getAttributes().containsKey("XPlatformUA"))
        {
            XPlatformUA xPlatformUA = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), reqenedit.getAttributeValueOf(Long.class, "XPlatformUA"));
            ValidateChannel(alertid.getChannel(), xPlatformUA);
        }
    }

    private void ValidateChannel(Channel ch, XPlatformUA xua) throws GravityIllegalArgumentException
    {
        if (!ch.equals(xua.getChannel()))
        {
            throw new GravityIllegalArgumentException("Invalid Channel",
                    EventFailedCause.ValueOutOfRange,
                    EvCauseRequestValidationFail.InvalidParamName);
        }
    }
}
