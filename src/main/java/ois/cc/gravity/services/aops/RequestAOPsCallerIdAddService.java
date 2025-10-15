package ois.cc.gravity.services.aops;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformSID;


public class RequestAOPsCallerIdAddService extends RequestEntityAddService
{

    public RequestAOPsCallerIdAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {
        if (!reqenadd.getAttributes().containsKey("Channel"))
        {
            throw new GravityIllegalArgumentException("Channel", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!reqenadd.getAttributes().containsKey("AOPs"))
        {
            throw new GravityIllegalArgumentException("AOPs", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!reqenadd.getAttributes().containsKey("XPlatformID"))
        {
            throw new GravityIllegalArgumentException("XPlatformID", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!reqenadd.getAttributes().containsKey("XPlatform"))
        {
            throw new GravityIllegalArgumentException("XPlatform", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        Channel channel = reqenadd.getAttributeValueOf(Channel.class, "Channel");
        if (channel.equals(Channel.Call))
        {
            throw new GravityIllegalArgumentException("Channel", "Channel==" + Channel.Call, EventFailedCause.ValueOutOfRange);
        }

    }


    private Boolean assertXPlatformSIDChannel(XPlatformSID pltfrmsid, Channel chnl)
    {
        switch (pltfrmsid)
        {
            case Email ->
            {
                switch (chnl)
                {
                    case Email:
                        return true;
                }
            }
            case SocialListening ->
            {
                switch (chnl)
                {
                    case Social:
                        return true;
                }
            }
            case VideoCalling ->
            {
                switch (chnl)
                {
                    case Video:
                        return true;
                }
            }
            case InstantMessaging, TextToSpeech, LanguageTranslation, ChatBot ->
            {
                switch (chnl)
                {
                    case Chat:
                        return true;
                }
            }
            case SpeechToText, VoiceBot ->
            {
                switch (chnl)
                {
                    case Call:
                        return true;
                }
            }
            case SentimentDetection ->
            {
                switch (chnl)
                {
                    case Call, Chat, Email, Social:
                        return true;
                }
            }
            case ConversationalAI ->
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

    private XPlatformSID validateXPlatformSID(String xplatformSID) throws GravityIllegalArgumentException
    {
        try
        {
            return XPlatformSID.valueOf(xplatformSID);
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(xplatformSID, EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.ParamValueOutOfRange);
        }
    }
}
