package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;
import code.ua.events.EventEntityEdited;
import code.ua.requests.Request;
import code.uaap.service.common.entities.app.Policy;
import ois.cc.gravity.framework.requests.user.RequestProfileEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.si.uaap.UAAPServiceManager;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Profile;

import java.util.ArrayList;

public class RequestProfileEditService extends ARequestEntityService
{

    public RequestProfileEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestProfileEdit req = (RequestProfileEdit) request;
        Profile profile = _tctx.getDB().FindAssert(EN.Profile.getEntityClass(), req.getProfileId());

        profile.setName(req.getName() == null ? profile.getName() : req.getName());
        profile.setDescription(req.getDescription() == null ? profile.getDescription() : req.getDescription());
//        code.uaap.service.event.Event uaapEv = null;
        profile.setPolicy(req.getPolicy() == null ? profile.getPolicy() : req.getPolicy());
//        if (req.getPolicy() != null)
//        {
//            Policy policy = buildPolicy(req, profile);
//            if (policy.getId() == null)
//            {
//                uaapEv = UAAPServiceManager.This().AddPolicy(_tctx,buildPolicy(req, profile));
//            }
//            else
//            {
//                uaapEv = UAAPServiceManager.This().EditPolicy(_tctx,buildPolicy(req, profile));
//            }
//
//            if (uaapEv.getMessage().contains("Errorr Invoking UAAP Service>> timeout"))
//            {
//                throw new GravityUnhandledException(new Exception(uaapEv.getMessage()));
//            }
//            if (!uaapEv.getEventCode().equals(EventCode.Success))
//            {
//                throw new GravityUnhandledException(new Exception(uaapEv.getMessage()));
//            }
//        }

        _tctx.getDB().Update(_uac.getUserSession().getUser(), profile);

        EventEntityEdited ev = new EventEntityEdited(req, profile.getId().toString(), Profile.class.getName());
        return ev;

    }

    private Policy buildPolicy(RequestProfileEdit req, Profile profile) throws Exception, GravityRuntimeCheckFailedException
    {
        ArrayList<Policy> policies = UAAPServiceManager.This().FetchPoliciesByCode(_tctx, profile);
        Policy policy = JSONUtil.FromJSON(req.getPolicy(), Policy.class);
        if (!policies.isEmpty())
        {
            policy.setId(policies.get(0).getId());
            policy.setCode(policies.get(0).getCode());
            policy.setName(req.getName() == null ? profile.getName() : req.getName());
            policy.setDescription(req.getDescription() == null ? profile.getDescription() : req.getDescription());
        }
        policy.setCode(req.getName());
        policy.setName(req.getName());
        policy.setDescription(req.getDescription());

        return policy;
    }
}
