//package ois.cc.gravity.services.xs;
//
//import code.entities.AEntity_ad;
//import code.ua.events.Event;
//import code.ua.events.EventOK;
//import code.ua.requests.Request;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.radius.cc.entities.tenant.cc.AgentSkill;
//import ois.radius.cc.entities.tenant.cc.Skill;
//import ois.cc.gravity.db.MySQLDB;
//import ois.cc.gravity.db.queries.AgentSkillQuery;
//import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
//import ois.cc.gravity.services.ARequestService;
//import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
//import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
//
//import java.util.ArrayList;
//
//public class RequestSkillDeleteService extends ARequestService {
//    public RequestSkillDeleteService(UAClient uac) {
//        super(uac);
//    }
//
//    @Override
//    protected Event DoProcessRequest(Request request) throws Throwable {
//        RequestEntityDelete req = (RequestEntityDelete) request;
//        MySQLDB db = _tctx.getDB();
//
//        /**
//         * Logics:- <br>
//         * Before delete Skill need to check following points. <br>
//         * - Whether the Skill is mapped with any agent or not If not then only we delete the skill. <br>
//         * - The Skill mapped with the campaign must be in Unload. <br>
//         *
//         */
//        Skill skill = db.FindAssert(Skill.class,req.getEntityId());
//
//        ArrayList<AgentSkill> alas = db.Select(EN.AgentSkill, new AgentSkillQuery().filterBySkill(skill.getId()).toSelect());
//        if (!alas.isEmpty())
//        {
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AgentIsNotUnmappedYet);
////            EventEntityNotDeleted ev = new EventEntityNotDeleted(req, EN.Skill, EventEntityNotDeleted.Cause.AgentIsNotUnmappedYet);
////            return ev;
//        }
//
//        //Skill can be delete only when its campaign is in unload state.
////        Campaign camp = skill.getAOPs();
////        if (camp != null)
////        {
//////            AICampaign aicamp = _cctx.getCampaignStore().GetById(camp.getId());
//////            if (aicamp != null)
//////            {
//////                EventCampaignIllegalState ev = new EventCampaignIllegalState(req, camp.getId(), aicamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
//////                return ev;
//////            }
////        }
//        /**
//         * If skill deleted then we need to delete the AgentSkill also as there are no use of a AgentSkill without Skill.
//         */
//        ArrayList<AEntity_ad> entities = new ArrayList<>();
//        ArrayList<AgentSkill> agSkList = _tctx.getDB().Select(new AgentSkillQuery().filterBySkill(skill.getId()));
//        agSkList.forEach(agsk ->
//        {
//            entities.add(agsk);
//        });
//        entities.add(skill);
////        db.DeleteUpdate(entities);
//
//        return new EventOK(req);
//    }
//
//}
//
