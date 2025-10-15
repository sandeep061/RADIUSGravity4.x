package ois.cc.gravity.si.ucos;

import ois.cc.gravity.AppProps;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.radius.cc.entities.tenant.cc.AOPs;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UCOSClientContext
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private String _tenantToken;

    public String _tenantCode;

    private final UCOSInvoker _invoker;

    UCOSClientContext(String tntcode)
    {
        this._tenantCode = tntcode;
        this._invoker = new UCOSInvoker();
    }

    void initTenantToken(String token)
    {
        this._tenantToken = token;
    }

    public String getTenantToken()
    {
        return _tenantToken;
    }

    public String getTenantCode()
    {
        return _tenantCode;
    }

    public void GetProcess(AOPs aops) throws GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        String url = AppProps.RAD_UcoS_Service_Base_URL;
        switch (aops.getAOPsType())
        {
            case Campaign, Process ->
                url = url + "/process/" + aops.getCode();
        }
        String resp = _invoker.SendToUcosSerice(url, "GET", null, _tenantToken);
        JSONObject jsonresp = new JSONObject(resp);

        //key exist or not
        //if not success
        //If event.Entities is empty.
        if (!jsonresp.getString("EvType").equals("OK") || jsonresp.getString("EvType").equals("OK")
                && jsonresp.has("EvCodeApp") && "EntitiesFetched".equals(jsonresp.getString("EvCodeApp")) && (jsonresp.getJSONObject("Entity") == null || jsonresp.optJSONObject("Entity").isEmpty()))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AOPsNotFoundInUCOS, "AOPs not created Yet " + aops.getCode());
        }

    }

}
