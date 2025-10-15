package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.PWDUtil;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.XServerQuery;
import ois.cc.gravity.entities.util.AppUtil;
import ois.cc.gravity.framework.events.common.EventEntitiesFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XServer;

import java.util.ArrayList;
import java.util.Properties;

public class RequestXserverFetchService extends ARequestEntityService
{

    public RequestXserverFetchService(UAClient uac)
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
            XServerQuery enQry = new XServerQuery();
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

            ArrayList<XServer> xservers = _tctx.getDB().Select(getClass(), ctq);

            for (XServer xserver : xservers)
            {
                Properties authparams = xserver.getAuthParams();
                if (authparams == null || authparams.isEmpty())
                {
                    continue;
                }
                String[] passwordKeys =
                {
                    "CTRLPassword", "Cx_CTRL_Passwd"
                };

                for (String key : passwordKeys)
                {
                    if (authparams.containsKey(key))
                    {
                        String pwd = authparams.getProperty(key);
                        if (pwd != null && !pwd.isEmpty())
                        {
                            if(!pwd.startsWith("*>_"))
                            {
                                authparams.put(key, AppUtil.Encrypt(pwd));
                            }
                            // Update only once and break since only one key should match
                            xserver.setAuthParams(JSONUtil.ToJSON(authparams).toString());
                        }
                        break; // stop after first match
                    }
                }

            }

            EventEntitiesFetched ev = new EventEntitiesFetched(request, new ArrayList<>(xservers));
            ev.setRecordCount(recCount);
            return ev;
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
}
