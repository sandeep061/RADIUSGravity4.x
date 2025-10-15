//package ois.cc.gravity.services.user;
//
//import code.common.exceptions.CODEException;
//import code.ua.events.Event;
//import code.ua.events.EventEntityEdited;
//import code.ua.events.EventFailedCause;
//import code.ua.requests.Request;
//import ois.cc.gravity.services.ARequestEntityService;
//import ois.cc.gravity.services.exceptions.*;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.radius.cc.entities.UserGroupType;
//import ois.radius.cc.entities.UserRole;
//import ois.radius.cc.entities.tenant.cc.*;
//import ois.cc.gravity.db.MySQLDB;
//import ois.cc.gravity.db.queries.UserQuery;
//import ois.cc.gravity.entities.util.EntityBuilder;
//import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
//import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
//import org.vn.radius.cc.platform.exceptions.RADException;
//
//public class RequestUserGroupEditService extends ARequestEntityService
//{
//    public RequestUserGroupEditService(UAClient uac)
//    {
//        super(uac);
//    }
//
//    @Override
//    protected Event DoProcessEntityRequest(Request request) throws Throwable
//    {
//
//        RequestEntityEdit req = (RequestEntityEdit) request;
//        MySQLDB db = _tctx.getDB();
//        UserGroup usergrp = db.FindAssert(EN.UserGroup.getEntityClass(), req.getEntityId());
////        UserGroupType userGroupType = usergrp.getUserGroupType();
//
//        //Set updated attribute values.
//        try
//        {
//            if (req.getAttributes() != null && !req.getAttributes().isEmpty())
//            {
//                EntityBuilder.BuildEntity(db, usergrp, req.getAttributes());
//            }
//        }
//        catch (GravityNoSuchFieldException radex)
//        {
////            EventAttributeInvalid ev = new EventAttributeInvalid(request, radex.getClassName(), radex.getFlieldName());
////            return ev;
//            return null;
//        }
//
//        //do process for Collection attributes like Agents and Campaigns.
////        if (req.getAttributeCollectionAppend() != null && !req.getAttributeCollectionAppend().isEmpty())
////        {
////            doAppendAttributes(req, usergrp, userGroupType);
////        }
////        if (req.getAttributeCollectionRemove() != null && !req.getAttributeCollectionRemove().isEmpty())
////        {
////            doRemoveAttributes(req, usergrp);
////        }
//
//        //update entity to db.
//        db.Update(_uac.getUserSession().getUser(), usergrp);
//
//        EventEntityEdited ev = new EventEntityEdited(req, usergrp);
//        return ev;
//    }
//
//    private void doAppendAttributes(RequestEntityEdit req, UserGroup agrp,UserGroupType ugt) throws GravityEntityNotFoundException, RADException, CODEException, GravityException
//    {
//        for (String key : req.getAttributeCollectionAppend().keySet())
//        {
//            switch (key)
//            {
//                case "Users":
//                    for (String val : req.getAttributeCollectionAppend().get("Users"))
//                    {
//                        /*
//                        Fetch User by UserId
//                        JPAQuery query=new JPAQuery("Select u from User u where u.UserId=:uid");
//                         query.setParam("uid",agid);
//                         User user = _tctx.getDB().Find(EN.User,query);
//                        */
//                        User user = _tctx.getNucleusCtx().GetUserById(_tctx.getTenant().getCode(),val,getUserRole(agrp).name());
////                        assertValidUserForUserGroup(agrp,user);
//                        mapUser(req, agrp, user);
//                    }
//                    break;
//                case "AOPs":
//                    for (String val : req.getAttributeCollectionAppend().get("AOPs"))
//                    {
//                        Long campid = Long.valueOf(val);
//                        Campaign camp = _tctx.getDB().FindAssert(EN.Campaign.getEntityClass(), campid);
//                        mapCampaign(req, agrp, camp);
//                    }
//                    break;
//                default:
//                     throw new GravityNoSuchFieldException(new Exception(key+" not found in UserGroup"), EN.UserGroup.name(), key);
//
//            }
//        }
//    }
//
////    private UserRole getUserRole(UserGroup usrgp)
////    {
////        UserRole role = null;
////        switch (usrgp.getUserGroupType())
////        {
////            case AdminGroup:
////                role = UserRole.Admin;
////                break;
////            case AgentGroup:
////                role = UserRole.Agent;
////                break;
////        }
////        return role;
////    }
//
////    private void assertValidUserForUserGroup(UserGroup usrgp,User user) throws GravityIllegalObjectTypeException
////    {
////        switch (usrgp.getUserGroupType())
////        {
////            case AdminGroup:
////                if (!user.getUserRole().equals(UserRole.Admin))
////                {
////                    throw new GravityIllegalObjectTypeException(EN.User.name(),user.getUserId(),user.getUserRole().toString(),"Admin");
////                }
////                break;
////            case AgentGroup:
////            {
////                if (!user.getUserRole().equals(UserRole.Agent))
////                {
////                    throw new GravityIllegalObjectTypeException(EN.User.name(),user.getUserId(),user.getUserRole().toString(),"User");
////                }
////            }
////            break;
////        }
////    }
//
//    private void doRemoveAttributes(RequestEntityEdit req, UserGroup agrp) throws GravityException, RADException, CODEException
//    {
//        for (String key : req.getAttributeCollectionRemove().keySet())
//        {
//            switch (key)
//            {
//                case "Users":
//                    for (String val : req.getAttributeCollectionRemove().get("Users"))
//                    {
//                        User user = _tctx.getDB().FindAssert(new UserQuery().filterByUserId(val));
////                        assertValidUserForUserGroup(agrp,user);
//
//                        unmapUser(agrp,user);
//                    }
//                    break;
//                case "AOPs":
//                    for (String val : req.getAttributeCollectionRemove().get("AOPs"))
//                    {
//                        try
//                        {
//                            Long campid = Long.valueOf(val);
//                            Campaign camp = _tctx.getDB().FindAssert(EN.Campaign.getEntityClass(), campid);
//                            unmapCampaign(agrp, camp);
//                        }
//                        catch (NumberFormatException nfe)
//                        {
//                            //
//                        }
//                    }
//                    break;
//                default:
//                    throw new GravityNoSuchFieldException(new Exception(key+" not found in UserGroup"), EN.UserGroup.name(), key);
//
//            }
//        }
//    }
//
//    /**
//     * Map the agent with this AgentGroup. <br>
//     * -Check the active(Load/Start) campaigns and send the respective status event to the agent.
//     *
//     * @param req
//     * @param agrp
//     * @param ag
//     * @throws RADException
//     */
//    private void mapUser(RequestEntityEdit req, UserGroup agrp, User ag) throws RADException
//    {
//
////        agrp.getUsers().add(ag);
//        //TBD need to be implement in Dark
////        AIAgent aiag = _cctx.getAgentStore().GetById(ag.getId());
////        if (aiag != null)
////        {
////            //get all the assignable campaign for this agent.
////            List<AICampaign> aicamps = getAssignableCampaignsForAgent(agrp, aiag);
////            for (AICampaign aic : aicamps)
////            {
////                statEvProc.This().Build_Send_CampaignStChEv_ToAgent(aic, aiag, _uac.getUserId(), req.getReqId());
////            }
////        }
//
//    }
//
//    private void unmapUser(UserGroup agrp, User ag) throws    GravityIllegalArgumentException
//    {
//        //If agent not found in AgentGroup.
////        if (!agrp.getUsers().contains(ag))
////        {
////
////            throw new GravityIllegalArgumentException("Agent not mapped with this AgentGroup.", "[AgentGroup,Agent==" + agrp.getId() + "," + ag.getId() + "]", EventFailedCause.ValueOutOfRange);
////        }
//        /**
//         * Agent can remove from AgentGroup iff Agent is in Login state and he should not joined any campaign.
//         */
////        AIAgent aiag = _cctx.getAgentStore().GetById(ag.getId());
////        if (aiag != null)
////        {
////            AgentState currAgSt = aiag.getRtAgent().getAgentState();
////            if (AgentState.LoggedIn.equals(currAgSt))
////            {
////                //check is agent joined any campaign or not.
////                ArrayList<RtCampaign> camps = aiag.getRtAgent().getCampaigns();
////                if (!camps.isEmpty())
////                {
////                    throw new RADRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AgentHaveJoinedAssociatedCampaign, "[AgentGroup.Campaigns == " + camps.stream().map(c -> c.getId()).collect(Collectors.toList()) + "]");
////                }
////            }
////            else
////            {
////                throw new RADIllegalObjectStateException(EN.Agent.getEntityClass(), aiag.getAgent().getId(), currAgSt, AgentState.LoggedIn);
////            }
////        }
//
////        agrp.getUsers().remove(ag);
//    }
//
//    private void mapCampaign(RequestEntityEdit req, UserGroup agrp, Campaign camp) throws RADException
//    {
////        agrp.getAOPs().add(camp);
//
////        //Check campaign is in active(Load/Start) state or not
////        AICampaign aicamp = _cctx.getCampaignStore().GetById(camp.getId());
////        if (aicamp == null
////                || !(CampaignState.Load.equals(aicamp.getRtCampaign().getCampaignState())
////                || aicamp.isCampaignStarted()))
////        {
////            _logger.debug(camp + " : is not in Active (Load/Start) state so no need to send CampaignStChEv");
////            return;
////        }
//
////        for (Agent ag : agrp.getAgents())
////        {
////            AIAgent aiag = _cctx.getAgentStore().GetById(ag.getId());
////            if (aiag == null)
////            {
////                _logger.debug(ag + " is not LoggedIn in application so no need to send CampaignStChEv.");
////                continue;
////            }
////
////            statEvProc.This().Build_Send_CampaignStChEv(aicamp, _uac.getUserId(), req.getReqId());
////        }
//
//    }
//
//    private void unmapCampaign(UserGroup agrp, Campaign camp) throws GravityIllegalObjectStateException
//    {
////        AICampaign aicamp = _cctx.getCampaignStore().GetById(camp.getId());
////        if (aicamp != null)
////        {
////            throw new RADIllegalObjectStateException(EN.Campaign.getEntityClass(), aicamp.getCampaign().getId(), aicamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
////        }
//        /**
//         * No need to send CampaignStateChanged event to associated agents because campaign can be removed only on unload state and on unload state not agent
//         * have joined this campaign.
//         */
//
////        agrp.getAOPs().remove(camp);
//    }
//
//    /**
//     * Return all assignable campaigns for this agent. <br>
//     * -Get all active(Load/Start) campaigns of this agent group. <br>
//     * -Check is campaigns part of agent's registered media server or not. <br>
//     *
//     * @param agrp
//     * @param aiag
//     * @return
//     */
////    private List<AICampaign> getAssignableCampaignsForAgent(AgentGroup agrp, AIAgent aiag) throws RADRuntimeCheckFailedException
////    {
////        List<AICampaign> assgnCamps = new ArrayList<>();
////
////        //get all the active(Load/Start) campaign of this agent group.
////        List<AICampaign> aicamps = getActiveCamps(agrp);
////
////        //get agent's registered terminals.
////        HashMap<Channel, RtTerminal> hmTerms = aiag.getRtAgent().getTerminals();
////
////        //match campaign channel with agent's registered channel.
////        for (AICampaign aicm : aicamps)
////        {
////            for (XServer xs : aicm.getActiveXServers())
////            {
////                if (xs.getId().equals(hmTerms.get(xs.getChannel()).getXServerId()))
////                {
////                    assgnCamps.add(aicm);
////                }
////            }
////        }
////        return assgnCamps;
////    }
////
////    /**
////     * return all the active(Load/Start) campaign of this agent group.
////     *
////     * @param agrp
////     * @return
////     */
////    private List<AICampaign> getActiveCamps(AgentGroup agrp) throws RADRuntimeCheckFailedException
////    {
////        List<AICampaign> actvAiCmList = new ArrayList<>();
////        CampaignStore campaignStore = _cctx.getCampaignStore();
////
////        for (Campaign camp : agrp.getCampaigns())
////        {
////            AICampaign aicamp = campaignStore.GetById(camp.getId());
////            if (aicamp != null)
////            {
////                if (aicamp.isCampaignStarted())
////                {
////                    actvAiCmList.add(aicamp);
////                }
////            }
////
////        }
////        return actvAiCmList;
////    }
////    }
//}
//
