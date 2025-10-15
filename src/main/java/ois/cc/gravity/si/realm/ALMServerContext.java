package ois.cc.gravity.si.realm;

import CrsCde.CODE.Common.Collections.TWHashMap;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.si.nucleus.NucleusClientContext;
import ois.radius.cc.entities.sys.Tenant;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ois.cc.gravity.services.exceptions.GravityUnhandledRealMException;

/*
 *
 * @author rumana.begum
 * @since 20 Jun, 2024
 */
public class ALMServerContext
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private final RealMInvoker _invoker;

    private static ALMServerContext _this = null;

    private final HashMap<String, ALMClientContext> _almcctx;

    private final TWHashMap<String, String> _hmTntToken;

    private ALMServerContext()
    {
        this._invoker = new RealMInvoker();
        this._almcctx = new HashMap<>();
        this._hmTntToken = new TWHashMap<>();
    }

    public static synchronized ALMServerContext This()
    {
        return _this;
    }

    public static void Init()
    {
        if (_this == null)
        {
            _this = new ALMServerContext();
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

//    private synchronized String TenantLogin(String tntcode) throws GravityUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/register";
//
////    reqJson.put("LoginId", tenant.getDefAdminLoginId());
////    reqJson.put("Password", tenant.getDefAdminPassword());
////    reqJson.put("TenantCode", tenant.getCode());
//        NucleusClientContext nuclxt = ServerContext.This().getNucleusCtx().GetNucleusTenantContext(tntcode);
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("Token", nuclxt.get_tenantToken());
////    RequestRegister req = new RequestRegister(UIDUtil.GenerateUniqueId());
////    req.setTenantCode(otenant.getCode());
////    req.setToken(nuclxt.getTenantToken());
//
//        String body = _invoker.SendToRealMSerice(url, "POST", reqJson, null);
//        JSONObject bodyJson = new JSONObject(body);
//
//        String token = bodyJson.getString("Token");
//        addTenantToken(tntcode, token);
//
//        return token;
//    }
    public synchronized String TenantLogin(String tntcode) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/tenants/" + tntcode + "/register";

        NucleusClientContext nuclxt = ServerContext.This().getNucleusCtx().GetNucleusTenantContext(tntcode);

//        RequestRegister req = new RequestRegister(UIDUtil.GenerateUniqueId());
//        req.setTenantCode(otenant.getCode());
        JSONObject reqJson = new JSONObject();
        reqJson.put("auth_code", nuclxt.get_tenantToken());
        reqJson.put("role", "Service");
        String event = _invoker.SendToRealMSericeForAuth(url, "POST", reqJson, null);

        JSONObject resJosn = null;
        String token = null;
        try
        {
            resJosn = new JSONObject(event);
            if (resJosn.has("Token"))
            {
                token = resJosn.getString("Token");
                addTenantToken(tntcode, token);

            }
        }
        catch (Throwable th)
        {
            logger.error(event);
        }

        return token;

    }

    public ALMClientContext CreateTenantCtx(String tntcode) throws GravityUnhandledException
    {
        String token = TenantLogin(tntcode);
        ALMClientContext cctx = new ALMClientContext(tntcode);
        cctx.initTenantToken(token);
        _almcctx.put(tntcode, cctx);

        return cctx;
    }

//    private String GenerateToken(String tntcode) throws GravityUnhandledException, GravityRuntimeCheckFailedException
//    {
//        if (!_almcctx.containsKey(tntcode))
//        {
//            ALMClientContext nucleuscctx = CreateTenantCtx(tntcode);
//            _almcctx.put(tntcode, nucleuscctx);
//        }
//
//        return _almcctx.get(tntcode).getTenantToken();
//    }
    String regenerateToken(String oldtoken) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        if (!_hmTntToken.containsValue(oldtoken))
        {
            throw new GravityUnhandledException(new Exception("Token not found..."));
        }

        String tntCode = _hmTntToken.getKey(oldtoken);
        ALMClientContext nutcxt = _almcctx.get(tntCode);
        if (nutcxt == null)
        {
            throw new GravityUnhandledException(new Exception("Tenant not found with Code : " + tntCode));
        }
        String usrToken = TenantLogin(nutcxt.getTenantCode());
        nutcxt.initTenantToken(usrToken);

        return usrToken;
    }

    public synchronized void TenantLogout(Tenant tenant) throws GravityUnhandledException
    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/user-signout";
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("LoginId", tenant.getDefAdminLoginId());
//        reqJson.put("Password", tenant.getDefAdminPassword());
//        reqJson.put("TenantCode", tenant.getCode());
//        String body = _invoker.SendToRealMSerice(url, "POST", reqJson, null);
//        JSONObject bodyJson = new JSONObject(body);

        //set _suToken.
//        _tenantToken = bodyJson.getString("Token");
        removeTenantToken(tenant.getCode());
        if (_almcctx.containsKey(tenant.getCode()))
        {
            _almcctx.remove(tenant.getCode());
        }
    }

    public ALMClientContext GetALMTenantContext(String tntcode) throws GravityRuntimeCheckFailedException
    {
        if (_almcctx.containsKey(tntcode))
        {
            return _almcctx.get(tntcode);
        }
        else
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantNotFoundFromALM);
        }
    }

}
