package ois.cc.gravity.services.auth;

import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.services.ARequestCmdService;
import ois.radius.cc.entities.sys.Tenant;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.framework.requests.auth.RequestUserAbase;

public abstract class RequestUserAbaseService extends ARequestCmdService
{

//    @Autowired
    public ServerContext _sCtx = ServerContext.This();

//    public TenantContext _tCtx;

    protected Tenant _tenant;

    public RequestUserAbaseService()
    {
       
    }

    @Override
    protected Event ProcessCmdRequest(Request request) throws Throwable
    {
        RequestUserAbase req = (RequestUserAbase) request;
        GReqCode code = (GReqCode) request.getReqCode();
        if (code != GReqCode.UserSignin)
        {
//            EventProcessFailed evf = new EventProcessFailed(request, FailedCause.TenantNotStarted);
//            evf.setMessage("Please Check Tenant status.");
//            return evf;
        	return null;
        }
        return DoProcessUserRequest(request);
    }

    protected abstract Event DoProcessUserRequest(Request request) throws Throwable;

}

