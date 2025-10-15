/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.context;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.db.MySQLDBFactory;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.si.nucleus.NucleusClientContext;
import ois.cc.gravity.si.realm.ALMClientContext;
import ois.cc.gravity.si.ucos.UCOSClientContext;
import ois.radius.ca.enums.AgentAOPsState;
import ois.radius.ca.enums.AgentQueueState;
import ois.radius.ca.enums.AgentState;
import ois.radius.ca.enums.aops.AOPsState;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.*;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ois.cc.gravity.entities.util.UserUtil;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Prakasha.prusty 1 Aug, 2024
 */
public class TenantContext {

    static final org.slf4j.Logger _logger = LoggerFactory.getLogger(TenantContext.class);

    private static TenantContext _tCtx;



    private MySQLDB _db;

    private ALMClientContext _almCtx;
    private UCOSClientContext _ucosCtx;
    private NucleusClientContext _nucleusCtx;

    private ServerContext _sCtx;

    private Tenant _tenant;

    private String _darkToken;

    public TenantContext(Tenant tnt) {

        this._tenant = tnt;
        this._sCtx = ServerContext.This();


    }

    void InitTenantContext() throws GravityUnhandledException, GravityRuntimeCheckFailedException, CODEException {
        try {
            _sCtx.getNucleusCtx().GenerateToken(_tenant);
            this._nucleusCtx = _sCtx.getNucleusCtx().GetNucleusTenantContext(_tenant.getCode());

            this._db = MySQLDBFactory.CreateGravityDB(_tenant);
        } catch (Throwable th) {
            _logger.error(th.getMessage(), th);
            throw new CODEException(th);
        }

    }

    public boolean Start() throws Throwable {

        _db.getMsqlem().ExecuteStoredProcedure(new ClassPathResource("spExeUpdateQueries.sql").getFile().getAbsolutePath(),"ccdb_"+_tenant.getCode().toLowerCase());
        initDefUserOfTenant();
        User user = _db.FindAssert(new UserQuery().filterByLoginId(_tenant.getCode() + "RootUser"));
        UserUtil.AddProfileForDefaultUser(this, user);
        _sCtx.getDarkServer().DarkTokenInit(_tenant);
        return false;
    }

    public ServerContext getsCtx() {
        return _sCtx;
    }

    public MySQLDB getDB() {
        return _db;
    }

    public Tenant getTenant() {
        return _tenant;
    }

    public ALMClientContext getALMCtx() throws GravityUnhandledException, GravityRuntimeCheckFailedException {
        if (_almCtx == null) {
            _almCtx = _sCtx.getALMCtx().CreateTenantCtx(_tenant.getCode());
        }
        return _almCtx;
    }

    public UCOSClientContext getUCOSCtx() throws GravityUnhandledException {
        if (_ucosCtx == null) {
            _ucosCtx = _sCtx.getUcosCtx().CreateTenantCtx(_tenant.getCode());
        }
        return _ucosCtx;
    }

    public NucleusClientContext getNucleusCtx() {
        return _nucleusCtx;
    }

    public void Stop() {
        try {
            if (_db != null) {
                _sCtx.getNucleusCtx().StopTenant(_tenant);

                _sCtx.getALMCtx().TenantLogout(_tenant);
                _sCtx.getUcosCtx().TenantLogout(_tenant);
                _db.Close();
                _logger.info("Tenant Stopped sucessfully.");
            }
        } catch (Throwable th) {
            _logger.error(th.getMessage(), th);
        }
    }

    private User initDefUserOfTenant() throws CODEException, GravityUnhandledException {
        NucleusClientContext nuCctx = _sCtx.getNucleusCtx().GetNucleusTenantContext(_tenant.getCode());

        HashMap<String, ArrayList<String>> filters = new HashMap<>();
        filters.put("byloginid", new ArrayList<>(List.of(_tenant.getCode() + "RootUser")));
        ArrayList<User> nuAdmins = nuCctx.GetUsers(_tenant.getCode(), filters, UserRole.Admin.name());
        User defAdmin = nuAdmins.get(0);

        return defAdmin;
    }

