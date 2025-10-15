//package ois.cc.gravity.services.aops;
//
//import code.ua.events.Event;
//import code.ua.events.EventOK;
//import code.ua.requests.Request;
//import ois.cc.gravity.services.ARequestEntityService;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.radius.cc.entities.tenant.cc.Campaign;
//import ois.radius.cc.entities.tenant.cc.Queue;
//import ois.radius.cc.entities.tenant.cc.Skill;
//import ois.cc.gravity.framework.events.EventCode;
//import ois.cc.gravity.framework.requests.aops.RequestSkillQueueUnMap;
//import ois.cc.gravity.services.ARequestService;
//import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
//import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
//
//public class RequestSkillQueueUnMapService extends ARequestEntityService
//{
//    public RequestSkillQueueUnMapService(UAClient uac)
//    {
//        super(uac);
//    }
//    @Override
//    protected Event DoProcessEntityRequest(Request request) throws Throwable
//    {
//        RequestSkillQueueUnMap req = (RequestSkillQueueUnMap) request;
//        Skill skill = _tctx.getDB().FindAssert(EN.Skill.getEntityClass(), req.getSkill());
//        Queue queue = _tctx.getDB().FindAssert(EN.Queue.getEntityClass(), req.getQueue());
//
//        //Make sure Campaign is in unload state
//        Campaign camp = (Campaign) skill.getAOPs();
////        AICampaign aicamp = _uac.getCCtx().getCampaignStore().GetById(camp.getId());
////        if (aicamp != null)
////        {
////            throw new RADIllegalObjectStateException(Campaign.class, camp.getId(), aicamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
////        }
//        //Check if queue is not mapped with skill yet or the supplied queue is not mapped with skill.
//        if (skill.getQueue() == null || skill.getQueue() != queue)
//        {
//            throw  new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.QueueNotMappedWithSkillYet);
////            EventRuntimeCheckFailed ev = new EventRuntimeCheckFailed(req, EvCauseRuntimeCheckFailed.QueueNotMappedWithSkillYet);
////            return ev;
//        }
//
//        skill.setQueue(null);
//        _tctx.getDB().Update(_uac.getUserSession().getUser(),skill);
//
//        return new EventOK(req,EventCode.SkillQueueUnMapped);
//
//    }
//}
