package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.entities.EntityState;
import code.ua.events.Event;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.db.queries.*;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.aops.AOPsType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Process;
import ois.radius.cc.entities.tenant.cc.*;

import java.util.ArrayList;

public class RequestAOPsMediaDeleteService extends ARequestEntityService {

//    private ArrayList<NameValuePair> entites = new ArrayList<>();

    public RequestAOPsMediaDeleteService(UAClient uac) {
        super(uac);
    }

    private ArrayList<AEntity> deleteentitylist = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityDelete req = (RequestEntityDelete) request;
        AOPsMedia aopsMedia = _tctx.getDB().FindAssert(EN.AOPsMedia.getEntityClass(), req.getEntityId());
        AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aopsMedia.getAOPs());

        ArrayList<AEntity> entites = getAOPsPropsKeysByChannel(aopsMedia.getChannel(), _tctx.getDB(), aopsMedia.getAOPs().getId());

        deleteentitylist.add(aopsMedia);
        entites.forEach(entity -> deleteentitylist.add(entity));
        deleteCDNAddress(aopsMedia.getAOPs().getId(), aopsMedia.getChannel());
        deleteCalleridAddress(aopsMedia.getAOPs().getId(), aopsMedia.getChannel());
        if (aopsMedia.getChannel().equals(Channel.Call)) {
            unmapCallerIDPlanAndDialedIDPlan(aopsMedia.getAOPs(), deleteentitylist);
        }

        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), deleteentitylist);

        EventSuccess evs = new EventSuccess(req);
        return evs;

    }

    public ArrayList<AEntity> getAOPsPropsKeysByChannel(Channel channel, MySQLDB db, Long aopid) throws GravityException, CODEException, Exception {
        ArrayList<AEntity> aldeleteentity = new ArrayList<>();
        for (AOPsProperties.Keys key : AOPsProperties.Keys.values()) {
            if (key.getChn() != channel) {
                continue;
            }

            AOPsProperties dbaopsProp = db.Find(new AOPsPropertiesQuery().filterByAOPs(aopid).filterByConfKey(key));
            if (dbaopsProp == null) {
                continue;
            }
            aldeleteentity.add(dbaopsProp);

        }

        return aldeleteentity;

    }

    private void unmapCallerIDPlanAndDialedIDPlan(AOPs aops, ArrayList<AEntity> list) throws CODEException, GravityException {
        if (aops.getAOPsType().equals(AOPsType.Campaign)) {
            Campaign camp = (Campaign) aops;
            deleteDialIDPlan(camp.getId(), list);
        } else {
            Process pro = (Process) aops;
            switch (pro.getProcessType()) {
                case Inbound:
                    deleteCallerIDPlan(aops.getId(), list);
                    break;
                case Blended:
                    deleteDialIDPlan(aops.getId(), list);
                    deleteCallerIDPlan(aops.getId(), list);
                    break;
            }
        }

    }

    private void deleteDialIDPlan(Long aopsId, ArrayList<AEntity> list) throws CODEException, GravityException {
        JPAQuery query = new DialIDPlanQuery().filterByAops(aopsId).toSelect();
        ArrayList<DialIDPlan> alDialIDPlans = _tctx.getDB().Select(EN.DialIDPlan, query);
        if (!alDialIDPlans.isEmpty()) {
//            entites.add(new NameValuePair<>("Delete", alDialIDPlans.toArray(new DialIDPlan[0])));
            alDialIDPlans.forEach(dplan -> list.add(dplan));
        }
    }

    private void deleteCallerIDPlan(Long aopsId, ArrayList<AEntity> list) throws CODEException, GravityException {
        JPAQuery query = new CallerIDPlanQuery().filterByAOPs(aopsId).toSelect();
        ArrayList<CallerIDPlan> alCallerIDPlans = _tctx.getDB().Select(EN.CallerIDPlan, query);
        if (!alCallerIDPlans.isEmpty()) {

            alCallerIDPlans.forEach(cid -> list.add(cid));
//            entites.add(new NameValuePair<>("Delete", alCallerIDPlans.toArray(new CallerIDPlan[0])));
        }
    }

    private void deleteCalleridAddress(long aopid, Channel chnnel) throws CODEException, GravityException {

        ArrayList<AOPsCallerId> callerids=_tctx.getDB().Select(new AOPsCallerIdQuery().filterByAOPs(aopid).filterByChannel(chnnel));
        for(AOPsCallerId callerid:callerids){
            ArrayList<AOPsCallerIdAddress>address=_tctx.getDB().Select(new AOPsCallerIdAddressQuery().filterByAOPsCallerId(callerid.getId()));
            deleteentitylist.add(callerid);
            deleteentitylist.addAll(address);
        }
    }

    private void deleteCDNAddress(long aopid, Channel channel) throws CODEException, GravityException {

        ArrayList<AOPsCDN> cdns=_tctx.getDB().Select(new AOPsCDNQuery().filterByAOPs(aopid).filterByChannel(channel));
        for(AOPsCDN cdn:cdns){
            ArrayList<AOPsCallerIdAddress>cdnaddress=_tctx.getDB().Select(new AOPsCDNAddressQuery().filterByAOPsCDN(cdn.getId()));
            deleteentitylist.add(cdn);
            deleteentitylist.addAll(cdnaddress);
        }
    }
}
