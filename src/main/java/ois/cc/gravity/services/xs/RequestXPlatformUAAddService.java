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
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XPlatform;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;
import java.util.HashMap;
import java.util.Map;

public class RequestXPlatformUAAddService extends RequestEntityAddService
{

    public RequestXPlatformUAAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd req = (RequestEntityAdd) request;
        if (!req.getAttributes().containsKey("Code"))
        {
            throw new GravityIllegalArgumentException("Code", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!req.getAttributes().containsKey("Channel"))
        {
            throw new GravityIllegalArgumentException("Channel", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!req.getAttributes().containsKey("Address"))
        {
            throw new GravityIllegalArgumentException("Address", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!req.getAttributes().containsKey("PlatformID"))
        {
            throw new GravityIllegalArgumentException("PlatformID", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!req.getAttributes().containsKey("Properties"))
        {
            throw new GravityIllegalArgumentException("Properties", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        Channel channel = req.getAttributeValueOf(Channel.class, "Channel");
        //This is required in case of SentimentDetection,VoiceBot...etc. But this UA can't used for CallerId and CDN address.
//        if (channel.equals(Channel.Call)) {
//            throw new GravityIllegalArgumentException("Channel", "Channel==" + Channel.Call, EventFailedCause.ValueOutOfRange);
//        }
        long platformID = req.getAttributeValueOf(Long.class, "PlatformID");
        XPlatform xplatform = _tctx.getDB().FindAssert(EN.XPlatform.getEntityClass(), platformID);
        if (!assertXPlatformChannel(xplatform.getPlatformID(), channel))
        {
            throw new GravityIllegalArgumentException(xplatform.getPlatformID().name() + "is not valid XPlatformID", xplatform.getPlatformID().name(), EventFailedCause.ValueOutOfRange);
        }

        String props = req.getAttributeValueOf(String.class, "Properties");
        HashMap<String, String> xplatkeys = JSONUtil.FromJSON(props, HashMap.class);
        HashMap<XPlatformUA.Keys, String> uaprops = validatePropertiesKeys(xplatform.getPlatformID(), xplatkeys);

        XPlatformUA entity = new XPlatformUA();
        entity.setAddress(req.getAttributeValueOf(String.class, "Address"));
        entity.setCode(req.getAttributeValueOf(String.class, "Code"));
        entity.setChannel(channel);
        entity.setXPlatform(xplatform);
        entity.setProperties(uaprops);

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), entity);
        EventSuccess evs = new EventSuccess(req);
        return evs;

    }

    private HashMap<XPlatformUA.Keys, String> validatePropertiesKeys(XPlatformID pltfrom, HashMap<String, String> properties) throws GravityIllegalArgumentException
    {
        HashMap<XPlatformUA.Keys, String> props = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet())
        {
            try
            {
                XPlatformUA.Keys keys = XPlatformUA.Keys.valueOf(entry.getKey());
                if (keys.getPlatformID().equals(pltfrom))
                {
                    props.put(keys, entry.getValue());
                }
                else
                {
                    throw new IllegalArgumentException();
                }
            }
            catch (IllegalArgumentException iex)
            {
                throw new GravityIllegalArgumentException(entry.getKey() + " is not valid Key for " + pltfrom.name(), entry.getKey(), EventFailedCause.ValueOutOfRange);
            }
        }
        return props;
    }

    private Boolean assertXPlatformChannel(XPlatformID pltfrmid, Channel chnl)
    {
        switch (pltfrmid)
        {
            case GMail, Microsoft365 ->
            {
                switch (chnl)
                {
                    case Email:
                        return true;
                }
            }
            case Facebook, Instagram, X_Twitter ->
            {
                switch (chnl)
                {
                    case Social, Chat:
                        return true;
                }
            }
            case WhatsApp, MicrosoftTeams, GoogleDialogflow, Line, Nexmo_Viber,Telegram ->
            {
                switch (chnl)
                {
                    case Chat,Video:
                        return true;
                }
            }
            case Zoom, Jitsi ->
            {
                switch (chnl)
                {
                    case Video:
                        return true;
                }
            }
            case OpenAI ->
            {
                switch (chnl)
                {
                    case Call, Chat, Email, Social:
                        return true;
                }
            }
            case GoogleCloud ->
            {
                switch (chnl)
                {
                    case Call, Chat:
                        return true;
                }
            }
        }
        return false;
    }

}
