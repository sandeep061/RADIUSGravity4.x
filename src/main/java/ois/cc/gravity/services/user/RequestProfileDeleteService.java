package ois.cc.gravity.services.user;

import code.entities.AEntity;
import code.entities.AEntity_ad;
import code.ua.events.Event;
import code.ua.events.EventCode;
import code.ua.events.EventEntityDeleted;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import code.uaap.service.common.entities.app.Policy;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.si.uaap.UAAPServiceManager;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.AEntity_ccad;
import ois.radius.cc.entities.AEntity_cces;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Profile;
import ois.radius.cc.entities.tenant.cc.UserProfile;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.db.queries.UserProfileQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestService;

import java.util.ArrayList;

public class RequestProfileDeleteService extends ARequestEntityService {

    public RequestProfileDeleteService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {

        RequestEntityDelete req = (RequestEntityDelete) request;
        Profile profile = _tctx.getDB().FindAssert(EN.Profile.getEntityClass(), req.getEntityId());
//        ArrayList<Policy> policies = UAAPServiceManager.This().FetchPoliciesByCode(_tctx,profile);

        ArrayList<AEntity> entities = new ArrayList<>();


//        if (!policies.isEmpty())
//        {
//            UAAPServiceManager.This().DeletePolicy(_tctx,profile.getCode());

        //Delete UserProfile form RADIUS.
        UserProfileQuery qry = new UserProfileQuery();
        qry.filterByProfile(profile.getId());
        ArrayList<UserProfile> uprof = _tctx.getDB().Select(qry);

        entities.addAll(uprof);
        entities.add(profile);
//        }

        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), entities);
        EventEntityDeleted ev = new EventEntityDeleted(req, profile.getId().toString(), Profile.class.getName());
        return ev;

//        return new EventOK(request, EventCode.EntityDeleted);

    }

}


