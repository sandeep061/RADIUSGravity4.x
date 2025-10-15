package ois.cc.gravity.services.common;

import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EventEntitiesFetched;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.db.queries.EntityQuery;
import ois.cc.gravity.db.queries.EntityQueryFactory;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ARequestEntityService;

import java.util.ArrayList;

public class RequestEntityFetchService extends ARequestEntityService
{

    public RequestEntityFetchService(UAClient uac)

    {
        super(uac);
    }

    @Override
    protected final Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

            EntityQuery enQry = EntityQueryFactory.CreateQueryBuilder(en);
            if (reqFetch.getFilters() != null)
            {
                enQry.doApplyFilters(reqFetch.getFilters());
            }

            enQry.ApplyOrderBy(reqFetch.getOrderBy());

            Integer recCount = null;

            Boolean reqcnt = reqFetch.getIncludeCount() != null && reqFetch.getIncludeCount();

            if (reqcnt)
            {
                recCount = _tctx.getDB().SelectCount(enQry);

            }
            if (reqFetch.getLimit() != null)
            {
                enQry.setLimit(reqFetch.getLimit());
            }

            if (reqFetch.getOffset() != null)
            {
                enQry.setOffset(reqFetch.getOffset());
            }

            JPAQuery ctq = enQry.toSelect();

            ArrayList<AEntity> arrEnty = _tctx.getDB().Select(getClass(), ctq);

            _logger.info("Fetched entity:" + arrEnty.size());
            return BuildFetchedEvents(reqFetch, arrEnty, recCount);

    }

    /**
     * Build event with respect to various exception that we expect while fetching entities. This method can be used by subclasses.
     *
     * @param request
     * @param arrEnty
     * @return
     */

    protected Event BuildFetchedEvents(RequestEntityFetch request, ArrayList<AEntity> arrEnty, Integer reccnt)
    {
        if (arrEnty == null || arrEnty.isEmpty())
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, request.getEntityName().name());
            return ev;
        }

        EventEntitiesFetched ev = new EventEntitiesFetched(request, new ArrayList<>(arrEnty));
        ev.setLimit(request.getLimit());
        ev.setOffset(request.getOffset());

        if (reccnt != null)
        {
            ev.setRecordCount(reccnt);
        }
        _logger.info("Fetched Entity: " + ev.toString());
        return ev;
    }

}
