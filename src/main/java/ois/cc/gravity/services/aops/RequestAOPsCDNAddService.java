package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.DATEUtil;
import code.db.jpa.JPAQuery;
import code.entities.EntityState;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCDN;

import java.util.ArrayList;

public class RequestAOPsCDNAddService extends ARequestEntityService
{

    public RequestAOPsCDNAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        ArrayList<NameValuePair> entities = new ArrayList<>();
        RequestEntityAdd reqenadd = (RequestEntityAdd) request;
        if (!reqenadd.getAttributes().containsKey("Channel"))
        {
            throw new GravityIllegalArgumentException("Channel", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!reqenadd.getAttributes().containsKey("Code"))
        {
            throw new GravityIllegalArgumentException("Code", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!reqenadd.getAttributes().containsKey("Address"))
        {
            throw new GravityIllegalArgumentException("Address", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (!reqenadd.getAttributes().containsKey("AOPs"))
        {
            throw new GravityIllegalArgumentException("AOPs", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        Channel channel = reqenadd.getAttributeValueOf(Channel.class, "Channel");
        if (channel.equals(Channel.Call))
        {
            throw new GravityIllegalArgumentException("Channel", "Channel==" + Channel.Call, EventFailedCause.ValueOutOfRange);
        }
        XPlatformSID xplatformSID = null;
        //find Platform
        try
        {
            if (reqenadd.getAttributes().containsKey("XPlatformSID"))
            {
                xplatformSID = XPlatformSID.valueOf(reqenadd.getAttribute("XPlatformSID").toString());
                if (!assertXPlatformSIDChannel(xplatformSID, channel))
                {
//                    throw new GravityIllegalArgumentException("platformSID","platformSID is not valid wit",EvCauseRequestValidationFail.ParamValueOutOfRange);
                    throw new GravityIllegalArgumentException("XPlatformSID is not valid with this Channel " + channel.name(), "XPlatformSID", EventFailedCause.ValueOutOfRange);
                }
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(xplatformSID.name(), EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.ParamValueOutOfRange);
        }
        AOPsCDN aopscdn = EntityBuilder.New(EN.AOPsCDN);
        EntityBuilder.BuildEntity(_tctx.getDB(), aopscdn, reqenadd.getAttributes());
        String code = aopscdn.getCode().substring(0, 3) + aopscdn.getAOPs().getCode().substring(0, 3) + DATEUtil.Now().getTime();
        aopscdn.setCode(code);
        JPAQuery query = new JPAQuery("SELECT a FROM AOPsCDN a WHERE a.AOPs.Id = :aops AND a.Channel = :channel And a.EntityState=:entstate");
        query.setParam("aops", aopscdn.getAOPs().getId());
        query.setParam("channel", aopscdn.getChannel());
        query.setParam("entstate", EntityState.Active);
        AOPsCDN dbaopcdn = _tctx.getDB().FindFromDB(AOPsCDN.class, query);
        if (dbaopcdn != null)
        {
            throw new GravityEntityExistsException(EN.AOPsCDN.name(), "AOPs,Channel,Address", OPRelational.Eq, aopscdn.getAOPs().getId() + "," + channel );
        }
        _tctx.getDB().Insert(_uac.getUserSession().getUser(), aopscdn);
        EventEntityAdded ev = new EventEntityAdded(reqenadd, aopscdn);
        return ev;
    }

//    @Override
//    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
//    {
//
//
////        if (!assertXPlatformSIDChannel(xplatformSID, channel))
////        {
////            throw new GravityIllegalArgumentException("platformSID", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.ParamValueOutOfRange);
////        }
//
//    }

    //    private Boolean validateXPlatformSIDs(XPlatformSID sid, XPlatformID pltfrmid)
//    {
//
//        if (pltfrmid.getPlatformSID().contains(sid))
//        {
//            return true;
//        }
//        return false;
//    }
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

}