    private void UpdatetemporaryState() throws CODEException, GravityException, UnsupportedEncodingException, NoSuchAlgorithmException {
//        ArrayList<AEntity> lists = new ArrayList<>();
//
//        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("spExeUpdateQueries");
//        query.registerStoredProcedureParameter("_schema", String.class, ParameterMode.IN);
////        query.setParameter("_schema", );
//        query.execute();
//
//        lists.addAll(getAgentAOPsStateCh(_db));
//        lists.addAll(getAgentStateCh(_db));
//        lists.addAll(getAopsStatus(_db));
//        lists.addAll(getAopsStatech(_db));
//        lists.addAll(getAgentQueueStCh(_db));
//        lists.addAll(getAgentStatus(_db));
//        _db.Update(_sCtx.getNucleusCtx().DummySysUser(), lists);
    }

//    public void executeSpExeUpdateQueries(String schemaName) {
//        StoredProcedureQuery query = entityManager
//                .createStoredProcedureQuery("spExeUpdateQueries");
//        query.registerStoredProcedureParameter("_schema", String.class, ParameterMode.IN);
//        query.setParameter("_schema", schemaName);
//        query.execute();
//    }




    private ArrayList<AOPsStatus> getAopsStatus(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery aopsQuery = new JPAQuery("SELECT a FROM AOPsStatus a WHERE a.AOPsState <>:state");
        aopsQuery.setParam("state", AOPsState.Stop);
        ArrayList<AOPsStatus> aopsstatuslists = db.Select(EN.AOPsStatus, aopsQuery);
        aopsstatuslists.forEach(aopsStatus -> aopsStatus.setAOPsState(AOPsState.Stop));
        return aopsstatuslists;
    }

    private ArrayList<AOPsStateCh> getAopsStatech(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery aopsQuery = new JPAQuery("SELECT a FROM AOPsStateCh a WHERE a.AOPsState <>:state");
        aopsQuery.setParam("state", AOPsState.Stop);
        ArrayList<AOPsStateCh> aopsstatechlists = db.Select(EN.AOPsStateCh, aopsQuery);
        aopsstatechlists.forEach(aopsStatech -> aopsStatech.setAOPsState(AOPsState.Stop));
        return aopsstatechlists;
    }

    private ArrayList<AgentStatus> getAgentStatus(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery agentQuery = new JPAQuery("SELECT a FROM AgentStatus a WHERE a.AgentState <>:state");
        agentQuery.setParam("state", AgentState.LogOut);
        ArrayList<AgentStatus> agentstatuslists = db.Select(EN.AgentStatus, agentQuery);

        agentstatuslists.forEach(agentStatus -> agentStatus.setAgentState(AgentState.LogOut));
        return agentstatuslists;
    }

    private ArrayList<AgentStateCh> getAgentStateCh(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery agentQuery = new JPAQuery("SELECT a FROM AgentStateCh a WHERE a.AgentState <>:state");
        agentQuery.setParam("state", AgentState.LogOut);
        ArrayList<AgentStateCh> agentstatechlists = db.Select(EN.AgentStateCh, agentQuery);

        agentstatechlists.forEach(agentStatech -> agentStatech.setAgentState(AgentState.LogOut));
        return agentstatechlists;
    }

    private ArrayList<AgentAOPsStCh> getAgentAOPsStateCh(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery agentQuery = new JPAQuery("SELECT a FROM AgentAOPsStCh a WHERE a.AgentAOPsState <>:state");
        agentQuery.setParam("state", AgentAOPsState.Left);
        ArrayList<AgentAOPsStCh> agentaopsstchlists = db.Select(EN.AgentAOPsStCh, agentQuery);

        agentaopsstchlists.forEach(agentaopsStch -> agentaopsStch.setAgentAOPsState(AgentAOPsState.Left));
        return agentaopsstchlists;
    }

    private ArrayList<AgentQueueStCh> getAgentQueueStCh(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery agentQuery = new JPAQuery("SELECT a FROM AgentQueueStCh a WHERE a.AgentQueueState <>:state");
        agentQuery.setParam("state", AgentQueueState.Left);
        ArrayList<AgentQueueStCh> AgentQueueStChlists = db.Select(EN.AgentQueueStCh, agentQuery);

        AgentQueueStChlists.forEach(agentaopsStch -> agentaopsStch.setAgentQueueState(AgentQueueState.Left));
        return AgentQueueStChlists;
    }
}