package ois.cc.gravity.services.auth;

import code.common.exceptions.CODEException;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.framework.events.auth.EventUserLogout;
import ois.cc.gravity.framework.requests.auth.RequestUserLogout;
import ois.cc.gravity.services.ARequestCmdService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UACRegistry;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RequestUserLogoutService extends ARequestCmdService
{
    protected Tenant _tenant;
    public ServerContext _sCtx;
    public RequestUserLogoutService()
    {
        this._sCtx = ServerContext.This();
    }

    @Override
    public Event ProcessCmdRequest(Request request) throws Throwable
    {
        RequestUserLogout req = (RequestUserLogout) request;
        String token = req.getToken();

        HashMap<String, String> nuProps = _sCtx.getNucleusCtx().ValidateUser(_tenant,token, req.getUserRole());

        String tenantCode = nuProps.get("TenantCode");
        String loginId = nuProps.get("LoginId");

        TenantContext tctx = _sCtx.GetTenantCtxByCode(tenantCode);

        if (tctx == null)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantNotStartedYet, "[Tenant.Code==" + tenantCode + "]");
        }
        _tenant = _sCtx.getNucleusCtx().GetTenantByCodeAssert(tenantCode);

        UAClient uac = UACRegistry.This().findBy_Client_LoginId(_tenant,loginId);
        HashMap<String, ArrayList<String>> fltr = new HashMap<>();
        fltr.put("byloginid", new ArrayList<>(List.of(loginId)));

        ArrayList<User> nuUsers = tctx.getNucleusCtx().GetUsers(tenantCode, fltr, req.getUserRole());
        if (nuUsers.isEmpty())
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UserNotFoundFromNucleus, loginId);
        }

        User user = nuUsers.get(0);
        UserSession userSession = uac.getUserSession();

      DoSuccessLogoutProcess(userSession,user);
        UACRegistry.This().Remove(uac);

        EventUserLogout ev=new EventUserLogout(req);
        ev.setAccessToken(req.getToken());
//        return new EventOK(request,)

        return ev;
    }

    private void DoSuccessLogoutProcess(UserSession us,User user) throws GravityException, CODEException
    {
        TenantContext cCtx = _sCtx.GetTenantCtxByCode(_tenant.getCode());

        MySQLDB db = cCtx.getDB();
        us.setEndReason(UserSession.EndReason.UserLoggedOut);
        us.setLogoutAt(new Date());
        us.setEndAt(new Date());
        db.Update(user,us);

    }
}
