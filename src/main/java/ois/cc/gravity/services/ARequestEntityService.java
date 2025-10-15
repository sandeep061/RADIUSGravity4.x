package ois.cc.gravity.services;

import ois.cc.gravity.ua.UAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.context.TenantContext;

public abstract class ARequestEntityService extends ARequestService
{

    protected Logger _logger = LoggerFactory.getLogger(getClass());
    /**
     * There can't be a RequestProcessor without an UAClient.
     */
    protected final UAClient _uac;
    protected TenantContext _tctx;

    //    protected final MySQLDB _sysDB;
//
    public ARequestEntityService(UAClient uac)
    {
        this._uac = uac;
        this._tctx = uac.getTenantContext();
    }

    @Override
    public final Event DoProcessRequest(Request request) throws Throwable
    {
        return DoProcessEntityRequest(request);
    }

    protected abstract Event DoProcessEntityRequest(Request request) throws Throwable;
}
