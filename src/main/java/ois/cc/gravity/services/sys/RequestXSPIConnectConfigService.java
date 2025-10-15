package ois.cc.gravity.services.sys;

import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.requests.sys.RequestXSPIConnectConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.sys.XSPIConnect;

public class RequestXSPIConnectConfigService extends ARequestEntityService
{

    public RequestXSPIConnectConfigService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestXSPIConnectConfig reqedit = (RequestXSPIConnectConfig) request;
        String tntCode = reqedit.getTenantCode();
        String xsCode = reqedit.getXServerCode();

        JPAQuery query = new JPAQuery("SELECT xc FROM XSPIConnect xc WHERE xc.TenantCode = :tenantCode AND xc.XServerCode = :xServerCode");
        query.setParam("tenantCode", tntCode);
        query.setParam("xServerCode", xsCode);
        XSPIConnect entity = _sCtx.getSysDB().Find(EN.XSPIConnect.getEntityClass(), query);
        if (entity == null)
        {
            entity = EntityBuilder.New(EN.XSPIConnect);
        }
        entity = buildXspiConnect(reqedit, entity);
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

    private XSPIConnect buildXspiConnect(RequestXSPIConnectConfig req, XSPIConnect xspicon)
    {
        xspicon.setCCUAC(req.getCCUAC() == null ? xspicon.getCCUAC() : req.getCCUAC());
        xspicon.setPIUAC(req.getPIUAC() == null ? xspicon.getPIUAC() : req.getPIUAC());
        xspicon.setTenantCode(req.getTenantCode());
        xspicon.setXServerCode(req.getXServerCode());
        xspicon.setXSPIConnId(req.getXSPIConnId() == null ? xspicon.getXSPIConnId() : req.getXSPIConnId());
        xspicon.setMapAt(req.getMapAt() == null ? xspicon.getMapAt() : req.getMapAt());
        xspicon.setUnmapAt(req.getUnmapAt() == null ? xspicon.getUnmapAt() : req.getUnmapAt());
        xspicon.setCCUACStatus(req.getCCUACStatus() == null ? xspicon.getCCUACStatus() : req.getCCUACStatus());
        xspicon.setNodeId(req.getNodeId() == null ? xspicon.getNodeId() : req.getNodeId());
        xspicon.setPIUACStatus(req.getPIUACStatus() == null ? xspicon.getPIUACStatus() : req.getPIUACStatus());
        return xspicon;
    }
}
