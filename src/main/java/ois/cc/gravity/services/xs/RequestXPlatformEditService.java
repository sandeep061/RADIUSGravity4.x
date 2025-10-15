package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;
import code.ua.events.EventFailedCause;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XPlatform;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestXPlatformEditService extends RequestEntityEditService {
    public RequestXPlatformEditService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityEdit req = (RequestEntityEdit) request;

        //find the entity
        XPlatform entity = _tctx.getDB().FindAssert(EN.XPlatform.getEntityClass(), req.getEntityId());

        if (req.getAttributes().containsKey("Name")) {
            entity.setName(req.getAttributeValueOf(String.class, "Name"));
        }
        if (req.getAttributes().containsKey("PlatformSIDs")) {
            String platformSIDs = req.getAttributeValueOf(String.class, "PlatformSIDs");

            if (validateXPlatformSIDs(getPlatformids(platformSIDs), entity.getPlatformID())) {
                entity.setPlatformSIDs(getPlatformids(platformSIDs));
            } else {
                throw new GravityIllegalArgumentException(platformSIDs + "is not valid XPlatformID", platformSIDs, EventFailedCause.ValueOutOfRange);

            }
        }
        if (req.getAttributes().containsKey("Properties")) {
            String props = req.getAttributeValueOf(String.class, "Properties");
            HashMap<String, String> reqxplatkeys = JSONUtil.FromJSON(props, HashMap.class);
            HashMap<XPlatform.Keys, String> xplatkey = validatePropertiesKeys(entity.getPlatformID(), reqxplatkeys);
            entity.setProperties(xplatkey);
        }
        if (req.getAttributes().containsKey("Protocol")) {
            entity.setProtocol(req.getAttributeValueOf(String.class, "Protocol"));
        }
        _tctx.getDB().Update(_uac.getUserSession().getUser(), entity);

        EventSuccess evs = new EventSuccess(request);
        return evs;

    }


    private Boolean validateXPlatformSIDs(ArrayList<XPlatformSID> sids, XPlatformID pltfrmid) {
//        for (XPlatformSID sid : sids)
//        {
//            if (pltfrmid.getPlatformSID().contains(sid))
//            {
//                return true;
//            }
//        }
        if (pltfrmid.getPlatformSID().containsAll(sids)) {
            return true;
        }
        return false;
    }


    private HashMap<XPlatform.Keys, String> validatePropertiesKeys(XPlatformID pltfrom, HashMap<String, String> properties) throws GravityIllegalArgumentException {
        HashMap<XPlatform.Keys, String> props = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            try {
                XPlatform.Keys keys = XPlatform.Keys.valueOf(entry.getKey());
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

    private ArrayList<XPlatformSID> getPlatformids(String platformSIDs) throws Exception, GravityIllegalArgumentException {
        ArrayList<String> sids = JSONUtil.FromJSON(platformSIDs, ArrayList.class);
        ArrayList<XPlatformSID> xPlatformSIDS = new ArrayList<>();
        for (String sid : sids) {
            try {
                xPlatformSIDS.add(XPlatformSID.valueOf(sid));
            } catch (Exception e) {
                throw new GravityIllegalArgumentException(sid, EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.ParamValueOutOfRange);
            }
        }
        return xPlatformSIDS;
    }
}
