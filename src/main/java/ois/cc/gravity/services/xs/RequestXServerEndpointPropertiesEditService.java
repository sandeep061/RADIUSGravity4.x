package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Utils.EnumUtil;
import code.ua.events.*;
import code.ua.requests.Request;
import java.util.ArrayList;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XServerEndpointProperties;
import ois.radius.ca.enums.EndPointType;

public class RequestXServerEndpointPropertiesEditService extends RequestEntityEditService
{

    public RequestXServerEndpointPropertiesEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityEdit reqEdit = (RequestEntityEdit) request;

        EndPointType entype = null;
        EndPointType.ConfigKey Confkey = null;
        String confValue = null;

        EN en = reqEdit.getEntityName();
        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqEdit.getEntityName().name());
            return ev;
        }
        //Find _thisEntity by Id supplied.
        XServerEndpointProperties entity = _tctx.getDB().FindAssert(en.getEntityClass(), reqEdit.getEntityId());
        if (reqEdit.getAttributes().containsKey("EndpointType"))
        {
            String endpointType = reqEdit.getAttribute("EndpointType").toString();
            entype = EnumUtil.ValueOf(EndPointType.class, endpointType);
            if (entype == null)
            {
                throw new GravityIllegalArgumentException(endpointType + " is Not a valid EndpointType", "EndpointType", EventFailedCause.ValueOutOfRange);
            }
            Confkey = EnumUtil.ValueOf(EndPointType.ConfigKey.class, entity.getPropKey());
            ArrayList<EndPointType> endPoints = Confkey.getEndPoints();
            if (!endPoints.contains(entype)) {
                String msg = "Found : " + endPoints + ", Expected : " + entype.name();
                throw new GravityIllegalArgumentException(msg, "ConfKey", EventFailedCause.ValueOutOfRange);
            }
            entity.setEndpointType(entype);
        }
        if (reqEdit.getAttributes().containsKey("PropKey"))
        {
            String propfkey = reqEdit.getAttribute("PropKey").toString();
//            if (!propfkey.isEmpty() && entity.getEndpointType().equals(EndPointType.OtherSIP)||entity.getEndpointType().equals(EndPointType.ExternalPhone))
//            {
//                throw new GravityIllegalArgumentException(Confkey + " is Not a valid Confkey", "Confkey", EventFailedCause.ValueOutOfRange);
//            }
//            Confkey = EnumUtil.ValueOf(EndPointType.ConfigKey.class, propfkey);
//            if (Confkey == null)
//            {
//                throw new GravityIllegalArgumentException(Confkey + " is Not a valid Confkey", "Confkey", EventFailedCause.ValueOutOfRange);
//            }
            entity.setPropKey(propfkey);
        }
        if (reqEdit.getAttributes().containsKey("PropValue"))
        {
            String propValue = reqEdit.getAttribute("PropValue").toString();
            entity.setPropValue(propValue);
        }

        _tctx.getDB().Update(_uac.getUserSession().getUser(), entity);
        EventEntityEdited ev = new EventEntityEdited(request, entity);
        return ev;
    }
}
