package ois.cc.gravity.services.user;

import code.ua.events.Event;
import code.ua.events.EventObjectsFetched;
import code.ua.requests.Request;
import code.uaap.service.common.entities.app.Policy;
import ois.cc.gravity.db.queries.ProfileQuery;
import ois.cc.gravity.framework.objects.OProfile;
import ois.cc.gravity.framework.objects.pob;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.ARequestService;
import ois.cc.gravity.si.uaap.UAAPServiceManager;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.Profile;

import java.util.ArrayList;

public class RequestProfileFetchService  extends ARequestEntityService
{

    public RequestProfileFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityFetch req = (RequestEntityFetch) request;

        ProfileQuery qry = new ProfileQuery();
        qry.ApplyFilters(req.getFilters());
        qry.ApplyOrderBy(req.getOrderBy());

        Integer recCount = null;
        Boolean reqcnt = req.getIncludeCount() == null ? false : req.getIncludeCount();
        if (reqcnt)
        {
            recCount = _tctx.getDB().SelectCount(qry);
        }

        qry.setLimit(req.getLimit());
        qry.setOffset(req.getOffset());

        ArrayList<OProfile> alPolfiles = new ArrayList<>();
        ArrayList<Profile> alProf = _tctx.getDB().Select(qry);
        for (Profile pf : alProf)
        {
            ArrayList<Policy> policies = UAAPServiceManager.This().FetchPoliciesByCode(_tctx,pf);
            Policy policy = policies.isEmpty() ? null : policies.get(0);

            OProfile oPolicy = pob.Build(pf, policy);
            alPolfiles.add(oPolicy);
        }

        EventObjectsFetched ev = new EventObjectsFetched(req);
        ev.setObjects(new ArrayList<>(alPolfiles));
        ev.setRecordCount(recCount);
        return ev;

    }

}
