package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity_ad;
import code.entities.EntityState;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.*;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.aops.AOPsState;
import ois.radius.cc.entities.AEntity_cc;
import ois.radius.cc.entities.AEntity_ccad;
import ois.radius.cc.entities.AEntity_cces;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;
import org.vn.radius.cc.platform.exceptions.RADException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class RequestAOPsDeleteService extends ARequestEntityService
{

    public RequestAOPsDeleteService(UAClient uac)
    {
        super(uac);
    }

    ArrayList<NameValuePair> alEntites = new ArrayList<>();

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;

        AOPs aops = _tctx.getDB().FindAssert(AOPs.class, req.getEntityId());
//        AICampaign aiCamp = _cctx.getCampaignStore().GetById(req.getEntityId());
        /**
         * A Campaign can delete from DB if it satisfy below conditions. <be>
         * - Campaign should not be Loaded. <br>
         * - Campaign should not be in RtDB. <br>
         * - Should not mapped with any Admin or Agent if mapped then we have unmapped first. <br>
         * - Should not mapped with any ContactList. if found mapped then we have to remove. <br>
         */
//        if (aiCamp != null)
//        {
//            /**
//             * -Campaign cant be deleted becoz Campaign state is in Load and it may be active in application. <br>
//             * -No need to check RtCampaign. as there is no chance if campaign is not Loaded then is not available in RtDB. <br>
//             */
//            EventCampaignIllegalState ev = new EventCampaignIllegalState(req, aiCamp.getCampaign().getId(),
//                    aiCamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
//            return ev;
//        }
        AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aops);

        /**
         * V:090822. <br>
         * Check for any pending contact scheduled are available or not. <br>
         * -Campaign is allowed to delete only when there are no pending call backs. <br>
         * -If still user wants to delete the campaign then he/she need to cancel all pending call backs.
         */
        ContactScheduledQuery cntshQry = new ContactScheduledQuery()
                .filterByCampaign(aops.getId())
                .filterByScheduledStates(ContactScheduled.State.Scheduled, ContactScheduled.State.Assigned);
        ArrayList<ContactScheduled> pendingConSh = _tctx.getDB().Select(cntshQry);
        if (!pendingConSh.isEmpty())
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.PendingContactScheduledExist);
        }

        ArrayList<NameValuePair> entities = doPreDeleteProcesses(aops);
        entities.add(new NameValuePair("Delete", aops));

        try {
            _tctx.getALMCtx().DeleteCampaign(aops);
        }
        catch (GravityUnhandledException gex)
        {

            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AOPsNotStopedInRealm, aops.getCode());
        }

        getAopsBfToDelete(aops);
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        return new EventEntityDeleted(req, aops);
    }

    private ArrayList<NameValuePair> doPreDeleteProcesses(AOPs aops) throws GravityException, RADException, CODEException
    {

        alEntites.add(new NameValuePair<>("Delete", doUnmapUserGroupAops(aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.AOPsSchedule, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.CallerIDPlan, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.AOPsProperties, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.AOPsMedia, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.DialIDPlan, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Update", getEntityToDelete(EN.Disposition, aops).toArray(new AEntity_ad[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.Skill, aops).toArray(new AEntity_cces[0])));
        alEntites.add(new NameValuePair<>("Delete", getAgentSkillByCampaign(aops).toArray(new AEntity_cc[0])));
        //TBD:do process for AOPsCDN and AOPsCallerId.
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.AOPsCDN, aops).toArray(new AEntity_cces[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.AOPsCallerId, aops).toArray(new AEntity_cces[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.ContactBook, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.UserGroupAops, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.AOPsAIProperties, aops).toArray(new AEntity_cc[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.WorkFlow, aops).toArray(new AEntity_cces[0])));
        alEntites.add(new NameValuePair<>("Delete", getEntityToDelete(EN.XSessionStatusRedial, aops).toArray(new AEntity_cces[0])));
        return alEntites;
    }

    private ArrayList<AEntity_ad> getEntityToDelete(EN en, AOPs aops) throws CODEException, GravityException
    {

        Boolean isAdEn = false;
        Boolean isccesEn = false;
        if (AEntity_ccad.class.isAssignableFrom(en.getEntityClass()))
        {
            isAdEn = true;
        }
        if (AEntity_cces.class.isAssignableFrom(en.getEntityClass()))
        {
            isccesEn = true;
        }

        String clsname = en.getEntityClass().getSimpleName();  // Assume getTableName() returns the entity class name
        StringBuilder select = new StringBuilder("Select " + clsname + " from " + clsname + " " + clsname
                + " where " + clsname + ".AOPs.Id =: campid ");

        if (isAdEn)
        {
            select.append("AND ").append(clsname).append(".Deleted = :isdel");
        }

        if (isccesEn)
        {
            select.append("AND ").append(clsname).append(".EntityState =: enstate");

        }

        JPAQuery dbq = new JPAQuery(select.toString());
        dbq.setParam("campid", aops.getId());

        if (isAdEn)
        {
            dbq.setParam("isdel", false);
        }
        if (isccesEn)
        {
            dbq.setParam("enstate", EntityState.Active);
        }

        ArrayList<AEntity_ad> entities = _tctx.getDB().Select(en, dbq);
        return entities;
    }

    private ArrayList<UserGroupAops> doUnmapUserGroupAops(AOPs aops) throws RADException, GravityException, CODEException
    {
        UserGroupAopsQuery adminGpQry = new UserGroupAopsQuery().filterByAops(aops.getId());
        ArrayList<UserGroupAops> alAdGp = _tctx.getDB().Select(adminGpQry);
        return alAdGp;
    }

//    private ArrayList<DNCList> doUnmapDNCLists(AOPs aops) throws RADException, GravityException, CODEException
//    {
//        DNCListQuery dncqry = new DNCListQuery().filterByAOPs(aops.getId());
//        ArrayList<DNCList> alDNCs = _tctx.getDB().Select(dncqry);
//        if (!alDNCs.isEmpty())
//        {
//            alDNCs.stream()
//                    .forEach(dnc -> dnc.setAOPs(updateCampaigns(dnc.getAOPs(), aops)));
//        }
//        return alDNCs;
//    }
//    private ArrayList<BlockList> doUnmapBlockList(AOPs aops) throws RADException, GravityException, CODEException
//    {
//        BlockListQuery dncqry = new BlockListQuery().filterByaops(aops.getId());
//        ArrayList<BlockList> alBlLst = _tctx.getDB().Select(dncqry);
//        if (!alBlLst.isEmpty())
//        {
//            alBlLst.stream().forEach(dnc -> dnc.setAOPs(updateCampaigns(dnc.getAOPs(), aops)));
//        }
//        return alBlLst;
//    }
    private Set<AOPs> updateCampaigns(Set<AOPs> aops, AOPs camp)
    {
        if (aops != null)
        {
            Iterator<AOPs> itrCamp = aops.iterator();
            while (itrCamp.hasNext())
            {
                if (itrCamp.next() == camp)
                {
                    itrCamp.remove();
                }
            }
        }

        return aops;
    }

    private ArrayList<AgentSkill> getAgentSkillByCampaign(AOPs aops) throws RADException, GravityException, CODEException
    {
        JPAQuery query = new JPAQuery("Select agsk from AgentSkill agsk join agsk.Skill s where s.AOPs.Id=:id");
        query.setParam("id", aops.getId());
        ArrayList<AgentSkill> agSks = _tctx.getDB().Select(EN.AgentSkill, query);
        return agSks;
    }

    private void getAopsBfToDelete(AOPs aops) throws CODEException, GravityException
    {
        ArrayList<AOPsBF> aopsbfs = _tctx.getDB().Select(new AOPsBFQuery().filterByAOPs(aops.getId()));
        for (AOPsBF aopbf : aopsbfs)
        {
            ArrayList<AOPsBFProperties> aoPsBFProperties = getDeleteAOPsBFProperties(aopbf);
            alEntites.add(new NameValuePair<>("Delete", aoPsBFProperties.toArray(new AEntity_cc[0])));
            alEntites.add(new NameValuePair<>("Delete", aopbf));
        }
    }

    private ArrayList<AOPsBFProperties> getDeleteAOPsBFProperties(AOPsBF bf) throws CODEException, GravityException
    {
        ArrayList<AOPsBFProperties> entities = _tctx.getDB().Select(new AOPsBFPropertiesQuery().filterByAOPsBF(bf.getId()));
        return entities;
    }

}
