package ois.cc.gravity.services.survey;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.SurveyDataQuery;
import ois.cc.gravity.framework.events.survey.EventSurveyDataFetch;
import ois.cc.gravity.framework.requests.survey.RequestSurveyDataFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityEntityNotFoundException;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.SurveyData;

public class RequestSurveyDataFetchService extends ARequestEntityService
{

    public RequestSurveyDataFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        RequestSurveyDataFetch req = (RequestSurveyDataFetch) request;

        //Check view is allowed or  not.

        SurveyData surveydata = getSurveyDataOnMaxAttempt(req.getUSUID());

        if(surveydata==null){
            throw  new GravityEntityNotFoundException(EN.SurveyData.name());
        }
        if(surveydata.getSurveyDR()==null){
            throw  new GravityEntityNotFoundException(EN.SurveyDR.name());
        }

        if (surveydata.getSurveyDR().getSurvey().getCanView().equals(Boolean.FALSE))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.OperationNotAllowed, "Survey view is not allowed for this survey Code " + surveydata.getSurveyDR().getSurvey().getCode());
        }
        EventSurveyDataFetch fetched = new EventSurveyDataFetch(req);
        fetched.setSurveyData(surveydata);

        return fetched;
    }

    private SurveyData getSurveyDataOnMaxAttempt(String id) throws CODEException, GravityException
    {

        JPAQuery query = new JPAQuery("SELECT s FROM SurveyData s WHERE s.USUID = :usuid ORDER BY s.Attempt DESC");
        query.setParam("usuid", id);
        query.setLimit(1);
        return _tctx.getDB().Find(EN.SurveyData.getEntityClass(), query);
    }
}
