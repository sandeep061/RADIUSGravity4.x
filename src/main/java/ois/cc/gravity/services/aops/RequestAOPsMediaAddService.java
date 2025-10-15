package ois.cc.gravity.services.aops;

import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsMedia;
import ois.radius.cc.entities.tenant.cc.XServer;

public class RequestAOPsMediaAddService extends ARequestEntityService
{
    public RequestAOPsMediaAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd reqAdd = (RequestEntityAdd) request;
        if (!reqAdd.getAttributes().containsKey("Channel"))
        {
            throw new GravityIllegalArgumentException("Channel", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!reqAdd.getAttributes().containsKey("AOPs"))
        {
            throw new GravityIllegalArgumentException("AOPs", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        Channel ch = null;
        if (reqAdd.getAttributes().containsKey("Channel"))
        {
            ch = reqAdd.getAttributeValueOf(Channel.class,"Channel");
        }
        if(ch != Channel.Visit){
            if (!reqAdd.getAttributes().containsKey("XServer"))
            {
                throw new GravityIllegalArgumentException("XServer", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            XServer xserver= _tctx.getDB().FindAssert(EN.XServer.getEntityClass(),reqAdd.getAttributeValueOf(Long.class,"XServer"));
            if (!xserver.getChannel().equals(reqAdd.getAttributeValueOf(Channel.class,"Channel")))
            {
                throw new GravityIllegalArgumentException("Channel is not match with supplied Xserver's  Channel ","Channel", EventFailedCause.ValueOutOfRange);
            }
        }

        AOPsMedia entity = EntityBuilder.New(EN.AOPsMedia);
        EntityBuilder.BuildEntity(_tctx.getDB(), entity, reqAdd.getAttributes());

        _tctx.getALMCtx().MapChannelWithAOPs(entity.getAOPs(), ch);
        _tctx.getDB().Insert(_uac.getUserSession().getUser(), entity);
        EventEntityAdded ev = new EventEntityAdded(request, entity);
        return ev;
    }
}
