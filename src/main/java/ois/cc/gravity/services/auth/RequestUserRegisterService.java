package ois.cc.gravity.services.auth;

import CrsCde.CODE.Common.Enums.DATEFormats;
import CrsCde.CODE.Common.Utils.DATEUtil;
import CrsCde.CODE.Common.Utils.JSONUtil;
import code.common.exceptions.CODEException;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.AppConst;
import ois.cc.gravity.framework.events.user.EventUserRegisterFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.ua.UACRegistry;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserSession;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.context.TenantContext;

import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.entities.util.AppUtil;
import ois.cc.gravity.framework.events.auth.EventUserRegistered;
import ois.cc.gravity.framework.requests.auth.RequestUserRegister;
import ois.cc.gravity.services.ARequestCmdService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import ois.radius.cc.entities.UserRole;

public class RequestUserRegisterService extends ARequestCmdService
{

    public ServerContext _sCtx;
    protected Tenant _tenant;

    public RequestUserRegisterService()
    {
        this._sCtx = ServerContext.This();
    }

    @Override
    public Event ProcessCmdRequest(Request request) throws Throwable
    {
        RequestUserRegister req = (RequestUserRegister) request;
        String token = req.getAuthToken();
        _tenant = _sCtx.getNucleusCtx().GetTenantByCode(req.getTenantCode());
        try
        {
            HashMap<String, String> nuProps = _sCtx.getNucleusCtx().ValidateUser(_tenant, token, req.getUserRole());
            if (_logger.isDebugEnabled())
            {
                _logger.trace(JSONUtil.ToJSON(nuProps).toString());
            }
            String tenantCode = nuProps.get("TenantCode");
            String nuToken = nuProps.get("Token");
            String loginId = nuProps.get("LoginId");

            // Find index of '@'
//            int atIndex = loginId.indexOf('@');
//
//            String rootUserLogin = loginId.substring(0, atIndex);
//            if (rootUserLogin.equals(_tenant.getCode() + "RootUser"))
//            {
//                loginId = rootUserLogin;
//            }
            String userId = nuProps.get("UserId");

            TenantContext tctx = _sCtx.GetTenantCtxByCode(tenantCode);
            if (tctx == null)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantNotStartedYet, "[Tenant.Code==" + tenantCode + "]");
            }

            HashMap<String, ArrayList<String>> fltr = new HashMap<>();
            fltr.put("byloginid", new ArrayList<>(List.of(loginId)));

            ArrayList<User> nuUsers = tctx.getNucleusCtx().GetUsers(tenantCode, fltr, req.getUserRole());
            if (nuUsers.isEmpty())
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UserNotFoundFromNucleus, loginId);
            }

            User user = nuUsers.get(0);
//            String grvtToken = generateToken(token, userId, req.getUserRole());

            DoSuccessLoginProcess(req, nuToken, user);

            EventUserRegistered ev = new EventUserRegistered(req);
            ev.setAccessToken(nuToken);
            ev.setTenantCode(tenantCode);
            return ev;
        }
        catch (Throwable e)
        {
            EventUserRegisterFailed evF = new EventUserRegisterFailed(request);

            JSONObject errResp = null;

            _logger.error(e.getMessage(), e);
            if (e.getCause() != null)
            {
                String exMsg = e.getCause().getMessage();

                try
                {
                    errResp = new JSONObject(exMsg);
                }
                catch (Exception ex)
                {
                    evF.setMessage(exMsg);
                    return evF;
                }
            }

            if (e instanceof GravityUnhandledException)
            {
                GravityUnhandledException gex = (GravityUnhandledException) e;
                if (errResp.getJSONObject("Error").get("Code").toString().equals("UNAUTHORIZED") || errResp.get("Message").toString().contains("Token"))
                {
                    evF.setCause(EventUserRegisterFailed.Cause.InvalidToken);
                }

            }
            if (e instanceof GravityRuntimeCheckFailedException)
            {
                throw e;
            }

            evF.setMessage(e.getMessage());
//            evF.setCause(e.get);
            return evF;
        }

    }

    private void DoSuccessLoginProcess(RequestUserRegister reqlogin, String token, User user) throws GravityException, CODEException
    {
        try
        {
            //set to uac
            //It must set after successfully logged in.
            TenantContext cCtx = _sCtx.GetTenantCtxByCode(_tenant.getCode());

            MySQLDB db = cCtx.getDB();

            //Create new usersessoin and map to uaclient.
            UserSession us = InitUserSession(reqlogin, user, getUserRole(reqlogin.getUserRole()), AppConst.NU_SYS_APPCode);
            db.Insert(user, us);

            UAClient _uac = UACRegistry.This().NewUAC(token, cCtx, us);
            _uac.setCtClient(cCtx.getTenant());
            _uac.setCCtx(cCtx);

            _uac.setUserSession(us);

            //On login success we need to map the uac with ctclient in UACRegistry.
            UACRegistry.This().MapLoggedInUACs(_uac, _tenant);
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }

    }

    /**
     * Init an usersession object.
     *
     * @param user
     */
    private UserSession InitUserSession(RequestUserRegister reqlogin, User user, UserRole role, String appcode) throws Exception
    {
        UserSession us = new UserSession();
        us.setSessionId(UUID.randomUUID().toString());
        us.setLoginId(user.getLoginId());
        us.setApplicationCode(appcode);
//        us.setDevice(reqlogin.getDevice());
        us.setLoginAt(DATEUtil.Now());
//        us.setRemoteIP(reqlogin.getRemoteIP());
        us.setUser(user);
        us.setUserRole(role);

        return us;
    }

//    private String generateToken(String appcode, String username, String role) throws Throwable
//    {
//        //TBD: JSON body need to include User code
//        String timestamp = DATEUtil.ToString(DATEUtil.Now(), DATEFormats.yyyyMMddHHmmss);
//
//        JSONObject tokenobj = new JSONObject();
//
//        tokenobj.put("Date", timestamp);
//        tokenobj.put("TenantCode", _tenant.getCode());
//        tokenobj.put("appcode", appcode);
//        tokenobj.put("username", username);
//        tokenobj.put("UserRole", role);
//        return AppUtil.Encrypt(tokenobj.toString());
//    }
    private UserRole getUserRole(String userrole)
    {
        switch (userrole.toLowerCase())
        {
            case "system":
                return UserRole.System;
            case "agent":
                return UserRole.Agent;
            case "server":
                return UserRole.Server;
            case "admin":
            case "service":
                return UserRole.Admin;
            default:
                return null;
        }

    }

}
