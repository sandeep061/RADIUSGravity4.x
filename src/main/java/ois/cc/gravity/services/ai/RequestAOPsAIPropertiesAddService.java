package ois.cc.gravity.services.ai;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.aops.RequestAOPsAIPropertiesAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.XAICategory;
import ois.radius.ca.enums.XAIPlatformSID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.AOPsAIProperties;

public class RequestAOPsAIPropertiesAddService extends ARequestEntityService {
    public RequestAOPsAIPropertiesAddService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestAOPsAIPropertiesAdd req = (RequestAOPsAIPropertiesAdd) request;

        AOPs aop = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), req.getAOPs());
        ValidateXAICategory(req);
        AOPsAIProperties aiprops = new AOPsAIProperties();
        aiprops.setAOPs(aop);
        aiprops.setProperties(req.getProperties());
        aiprops.setXAICategory(req.getXAICategory());
        aiprops.setXAIPlatformID(req.getXAIPlatformID());
        aiprops.setXAIPlatformSIDs(req.getXAIPlatformSIDs());
        aiprops.setChannels(req.getChannels());
        _tctx.getDB().Insert(_uac.getUserSession().getUser(), aiprops);

        EventEntityAdded ev=new EventEntityAdded(req,aiprops);

        return ev;
    }

    private void ValidateXAICategory(RequestAOPsAIPropertiesAdd req) throws GravityException, CODEException {
        if (req.getXAICategory().equals(XAICategory.Assist)) {
            if (req.getXAIPlatformSIDs().size() > 1) {
                throw new GravityIllegalArgumentException("Found multiple XAIPlatformSID ","XAIPlatformSID",EventFailedCause.ValueOutOfRange);
            }
            XAIPlatformSID xaiPlatformSID = req.getXAIPlatformSIDs().get(0);
            if (!xaiPlatformSID.getChannels().containsAll(req.getChannels())){
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange,"Channels not Allowed this XAIPlatformSID ");
            }
            JPAQuery query = new JPAQuery("SELECT COUNT(a) FROM AOPsAIProperties a JOIN a.XAIPlatformSIDs s WHERE a.AOPs.Id = :aops AND a.XAIPlatformID = :platformId AND s IN :sid");
            query.setParam("aops", req.getAOPs());
            query.setParam("platformId", req.getXAIPlatformID());
            query.setParam("sid", req.getXAIPlatformSIDs());


            long size = _tctx.getDB().SelectScalar(query);
            if (size >= 1) {
                throw new GravityEntityExistsException(EN.AOPsAIProperties.name(), "");
            }

        }
        else {
            if (req.getChannels() != null) {

                for (XAIPlatformSID sid : req.getXAIPlatformSIDs())
                    if (!sid.getChannels().containsAll(req.getChannels())) {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TerminalIsInUse, "");
                    }
            }
        }

    }
}
