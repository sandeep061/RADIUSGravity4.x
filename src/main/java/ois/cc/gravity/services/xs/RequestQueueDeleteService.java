package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventCode;
import code.ua.events.EventEntityDeleted;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AgentSkillQuery;
import ois.cc.gravity.db.queries.SkillQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AgentSkill;
import ois.radius.cc.entities.tenant.cc.Queue;
import ois.radius.cc.entities.tenant.cc.Skill;


import java.util.ArrayList;

public class RequestQueueDeleteService extends ARequestEntityService
{

    public RequestQueueDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        /**
         * Queue delete process pre-checks. <br>
         * - Check queue is mapped with any skill or not. <br>
         * - If mapped then find the campaign and check campaign must in unload state. <br>
         * - Check media server of the specified queue must be in Inservice. <br>
         * - Then send queue delete request to provider.
         */
        RequestEntityDelete req = (RequestEntityDelete) request;
        Queue queue = _tctx.getDB().Find(Queue.class, req.getEntityId());
        Long xsid = queue.getXServer().getId();
//        AIXServer aixs = _tctx.getXServerStore().GetById(xsid);
//        ProviderState prvSt = aixs == null ? ProviderState.Shutdown : aixs.getProviderState();
//        if (!prvSt.equals(ProviderState.InService))      //xserver must be in INSERVICE state
//        {
//            throw new GravityIllegalObjectStateException(EN.XServer.name(), xsid.toString(), ProviderState.Shutdown, ProviderState.InService);
//        }

        Skill skill = _tctx.getDB().Find(new SkillQuery().filterByQueue(queue.getId()));
        ArrayList<NameValuePair> alEntites = new ArrayList<>();
        ArrayList<AgentSkill> mappedAgSkills = new ArrayList<>();
        if (skill != null)
        {
            Long campid = skill.getAOPs().getId();
//            AICampaign aicamp = _uac.getCCtx().getCampaignStore().GetById(campid);
//            if (aicamp != null)    //campaign must be in UnLoad
//            {
//                throw new RADIllegalObjectStateException(EN.Campaign.name(), campid.toString(), aicamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
//            }

            skill.setQueue(null);

            AgentSkillQuery asquery = new AgentSkillQuery();
            asquery.filterBySkill(skill.getId());
            mappedAgSkills = _tctx.getDB().Select(asquery);

            alEntites.add(new NameValuePair<>("Delete", mappedAgSkills.toArray(new AEntity[0])));
            alEntites.add(new NameValuePair<>("Update", skill));

        }
        alEntites.add(new NameValuePair<>("Delete", queue));

//        sendQueueDeleteToProvider(queue);

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), alEntites);

//        if (!mappedAgSkills.isEmpty())
//        {
//            sendEventToUsers(req, mappedAgSkills);
//        }
        EventEntityDeleted ev = new EventEntityDeleted(req,queue.getId().toString(),EN.Queue.name());
        return ev;
//        return new EventOK(req, EventCode.EntityDeleted);
    }

//    protected void sendQueueDeleteToProvider(Queue queue) throws GravityXSPIException, RADStoreEntityNotFoundException
//    {
//
//        XServer xServer = queue.getXServer();
////        AIXServer aixs = _tctx.getXServerStore().GetByIdAssert(xServer.getId());
//
////        XProviderStub providerStub = aixs.getProviderStub();
////        providerStub.DeleteQueue(queue.getAddress());
//    }

//    private void sendEventToUsers(RequestEntityDelete req, ArrayList<AgentSkill> agsks) throws GravityException
//    {
//        for (AgentSkill agsk : agsks)
//        {
//            statEvProc.This().Build_Send_SkillRemovedEv(req.getReqId(), _tctx, agsk, _uac.getUserSession().getUser());
//        }
//    }

}

