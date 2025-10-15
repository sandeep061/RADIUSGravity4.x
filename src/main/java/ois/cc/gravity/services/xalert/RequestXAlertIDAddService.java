package ois.cc.gravity.services.xalert;

import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCSATConf;
import ois.radius.cc.entities.tenant.cc.XAlertID;
import ois.radius.cc.entities.tenant.cc.XPlatform;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

public class RequestXAlertIDAddService extends ARequestEntityService {

    public RequestXAlertIDAddService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityAdd req = (RequestEntityAdd) request;
        XPlatform platform = null;
        XPlatformUA platformua = null;
        AOPsCSATConf aopscsatconf = null;
        Channel channel = null;

        String template = null;

        ValidateRequestAttribute(req);

        platform = _tctx.getDB().FindAssert(EN.XPlatform.getEntityClass(), req.getAttributeValueOf(Long.class, "XPlatform"));

        platformua = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), req.getAttributeValueOf(Long.class, "XPlatformUA"));

        if (req.getAttributes().containsKey("AOPsCSATConf")) {
            aopscsatconf = _tctx.getDB().FindAssert(EN.AOPsCSATConf.getEntityClass(), req.getAttributeValueOf(Long.class, "AOPsCSATConf"));
        }
        channel = req.getAttributeValueOf(Channel.class, "Channel");

        if (req.getAttributes().containsKey("Template")) {
            template = req.getAttributeValueOf(String.class, "Template");
        }

        ValidateChannel(channel, platformua);

        XAlertID alertID = new XAlertID();
        alertID.setChannel(channel);
        alertID.setTemplate(template);
        alertID.setXPlatform(platform);
        alertID.setXPlatformUA(platformua);
        alertID.setAOPsCSATConf(aopscsatconf);

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), alertID);

        EventEntityAdded ev = new EventEntityAdded(req, alertID);
        return ev;

    }

    private void ValidateRequestAttribute(RequestEntityAdd reqadd) throws GravityIllegalArgumentException {

        if (!reqadd.getAttributes().containsKey("Channel")) {
            throw new GravityIllegalArgumentException("Channel", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }

        if (!reqadd.getAttributes().containsKey("XPlatform")) {
            throw new GravityIllegalArgumentException("XPlatform", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }

        if (!reqadd.getAttributes().containsKey("XPlatformUA")) {
            throw new GravityIllegalArgumentException("XPlatformUA", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
//
//        if (!reqadd.getAttributes().containsKey("AOPsCSATConf"))
//        {
//            throw new GravityIllegalArgumentException("AOPsCSATConf", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
//        }
    }

    private void ValidateChannel(Channel ch, XPlatformUA xua) throws GravityIllegalArgumentException {
        if (!ch.equals(xua.getChannel())) {
            throw new GravityIllegalArgumentException("Invalid Channel",
                    EventFailedCause.ValueOutOfRange,
                    EvCauseRequestValidationFail.InvalidParamName);
        }
    }
}
