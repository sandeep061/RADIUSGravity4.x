package ois.cc.gravity.si.nucleus;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.LOGUtil;
import code.common.exceptions.CODEException;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.sys.Application;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.User;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import ois.cc.gravity.entities.util.UserUtil;

import ois.cc.gravity.services.exceptions.GravityException;

public class NucleusPob
{

    private static Logger _logger = LoggerFactory.getLogger(NucleusPob.class);

    public static ArrayList<Tenant> BuildTenant(String jsonstr) throws GravityUnhandledException
    {
        try
        {
            ArrayList<Tenant> alltenant = new ArrayList<>();

            if (jsonstr != null)
            {
                JSONObject jsonobj = new JSONObject(jsonstr);
                if (jsonobj.has("Object"))
                {
                    JSONObject tntJson = jsonobj.getJSONObject("Object");

                    if (tntJson.has("CreatedOn"))
                    {
                        tntJson.remove("CreatedOn");
                    }
                    if (tntJson.has("EditedOn"))
                    {
                        tntJson.remove("EditedOn");
                    }
                    Tenant tenat = JSONUtil.FromJSON((JSONObject) tntJson, Tenant.class);
                    alltenant.add(tenat);

                }
                else if (jsonobj.has("Objects"))
                {
                    JSONArray jsonArray = jsonobj.getJSONArray("Objects");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        JSONObject tntJson = jsonArray.getJSONObject(i);
                        if (tntJson.has("CreatedOn"))
                        {
                            tntJson.remove("CreatedOn");
                        }
                        if (tntJson.has("EditedOn"))
                        {
                            tntJson.remove("EditedOn");
                        }
                        Tenant tenant = JSONUtil.FromJSON((JSONObject) tntJson, Tenant.class);
                        alltenant.add(tenant);
                    }

                }

            }
            return alltenant;
        }
        catch (Exception ex)
        {
            throw new GravityUnhandledException(ex);
        }
    }

    /**
     * @param tntcode
     * @param jsonstr
     * @param role
     * @return
     * @throws GravityUnhandledException
     * @throws CODEException
     */
    public static ArrayList<User> BuildUser(String tntcode, String jsonstr, String role) throws GravityUnhandledException, CODEException
    {
        LOGUtil.ArgString(tntcode, jsonstr, role);

        /**
         * {
         * "ReqType": "SysAdmin", "EvCode": "ObjectsFetched", "Message": "User fetched successfully.", "Objects": [ { "LoginId": "RADIUSRootUser", "DoB": "Jul
         * 12, 2025, 12:40:49 PM", "FullName": "RADIUSDefaultUser", "Gender": "Other", "Id": 2, "NickName": "RADIUSDefaultUser" } ], "ReqId":
         * "NAS00WZ1752571391563AA", "ReqCode": "UserFetch", "EvGen": "Sync", "RecordCount": 1, "EvId": "XZ1752571391577AA", "EvType": "OK" }
         */
        try
        {
            ArrayList<User> allusers = new ArrayList<>();
            ArrayList<User> dbusers = new ArrayList<>();

            if (jsonstr != null)
            {
                JSONObject jsonobj = new JSONObject(jsonstr);
                if (jsonobj.has("Object"))
                {
                    JSONObject userJson = jsonobj.getJSONObject("Object");

                    User user = new User();
                    user.setName(userJson.getString("FullName"));
                    user.setLoginId(userJson.getString("LoginId"));
                    user.setUserId(userJson.get("Id").toString());
                    allusers.add(user);

                }
                else if (jsonobj.has("Objects"))
                {
                    JSONArray jsonArray = jsonobj.getJSONArray("Objects");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        JSONObject userJson = jsonArray.getJSONObject(i);

                        User user = new User();
                        user.setName(userJson.getString("FullName"));
                        user.setLoginId(userJson.getString("LoginId"));
                        user.setUserId(userJson.get("Id").toString());
                        allusers.add(user);
                    }

                }

            }
            if (!allusers.isEmpty())
            {
                dbusers = checkAndReturnGravityUser(tntcode, allusers, role);
            }
            return dbusers;
        }
        catch (GravityException | Exception ex)
        {
            throw new GravityUnhandledException(ex);
        }
    }

    private static ArrayList<User> checkAndReturnGravityUser(String tntcode, ArrayList<User> users, String role) throws GravityException, Exception, CODEException
    {
        TenantContext tcxt = ServerContext.This().GetTenantCtxByCode(tntcode);
        ArrayList<User> dbusers = new ArrayList<>();

        if (tcxt == null)
        {
            return users;
        }

        for (User user : users)
        {
            User dbUser = tcxt.getDB().Find(new UserQuery().filterByUserId(user.getUserId()));
            if (dbUser == null)
            {
                dbUser = new User();

                String loginId = user.getLoginId();

                dbUser.setLoginId(loginId);
                dbUser.setName(user.getName());
                dbUser.setUserId(user.getUserId());

                // dbUser.setUserRole(getUserRole(role));
                tcxt.getDB().Insert(user, dbUser);
                if (loginId.contains("@"))
                {
                    int atIndex = loginId.indexOf('@');

                    String rootUserLogin = loginId.substring(0, atIndex);
                    if (rootUserLogin.equalsIgnoreCase(tntcode + "RootUser"))
                    {
                        //Check default profile added for this default user or not.
                        UserUtil.AddProfileForDefaultUser(tcxt,dbUser);
//                        loginId=rootUserLogin;
                    }
                }
            }
            else
            {
                //For deleted user. If user is deleted then added with same login id then the user id need to be update.
            String loginId=  user.getLoginId();
                if (loginId.contains("@"))
                {
                    int atIndex = loginId.indexOf('@');

                    String rootUserLogin = loginId.substring(0, atIndex);
                    if (rootUserLogin.equalsIgnoreCase(tntcode + "RootUser"))
                    {
                        //Check default profile added for this default user or not.
                        UserUtil.AddProfileForDefaultUser(tcxt,dbUser);
//                        loginId=rootUserLogin;
                    }
                }
                dbUser.setName(user.getName());
                dbUser.setUserId(user.getUserId());
                tcxt.getDB().Update(user, dbUser);
            }
            dbusers.add(dbUser);
        }
        return dbusers;
    }

    private static UserRole getUserRole(String role)
    {
        role = role.toUpperCase();
        switch (role)
        {
            case "ADMIN":
            case "SERVICE":
                return UserRole.Admin;
            case "AGENT":
                return UserRole.Agent;
            case "SERVER":
                return UserRole.Server;
            case "SYSTEM":
                return UserRole.System;
            default:
                return null;
        }
    }

    public static ArrayList<Application> BuildApplication(String jsonstr) throws GravityUnhandledException
    {
        try
        {
            ArrayList<Application> allapplication = new ArrayList<>();

            if (jsonstr != null)
            {
                JSONObject jsonobj = new JSONObject(jsonstr);
                if (jsonobj.has("Entity"))
                {
                    JSONObject appJson = jsonobj.getJSONObject("Entity");

                    Application application = JSONUtil.FromJSON((JSONObject) appJson, Application.class);
                    allapplication.add(application);

                }
                else if (jsonobj.has("Entities"))
                {
                    JSONArray jsonArray = jsonobj.getJSONArray("Entities");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        JSONObject appJson = jsonobj.getJSONObject("Entity");

                        Application application = JSONUtil.FromJSON((JSONObject) appJson, Application.class);
                        allapplication.add(application);
                    }

                }

            }
            return allapplication;
        }
        catch (Exception ex)
        {
            throw new GravityUnhandledException(ex);
        }
    }

//    public static ArrayList<AppConfig> BuildAppConfigs(String propsResp)
//    {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
}
