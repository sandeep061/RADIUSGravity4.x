package ois.cc.gravity.services.oi;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.OIMetricsQuery;
import ois.cc.gravity.framework.events.oi.EventOISLAMetricsFetch;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.*;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.oi.OIAlertConfig;
import ois.radius.cc.entities.tenant.oi.OIMetrics;
import ois.radius.cc.entities.tenant.oi.OIRule;

import java.util.ArrayList;

public class RequestOIMetricsFetchService extends ARequestEntityService {
    public RequestOIMetricsFetchService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {

        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        try
        {
            OIMetricsQuery enQry = new OIMetricsQuery();
            if (reqFetch.getFilters() != null)
            {
                enQry.doApplyFilters(reqFetch.getFilters());
            }
            enQry.ApplyOrderBy(reqFetch.getOrderBy());

            Integer recCount = null;

            Boolean reqcnt = reqFetch.getIncludeCount() != null && reqFetch.getIncludeCount();

            if (reqcnt)
            {
                recCount = _tctx.getDB().SelectCount(enQry);

            }
            if (reqFetch.getLimit() != null)
            {
                enQry.setLimit(reqFetch.getLimit());
            }

            if (reqFetch.getOffset() != null)
            {
                enQry.setOffset(reqFetch.getOffset());
            }

            JPAQuery ctq = enQry.toSelect();

            ArrayList<OISLAMetrics> oislametrics = new ArrayList<>();
            ArrayList<OIMetrics> dboimetrics = _tctx.getDB().Select(getClass(), ctq);
            for (OIMetrics metrics : dboimetrics)
            {
                OISLAMetrics oislaMetrics=new OISLAMetrics();
                oislaMetrics.setEntityId(metrics.getEntityID());
                oislaMetrics.setEntity(metrics.getDimension());
                oislaMetrics.setOIMetrics(BuildOMetrics(metrics));
                oislaMetrics.setOIRule(BuildORule(metrics));
                oislametrics.add(oislaMetrics);
            }

            EventOISLAMetricsFetch event = new EventOISLAMetricsFetch(reqFetch);
            event.setOISLAMetrics(oislametrics);
//            event.setRecordCount(recCount);

            return event;
        }
        catch (GravityException rex)
        {
            return BuildExceptionEvents(reqFetch, rex);
        }



    }

    protected Event BuildExceptionEvents(RequestEntityFetch reqfetch, GravityException rex) throws GravityNoSuchFieldException, GravityException
    {
        if (rex instanceof GravityNoSuchFieldException)
        {
            GravityNoSuchFieldException fex = (GravityNoSuchFieldException) rex;
            throw fex;
        }
        else
        {
            throw rex;
        }
    }

    private OISLAMetrics BuildOOIAlertConfig(OIMetrics metrics) throws CODEException, GravityException {
        OISLAMetrics oislaMetrics=new OISLAMetrics();
        oislaMetrics.setOIMetrics(BuildOMetrics(metrics));
        oislaMetrics.setOIRule(BuildORule(metrics));
        return oislaMetrics;

    }
    private OMetrics BuildOMetrics(OIMetrics metrics){
        OMetrics ometrics=new OMetrics();
        ometrics.setCode(metrics.getCode());
        ometrics.setMetricsKey(metrics.getMetricsKey());
        ometrics.setDimension(metrics.getDimension());
        ometrics.setName(metrics.getName());
        ometrics.setPeriod(metrics.getPeriod());
        ometrics.setFrequency(metrics.getFrequency());
        ometrics.setRetention(metrics.getIsRetention());
        ometrics.setStreamID(metrics.getStreamID());
        ometrics.setId(metrics.getId());
        ometrics.setMetricsUnit(metrics.getUnit());
        return ometrics;
    }
    private ORule BuildORule(OIMetrics metrics) throws CODEException, GravityException {
   JPAQuery query=new JPAQuery("SELECT DISTINCT r FROM OIRule r JOIN r.OIMetrics m WHERE m.Id= :metricIds");
   query.setParam("metricIds",metrics.getId());
  OIRule rule=  _tctx.getDB().Find(EN.OIRule,query);

       ORule orule=new ORule();
       orule.setName(rule.getName());
       orule.setId(rule.getId());
       orule.setRuleCondition(rule.getRuleCondition());
       orule.setOIAlertConfigs(BuildOIALertConfig(new ArrayList<>( rule.getOIAlertConfigs())));
     return orule;
    }

    private ArrayList<OAlertConfig> BuildOIALertConfig(ArrayList<OIAlertConfig> configs){

        ArrayList<OAlertConfig>oAlertConfiglist=new ArrayList<>();
         for (OIAlertConfig oiconfig:configs){
             OAlertConfig oconfig=new OAlertConfig();
             oconfig.setXAlertIDs(new ArrayList<>(oiconfig.getXAlertIDs()));
             oconfig.setUsers(BuildOUser(new ArrayList<>(oiconfig.getUsers())));
             oconfig.setId(oiconfig.getId());
             oconfig.setInApp(oiconfig.getInApp());
             oAlertConfiglist.add(oconfig);
         }
         return oAlertConfiglist;
    }
    private ArrayList<OUser> BuildOUser(ArrayList<User> users) {
        ArrayList<OUser>ousers=new ArrayList<>();
        for (User user : users) {
            OUser ouser = new OUser();
            ouser.setName(user.getName());
            ouser.setUserId(user.getUserId());
            ouser.setLoginId(user.getLoginId());
            ouser.setId(user.getId());
            ousers.add(ouser);
        }
        return ousers;
    }
}
