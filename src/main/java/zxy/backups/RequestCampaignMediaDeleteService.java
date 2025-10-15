//package ois.cc.gravity.services.aops;
//
//import CrsCde.CODE.Common.Classes.NameValuePair;
//import code.common.exceptions.CODEException;
//import code.db.jpa.JPAQuery;
//import code.entities.AEntity;
//import code.ua.events.Event;
//import code.ua.events.EventEntityDeleted;
//import code.ua.events.EventEntityEdited;
//import code.ua.requests.Request;
//import ois.cc.gravity.db.queries.*;
//import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
//import ois.cc.gravity.services.ARequestEntityService;
//import ois.cc.gravity.services.exceptions.GravityException;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.radius.cc.entities.tenant.cc.*;
//import ois.radius.ca.enums.Channel;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//public class RequestCampaignMediaDeleteService extends ARequestEntityService
//{
//
//    public RequestCampaignMediaDeleteService(UAClient uac)
//    {
//        super(uac);
//    }
//
//    private final ArrayList<NameValuePair> _entities = new ArrayList<>();
//
//    @Override
//    protected Event DoProcessEntityRequest(Request request) throws Throwable
//    {
//        RequestEntityDelete req = (RequestEntityDelete) request;
//
//        AOPsMedia campMda = _tctx.getDB().FindAssert(AOPsMedia.class, req.getEntityId());
//
//        _entities.add(new NameValuePair("Delete", campMda));
//
//        AOPs aops = campMda.getAOPs();
//        Campaign camp = (Campaign) aops;
////        AICampaign aicamp = _tctx.getCampaignStore().GetById(camp.getId());
////        if (aicamp != null)
////        {
////            return new EventCampaignIllegalState(req, camp.getId(), aicamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
////        }
//
//        CampaignProperties campProp = _tctx.getDB().Find(new CampaignPropertiesQuery().filterByAops(camp.getId()));
//        if (campProp != null)
//        {
//            switch (campMda.getChannel())
//            {
//                case Call:
//                    if (campProp.getXT() != null)
//                    {
//                        campProp.setXT(null);
//                    }
//                    break;
//                case Chat:
//                    if (campProp.getXCH() != null)
//                    {
//                        campProp.setXCH(null);
//                    }
//                    break;
//                case Email:
//                    if (campProp.getXEM() != null)
//                    {
//                        campProp.setXEM(null);
//                    }
//                    break;
//                case Video:
//                    if (campProp.getXVD() != null)
//                    {
//                        campProp.setXVD(null);
//                    }
//                    break;
//                case Social:
//                    if (campProp.getXSO() != null)
//                    {
//                        campProp.setXSO(null);
//                    }
//                    break;
//                case SMS:
//                    if (campProp.getXM() != null)
//                    {
//                        campProp.setXM(null);
//                    }
//                    break;
//            }
//            _entities.add(new NameValuePair("Update", campProp));
//        }
//
//        unmapQueueAndSkill(req, campMda);
//
//        if (campMda.getChannel().equals(Channel.Call))
//        {
//            unmapCallerIDPlanAndDialedIDPlan(camp);
//        }
//        //unmap callerId treatment and dialid plan from campaign.
//        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(),_entities);
//
//        EventEntityDeleted ev = new EventEntityDeleted(req, campMda);
//        return ev;
//    }
//
//    private void unmapQueueAndSkill(RequestEntityDelete req, AOPsMedia campmda) throws GravityException, CODEException, Exception
//    {
//        ArrayList<Skill> mappedSkills = _tctx.getDB().Select(new SkillQuery().filterByAops(campmda.getAOPs().getId()).filterByChannel(campmda.getChannel()));
//        ArrayList<AgentSkill> mappedAgSkills = new ArrayList<>();
//
//        for (Skill sk : mappedSkills)
//        {
//            if (Objects.nonNull(sk.getQueue()))
//            {
//                sk.setQueue(null);
//                ArrayList<AgentSkill> alAgSk = _tctx.getDB().Select(new AgentSkillQuery().filterBySkill(sk.getId()));
//                mappedAgSkills.addAll(alAgSk);
//            }
//
//        }
//
//        _entities.add(new NameValuePair<>("Delete", mappedAgSkills.toArray(new AEntity[0])));
//        _entities.add(new NameValuePair("Update", mappedSkills.toArray(new AEntity[0])));
//
////        if (!mappedAgSkills.isEmpty())
////        {
////            sendEventToUsers(req, mappedAgSkills);
////        }
//
//    }
//
////    private void sendEventToUsers(RequestEntityDelete req, ArrayList<AgentSkill> mappedAgSkills) throws GravityException
////    {
////        for (AgentSkill agsk : mappedAgSkills)
////        {
////            statEvProc.This().Build_Send_SkillRemovedEv(req.getReqId(), _tctx, agsk, _uac.getUserSession().getUser());
////        }
////
////    }
//
//    /**
//     * For Temporary fix ,it is only allowed for Telephony but need to implement for all channel as we have OB for Other channels also.
//     *
//     */
//    private void unmapCallerIDPlanAndDialedIDPlan(Campaign camp) throws GravityException,CODEException,Exception
//    {
//        switch (camp.getCampaignType())
//        {
//            case Inbound:
//                deleteCallerIDPlan(camp.getId());
//                break;
//            case Outbound:
//                deleteDialIDPlan(camp.getId());
//                break;
//            case Blended:
//                deleteDialIDPlan(camp.getId());
//                deleteCallerIDPlan(camp.getId());
//                break;
//        }
//    }
//
//    private void deleteDialIDPlan(Long CampId) throws GravityException,CODEException,Exception
//    {
//        JPAQuery query = new DialIDPlanQuery().filterByAops(CampId).toSelect();
//        ArrayList<DialIDPlan> alDialIDPlans = _tctx.getDB().Select(EN.DialIDPlan, query);
//        if (!alDialIDPlans.isEmpty())
//        {
//            _entities.add(new NameValuePair<>("Delete", alDialIDPlans.toArray(new DialIDPlan[0])));
//        }
//    }
//
//    private void deleteCallerIDPlan(Long CampId) throws GravityException,CODEException,Exception
//    {
//        JPAQuery query = new CallerIDPlanQuery().filterByAOPs(CampId).toSelect();
//        ArrayList<CallerIDPlan> alCallerIDPlans = _tctx.getDB().Select(EN.CallerIDPlan, query);
//        if (!alCallerIDPlans.isEmpty())
//        {
//            _entities.add(new NameValuePair<>("Delete", alCallerIDPlans.toArray(new CallerIDPlan[0])));
//        }
//    }
//
//}
