package ois.cc.gravity.services.auth;



import code.ua.events.Event;
import code.ua.requests.Request;
import ois.radius.cc.entities.sys.Tenant;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.services.ARequestCmdService;

public abstract class RequestSUAbaseService extends ARequestCmdService
{

//    @Autowired
    public ServerContext _sCtx = ServerContext.This();

//    protected final MySQLDB _sysDB;

    protected Tenant _tenant;

    public RequestSUAbaseService(TenantContext tntctx)
    {
        this._tenant = tntctx.getTenant();
    }

    @Override
    public Event ProcessCmdRequest(Request request) throws Throwable
    {
        /**
         * Do common step
         */
//        RequestSUAbase req = (RequestSUAbase) request;
//        GReqCode code = (GReqCode) request.getReqCode();
//        switch (code)
//        {
//            case TenantStart:
//            case TenantStop:
//            {
//                _tenant = _sysDB.Find(Tenant.class, req.getTenantId());
//                if (_tenant == null)
//                {
//                    EventEntityNotFound ev = new EventEntityNotFound(request, GEN.Tenant);
//                    return null;
//                }
//            }
//            break;
//        }
        return DoProcessRequest(request);
    }

    protected abstract Event DoProcessSURequest(Request request) throws Throwable;

   

}
