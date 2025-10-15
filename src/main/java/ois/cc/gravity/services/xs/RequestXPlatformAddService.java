package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;
import code.ua.events.EventFailedCause;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.tenant.cc.XPlatform;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestXPlatformAddService extends RequestEntityAddService {

    public RequestXPlatformAddService(UAClient uac) {
        super(uac);
    }

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityAdd req = (RequestEntityAdd) request;
        String protocol = null;
        if (!req.getAttributes().containsKey("Code")) {
            throw new GravityIllegalArgumentException("Code", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!req.getAttributes().containsKey("Name")) {
            throw new GravityIllegalArgumentException("Name", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!req.getAttributes().containsKey("PlatformSIDs")) {
            throw new GravityIllegalArgumentException("PlatformSIDs", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!req.getAttributes().containsKey("PlatformID")) {
            throw new GravityIllegalArgumentException("PlatformID", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!req.getAttributes().containsKey("Properties")) {
            throw new GravityIllegalArgumentException("Properties", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (req.getAttributes().containsKey("Protocol")) {
            protocol = req.getAttributeValueOf(String.class, "Protocol");
        }
        String code = req.getAttributeValueOf(String.class, "Code");
        String name = req.getAttributeValueOf(String.class, "Name");

        XPlatformID platformID = req.getAttributeValueOf(XPlatformID.class, "PlatformID");

        String platformSIDs = req.getAttributeValueOf(String.class, "PlatformSIDs");

        ArrayList<XPlatformSID> xPlatformSIDS = getPlatformids(platformSIDs);

        String props = req.getAttributeValueOf(String.class, "Properties");
        HashMap<String, String> xplatkeys = JSONUtil.FromJSON(props, HashMap.class);


        if (!validateXPlatformSIDs(xPlatformSIDS, platformID)) {
            //throw an exception
            throw new GravityIllegalArgumentException("Invalid XPlatformSIDS for XPlatformID ", platformID.name(), EventFailedCause.ValueOutOfRange);
        }
        HashMap<XPlatform.Keys, String> keysStringHashMap = validatePropertiesKeys(platformID, xplatkeys);

        //save entity in db.
        XPlatform entity = new XPlatform();
        if (protocol != null) {
            entity.setProtocol(protocol);
        }
        entity.setCode(code);
        entity.setName(name);
        entity.setPlatformID(platformID);
        entity.setPlatformSIDs(xPlatformSIDS);
        entity.setProperties(keysStringHashMap);
        _tctx.getDB().Insert(_uac.getUserSession().getUser(), entity);
        EventSuccess evs = new EventSuccess(request);
        return evs;

    }

//    private Boolean assertXPlatformChannel(XPlatformID pltfrmid, Channel chnl)
//    {
//        switch (pltfrmid)
//        {
//            case GMail, Microsoft365 ->
//            {
//                switch (chnl)
//                {
//                    case Email:
//                        return true;
//                }
//            }
//            case Facebook, Instagram, X_Twitter ->
//            {
//                switch (chnl)
//                {
//                    case Social, Chat:
//                        return true;
//                }
//            }
//            case WhatsApp, MicrosoftTeams, GoogleDialogflow ->
//            {
//                switch (chnl)
//                {
//                    case Chat:
//                        return true;
//                }
//            }
//            case Zoom, Jitsi ->
//            {
//                switch (chnl)
//                {
//                    case Video:
//                        return true;
//                }
//            }
//            case OpenAI ->
//            {
//                switch (chnl)
//                {
//                    case Call, Chat, Email, Social:
//                        return true;
//                }
//            }
//            case GoogleCloud ->
//            {
//                switch (chnl)
//                {
//                    case Call, Chat:
//                        return true;
//                }
//            }
//        }
//        return false;
//    }

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
                throw new GravityIllegalArgumentException(platformSIDs + "is not valid XPlatformID", platformSIDs, EventFailedCause.ValueOutOfRange);
            }
        }
        return xPlatformSIDS;
    }
}
