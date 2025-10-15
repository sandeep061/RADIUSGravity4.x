package ois.cc.gravity.services.ai;

import code.ua.events.Event;
import code.ua.events.EventEntityEdited;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.aops.RequestAOPsAIPropertiesEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.XAICategory;
import ois.radius.ca.enums.XAIPlatformSID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsAIProperties;

import java.util.ArrayList;

public class RequestAOPsAIPropertiesEditService extends ARequestEntityService {
    public RequestAOPsAIPropertiesEditService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestAOPsAIPropertiesEdit req = (RequestAOPsAIPropertiesEdit) request;

        AOPsAIProperties entity = _tctx.getDB().FindAssert(EN.AOPsAIProperties.getEntityClass(), req.getId());
        if (entity.getXAICategory().equals(XAICategory.Assist)) {

            if (req.getXAIPlatformSIDs() != null && req.getXAIPlatformSIDs().size() > 1) {
                throw new GravityIllegalArgumentException("Found multiple XAIPlatformSID ", "XAIPlatformSID", EventFailedCause.ValueOutOfRange);
            }

            XAIPlatformSID xaiPlatformSID = req.getXAIPlatformSIDs() == null ? entity.getXAIPlatformSIDs().get(0) : req.getXAIPlatformSIDs().get(0);
             ArrayList<XAIPlatformSID>listsid=new ArrayList<>();
             listsid.add(xaiPlatformSID);
             entity.setXAIPlatformSIDs(listsid);
            if (req.getChannels() != null) {
                if (!xaiPlatformSID.getChannels().containsAll(req.getChannels())) {
                    throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, "Channels not Allowed this XAIPlatformSID ");
                }
                entity.setChannels(req.getChannels());
            }
            if(req.getXAIPlatformID()!=null){
                entity.setXAIPlatformID(req.getXAIPlatformID());
            }
            if (req.getProperties()!=null){
                entity.setProperties(req.getProperties());
            }
        }
           else {
            if (req.getChannels() != null) {
                for (XAIPlatformSID sids : req.getXAIPlatformSIDs()) {
                    if (!sids.getChannels().containsAll(req.getChannels())) {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, "Channels not Allowed this XAIPlatformSID ");
                    }
                }
                entity.setChannels(req.getChannels());
            }
            if(req.getXAIPlatformSIDs()!=null){
                entity.setXAIPlatformSIDs(req.getXAIPlatformSIDs());
            }
            if (req.getProperties()!=null){
                entity.setProperties(req.getProperties());
            }
        }

        _tctx.getDB().Update(_uac.getUserSession().getUser(),entity);
        EventEntityEdited ev=new EventEntityEdited(req,entity);
        return ev;
    }


}
