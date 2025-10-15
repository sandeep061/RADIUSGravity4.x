/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.survey;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Enums.DataType;
import CrsCde.CODE.Common.Utils.DATEUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ois.cc.gravity.db.queries.SurveyAttemptQuery;
import ois.cc.gravity.db.queries.SurveyAttributeQuery;
import ois.cc.gravity.db.queries.SurveyDRQuery;
import ois.cc.gravity.db.queries.SurveyFormQuery;
import ois.cc.gravity.framework.requests.survey.RequestSurveyDataConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;
import org.json.JSONObject;

/**
 *
 * @author Sandeepkumar.Sahoo
 * @since Aug 12, 2025
 */
public class RequestSurveyDataConfigService extends ARequestEntityService
{

    public RequestSurveyDataConfigService(UAClient uac)
    {
        super(uac);
    }

    private final ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestSurveyDataConfig req = (RequestSurveyDataConfig) request;

        SurveyForm srvFrm = _tctx.getDB().FindAssert(new SurveyFormQuery().filterByCode(req.getFormCode()));
        SurveyData surveyData = null;
        HashMap<String, String> frmData = getFromDatas(srvFrm, req.getSurveyData());
        SurveyDR surDR = _tctx.getDB().FindAssert(new SurveyDRQuery().filterByUSUID(req.getUSUID()));

        //Check expiry.
        if (DATEUtil.Now().after(surDR.getExpiriedOn()))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.SurveyLinkExpired, "Time Expired");
        }

        //find SurveyDataFetch
        SurveyData dbsurveyData = getSurveyData(req);
        if (dbsurveyData == null)
        {
            JSONObject fromDataJson = new JSONObject(frmData);

            dbsurveyData = new SurveyData();
            dbsurveyData.setSurveyData(fromDataJson.toString());
            dbsurveyData.setAttempt(0);
            dbsurveyData.setUSUID(req.getUSUID());
            dbsurveyData.setScore(req.getScore());
            dbsurveyData.setSurveyDR(surDR);

        }
        else
        {

            int attempt = dbsurveyData.getAttempt();
            //Check Attempt.
            if (srvFrm.getSurvey().getMaxAttempt() <= attempt)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.LimitExceed, "Attempt Limit Exceed");
            }

            if (srvFrm.getSurvey().getIsOverrid())
            {
                dbsurveyData = new SurveyData();
                JSONObject fromDataJson = new JSONObject(frmData);
                dbsurveyData.setSurveyData(fromDataJson.toString());
                dbsurveyData.setUSUID(req.getUSUID());
                dbsurveyData.setScore(req.getScore());
                dbsurveyData.setAttempt(attempt + 1);
                dbsurveyData.setSurveyDR(surDR);
            }
            else
            {
                //check survey data,scorring,attempt
                dbsurveyData.setSurveyData(req.getSurveyData() == null ? dbsurveyData.getSurveyData() : new JSONObject(req.getSurveyData()).toString());
                dbsurveyData.setAttempt(attempt + 1);
                dbsurveyData.setScore(req.getScore() == null ? dbsurveyData.getScore() : req.getScore());

            }

        }

        //addEditAttempt.
        UpdateSurveyAttempt(req.getUSUID());

        if (dbsurveyData.getId() == null)
        {
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), dbsurveyData));
        }
        else
        {
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), dbsurveyData));
        }

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        EventEntityAdded ev = new EventEntityAdded(request, dbsurveyData);
        return ev;
    }

    private HashMap<String, String> getFromDatas(SurveyForm sf, HashMap<String, String> reqdata) throws GravityException, CODEException
    {
        HashMap<String, String> validData = new HashMap<>();
        for (Map.Entry<String, String> data : reqdata.entrySet())
        {
            String key = data.getKey();
            SurveyAttribute sa = _tctx.getDB().Find(new SurveyAttributeQuery().filterBySurveyForm(sf.getId()).filterByCode(key));
            if (sa == null)
            {
                continue;
            }
            DataType dataType = sa.getDataType();
            validateFormData(dataType, data.getValue());
            validData.put(key, data.getValue());
        }

        return validData;
    }

    private void validateFormData(DataType dt, String val) throws GravityIllegalArgumentException
    {

        try
        {
            switch (dt)
            {
                case Byte:
                    Byte.valueOf(val);
                    break;

                case Short:
                    Short.valueOf(val);
                    break;

                case Integer:
                    Integer.valueOf(val);
                    break;

                case Long:
                    Long.valueOf(val);
                    break;

                case Float:
                    Float.valueOf(val);
                    break;

                case Double:
                    Double.valueOf(val);
                    break;

                case Boolean:
                    if (!val.equalsIgnoreCase("true") && !val.equalsIgnoreCase("false"))
                    {
                        throw new IllegalArgumentException("Invalid boolean value: " + val);
                    }
                    Boolean.valueOf(val);
                    break;

                case String:
                    // Always valid
//                    String.valueOf(val);
                    break;
                case Date:
                    DATEUtil.ValueOf(val);
                    break;

                case DateTime:
                    DATEUtil.ValueOf(val);
                    break;
                case Time:
                    DATEUtil.ValueOf(val); // expects hh:mm:ss
                    break;

                default:
                    throw new GravityIllegalArgumentException("Unsupported data type: " + dt);
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException("Validation failed for " + dt + " with value: " + val);
        }
    }

    private void UpdateSurveyAttempt(String usuid) throws CODEException, GravityException
    {
        SurveyAttempt dbSurveyAttempt = _tctx.getDB().Find(new SurveyAttemptQuery().filterByUSurId(usuid));
        if (dbSurveyAttempt == null)
        {
            dbSurveyAttempt = new SurveyAttempt();
            dbSurveyAttempt.setUSUID(usuid);
            dbSurveyAttempt.setAttempt(0);
        }
        else
        {
            dbSurveyAttempt.setAttempt(dbSurveyAttempt.getAttempt() + 1);
        }

        if (dbSurveyAttempt.getId() == null)
        {
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), dbSurveyAttempt));
        }
        else
        {
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), dbSurveyAttempt));
        }

    }

    private SurveyData getSurveyData(RequestSurveyDataConfig req) throws CODEException, GravityException
    {

        JPAQuery query = new JPAQuery("SELECT s FROM SurveyData s WHERE s.USUID = :ucuid ORDER BY s.Attempt DESC");
        query.setParam("ucuid", req.getUSUID());
        query.setLimit(1);
        return _tctx.getDB().Find(EN.SurveyData.getEntityClass(), query);
    }

}
