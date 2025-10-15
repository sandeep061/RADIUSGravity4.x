package ois.cc.gravity.services.oi;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.OIMetricsMapQuery;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.framework.requests.aops.RequestAOPsSLAConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.XAlertID;
import ois.radius.cc.entities.tenant.oi.OIAlertConfig;
import ois.radius.cc.entities.tenant.oi.OIMetrics;
import ois.radius.cc.entities.tenant.oi.OIMetricsMap;
import ois.radius.cc.entities.tenant.oi.OIRule;

import java.util.ArrayList;

public class RequestAOPsSLAConfigService extends ARequestEntityService {

    public RequestAOPsSLAConfigService(UAClient uac) {
        super(uac);
    }

    private final ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {

        RequestAOPsSLAConfig req = (RequestAOPsSLAConfig) request;

        OIMetricsMap metricsmap = _tctx.getDB().Find(new OIMetricsMapQuery().filterByEntityId(req.getEntityId()).filterByEN(req.getEntity()));
        OIMetrics metrics = null;
        if (metricsmap==null||metricsmap.getOIMetrics() == null) {
            metrics = BuildOIMetrics(req);
            ArrayList<OIMetrics> listmetrics = new ArrayList<>();
            listmetrics.add(metrics);

            ArrayList<OIAlertConfig> oiAlertConfigs = BuildOIAlertConfig(req);

            BuildOIRule(req, listmetrics, oiAlertConfigs);
            BuildOIMetricsMap(req, metrics, oiAlertConfigs);
        } else {
            metrics = metricsmap.getOIMetrics();
            OIMetrics oiMetrics = UpdateOIMetrics(req, metricsmap.getOIMetrics());
            ArrayList<OIMetrics> listupdatemetrics = new ArrayList<>();
            listupdatemetrics.add(oiMetrics);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), metrics));

        }
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
        EventEntityAdded ev = new EventEntityAdded(req, metrics);
        return ev;
    }

    private OIMetrics BuildOIMetrics(RequestAOPsSLAConfig req) {
        OIMetrics metrics = new OIMetrics();
        metrics.setCode(req.getOIMetrics().getCode());
        metrics.setName(req.getOIMetrics().getName());
        metrics.setMetricsKey(req.getOIMetrics().getMetricsKey());
        metrics.setDimension(req.getOIMetrics().getDimension());
        metrics.setUnit(req.getOIMetrics().getMetricsUnit());
        metrics.setFrequency(req.getOIMetrics().getFrequency());
        metrics.setPeriod(req.getOIMetrics().getPeriod());
        metrics.setStreamID(_tctx.getTenant().getCode() + "_" + metrics.getCode());
        metrics.setIsRetention(req.getOIMetrics().getRetention());
        metrics.setEntityID(req.getEntityId());
        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), metrics));
        return metrics;
    }

    private OIMetrics UpdateOIMetrics(RequestAOPsSLAConfig req, OIMetrics metrics) {

        metrics.setCode(req.getOIMetrics().getCode() == null ? metrics.getCode() : req.getOIMetrics().getCode());
        metrics.setName(req.getOIMetrics().getName() == null ? metrics.getName() : req.getOIMetrics().getName());
        metrics.setMetricsKey(req.getOIMetrics().getMetricsKey() == null ? metrics.getMetricsKey() : req.getOIMetrics().getMetricsKey());
        metrics.setDimension(req.getOIMetrics().getDimension() == null ? metrics.getDimension() : req.getOIMetrics().getDimension());
        metrics.setUnit(req.getOIMetrics().getMetricsUnit() == null ? metrics.getUnit() : req.getOIMetrics().getMetricsUnit());
        metrics.setFrequency(req.getOIMetrics().getFrequency() == null ? metrics.getFrequency() : req.getOIMetrics().getFrequency());
        metrics.setPeriod(req.getOIMetrics().getPeriod() == null ? metrics.getPeriod() : req.getOIMetrics().getPeriod());
        metrics.setStreamID(_tctx.getTenant().getCode() + "_" + metrics.getCode());
        metrics.setIsRetention(req.getOIMetrics().getRetention() == null ? metrics.getIsRetention() : req.getOIMetrics().getRetention());
        entities.add(new NameValuePair(ENActionList.Action.Update.name(), metrics));
        return metrics;
    }

    private ArrayList<OIAlertConfig> BuildOIAlertConfig(RequestAOPsSLAConfig req) throws CODEException, Exception, GravityException {
        ArrayList<RequestAOPsSLAConfig.ORule.OAlertConfig> oAlertConfigs = req.getOIRule().getOIAlertConfigs();
        ArrayList<OIAlertConfig> oiAlertConfig = new ArrayList<>();

        for (RequestAOPsSLAConfig.ORule.OAlertConfig configreq : oAlertConfigs) {

            OIAlertConfig alertConfig = new OIAlertConfig();
            alertConfig.setInApp(configreq.getInApp());
            alertConfig.setUsers(getUsers(configreq.getUsers()));
            alertConfig.setXAlertIDs(getXAlerts(configreq.getXAlertIDs()));
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), alertConfig));
            oiAlertConfig.add(alertConfig);
        }
        return oiAlertConfig;
    }

    private OIRule BuildOIRule(RequestAOPsSLAConfig req, ArrayList<OIMetrics> metrics, ArrayList<OIAlertConfig> oialertconfigs) {
        OIRule rule = new OIRule();
        rule.setName(req.getOIRule().getName());
        rule.setRuleCondition(req.getOIRule().getRuleCondition());
        rule.setOIMetrics(metrics);
        rule.setOIAlertConfigs(oialertconfigs);
        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), rule));
        return rule;
    }

    private ArrayList<User> getUsers(ArrayList<String> userids) throws Exception, CODEException, GravityException {
        ArrayList<User> userlist = new ArrayList<>();

        for (String userid : userids) {
            User user = _tctx.getDB().FindAssert(new UserQuery().filterByUserId(userid));
            userlist.add(user);
        }
        return userlist;
    }

    private ArrayList<XAlertID> getXAlerts(ArrayList<Long> ids) throws Exception, CODEException, GravityException {
        ArrayList<XAlertID> alertlist = new ArrayList<>();

        for (Long id : ids) {
            XAlertID alertid = _tctx.getDB().FindAssert(EN.XAlertID.getEntityClass(), id);
            alertlist.add(alertid);
        }
        return alertlist;
    }

    private void BuildOIMetricsMap(RequestAOPsSLAConfig req, OIMetrics metrics, ArrayList<OIAlertConfig> alertConfigs) {

        OIMetricsMap metrixmap = new OIMetricsMap();

        metrixmap.setOIMetrics(metrics);
        metrixmap.setEntity(req.getEntity());

        metrixmap.setEntityID(req.getEntityId());
        metrixmap.setOIAlerts(alertConfigs);
        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), metrixmap));


    }

    private OIRule UpdateOIRule(RequestAOPsSLAConfig req, ArrayList<OIMetrics> metrics, ArrayList<OIAlertConfig> oialertconfigs) throws CODEException, GravityException {

        JPAQuery query = new JPAQuery("SELECT a FROM OIRule a JOIN a.OIMetrics s WHERE  s.Id :id");
          query.setParam("id",metrics.get(0).getId());

        OIRule rule=  _tctx.getDB().Find(EN.OIRule,query);
        if(rule==null) {
            rule = new OIRule();
            rule.setName(req.getOIRule().getName());
            rule.setRuleCondition(req.getOIRule().getRuleCondition());
            rule.setOIMetrics(metrics);
            rule.setOIAlertConfigs(oialertconfigs);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), rule));

        }
        else {
            rule.setName(req.getOIRule().getName()==null?rule.getName():req.getOIRule().getName());
            rule.setRuleCondition(req.getOIRule().getRuleCondition()==null?rule.getRuleCondition():req.getOIRule().getRuleCondition());
            rule.setOIMetrics(metrics);
            rule.setOIAlertConfigs(oialertconfigs);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), rule));
        }
        return rule;
    }

}
