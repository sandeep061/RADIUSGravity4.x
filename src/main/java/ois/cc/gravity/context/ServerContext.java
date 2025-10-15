/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import code.common.exceptions.CODEException;
import ois.cc.gravity.AppConst;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.si.realm.ALMServerContext;
import ois.cc.gravity.si.ucos.UCOSServerContext;
import ois.cc.gravity.si.nucleus.NucleusServerContext;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.si.dark.DarkServer;
import ois.cc.gravity.si.dark.DarkServerListener;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.User;
import org.slf4j.LoggerFactory;

/**
 * @author sandeep.sahoo
 * @since 1 Aug, 2024
 */
//@Component
public class ServerContext
{

    private static final org.slf4j.Logger _logger = LoggerFactory.getLogger(ServerContext.class);

    private static ServerContext _this;

    private static Tenant _sysTnt;

    private static User _sysUser;

    private static MySQLDB _sysDB;

    private DarkServer _darkServer;

    private static void Init_Start_SysClient_Ctx() throws Exception, GravityRuntimeCheckFailedException
    {

        TenantContext clntCtx = new TenantContext(_sysTnt);

        /**
         * Put Sys client context to hashmap.
         */
        _this._hmTenatCtx.put(_sysTnt.getCode(), clntCtx);

    }

    private final HashMap<String, TenantContext> _hmTenatCtx;

    private NucleusServerContext _nuSctx;

    private ALMServerContext _almSCtx;

    private UCOSServerContext _ucoSCtx;

    private ServerContext()
    {
        this._hmTenatCtx = new HashMap<>();
    }

    public static ServerContext This()
    {
        if (_this == null)
        {
            _this = new ServerContext();
        }
        return _this;
    }

    public static Tenant getSysTnt()
    {
        return _sysTnt;
    }

    public static User getSysUser()
    {
        return _sysUser;
    }

    public void InitTenantContext(Tenant tenant) throws GravityRuntimeCheckFailedException, GravityUnhandledException, CODEException
    {
        _logger.trace("Initiating tenant context for Tenant : " + tenant.getCode());
        TenantContext tctx = new TenantContext(tenant);
        tctx.InitTenantContext();

        synchronized (_hmTenatCtx)
        {
            _hmTenatCtx.put(tenant.getCode(), tctx);
        }

        _logger.trace("Tenant context for Tenant : " + tenant.getCode() + " initiated...");
    }

    public static void InitContext() throws Exception, GravityException, CODEException
    {

        _sysTnt = DummySysClient();
        _this = This();

        SysDBContext sysctx = new SysDBContext();
        _sysDB = sysctx.TheFirstDBInit();
        NucleusServerContext.Init();
        _sysUser = NucleusServerContext.This().DummySysUser();

        _logger.info("Initializing sys client context...");
        Init_Start_SysClient_Ctx();

        //ReqlM su-login.
        ALMServerContext.Init();
        UCOSServerContext.Init();

        _logger.info("Initializing sys client context...");

        _logger.info("ServerContext intialization done.");

    }

    public void InitClientCtx(Tenant client) throws Exception, CODEException, GravityRuntimeCheckFailedException
    {

        TenantContext cCtx = new TenantContext(client);
        _this._hmTenatCtx.put(client.getCode(), cCtx);
    }

    public void DeinitClientCtx(Tenant client)
    {
        _this._hmTenatCtx.remove(client.getCode());
    }

    /**
     * Starting Server Context
     *
     * @throws CODEException
     */
    public void Start() throws CODEException
    {
        try
        {
//            this._darkServer = new DarkServer(AppProps.RAD_Dark_IP, AppProps.RAD_Dark_Port, new DarkServerListener());
//            this._darkServer.Connect();

            _nuSctx = NucleusServerContext.This();

            _nuSctx.SUSignIn();

            _almSCtx = ALMServerContext.This();
//            _almSCtx.SULogin();
            _ucoSCtx = UCOSServerContext.This();
            checkAndStartTenantContext();

        }
        catch (Exception | GravityException ex)
        {
            _logger.error(ex.getMessage(), ex);
        }

    }

    public NucleusServerContext getNucleusCtx()
    {
        return _nuSctx;
    }

    public ALMServerContext getALMCtx()
    {
        return _almSCtx;
    }

    public UCOSServerContext getUcosCtx()
    {
        return _ucoSCtx;
    }

    public DarkServer get_darkServer()
    {
        return _darkServer;
    }

    private void checkAndStartTenantContext() throws Exception, GravityException
    {
        HashMap<String, ArrayList<String>> filter = new HashMap<>();
        filter.put("bystate", new ArrayList<>(Arrays.asList("Start")));
        ArrayList<Tenant> actvTnts = _nuSctx.GetTenants(filter);
        for (Tenant tnt : actvTnts)
        {
            _logger.trace("Doing process for Tenant : " + tnt.getCode());

            try
            {
                InitTenantContext(tnt);
                TenantContext tcxt = this._hmTenatCtx.get(tnt.getCode());
                tcxt.Start();
            }
            catch (Throwable th)
            {
                _logger.error(th.getMessage(), th);
                continue;
            }
        }
    }

    /**
     * Getting TenantContext From TenantCode
     *
     * @param code
     * @return
     */
    public TenantContext GetTenantCtxByCode(String code)
    {
        synchronized (_hmTenatCtx)
        {
            return _hmTenatCtx.containsKey(code) ? _hmTenatCtx.get(code) : null;
        }
    }

    public void Stop(TenantContext tntctx) throws GravityUnhandledException
    {
        tntctx.Stop();
        DeinitClientCtx(tntctx.getTenant());
    }

    static Tenant DummySysClient()
    {
        Tenant sysClient = new Tenant();
        sysClient.setId(AppConst.SYS_CLIENT_EN_ID);
        sysClient.setCode(AppConst.SYS_CLIENT_CODE);
        sysClient.setName(AppConst.SYS_CLIENT_NAME);

        return sysClient;
    }

    public MySQLDB getSysDB()
    {
        return _sysDB;
    }

    public DarkServer getDarkServer()
    {
        return _darkServer;
    }

    public void setDarkServer(DarkServer _darkServer)
    {
        this._darkServer = _darkServer;
    }

}
