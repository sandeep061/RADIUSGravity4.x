package ois.cc.gravity.si.nucleus;

import CrsCde.CODE.Common.Collections.TWHashMap;
import com.google.gson.Gson;
import ois.cc.gravity.framework.requests.auth.RequestSUSignin;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.sys.Tenant;
import ois.cc.gravity.AppConst;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.radius.cc.entities.tenant.cc.User;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NucleusServerContext
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private String _suToken;

    private final NucleusInvoker _invoker;

    private static NucleusServerContext _this = null;

    private final HashMap<String, NucleusClientContext> _nucleuscctx;

    private final HashMap<String, Tenant> _hmTnt;

    /**
     * Tenant.Code-Token
     */
    private final TWHashMap<String, String> _hmTntToken;

    private NucleusServerContext()
    {
        this._invoker = new NucleusInvoker();
        this._nucleuscctx = new HashMap<>();
        this._hmTnt = new HashMap<>();
        this._hmTntToken = new TWHashMap<>();
    }

    String getSuToken()
    {
        return _suToken;
    }

    public static synchronized NucleusServerContext This()
    {
        return _this;
    }

    public static void Init()
    {
        if (_this == null)
        {
            _this = new NucleusServerContext();
        }
    }

    private void addTenantToken(String code, String token)
    {
        synchronized (_hmTntToken)
        {
            _hmTntToken.put(code, token);
        }

    }

    private void removeTenantToken(String tntcode)
    {
        Iterator<Map.Entry<String, String>> itr = _hmTntToken.entrySet().iterator();
        while (itr.hasNext())
        {
            Map.Entry<String, String> next = itr.next();
            if (next.getKey().equals(tntcode))
            {
                itr.remove();
            }

        }
    }

    public void SUSignIn() throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/sys-admin/sign-in";

        JSONObject reqJson = new JSONObject();
        reqJson.put("LoginId", "suroot");
        reqJson.put("Password", "suroot");

        String evSignIn = _invoker.SendToNucleusSerice(url, "POST", reqJson, null);

        try
        {
            JSONObject evJson = new JSONObject(evSignIn);
            if (evJson.has("EvCodeApp") && evJson.getString("EvCodeApp").equals("AuthenticationSuccess"))
            {
                _suToken = evJson.getString("AccessToken");
            }
            else
            {
                throw new Exception("Getting during su-signin : " + evJson.toString());
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UnAuthenticatedUser, ex.getMessage());
        }

    }

    public Tenant GetTenantByCode(String tenantcode) throws GravityUnhandledException
    {
        Tenant tenant = null;
        if (_hmTnt.containsKey(tenantcode))
        {
            return _hmTnt.get(tenantcode);
        }

        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tenantcode;
        JSONObject reqJson = new JSONObject();

        String jsonStr = _invoker.SendToNucleusSerice(url, "GET", reqJson, _suToken);

        ArrayList<Tenant> tenants = NucleusPob.BuildTenant(jsonStr);
        if (tenants != null && !tenants.isEmpty())
        {
            tenant = tenants.get(0);
        }

        _hmTnt.put(tenantcode, tenant);

        return tenant;
    }

    public Tenant GetTenantByCodeAssert(String tenantcode) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        Tenant tenant = null;
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tenantcode;
        JSONObject reqJson = new JSONObject();

        String jsonStr = _invoker.SendToNucleusSerice(url, "GET", reqJson, _suToken);

        ArrayList<Tenant> tenants = NucleusPob.BuildTenant(jsonStr);
        if (tenants == null || tenants.isEmpty())
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantNotFoundFromNucleus);
        }
        tenant = tenants.get(0);
        return tenant;
    }

    public ArrayList<Tenant> GetTenants(HashMap<String, ArrayList<String>> filters) throws GravityUnhandledException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants";

        if (filters != null && !filters.isEmpty())
        {
            try
            {
                Gson gson = new Gson();
                String jsonFilters = gson.toJson(filters);
                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
                url = url + "?filters=" + fltrs;
            }
            catch (Throwable th)
            {
                throw new GravityUnhandledException(th);
            }

        }
        JSONObject reqJson = new JSONObject();
        String jsonStr = _invoker.SendToNucleusSerice(url, "GET", reqJson, _suToken);

        return NucleusPob.BuildTenant(jsonStr);

    }

    public String GenerateToken(Tenant tnt) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        if (!_nucleuscctx.containsKey(tnt.getCode()))
        {
            NucleusClientContext nucleuscctx = createTenantCtx(tnt);
            _nucleuscctx.put(tnt.getCode(), nucleuscctx);
        }

        return _nucleuscctx.get(tnt.getCode()).get_tenantToken();
    }

    String regenerateToken(String tntcode) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {

        NucleusClientContext nutcxt = _nucleuscctx.get(tntcode);
        if (nutcxt == null)
        {
            throw new GravityUnhandledException(new Exception("Tenant not found with Code : " + tntcode));
        }
        String usrToken = TenantLogin(nutcxt.getTenant());
        nutcxt.initToken(usrToken);

        return usrToken;
    }

    private NucleusClientContext createTenantCtx(Tenant tenant) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        String usrToken = TenantLogin(tenant);
        NucleusClientContext nucleuscctx = new NucleusClientContext(tenant);
        nucleuscctx.initToken(usrToken);
        return nucleuscctx;
    }

    public void StopTenant(Tenant tenant) throws GravityUnhandledException
    {
        NucleusClientContext nuCctx = GetNucleusTenantContext(tenant.getCode());
        if (nuCctx == null)
        {
            logger.trace("TenantConetxt not found for tenant : " + tenant.getCode());
        }

        removeTenantToken(tenant.getCode());
        _nucleuscctx.remove(tenant.getCode());
    }

    public NucleusClientContext GetNucleusTenantContext(String tntcode)
    {
        synchronized (_nucleuscctx)
        {
            if (_nucleuscctx.containsKey(tntcode))
            {
                return _nucleuscctx.get(tntcode);
            }
            return null;
        }
    }

    public String TenantLogin(Tenant tenant) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tenant.getCode() + "/users/auth?response_type=code id_token";

        JSONObject reqJson = new JSONObject();
        reqJson.put("LoginId", tenant.getCode() + "RootUser");
        reqJson.put("Password", tenant.getCode() + "RootPwd");
        reqJson.put("AuthorizeBy", "ApiService");

        String evSignIn = _invoker.SendToNucleusForAuth(url, "POST", tenant.getCode(), reqJson);

        try
        {
            JSONObject evJson = new JSONObject(evSignIn);
            if (evJson.has("KeyValue"))
            {
                JSONObject authJson  = evJson.getJSONObject("KeyValue");

                if (authJson.has("AuthCode"))
                {
                    String authCode = authJson.getString("AuthCode");
                    addTenantToken(tenant.getCode(), authCode);
                    return authCode;
                }
                else
                {
                    throw new Exception("Invalid user");
                }
            }
            else
            {
                throw new Exception("Invalid user");
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UnAuthenticatedUser, "[User.LoginId = " + tenant.getDefAdminLoginId());
        }

    }

    public HashMap<String, String> ValidateUser(Tenant tnt, String token, String role) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tnt.getCode() + "/users/auth/token";

        JSONObject reqJson = new JSONObject();
        reqJson.put("grant_type", "authorization_code");
        reqJson.put("auth_code", token);
        reqJson.put("role", role);

        String evSignIn = _invoker.SendToNucleusForGetToken(url, "POST", tnt.getCode(), reqJson);

        try
        {
            JSONObject evJson = new JSONObject(evSignIn);
            if (evJson.has("EvCodeApp") && evJson.getString("EvCodeApp").equals("AuthenticationSuccess"))
            {
                return parseAuthToken(evJson.getString("AccessToken"));
            }
            else
            {
                String errMsg = evJson.has("Error") ? evJson.getString("Error") : "Unhandeled Error";
                throw new Exception(errMsg);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UnAuthorizedUser, ex.getMessage());
        }

    }

    private HashMap<String, String> parseAuthToken(String token)
    {
        /**
         * Example after parse the data : <br>
         * {"LoginId":"RADIUSINTELLORootUser","UserId":1,"UserRoles":["SERVICE"],"TenantCode":"RADIUSINTELLO","FullNmae":"RADIUSINTELLODefaultUser","ServiceID":"dark","sub":"radiusintello_RADIUSINTELLORootUser","jti":"fead8fce-0b19-422c-a05e-25951d7a2549","iss":"nucleus-auth","aud":"dark","iat":1752633282,"exp":1784169282}
         */
        HashMap<String,String> hmRes = new HashMap<>();

        String data = token.split("\\.")[1];
        String body = new String(Base64.getUrlDecoder().decode(data), StandardCharsets.UTF_8);
        JSONObject authJsn = new JSONObject(body);

        hmRes.put("Token", token);
        hmRes.put("LoginId", authJsn.get("LoginId").toString());
        hmRes.put("UserId", authJsn.get("UserId").toString());
        hmRes.put("TenantCode", authJsn.getString("TenantCode"));

        return hmRes;
    }

    public void ValidateSysUser(RequestSUSignin req, User sysUser) throws GravityRuntimeCheckFailedException
    {
        if (!(req.getLoginId().equals(sysUser.getLoginId())))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UnAuthorizedUser, "LoginId/Password Incorrect");
        }
    }

    public User DummySysUser() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        User user = new User();
//        user.setId(AppConst.SYS_USER_EN_ID);
        user.setLoginId(AppConst.SU_USER_LOGIN_ID);
//        user.setPassword(PWDUtil.Encrypt(AppConst.SU_USER_PASSWORD));
//        user.setUserRole(UserRole.System);

        return user;
    }

    public User GetTenantAdminLogin() throws GravityUnhandledException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/validate";

        JSONObject reqJson = new JSONObject();
//        reqJson.put("Token", token);
//        reqJson.put("AppCode", AppConst.NU_SYS_APPCode);
//        reqJson.put("AppUserRoleCode", role);

        String evSignIn = _invoker.SendToNucleusSerice(url, "POST", reqJson, null);
        return null;
    }

}
