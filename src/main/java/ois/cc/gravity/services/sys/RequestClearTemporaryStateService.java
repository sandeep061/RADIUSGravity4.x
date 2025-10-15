package ois.cc.gravity.services.sys;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.framework.requests.auth.RequestClearTemporaryState;
import ois.cc.gravity.services.ARequestCmdService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.radius.ca.enums.AgentState;
import ois.radius.ca.enums.aops.AOPsState;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsStatus;
import ois.radius.cc.entities.tenant.cc.AgentStatus;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class RequestClearTemporaryStateService extends ARequestCmdService
{

    @Override
    protected Event ProcessCmdRequest(Request request) throws CODEException, GravityException, UnsupportedEncodingException, NoSuchAlgorithmException
    {

        RequestClearTemporaryState reqstate = (RequestClearTemporaryState) request;

        ArrayList<AEntity> lists = new ArrayList<>();
        MySQLDB db = _sCtx.GetTenantCtxByCode(reqstate.getTenantCode()).getDB();

        lists.addAll(getAopsStatus(db));
        lists.addAll(getAgentStatus(db));
        
        db.Update(_sCtx.getNucleusCtx().DummySysUser(), lists);
        EventOK ok = new EventOK(reqstate, EventCode.ClearTemporaryState);
        return ok;
    }

    private ArrayList<AOPsStatus> getAopsStatus(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery aopsQuery = new JPAQuery("SELECT a FROM AOPsStatus a WHERE a.AOPsState <>:state");
        aopsQuery.setParam("state", AOPsState.Stop);
        ArrayList<AOPsStatus> aopsstatuslists = db.Select(EN.AOPsStatus, aopsQuery);
        aopsstatuslists.forEach(aopsStatus -> aopsStatus.setAOPsState(AOPsState.Stop));
        return aopsstatuslists;
    }

    private ArrayList<AgentStatus> getAgentStatus(MySQLDB db) throws CODEException, GravityException
    {
        JPAQuery agentQuery = new JPAQuery("SELECT a FROM AgentStatus a WHERE a.AgentState <>:state");
        agentQuery.setParam("state", AgentState.LogOut);
        ArrayList<AgentStatus> agentstatuslists = db.Select(EN.AgentStatus, agentQuery);
        
        agentstatuslists.forEach(agentStatus -> agentStatus.setAgentState(AgentState.LogOut));
        return agentstatuslists;
    }
}
