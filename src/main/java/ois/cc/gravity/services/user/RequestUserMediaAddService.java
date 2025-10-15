package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.db.jpa.JPAQuery;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.User;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.tenant.cc.UserMedia;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.radius.cc.entities.EN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequestUserMediaAddService extends RequestEntityAddService {

    private static Logger logger = LoggerFactory.getLogger(RequestUserMediaAddService.class);

    public RequestUserMediaAddService(UAClient uac) {
        super(uac);
    }

    private User _agent;

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {

        if (!reqenadd.getAttributes().containsKey("User"))
        {
            throw new GravityIllegalArgumentException("User", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        MySQLDB db = _tctx.getDB();
        String userid = reqenadd.getAttributeValueOf(String.class, "User");
        _agent = _tctx.getNucleusCtx().GetUserById(_tctx.getTenant().getCode(),userid, UserRole.Agent.name());
        reqenadd.setAttribute("User",_agent.getId());

        if (reqenadd.getAttributes().containsKey("Channel"))
        {
            Channel chn = reqenadd.getAttributeValueOf(Channel.class, "Channel");
            JPAQuery qry = new JPAQuery("Select um from UserMedia um where um.User.Id=:user");
            qry.setParam("user",_agent.getId());
//            qry.setParam("isdel",false);
            List<UserMedia> usermedias = db.Select(qry);
            boolean isChnExist = usermedias.stream().anyMatch(agm -> agm.getChannel().equals(chn));
//            boolean isChnExist = _agent.getAgentMedias().stream().anyMatch(agm -> agm.getChannel().equals(chn));
            if (isChnExist)
            {
                throw new GravityEntityExistsException(EN.User.name(), "Agent,AgentMedias.Channel", OPRelational.Eq, userid + "," + chn);
            }
        }

        /**
         * During agent media add as we don't have AgentMediaMap so these attribute can't configure now. These attributes will configured once AgentMediaMap
         * details added.
         */
        if (reqenadd.getAttributes().containsKey("AutoRegister"))
        {
            reqenadd.getAttributes().remove("AutoRegister");
        }
        if (reqenadd.getAttributes().containsKey("AutoRegAgentMedia"))
        {
            reqenadd.getAttributes().remove("AutoRegAgentMedia");
        }
        if (reqenadd.getAttributes().containsKey("AgentMediaMaps"))
        {
            reqenadd.getAttributes().remove("AgentMediaMaps");
        }

    }


}
