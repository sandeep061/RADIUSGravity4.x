package ois.cc.gravity.services.user;

import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.AgentSkill;
import ois.radius.cc.entities.tenant.cc.Skill;
import ois.radius.cc.entities.tenant.cc.User;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.db.queries.SkillQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestService;

public class RequestAgentSkillAddService extends ARequestEntityService
{

    public RequestAgentSkillAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd req = (RequestEntityAdd) request;

        String agentid = req.getAttributeValueOf(String.class, "Agent");
        JPAQuery query = new JPAQuery("Select u from User u where u.UserId=:uid");
        query.setParam("uid", agentid);
        User user = _tctx.getDB().Find(EN.User, query);
        if (user == null)
        {
            user = _tctx.getNucleusCtx().GetUserById(_tctx.getTenant().getCode(), agentid, UserRole.Agent.name());
        }
        Long skid = Long.valueOf(req.getAttribute("Skill").toString());
        Skill skill = _tctx.getDB().Find(new SkillQuery().filterById(skid));

        AgentSkill agsk = new AgentSkill();
        agsk.setAgent(user);
        agsk.setSkill(skill);
        if (req.getAttributes().containsKey("Level"))
        {
            Integer level = Integer.valueOf(req.getAttribute("Level").toString());
            agsk.setLevel(level);
        }

        agsk = (AgentSkill) _tctx.getDB().Insert(_uac.getUserSession().getUser(), agsk);

        EventEntityAdded ev = new EventEntityAdded(req, agsk);
        return ev;
    }
}
