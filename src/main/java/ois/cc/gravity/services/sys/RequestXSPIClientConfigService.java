package ois.cc.gravity.services.sys;

import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.requests.sys.RequestXSPIClientConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.sys.XSPIClient;

public class RequestXSPIClientConfigService extends ARequestEntityService
{

    public RequestXSPIClientConfigService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestXSPIClientConfig reqedit = (RequestXSPIClientConfig) request;
        String tntCode = reqedit.getTenantCode();
        String xsCode = reqedit.getXServerCode();

        JPAQuery query = new JPAQuery("SELECT xs FROM XSPIClient xs WHERE xs.TenantCode = :tenantCode AND xs.XServerCode = :xServerCode");
        query.setParam("tenantCode", tntCode);
        query.setParam("xServerCode", xsCode);
        XSPIClient entity = _sCtx.getSysDB().Find(EN.XSPIClient.getEntityClass(), query);
        if (entity == null)
        {
            entity = EntityBuilder.New(EN.XSPIClient);
        }
        entity = buildXspiClient(reqedit, entity);
        if (entity.getId() != null)
        {
            _sCtx.getSysDB().Update(_sCtx.getSysUser(), entity);
        }
        else
        {
            _sCtx.getSysDB().Insert(_sCtx.getSysUser(), entity);
        }

        EventSuccess evs = new EventSuccess(request);
        return evs;
    }

    private XSPIClient buildXspiClient(RequestXSPIClientConfig req, XSPIClient xspiclient)
    {

        xspiclient.setCCURL(req.getCCurl() == null ? xspiclient.getCCURL() : req.getCCurl());
        xspiclient.setNodeId(req.getNodeId() == null ? xspiclient.getNodeId() : req.getNodeId());
        xspiclient.setPIUAC(req.getPIUAC() == null ? xspiclient.getPIUAC() : req.getPIUAC());
        xspiclient.setTenantCode(req.getTenantCode());
        xspiclient.setXServerCode(req.getXServerCode());
        xspiclient.setPILastPingAt(req.getPILastPingAt() == null ? xspiclient.getPILastPingAt() : req.getPILastPingAt());
        return xspiclient;
    }
}
