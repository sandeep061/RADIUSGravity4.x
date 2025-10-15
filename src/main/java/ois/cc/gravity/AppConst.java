/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity;

import CrsCde.CODE.Common.Consts.OSConst;

/**
 *
 * @author Deepak
 */
public class AppConst
{

    public static enum DBProd
    {
        MySQL,
        ObjectDB,;
    }
    public static DBProd DB_PROD = DBProd.MySQL;
    public static String UA_SERVICE_BASE_PKG = "ois.cc.gravity.services";
    public static String UA_REQUEST_BASE_PKG = "ois.cc.gravity.framework.requests";
    public static String GRAVITY_ENTITIES_BASE_PKG = "ois.radius.gravity.entities.tenant";
    public static String CC_PLATFORM_REQ_BASE_PKG = "ois.cc.gravity.framework.requests";
//    public static  String CC_PLATFORM_REQ_CONFIG_PKG ="ois.radius.gravity.server.ua.processors.config.common";
    
    public static String SYS_TENANT_CODE = "SYS";
    public static String SU_USER_LOGIN_ID = "suroot";
    public static String SU_USER_PASSWORD = "suroot";
    public static String SYS_CLIENT_EN_ID = "2l";
    public static String SYS_CLIENT_CODE = "SYS";
    public static String SYS_CLIENT_NAME = "RADIUS CC Server";
    
//    public static String SRVR_ENTITY_BASE_PKG = "ois.radius.core.entities";
//    public static String SRVR_ENTITY_CLIENT_PKG = "ois.radius.cc.entities.tenant";
//    public static String SRVR_ENTITY_SYS_PKG = "ois.radius.cc.entities.sys";
    public static String EN_QUERY_BASE_PKG = "ois.cc.gravity.db.queries";
    public static String NU_SYS_APPCode = "GRAVITY";

    public static final String GRAVITY_C_BASE_URL = "/gravity-api/v1/c/";
    public static final String GRAVITY_C_BASE_URL_REGISTER = "/gravity-api/v1/c/register";
    public static final String GRAVITY_S_BASE_URL_SIGNIN = "/gravity-api/v1/s/signin";
    public static final String GRAVITY_C_BASE_URL_LOGOUT = "/gravity-api/v1/c/logout";
    public static final String GRAVITY_C_BASE_URL_CLEARTEMPSTATE = "/gravity-api/v1/c/cleartempstate";
    public static final String GRAVITY_C_SURVEYINFO = "/gravity-api/v1/c/surveyinfo";
    public static final String GRAVITY_C_BASE_URL_VERSIONFETCH = "/gravity-api/v1/c/version";
    public static final String DARK_C_BASE_URL_VERSIONFETCH = "/gravity-api/v1/d/version";
    public static final String DARK_N_BASE_URL = "/gravity-api/v1/d/n";
    public static final String GRAVITY_CTRL_BASE_URL_DARK = "/gravity-api/v1/d";
    public static final String GRAVITY_N_BASE_URL = "/gravity-api/v1/n";
    public static final String DARK_TENANT_START = "/gravity-api/v1/d/tenantstart";
    public static final String GRAVITY_HEALTH = "/gravity-api/health";


    public static final String RAD_CC_ENTITY_TENANT_PKG = "ois.radius.cc.entities.tenant";
    public static final String RAD_CC_SYS_ENTITY_TENANT_PKG = "ois.radius.cc.entities.sys";

     //dark

    static String getConfigPath()
    {

        if (OSConst.OSType.Linux.equals(OSConst.Type()))
        {
            return "/active/programs/radius/cc/core/gravity/service/conf/";
        }
        else
        {
            return "D:\\programs\\radius\\cc\\core\\gravity\\service\\conf\\";
        }
    }

    static String ConfigFileName = "GravityApplication.properties";

}
