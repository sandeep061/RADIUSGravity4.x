package ois.cc.gravity.services.sys;

import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.XSPIClientQuery;
import ois.cc.gravity.framework.events.common.EventEntitiesFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import java.util.ArrayList;

public class RequestXSPIClientFetchService extends ARequestEntityService
{

    public RequestXSPIClientFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityFetch reqFetch = (RequestEntityFetch) request;

        try
        {
            XSPIClientQuery enQry = new XSPIClientQuery();
            enQry.ApplyFilters(reqFetch.getFilters());
            enQry.ApplyOrderBy(reqFetch.getOrderBy());

            Integer recCount = null;
            Boolean reqcnt = reqFetch.getIncludeCount() != null && reqFetch.getIncludeCount();
            if (reqcnt)
            {
                recCount = _sCtx.getSysDB().SelectCount(enQry);
            }

            enQry.setLimit(reqFetch.getLimit());
            enQry.setOffset(reqFetch.getOffset());

            JPAQuery ctq = enQry.toSelect();

            ArrayList<AEntity> arrEnty = _sCtx.getSysDB().Select(getClass(), ctq);
            _logger.info("Fetched entity:" + arrEnty.size());
            return BuildFetchedEvents(reqFetch, arrEnty, recCount);

        }
        catch (GravityException rex)
        {
            return BuildExceptionEvents(reqFetch, rex);
        }
    }

    protected Event BuildExceptionEvents(RequestEntityFetch reqfetch, GravityException rex) throws GravityNoSuchFieldException, GravityException
    {
        if (rex instanceof GravityNoSuchFieldException)
        {
            GravityNoSuchFieldException fex = (GravityNoSuchFieldException) rex;
            throw fex;
        }

        else
        {
            throw rex;
        }
    }

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
