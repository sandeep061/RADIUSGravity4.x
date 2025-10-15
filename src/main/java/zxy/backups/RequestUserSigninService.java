//package  ois.radius.core.gravity.services.auth;
//
//import ois.radius.core.gravity.entities.sys.UserType;
//import ois.radius.core.gravity.entities.util.AppUtil;
//import ois.radius.core.gravity.framework.events.auth.EventUserRegistered;
//import ois.radius.core.gravity.framework.requests.auth.RequestUserSignin;
//import org.json.JSONObject;
//
//import CrsCde.CODE.Common.Enums.DATEFormats;
//import CrsCde.CODE.Common.Utils.DATEUtil;
//import code.ua.events.Event;
//import code.ua.requests.Request;
//import  ois.radius.core.gravity.context.TenantContext;
//
//
//public class RequestUserSigninService extends RequestUserAbaseService
//{
//
//    public RequestUserSigninService(TenantContext tntctx)
//    {
//
//        super(tntctx);
//    }
//
//    @Override
//    protected Event DoProcessUserRequest(Request request) throws Throwable
//    {
//        /**
//         * TBD:<br>
//         * User sign-in will be validate from RADIUS using API.<br>
//         * Token will generate on successful sign-in.<br>
//         * the token will be used as auth token for all request from this same user.
//         */
//        RequestUserSignin req = (RequestUserSignin) request;
//        String utoken = generateToken(req.getLoginId(), req.getPassword(), req.getTenantCode());
////        String utoken=req.getToken();
//        _sCtx.AddSession(utoken);
//        EventUserRegistered ev = new EventUserRegistered(req);
//        ev.setTenantCode(req.getTenantCode());
//        ev.setToken(utoken);
//
//        return ev;
//    }
//
//    private String generateToken(String loginid, String password, String tenantcode) throws Throwable
//    {
//        //TBD: JSON body need to include User code
//        String timestamp = DATEUtil.ToString(DATEUtil.Now(), DATEFormats.yyyyMMddHHmmss);
//
//        JSONObject tokenobj = new JSONObject();
//        tokenobj.put("LoginId", loginid);
//        tokenobj.put("Password", password);
//        tokenobj.put("Date", timestamp);
//        tokenobj.put("UserType", UserType.User);
//        tokenobj.put("TenantCode", tenantcode);
//        return AppUtil.Encrypt(tokenobj.toString());
//    }
//
//
//
//}
//
