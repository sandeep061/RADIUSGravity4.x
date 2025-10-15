package ois.cc.gravity.services.survey;

import CrsCde.CODE.Common.Utils.DATEUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.SurveyDRQuery;
import ois.cc.gravity.framework.events.survey.EventSurveyInfoFetch;
import ois.cc.gravity.framework.requests.survey.RequestSurveyInfoFetch;
import ois.cc.gravity.objects.OSurveyInfo;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityEntityNotFoundException;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.SurveyDR;
import ois.radius.cc.entities.tenant.cc.SurveyData;

public class RequestSurveyInfoFetchService extends ARequestEntityService
{

    public RequestSurveyInfoFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        RequestSurveyInfoFetch req = (RequestSurveyInfoFetch) request;

        SurveyDR drentity = _tctx.getDB().FindAssert(new SurveyDRQuery().filterByUSUID(req.getUSUID()));
        SurveyData surveydataentity = getSurveyDataOnMaxAttempt(req);

        int attempt = 0;
        if(surveydataentity!=null){
            attempt=surveydataentity.getAttempt();
        }

        OSurveyInfo surveyinfo = new OSurveyInfo();
        surveyinfo.setAttempt(attempt);
        surveyinfo.setCanEdit(drentity.getSurvey().getCanEdit());
        surveyinfo.setCanView(drentity.getSurvey().getCanView());
        surveyinfo.setMaxAttempt(drentity.getSurvey().getMaxAttempt());
        surveyinfo.setIsExpired(DATEUtil.Now().after(drentity.getExpiriedOn()));

        EventSurveyInfoFetch ev = new EventSurveyInfoFetch(req);
        ev.setSurveyInfo(surveyinfo);

        return ev;
    }

    private SurveyData getSurveyDataOnMaxAttempt(RequestSurveyInfoFetch req) throws CODEException, GravityException
    {
        JPAQuery query = new JPAQuery("SELECT s FROM SurveyData s WHERE s.USUID = :id ORDER BY s.Attempt DESC");
        query.setParam("id", req.getUSUID());
        query.setLimit(1);
        return _tctx.getDB().Find(EN.SurveyData.getEntityClass(), query);
    }
}
