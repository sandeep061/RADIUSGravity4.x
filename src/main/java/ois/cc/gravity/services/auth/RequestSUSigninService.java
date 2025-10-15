/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ois.cc.gravity.services.auth;

import CrsCde.CODE.Common.Enums.DATEFormats;
import CrsCde.CODE.Common.Utils.DATEUtil;
import code.common.exceptions.CODEException;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.AppConst;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.entities.util.AppUtil;
import ois.cc.gravity.framework.events.auth.EventUserRegistered;
import ois.cc.gravity.framework.requests.auth.RequestSUSignin;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UACRegistry;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserSession;
import org.json.JSONObject;

import java.util.UUID;
import ois.radius.cc.entities.UserRole;

/**
 *
 * @author Sandeepkumar.Sahoo
 * @since 14 Jan 2025
 */
public class RequestSUSigninService extends RequestSUAbaseService
{

    public RequestSUSigninService(TenantContext tntctx)
    {
        super(tntctx);
    }

    @Override
    public Event ProcessCmdRequest(Request request) throws Throwable
    {
        RequestSUSignin req = (RequestSUSignin)request;

        User sysUser = _sCtx.getNucleusCtx().DummySysUser();

        _sCtx.getNucleusCtx().ValidateSysUser(req,sysUser);
        //generate token and send in event.
        String grvtToken = generateToken(sysUser.getLoginId(),UserRole.System.name());
        DoSuccessLoginProcess(req, grvtToken, sysUser);

        EventUserRegistered ev = new EventUserRegistered(req);
        ev.setAccessToken(grvtToken);
        ev.setTenantCode(req.getTenantCode());
        return ev;
    }

    @Override
    protected Event DoProcessSURequest(Request request) throws Throwable
    {
        return null;
    }

    private String generateToken( String username, String role) throws Throwable
    {
        //TBD: JSON body need to include User code
        String timestamp = DATEUtil.ToString(DATEUtil.Now(), DATEFormats.yyyyMMddHHmmss);

        JSONObject tokenobj = new JSONObject();

        tokenobj.put("Date", timestamp);
        tokenobj.put("TenantCode", _tenant.getCode());
//        tokenobj.put("appcode", appcode);
        tokenobj.put("username", username);
        tokenobj.put("UserRole", role);
        return AppUtil.Encrypt(tokenobj.toString());
    }
    private void DoSuccessLoginProcess(RequestSUSignin reqlogin, String token, User user) throws GravityException, CODEException
    {
        try
        {
            //set to uac
            //It must set after successfully logged in.
            TenantContext cCtx = _sCtx.GetTenantCtxByCode(_tenant.getCode());

//            MySQLDB db = cCtx.getDB();
//
//            //Create new usersessoin and map to uaclient.
            UserSession us = InitUserSession(reqlogin, user, AppConst.NU_SYS_APPCode);
//            db.Insert(user, us);

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
    private UserSession InitUserSession(RequestSUSignin reqlogin, User user, String appcode) throws Exception
    {
        UserSession us = new UserSession();
        us.setSessionId(UUID.randomUUID().toString());
        us.setLoginId(user.getLoginId());
        us.setApplicationCode(appcode);
//        us.setDevice(reqlogin.getDevice());
        us.setLoginAt(DATEUtil.Now());
//        us.setRemoteIP(reqlogin.getRemoteIP());
        us.setUser(user);

        return us;
    }

}
