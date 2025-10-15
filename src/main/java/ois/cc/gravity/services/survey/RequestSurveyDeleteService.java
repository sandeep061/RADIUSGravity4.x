package ois.cc.gravity.services.survey;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.*;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;

import java.util.ArrayList;

public class RequestSurveyDeleteService extends ARequestEntityService {
    public RequestSurveyDeleteService(UAClient uac) {
        super(uac);
    }
    ArrayList<AEntity> entites = new ArrayList<>();
    ArrayList<NameValuePair> alEntites = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityDelete req = (RequestEntityDelete) request;


        Survey survey = _tctx.getDB().FindAssert(EN.Survey.getEntityClass(), req.getEntityId());
        //find csatconf
        ArrayList<AOPsCSATConf> aoPsCSATConfs = _tctx.getDB().Select(new AOPsCSATConfQuery().filterBySurvey(survey.getId()));
        //find AOPsProperties
        deleteAOPsProperties(aoPsCSATConfs);


        //find SurveyForm
        ArrayList<SurveyForm> surveyformlist = _tctx.getDB().Select(new SurveyFormQuery().filterBySurvey(req.getEntityId()));

        //find surveyAttribute
        ArrayList<SurveyAttribute> surveyattributelist = _tctx.getDB().Select(new SurveyAttributeQuery().filterBySurvey(req.getEntityId()));

        //add all in a list
        entites.addAll(surveyattributelist);
        entites.addAll(surveyformlist);
        entites.addAll(aoPsCSATConfs);
        entites.add(survey);
        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(),entites);

        EventEntityDeleted ev=new EventEntityDeleted(request,survey);
        return ev;
    }
    private void deleteAOPsProperties(ArrayList<AOPsCSATConf> aopslists) throws CODEException, Exception, GravityException {
        for (AOPsCSATConf aoPsCSATConf:aopslists){
            AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(),aoPsCSATConf.getAOPs());
            AOPsProperties properties = _tctx.getDB().FindAssert(new AOPsPropertiesQuery().filterByConfKey(AOPsProperties.Keys.Global_SurveyId).filterByAOPs(aoPsCSATConf.getAOPs().getId()));
            entites.add(properties);
        }
    }
}
