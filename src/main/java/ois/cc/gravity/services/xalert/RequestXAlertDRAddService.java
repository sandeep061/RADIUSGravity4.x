package ois.cc.gravity.services.xalert;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsXAlertDRQuery;
import ois.cc.gravity.db.queries.SurveyAlertQuery;
import ois.cc.gravity.framework.requests.survey.RequestXAlertDRAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsXAlertDR;
import ois.radius.cc.entities.tenant.cc.SurveyAlert;
import ois.radius.cc.entities.tenant.cc.XAlertDR;
import ois.radius.cc.entities.tenant.cc.XAlertID;

import java.util.ArrayList;

public class RequestXAlertDRAddService extends ARequestEntityService
{

    public RequestXAlertDRAddService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<AEntity> entites = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {


        RequestXAlertDRAdd req = (RequestXAlertDRAdd) request;
        XAlertID alertEntity = null;
        if (req.getXAlertID() != null)
        {
            alertEntity = _tctx.getDB().FindAssert(EN.XAlertID.getEntityClass(), req.getXAlertID());

        }
        //Buid XAlertDr

        XAlertDR alertDR = new XAlertDR();
        alertDR.setMessage(req.getMessage());
        alertDR.setXAlertID(alertEntity);
        alertDR.setChannel(req.getChannel());
        alertDR.setResponse(req.getResponse());
        alertDR.setUAltID(req.getUAltID());
        alertDR.setSendAt(req.getSendAt());
        alertDR.setStatus(req.getStatus());

        entites.add(alertDR);

        BuildSurveyAlert(req);
        BuildAOPsXAlertid(req);

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), entites);

        EventEntityAdded ev = new EventEntityAdded(req, alertDR);
        return ev;
    }

    private void BuildSurveyAlert(RequestXAlertDRAdd req) throws CODEException, GravityException
    {
        SurveyAlert surveyalert = _tctx.getDB().Find(new SurveyAlertQuery().filterByUAltID(req.getUAltID()));
        if (surveyalert == null)
        {
            surveyalert = new SurveyAlert();
            surveyalert.setUAltID(req.getUAltID());
            surveyalert.setUSUID(req.getUSUID());
            entites.add(surveyalert);
        }
    }

    private void BuildAOPsXAlertid(RequestXAlertDRAdd req) throws CODEException, GravityException
    {
        AOPsXAlertDR aopsalertdr = _tctx.getDB().Find(new AOPsXAlertDRQuery().filterByUAltID(req.getUAltID()));
        if (aopsalertdr == null)
        {
            aopsalertdr = new AOPsXAlertDR();
            aopsalertdr.setUAltID(req.getUAltID());
            aopsalertdr.setUAltID(req.getUAltID());
            aopsalertdr.setUXID(req.getUXID());
            aopsalertdr.setAOPs(_tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), req.getAOPs()));
            entites.add(aopsalertdr);
        }
    }
}
