package ois.cc.gravity.services.sys;

import code.ua.events.Event;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.requests.sys.RequestXSPIConnectAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.sys.XSPIConnect;

public class RequestXSPIConnectAddService extends ARequestEntityService
{

    public RequestXSPIConnectAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestXSPIConnectAdd reqadd = (RequestXSPIConnectAdd) request;
//        String tntCode = reqedit.getTenantCode();
//        String xsCode = reqedit.getXServerCode();

//        JPAQuery query = new JPAQuery("SELECT xc FROM XSPIConnect xc WHERE xc.TenantCode = :tenantCode AND xc.XServerCode = :xServerCode");
//        query.setParam("tenantCode", tntCode);
//        query.setParam("xServerCode", xsCode);
//        XSPIConnect entity = _sCtx.getSysDB().Find(EN.XSPIConnect.getEntityClass(), query);
//        if (entity == null)
//        {
        XSPIConnect   entity = EntityBuilder.New(EN.XSPIConnect);
//        }

        entity = buildXspiConnect(reqadd, entity);
//        if (entity.getId() != null)
//        {
//            _sCtx.getSysDB().Update(_sCtx.getSysUser(), entity);
//        }
//        else
//        {
        _sCtx.getSysDB().Insert(_sCtx.getSysUser(), entity);
//        }

        EventSuccess evs = new EventSuccess(request);
        return evs;
    }

    private XSPIConnect buildXspiConnect(RequestXSPIConnectAdd req, XSPIConnect xspiconnect)
    {
        xspiconnect.setCCUAC(req.getCCUAC());
        xspiconnect.setPIUAC(req.getPIUAC());
        xspiconnect.setTenantCode(req.getTenantCode());
        xspiconnect.setXServerCode(req.getXServerCode());
        xspiconnect.setXSPIConnId(req.getXSPIConnId());
        xspiconnect.setMapAt(req.getMapAt());
        xspiconnect.setUnmapAt(req.getUnmapAt());
        xspiconnect.setCCUACStatus(req.getCCUACStatus());
        xspiconnect.setNodeId(req.getNodeId());
        xspiconnect.setPIUACStatus(req.getPIUACStatus());
        return xspiconnect;
    }
}