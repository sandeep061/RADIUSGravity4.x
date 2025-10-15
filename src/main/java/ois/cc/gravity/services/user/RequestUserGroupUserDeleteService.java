package ois.cc.gravity.services.user;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.common.RequestEntityDeleteService;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.AgentState;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.AgentAOPsStCh;
import ois.radius.cc.entities.tenant.cc.UserGroupUser;
import org.vn.radius.cc.platform.exceptions.RADException;
import java.util.ArrayList;
import java.util.Arrays;
import ois.radius.ca.enums.AgentAOPsState;

public class RequestUserGroupUserDeleteService extends RequestEntityDeleteService
{

    public RequestUserGroupUserDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void doPreProcessDelete(RequestEntityDelete req, AEntity entity) throws Throwable
    {
        UserGroupUser uguser = (UserGroupUser) entity;

        Long userid = uguser.getUser().getId();
        if (isAgentLoggedin(userid))
        {
            //geting user from UserGroupUsers id
            ArrayList<Long> aopsId = getAops(uguser.getUserGroup().getId());
            isValidAopsState(aopsId, uguser.getUser().getId());
        }

    }

    //check Agent State
    private Boolean isAgentLoggedin(Long ids) throws RADException, Exception, GravityRuntimeCheckFailedException, CODEException, GravityException
    {
        JPAQuery qry = new JPAQuery("SELECT COUNT(a) FROM AgentStatus a WHERE  a.AgentState NOT IN(:states) AND a.AgentId =:userIds");
        qry.setParam("userIds", ids);
        qry.setParam("states", Arrays.asList(AgentState.LogOut, AgentState.Unknown));
        long size = _tctx.getDB().SelectScalar(qry);
        return size > 0;

    }

    private ArrayList<Long> getAops(Long ugid) throws Exception, GravityException, RADException, CODEException, CODEException
    {
        JPAQuery query = new JPAQuery("SELECT u.AOPs.Id FROM UserGroupAops u WHERE u.UserGroup.id = :userGroupId");
        query.setParam("userGroupId", ugid);
        return (ArrayList<Long>) _tctx.getDB().Select(query);

    }

    private void isValidAopsState(ArrayList<Long> aopsIds, Long uid) throws GravityException, GravityIllegalObjectTypeException, CODEException, Exception
    {
        for (Long aopsId : aopsIds)
        {
            JPAQuery query = new JPAQuery("SELECT agch FROM AgentAOPsStCh  agch WHERE agch.AgentId=:agid AND agch.AOPsId=:aopsIds ORDER BY agch.Id DESC");
            query.setParam("agid", uid);
            query.setParam("aopsIds", aopsId);
            AgentAOPsStCh agaopstch = _tctx.getDB().Find(EN.AgentAOPsStCh.getEntityClass(), query);

            if (agaopstch != null && (agaopstch.getAgentAOPsState().equals(AgentAOPsState.Join) || agaopstch.getAgentAOPsState().equals(AgentAOPsState.Active)))
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AgentStillJoinedAOPs, "[AOPs ==" + aopsId + "]");
            }
        }

    }
}
