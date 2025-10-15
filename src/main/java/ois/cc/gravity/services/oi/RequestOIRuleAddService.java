package ois.cc.gravity.services.oi;

import code.common.exceptions.CODEException;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.oi.OIMetrics;
import ois.radius.cc.entities.tenant.oi.OIRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import ois.radius.cc.entities.tenant.oi.OIAlertConfig;

public class RequestOIRuleAddService extends ARequestEntityService {
    public RequestOIRuleAddService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {

        RequestEntityAdd req = (RequestEntityAdd) request;

        if (!req.getAttributes().containsKey("Name"))
        {
            throw new GravityIllegalArgumentException("Name", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        OIRule rule = BuildOIRule(req);

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), rule);

        EventEntityAdded ev = new EventEntityAdded(req, rule);
        return ev;

    }

    private OIRule BuildOIRule(RequestEntityAdd req) throws Exception, CODEException, GravityException {


        String merticsid = req.getAttributeValueOf(String.class, "OISLAMetrics");
        String oialertconfig = req.getAttributeValueOf(String.class, "OIAlertConfigs");

        //getting ids
        ArrayList<Long> oiAlertConfigsid = getOIAlertConfigsid(oialertconfig);
        ArrayList<Long> oiMetricsId = getOIMetricsId(merticsid);

        ArrayList<OIMetrics> oiMetrics = getOIMetrics(oiMetricsId);
        ArrayList<OIAlertConfig> oiAlertConfg = getOIAlertConfg(oiAlertConfigsid);


        OIRule rule = new OIRule();
        rule.setOIAlertConfigs(oiAlertConfg);
        rule.setOIMetrics(oiMetrics);
        rule.setName(req.getAttributeValueOf(String.class, "Name"));
        if (req.getAttributes().containsKey("RuleCondition")) {
            rule.setRuleCondition(req.getAttributeValueOf(String.class, "RuleCondition"));
        }
        if (req.getAttributes().containsKey("RuleCondition")) {
            rule.setRuleCondition(req.getAttributeValueOf(String.class, "RuleCondition"));
        }

        return rule;
    }

    private ArrayList<Long> getOIMetricsId(String ids) {
        String[] metricsids = ids.split(",");
        return (ArrayList<Long>) Arrays.asList(metricsids).stream().map(id -> Long.valueOf(id.trim())).collect(Collectors.toList());
    }

    private ArrayList<Long> getOIAlertConfigsid(String confids) {
        String[] metricsids = confids.split(",");
        return (ArrayList<Long>) Arrays.asList(metricsids).stream().map(id -> Long.valueOf(id.trim())).collect(Collectors.toList());
    }

    private ArrayList<OIMetrics> getOIMetrics(ArrayList<Long> ids) throws CODEException, GravityException {

        ArrayList<OIMetrics> oiMetricslists = new ArrayList<>();
        for (Long id : ids) {
            OIMetrics oimetrics = _tctx.getDB().FindAssert(EN.OIMetrics.getEntityClass(), id);
            oiMetricslists.add(oimetrics);
        }
        return oiMetricslists;
    }

    private ArrayList<OIAlertConfig> getOIAlertConfg(ArrayList<Long> ids) throws CODEException, GravityException {

        ArrayList<OIAlertConfig> oialertconfiglists = new ArrayList<>();
        for (Long id : ids) {
            OIAlertConfig configs = _tctx.getDB().FindAssert(EN.OIAlertConfig.getEntityClass(), id);
            oialertconfiglists.add(configs);
        }
        return oialertconfiglists;
    }
}
