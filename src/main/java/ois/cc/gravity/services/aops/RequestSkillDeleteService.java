package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.AgentSkill;
import ois.radius.cc.entities.tenant.cc.Skill;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.db.queries.AgentSkillQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;

import java.util.ArrayList;
import ois.cc.gravity.entities.util.AOPsUtil;

public class RequestSkillDeleteService extends ARequestEntityService
{

    public RequestSkillDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;
        MySQLDB db = _tctx.getDB();

        /**
         * Logics:- <br>
         * Before delete Skill need to check following points. <br>
         * - Whether the Skill is mapped with any agent or not If not then only we delete the skill. <br>
         * - The Skill mapped with the campaign must be in Unload. <br>
         *
         */
        Skill skill = db.FindAssert(EN.Skill.getEntityClass(), req.getEntityId());

        ArrayList<AgentSkill> alas = db.Select(EN.AgentSkill, new AgentSkillQuery().filterBySkill(skill.getId()).toSelect());
        if (!alas.isEmpty())
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.EntityNotDeletedYet, "AgentSkill.Skill.Id==" + skill.getId());
        }

        //Skill can be delete only when its campaign is in unload state.
        AOPs aops = skill.getAOPs();
         AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aops);
        /**
         * If skill deleted then we need to delete the AgentSkill also as there are no use of a AgentSkill without Skill.
         */
        ArrayList<AEntity> entities = new ArrayList<>();
        ArrayList<AgentSkill> agSkList = _tctx.getDB().Select(new AgentSkillQuery().filterBySkill(skill.getId()));
        agSkList.forEach(agsk ->
        {
            entities.add(agsk);
        });
        entities.add(skill);
        db.DeleteEntities(_uac.getUserSession().getUser(), entities);

        return new EventEntityDeleted(req, skill.getId().toString(), EN.Skill.name());
    }

}
