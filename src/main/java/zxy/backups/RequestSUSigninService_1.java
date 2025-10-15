//package ois.radius.core.gravity.services.auth;
//
//import ois.radius.core.gravity.entities.sys.UserType;
//import ois.radius.core.gravity.framework.events.auth.EventUserRegistered;
//import org.json.JSONObject;
//
//import CrsCde.CODE.Common.Enums.DATEFormats;
//import CrsCde.CODE.Common.Utils.DATEUtil;
//import code.ua.events.Event;
//import code.ua.events.EventAuthenticationFailed;
//import code.ua.requests.Request;
//import ois.radius.core.gravity.AppConst;
//import ois.radius.core.gravity.entities.util.AppUtil;
//import ois.radius.core.gravity.context.TenantContext;
//import ois.radius.core.gravity.framework.requests.auth.RequestSUSignin;
//
//public class RequestSUSigninService extends RequestSUAbaseService
//{
//
//    public RequestSUSigninService(TenantContext tntctx)
//    {
//        super(tntctx);
//
//    }
//
//    @Override
//    public Event DoProcessSURequest(Request request) throws Throwable
//    {
//        RequestSUSignin req = (RequestSUSignin) request;
//        String loginid = req.getLoginId();
//        String password = req.getPassword();
//
//        if (true)
//        {
//            String token = generateToken(loginid, password);
//            _sCtx.AddSession(token);
//            EventUserRegistered evs = new EventUserRegistered(request);
////            evs.setLoginId(req.getLoginId());
//            evs.setToken(token);
//            evs.setMessage("SU logged in successfully.");
//            return evs;
//        }
//
//        EventAuthenticationFailed evf = new EventAuthenticationFailed(request);
//        evf.setMessage("SU login failed due to wrong credential.");
//        return evf;
//
//    }
//
//    private boolean validateCredential(String loginid, String Password)
//    {
//        if (loginid.equals(AppConst.SU_USER_LOGIN_ID) && Password.equals(AppConst.SU_USER_PASSWORD))
//        {
//            return true;
//        }
//        return false;
//    }
//
//    private String generateToken(String loginid, String password) throws Throwable
//    {
//        // TBD: JSON body need to include User code
//        String timestamp = DATEUtil.ToString(DATEUtil.Now(), DATEFormats.yyyyMMddHHmmss);
//
//        JSONObject tokenobj = new JSONObject();
//        tokenobj.put("LoginId", loginid);
//        tokenobj.put("Password", password);
//        tokenobj.put("Date", timestamp);
//        tokenobj.put("UserType", UserType.System);
//        return AppUtil.Encrypt(tokenobj.toString());
//    }
//
//}
