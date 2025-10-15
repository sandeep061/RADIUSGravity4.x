package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Utils.PWDUtil;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.TerminalQuery;
import ois.cc.gravity.entities.util.AppUtil;
import ois.cc.gravity.framework.events.common.EventEntitiesFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Terminal;
import java.util.ArrayList;

public class RequestTerminalFetchService extends ARequestEntityService
{

    public RequestTerminalFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        try
        {
            TerminalQuery enQry = new TerminalQuery();
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

            ArrayList<Terminal> terminals = _tctx.getDB().Select(getClass(), ctq);

            for (Terminal terminal : terminals)
            {

                if (terminal.getPassword() != null)
                {
                    if(!terminal.getPassword().startsWith("*>_"))
                    {
                        terminal.setPassword(AppUtil.Encrypt(terminal.getPassword()));
                    }

                }
            }

            EventEntitiesFetched ev = new EventEntitiesFetched(request, new ArrayList<>(terminals));
            ev.setRecordCount(recCount);
            return ev;
        }
        catch (GravityException rex)
        {
            return BuildExceptionEvents(reqFetch, rex);
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
}
