package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Utils.LOGUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.*;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.exceptions.GravityException;

import java.util.ArrayList;

public class RequestXServerDeleteService extends ARequestEntityService
{
    public RequestXServerDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;

        XServer xs = _tctx.getDB().FindAssert(XServer.class, req.getEntityId());

        //Check media server is running or not? media server must be in shutdown state.
//        AIXServer aixs = _cctx.getXServerStore().GetById(xs.getId());
//        if (aixs != null && !aixs.getProviderState().equals(ProviderState.Shutdown))
//        {
//            throw new RADIllegalObjectStateException(EN.XServer.getEntityClass(), xs.getId(), aixs.getProviderState(), ProviderState.Shutdown);
//        }

        final ArrayList<NameValuePair> entities = new ArrayList<>();
        entities.add(new NameValuePair("Delete", xs));

        //check campaign of this media server.Campaign must be in unload state.
        AOPsMediaQuery cmQry = new AOPsMediaQuery().filterByXServer(xs.getId());
        ArrayList<AOPsMedia> campMdas = _tctx.getDB().Select(cmQry);
        for (AOPsMedia campMedia : campMdas)
        {
            _logger.trace(LOGUtil.ArgString(campMedia));

//            AICampaign aicamp = _tctx.getCampaignStore().GetById(campMedia.getCampaign().getId());
//            if (aicamp != null)
//            {
//                EventCampaignIllegalState ev = new EventCampaignIllegalState(req, campMedia.getId(), aicamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
//                return ev;
//            }
            entities.add(new NameValuePair("Delete", campMedia));
        }

        //Unmap agnet associated agents.
        UnmapAgentfromXServer(entities, xs.getId());

        //check for Terminals.
        TerminalQuery termqry = new TerminalQuery().filterByXServer(xs.getId());
        ArrayList<Terminal> termList = _tctx.getDB().Select(termqry);
        for (Terminal term : termList)
        {
            //V:010324
            //RtTerminal rtTerm = _cctx.getRtDB().Find(RtTerminal.class, term.getId());
//            RtTerminal rtTerm = _tctx.getTerminalStore().GetRtById(term.getId());
//            if (rtTerm != null
//                    && !(TerminalState.Unknown.equals(rtTerm.getTerminalState())
//                    && TerminalState.Offline.equals(rtTerm.getTerminalState())))
//            {
//                throw new GravityIllegalObjectStateException(Terminal.class, term.getId().toString(), rtTerm.getTerminalState(), TerminalState.Offline, TerminalState.Unknown);
//            }
            entities.add(new NameValuePair("Delete", term));
        }

        QueueQuery queqry = new QueueQuery().filterByXServer(xs.getId());
        ArrayList<Queue> QueueList = _tctx.getDB().Select(queqry);
        ArrayList<AgentSkill> mappedAgSkills = new ArrayList<>();
        for (Queue que : QueueList)
        {
            entities.add(new NameValuePair("Delete", que));

            Skill skill = _tctx.getDB().Find(new SkillQuery().filterByQueue(que.getId()));
            if (skill == null)
            {
                continue;
            }

            skill.setQueue(null);
            entities.add(new NameValuePair("Update", skill));

            mappedAgSkills = _tctx.getDB().Select(new AgentSkillQuery().filterBySkill(skill.getId()));
            if (!mappedAgSkills.isEmpty())
            {
                entities.add(new NameValuePair("Delete", mappedAgSkills.toArray(new AgentSkill[0])));
            }

        }

        //Unmap XServerEndPointProperties.
        ArrayList<XServerEndpointProperties> xsEndPnts = getXServerEndpointPropertiesByXserver(xs.getId());
        if(!xsEndPnts.isEmpty())
        {
           entities.add(new NameValuePair("Delete",xsEndPnts.toArray(new XServerEndpointProperties[0])));
        }
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
//        sendEventToUsers(req, mappedAgSkills);

        return new EventEntityDeleted(req, xs.getId().toString(), EN.XServer.name());

    }

//    private void sendEventToUsers(RequestEntityDelete req, ArrayList<AgentSkill> agsks) throws RADException
//    {
//        for (AgentSkill agsk : agsks)
//        {
//            statEvProc.This().Build_Send_SkillRemovedEv(req.getReqId(), _tctx, agsk, _uac.getUserSession().getUser());
//        }
//    }

    private void UnmapAgentfromXServer(ArrayList<NameValuePair> entities, Long xsid) throws GravityException, Exception, CODEException
    {
//        List<AIAgent> mappedAgents = _tctx.getAgentStore().getAgentsByXServer(xsid);
//        if (!mappedAgents.isEmpty())
//        {
//            List<String> agLoginids = mappedAgents.stream().map(a -> a.getAgent().getLoginId()).collect(Collectors.toList());
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AgentsHaveAssociatedWithXServer, "Agents : " + agLoginids);
//        }

        ArrayList<AgentMediaMap> agMaps = findMapsByXServerID(xsid);
        if (agMaps.isEmpty())
        {
            return;
        }

        for (AgentMediaMap agmdmp : agMaps)
        {
            UserMedia usrmd = getAgentMediaByAgentMediaMap(agmdmp.getId());
            if(usrmd==null)
            {
                continue;
            }
            usrmd.getAgentMediaMaps().remove(agmdmp);
            entities.add(new NameValuePair("Update", usrmd));
        }

        entities.add(new NameValuePair("Delete", agMaps.toArray(new AEntity[0])));

    }

    private ArrayList<AgentMediaMap> findMapsByXServerID(Long id) throws GravityException, Exception, CODEException
    {
        JPAQuery qry = new JPAQuery("Select amp from AgentMediaMap amp where amp.XServer.Id=:id");
        qry.setParam("id", id);
        ArrayList<AgentMediaMap> maps = (ArrayList<AgentMediaMap>) _tctx.getDB().Select(qry);
        return maps;
    }

    private UserMedia getAgentMediaByAgentMediaMap(Long agmdmapid) throws GravityException, CODEException, Exception
    {
        JPAQuery qry = new JPAQuery("Select md from UserMedia md where  element(md.AgentMediaMaps).Id=:id");
        qry.setParam("id", agmdmapid);
//        qry.setParam("isdel", false);

        UserMedia am = _tctx.getDB().Find(EN.UserMedia, qry);
        return am;
    }

    private ArrayList<XServerEndpointProperties> getXServerEndpointPropertiesByXserver(Long xid) throws GravityException, CODEException, Exception
    {
        JPAQuery query = new JPAQuery("select x from XServerEndpointProperties x where x.XServer.Id=:id");
        query.setParam("id", xid);
        ArrayList<XServerEndpointProperties> xprop = (ArrayList<XServerEndpointProperties>) _tctx.getDB().Select(query);
        return xprop;
    }
}
