package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;
import code.uaap.service.common.entities.app.Policy;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.Profile;
import ois.cc.gravity.framework.requests.user.RequestProfileAdd;

public class RequestProfileAddService extends ARequestEntityService
{

    public RequestProfileAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestProfileAdd req = (RequestProfileAdd) request;
        Profile prof = new Profile();
        prof.setCode(req.getCode());
        prof.setName(req.getName());
        prof.setDescription(req.getDescription());
        prof.setPolicy(req.getPolicy());
//        if (req.getPolicy() != null)
//        {
//            code.uaap.service.event.Event uaapEv = UAAPServiceManager.This().AddPolicy(_tctx,buildPolicy(req));
//            if (!uaapEv.getEventCode().equals(EventCode.Success))
//            {
//                throw new GravityUnhandledException(new Exception(uaapEv.getMessage()));
//            }
//        }

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), prof);

        EventEntityAdded ev = new EventEntityAdded(req, prof.getId().toString(), prof.getName());
        return ev;
    }

    private code.uaap.service.common.entities.app.Policy buildPolicy(RequestProfileAdd req) throws Exception
    {

        code.uaap.service.common.entities.app.Policy policy = JSONUtil.FromJSON(req.getPolicy(), Policy.class);
        policy.setCode(req.getCode());
        policy.setName(req.getName());
        policy.setDescription(req.getDescription());

        return policy;
    }

}
