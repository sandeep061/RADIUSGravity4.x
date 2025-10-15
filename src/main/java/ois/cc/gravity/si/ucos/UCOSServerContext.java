package ois.cc.gravity.si.ucos;

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

public class UCOSServerContext
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private String _tenantToken;

    private String _suToken;

    private final UCOSInvoker _invoker;

    private static UCOSServerContext _this = null;

    private HashMap<String, UCOSClientContext> _ucoscctx;

    private final TWHashMap<String, String> _hmTntToken;

    private UCOSServerContext()
    {
        this._invoker = new UCOSInvoker();
        _ucoscctx = new HashMap<>();
        this._hmTntToken = new TWHashMap<>();
    }

    public static synchronized UCOSServerContext This()
    {
        return _this;
    }

    public static void Init()
    {
        if (_this == null)
        {
            _this = new UCOSServerContext();
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

    public synchronized String TenantLogin(String tntcode) throws GravityUnhandledException
    {
        String url = AppProps.RAD_UcoS_Service_Base_URL +"/register";

        NucleusClientContext nuclxt = ServerContext.This().getNucleusCtx().GetNucleusTenantContext(tntcode);

        JSONObject reqJson = new JSONObject();
        reqJson.put("Authcode", nuclxt.get_tenantToken());
        reqJson.put("Role", "Service");
        reqJson.put("TenantCode", tntcode);
        String event = _invoker.SendToUcosSericeForAuth(url, "POST", reqJson, null);

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

    public UCOSClientContext CreateTenantCtx(String tntcode) throws GravityUnhandledException
    {
        String token = TenantLogin(tntcode);
        UCOSClientContext cctx = new UCOSClientContext(tntcode);
        cctx.initTenantToken(token);
        _ucoscctx.put(tntcode, cctx);

        return cctx;
    }

    String regenerateToken(String oldtoken) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        if (!_hmTntToken.containsValue(oldtoken))
        {
            throw new GravityUnhandledException(new Exception("Token not found..."));
        }

        String tntCode = _hmTntToken.getKey(oldtoken);
        UCOSClientContext nutcxt = _ucoscctx.get(tntCode);
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
        if (_ucoscctx.containsKey(tenant.getCode()))
        {
            _ucoscctx.remove(tenant.getCode());
        }
    }

    public UCOSClientContext GetUcosTenantContext(String tntcode) throws GravityRuntimeCheckFailedException
    {
        if (_ucoscctx.containsKey(tntcode))
        {
            return _ucoscctx.get(tntcode);
        }
        else
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantNotFoundFromALM);
        }
    }

}
