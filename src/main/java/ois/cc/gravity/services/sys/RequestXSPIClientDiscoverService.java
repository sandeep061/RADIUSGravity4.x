package ois.cc.gravity.services.sys;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.DATEUtil;
import code.common.exceptions.CODEEntityNotFoundException;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.XSPIClientQuery;
import ois.cc.gravity.framework.events.common.EventEntitiesFetched;
import ois.cc.gravity.framework.requests.sys.RequestXSPIClientDiscover;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.sys.XSPIClient;
import java.util.ArrayList;
import java.util.Date;
import ois.radius.cc.entities.EN;

public class RequestXSPIClientDiscoverService extends ARequestEntityService
{

    public RequestXSPIClientDiscoverService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestXSPIClientDiscover req = (RequestXSPIClientDiscover) request;

        XSPIClient xspicon = _sCtx.getSysDB().Find(new XSPIClientQuery().filterByXServerCode(req.getXServerCode()).filterByTenantCode(req.getTenantCode()));

        if (xspicon == null)
        {
            throw new CODEEntityNotFoundException(EN.XSPIClient.name(), "TenantCode,XServerCode == " + req.getTenantCode() + "," + req.getXServerCode() + "]", OPRelational.Eq, req);
        }
        Date lastPingAt = xspicon.getPILastPingAt();
        Date now = DATEUtil.Now();

        Long Diff = DATEUtil.Diff(now, lastPingAt, DATEUtil.Unit.SECOND);
//        if (Diff > 30)
//        {
//            _logger.error("pingwindow is more than 30");
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.NoProviderimplFound, "[TenantCode==" + xspicon.getTenantCode() + ",XserverCode==" + xspicon.getXServerCode());
//        }

        ArrayList<AEntity> entites = new ArrayList<>();
        entites.add(xspicon);
        EventEntitiesFetched ev = new EventEntitiesFetched(req, entites);
        return ev;
    }
}
