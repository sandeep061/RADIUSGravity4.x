/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.survey;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ois.cc.gravity.db.queries.SurveyAttributeQuery;
import ois.cc.gravity.db.queries.SurveyFormQuery;
import ois.cc.gravity.framework.requests.survey.RequestSurveyFormConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Survey;
import ois.radius.cc.entities.tenant.cc.SurveyAttribute;
import ois.radius.cc.entities.tenant.cc.SurveyForm;
import org.json.JSONObject;

/**
 * @author Sandeepkumar.Sahoo
 * @since Aug 12, 2025
 */
public class RequestSurveyFormConfigService extends ARequestEntityService
{

    public RequestSurveyFormConfigService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestSurveyFormConfig req = (RequestSurveyFormConfig) request;

        //check attribute size
        if (req.getAttributes().size() > 64)
        {
            // throw an exception
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.LimitExceed, "attribute value should not more than 64");
        }
        //find survey
        Survey survey = _tctx.getDB().FindAssert(EN.Survey.getEntityClass(), req.getSurvey());

        //find SurveyForm based on code
        SurveyForm form = _tctx.getDB().Find(new SurveyFormQuery().filterByCode(req.getFormCode()));
        if (form == null)
        {
            form = BuildSurveyForm(survey, req);
        }

        BuildSurveyAttribute(survey, form, req.getAttributes());

//        _logger.trace(new JSONObject(entities).toString());
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        EventEntityAdded ev = new EventEntityAdded(request, form);
        return ev;
    }

    private SurveyForm BuildSurveyForm(Survey survey, RequestSurveyFormConfig request)
    {
        SurveyForm form = new SurveyForm();
        form.setIsPublished(request.getIsPublished());
        form.setSurvey(survey);
        form.setCode(request.getFormCode());
        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), form));
        return form;
    }

    private void BuildSurveyAttribute(Survey survey, SurveyForm form, ArrayList<SurveyAttribute> srvattr) throws CODEException, GravityException
    {
        ArrayList<SurveyAttribute> dbsurveyattributes = _tctx.getDB().Select(new SurveyAttributeQuery().filterBySurveyForm(form.getId()));

        Map<String, SurveyAttribute> dbAttrMap = new HashMap<>();
        if (!dbsurveyattributes.isEmpty())
        {
            for (SurveyAttribute attr : dbsurveyattributes)
            {
                dbAttrMap.put(attr.getCode(), attr);
            }
        }

        for (SurveyAttribute fresh : srvattr)
        {
            SurveyAttribute target = dbAttrMap.get(fresh.getCode());

            if (target == null)
            {
                // --- New: create and set all values ---
                target = new SurveyAttribute();
                target.setCode(fresh.getCode());
                target.setDataType(fresh.getDataType());
                target.setValidation(fresh.getValidation());
                target.setSurvey(survey);
                target.setSurveyForm(form);
                _logger.trace(new JSONObject(target).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), target));
            }
            else
            {
                // --- Existing: update all values ---
                target.setDataType(fresh.getDataType());
                target.setValidation(fresh.getValidation());
                target.setSurvey(survey);
                target.setSurveyForm(form);
                _logger.trace(new JSONObject(target).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), target));
            }

        }

    }

}
