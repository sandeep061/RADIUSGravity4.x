package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Enums.OPRelational;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Queue;
import ois.radius.cc.entities.tenant.cc.Skill;
import ois.cc.gravity.db.queries.SkillQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;

public class RequestSkillAddService extends RequestEntityAddService
{

    public RequestSkillAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd req) throws Throwable
    {
        /**
         * @since V:190723 <br>
         * Code and Name should be same in skill and Queue thats why we dont have setter method for Name
         */
        if (req.getAttributes().containsKey("Name"))
        {
            req.getAttributes().remove("Name");
        }
        if (req.getAttributes().containsKey("Code"))
        {
            Skill skill = _tctx.getDB().Find(new SkillQuery().filterByCode(req.getAttributeValueOf(String.class, "Code").toString().toUpperCase()));

            if (skill != null)
            {
            	GravityEntityExistsException ex = new GravityEntityExistsException(EN.Skill.name(), "Skill,Campaign", OPRelational.Eq, skill.getId() + "," + skill.getAOPs().getId());
                throw ex;
            }
        }
        if (!req.getAttributes().containsKey("Queue"))
        {
            return;
        }
        Long qid = req.getAttributeValueOf(Long.class, "Queue");
        Queue que = _tctx.getDB().FindAssert(EN.Queue.getEntityClass(), qid);

        Skill sk = _tctx.getDB().Find(new SkillQuery().filterByQueue(qid));
        if (sk != null)
        {
            throw new GravityEntityExistsException(EN.Skill.name(), "Queue", OPRelational.Eq, que.getId());
        }
    }
}

