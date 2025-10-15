package ois.cc.gravity.si.nucleus;

import code.common.exceptions.CODEException;
import com.google.gson.Gson;
import ois.radius.cc.entities.tenant.cc.User;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ois.radius.cc.entities.sys.Tenant;

public class NucleusClientContext
{

    public final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tenant _tnt;

    private String _tenantToken;

    private final NucleusInvoker _invoker;

    private final String _suToken;

    NucleusClientContext(Tenant tnt)
    {
        this._tnt = tnt;
        this._invoker = new NucleusInvoker();
        this._suToken = NucleusServerContext.This().getSuToken();
    }

    public Tenant getTenant()
    {
        return _tnt;
    }

    void initToken(String token)
    {
        this._tenantToken = token;
    }

    public String get_tenantToken()
    {
        return _tenantToken;
    }

//    public void AddUser(String tencode, User user) throws GravityUnhandledException
//    {
//        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tencode + "/users";
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("Salutation", user.getSalutation());
//        reqJson.put("Name", user.getName());
//        reqJson.put("LoginId", user.getLoginId());
//        reqJson.put("Password", user.getPassword());
//
//        _invoker.SendToNucleusSerice(url, "POST", reqJson, _suToken);
//    }

//    public void EditUser(String tencode, User user) throws GravityUnhandledException
//    {
//        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tencode + "/users/" + user.getId();
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("Name", user.getName());
//        reqJson.put("LoginId", user.getLoginId());
//        reqJson.put("Password", user.getPassword());
//
//        _invoker.SendToNucleusSerice(url, "PUT", reqJson, _suToken);
//    }

    public User GetUserById(String tencode, String userid, String role) throws GravityUnhandledException, CODEException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tencode + "/users/" + userid;
        JSONObject reqJson = new JSONObject();

        String jsnStr = _invoker.SendToNucleusSerice(url, "GET", reqJson, _suToken);
        ArrayList<User> users = NucleusPob.BuildUser(tencode, jsnStr, role);
        if (users.isEmpty())
        {
            //We have send a exception here. As this supplied userid is not a valid user.
            return null;
        }
        return users.get(0);
    }

    public ArrayList<User> GetUsers(String tencode, HashMap<String, ArrayList<String>> filters, String role) throws GravityUnhandledException, CODEException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tencode + "/users";

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

        return NucleusPob.BuildUser(tencode, jsonStr, role);

    }

    public User GetUserByLoginId(String tencode, String loginid, String role) throws GravityUnhandledException, CODEException, Exception
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tencode + "/users";

        HashMap<String, ArrayList<String>> filters = new HashMap<>();
        filters.put("byloginid", new ArrayList<>(List.of(loginid)));
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

        JSONObject reqJson = new JSONObject();
        String jsonStr = _invoker.SendToNucleusSerice(url, "GET", reqJson, _suToken);
        logger.trace(" Users From Nucleus " + new JSONObject(jsonStr).getJSONArray("Entities").toString());
        ArrayList<User> users = NucleusPob.BuildUser(tencode, jsonStr, role);

        if (users.isEmpty())
        {
            return null;
        }
        return users.get(0);
    }

    public void DeleteUser(String tenantcode, String userid) throws GravityUnhandledException
    {
        String url = AppProps.RAD_Nucleus_Service_Base_URL + "/tenants/" + tenantcode + "/users/" + userid;
        JSONObject reqJson = new JSONObject();

        _invoker.SendToNucleusSerice(url, "DELETE", reqJson, _suToken);
    }

}
