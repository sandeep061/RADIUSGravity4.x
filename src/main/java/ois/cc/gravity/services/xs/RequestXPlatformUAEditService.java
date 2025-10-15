package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;
import code.ua.events.EventFailedCause;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

import java.util.HashMap;
import java.util.Map;

public class RequestXPlatformUAEditService extends RequestEntityEditService {
    public RequestXPlatformUAEditService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityEdit reqedit = (RequestEntityEdit) request;

        //find the entity
        XPlatformUA entity = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), reqedit.getEntityId());

        if (reqedit.getAttributes().containsKey("Address")) {
            entity.setAddress(reqedit.getAttributeValueOf(String.class, "Address"));
        }
        if (reqedit.getAttributes().containsKey("Properties")) {
            String props = reqedit.getAttributeValueOf(String.class, "Properties");
            HashMap<String, String> reqxplatkeys = JSONUtil.FromJSON(props, HashMap.class);
            HashMap<XPlatformUA.Keys, String> xplatkey = validatePropertiesKeys(entity.getXPlatform().getPlatformID(), reqxplatkeys);
            entity.setProperties(xplatkey);
        }


        _tctx.getDB().Update(_uac.getUserSession().getUser(), entity);

        EventSuccess evs = new EventSuccess(request);
        return evs;
    }

    private HashMap<XPlatformUA.Keys, String> validatePropertiesKeys(XPlatformID pltfrom, HashMap<String, String> properties) throws GravityIllegalArgumentException {
        HashMap<XPlatformUA.Keys, String> props = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            try {
                XPlatformUA.Keys keys = XPlatformUA.Keys.valueOf(entry.getKey());
                if (keys.getPlatformID().equals(pltfrom)) {
                    props.put(keys, entry.getValue());
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException iex) {
                throw new GravityIllegalArgumentException(entry.getKey() + "is not valid Key for " + pltfrom.name(), entry.getKey(), EventFailedCause.ValueOutOfRange);
            }
        }
        return props;
    }
}
