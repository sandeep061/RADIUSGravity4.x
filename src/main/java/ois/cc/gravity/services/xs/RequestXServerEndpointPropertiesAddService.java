package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Utils.EnumUtil;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import java.util.ArrayList;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.radius.cc.entities.tenant.cc.XServerEndpointProperties;
import ois.radius.ca.enums.EndPointType;

public class RequestXServerEndpointPropertiesAddService extends ARequestEntityService
{

    public RequestXServerEndpointPropertiesAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd req = (RequestEntityAdd) request;
        EndPointType entype = null;
        EndPointType.ConfigKey Confkey = null;
        String confValue = null;

        if (!req.getAttributes().containsKey("XServer"))
        {
            throw new GravityIllegalArgumentException("XSerer can't be null", "XServer", EventFailedCause.NonOptionalConstraintViolation);
        }
        Long xid = req.getAttributeValueOf(Long.class, "XServer");
        XServer xserver = _tctx.getDB().FindAssert(EN.XServer.getEntityClass(), xid);

        if (!req.getAttributes().containsKey("EndpointType"))
        {
            throw new GravityIllegalArgumentException("EndpointType can't be null", "EndpointType", EventFailedCause.NonOptionalConstraintViolation);
        }
        String endpointType = req.getAttribute("EndpointType").toString();
        entype = EnumUtil.ValueOf(EndPointType.class, endpointType);
        if (entype == null)
        {
            throw new GravityIllegalArgumentException(endpointType + " is Not a valid EndpointType", "EndpointType", EventFailedCause.ValueOutOfRange);
        }

        if (!req.getAttributes().containsKey("PropKey"))
        {
            throw new GravityIllegalArgumentException("PropKey can't be null", "EndpointType", EventFailedCause.ValueOutOfRange);
        }
        Confkey = EnumUtil.ValueOf(EndPointType.ConfigKey.class, req.getAttribute("PropKey").toString());
//        if (!entype.equals(EndPointType.OtherSIP)&&!entype.equals(EndPointType.ExternalPhone)) {
//
//            if (Confkey == null) {
//                throw new GravityIllegalArgumentException(Confkey + " is Not a valid Confkey", "Confkey", EventFailedCause.ValueOutOfRange);
//            }
//            ArrayList<EndPointType> endPoints = Confkey.getEndPoints();
//
//            if (!endPoints.contains(entype)) {
//                String msg = "Found : " + endPoints + ", Expected : " + entype.name();
//                throw new GravityIllegalArgumentException(msg, "ConfKey", EventFailedCause.ValueOutOfRange);
//            }
//        }


        if (!req.getAttributes().containsKey("PropValue"))
        {
            throw new GravityIllegalArgumentException("PropValue can't be null", "PropValue", EventFailedCause.ValueOutOfRange);
        }
        confValue = req.getAttribute("PropValue").toString();
        XServerEndpointProperties entity = new XServerEndpointProperties();
        entity.setXServer(xserver);
        entity.setEndpointType(entype);
        entity.setPropKey(Confkey == null ? "" : Confkey.name());
        entity.setValue(confValue);
        _tctx.getDB().Insert(_uac.getUserSession().getUser(), entity);

        EventEntityAdded ev = new EventEntityAdded(request, entity);
        return ev;

    }
}
