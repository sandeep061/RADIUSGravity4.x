package ois.cc.gravity.services.user;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.*;
import code.ua.requests.Request;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AgentSkill;
import ois.cc.gravity.db.queries.AgentSkillQuery;
import ois.cc.gravity.framework.requests.user.RequestAgentSkillDelete;

import java.util.ArrayList;
import ois.cc.gravity.services.exceptions.GravityException;

public class RequestAgentSkillDeleteService extends ARequestEntityService
{

    public RequestAgentSkillDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        /**
         * AgentSkill can be delete process. <br>
         * 1.Check the associated agent is joined the campaign of this agent skill's skill or not. If so then agent skill should be delete. 2.On AgentSkill
         * successfully delete we have to notify the associated users(Agent/Admin) that skill removed.
         */
        RequestAgentSkillDelete req = (RequestAgentSkillDelete) request;

        ArrayList<AEntity> entities = new ArrayList<>();
        if (req.getId() != null)
        {
            AgentSkill agsk = _tctx.getDB().FindAssert(EN.AgentSkill.getEntityClass(), req.getId());
            entities.add(agsk);
        }
        else if (req.getBySkill() != null && !req.getBySkill().isEmpty())
        {
            ArrayList<AgentSkill> alAgSk = getAgentSkillsBySkill(req.getBySkill());
            entities.addAll(alAgSk);
        }
        else
        {
            Event ev = new EventRequestValidationFailed(req, "Id", EventFailedCause.NonOptionalConstraintViolation);
            return ev;
        }

        //TBD:
//        validateAgentSkillForDelete(entities);
        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), entities);

//        sendEventToUsers(req, entities);
        EventEntityDeleted ev = new EventEntityDeleted(req, req.getId().toString(), AgentSkill.class.getName());
        return ev;
    }

    private ArrayList<AgentSkill> getAgentSkillsBySkill(ArrayList<Long> skills) throws GravityException, CODEException
    {
        ArrayList<AgentSkill> agSkLists = new ArrayList<>();
        for (Long skill : skills)
        {
            ArrayList<AgentSkill> agSkList = _tctx.getDB().Select(new AgentSkillQuery().filterBySkill(skill));
            agSkLists.addAll(agSkList);
        }
        return agSkLists;
    }

//    private void validateAgentSkillForDelete(List<AgentSkill> agskList) throws GravityRuntimeCheckFailedException
//    {
//        for (AgentSkill agsk : agskList)
//        {
//            AIAgent aiag = _tctx.getAgentStore().GetById(agsk.getAgent().getId());
//            Campaign camp = agsk.getSkill().getCampaign();
//            if (aiag != null && aiag.isCampaignJoined(camp))
//            {
//                throw new RADRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AgentHaveJoinedAssociatedCampaign, "[Campaign,Agent == " + camp.getId() +","+ aiag.getAgentId()+"]");
//            }
//        }
//    }
//    private void sendEventToUsers(RequestAgentSkillDelete req, ArrayList<AgentSkill> agsks) throws RADException
//    {
//        for (AgentSkill agsk : agsks)
//        {
//            statEvProc.This().Build_Send_SkillRemovedEv(req.getReqId(), _cctx, agsk, _uac.getUserSession().getUser());
//        }
//    }
}
