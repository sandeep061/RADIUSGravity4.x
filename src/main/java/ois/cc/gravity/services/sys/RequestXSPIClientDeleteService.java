package ois.cc.gravity.services.sys;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.sys.RequestXSPIClientDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.sys.XSPIClient;

public class RequestXSPIClientDeleteService extends ARequestEntityService
{

    public RequestXSPIClientDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestXSPIClientDelete reqDelete = (RequestXSPIClientDelete) request;

        String tntCode = reqDelete.getTenantCode();
        String xsCode = reqDelete.getXServerCode();
        JPAQuery query = new JPAQuery("SELECT xc FROM XSPIClient xc WHERE xc.TenantCode = :tenantCode AND xc.XServerCode = :xServerCode");
        query.setParam("tenantCode", tntCode);
        query.setParam("xServerCode", xsCode);
        XSPIClient entity = _sCtx.getSysDB().Find(EN.XSPIClient.getEntityClass(), query);

        if (entity == null)
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, EN.XSPIClient.name());
            ev.setCondition("XServerCode,TenantCode", OPRelational.Eq, reqDelete.getXServerCode() + "," + reqDelete.getTenantCode());
            return ev;
        }
        _sCtx.getSysDB().DeleteEntity(_uac.getUserSession().getUser(), entity);

        EventSuccess evs = new EventSuccess(request);
        return evs;
    }
}
