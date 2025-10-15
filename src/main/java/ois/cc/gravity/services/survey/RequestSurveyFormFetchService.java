package ois.cc.gravity.services.survey;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.SurveyAttributeQuery;
import ois.cc.gravity.db.queries.SurveyFormQuery;
import ois.cc.gravity.framework.events.survey.EventSurveyFormFetch;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OSurveyAttribute;
import ois.cc.gravity.objects.OSurveyForm;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.SurveyAttribute;
import ois.radius.cc.entities.tenant.cc.SurveyForm;

import java.util.ArrayList;

public class RequestSurveyFormFetchService extends ARequestEntityService
{

    public RequestSurveyFormFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws CODEException, GravityException
    {

        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        SurveyFormQuery enQry = new SurveyFormQuery();
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
        ArrayList<OSurveyForm> oSurveyForm = new ArrayList<>();
        ArrayList<SurveyForm> surveyForms = _tctx.getDB().Select(getClass(), ctq);
        for (SurveyForm form : surveyForms)
        {
            oSurveyForm.add(buildOSurveyForms(form));
        }
        EventSurveyFormFetch fetch = new EventSurveyFormFetch(request);
        fetch.setSurveyForm(oSurveyForm);
        return fetch;
    }

    private OSurveyForm buildOSurveyForms(SurveyForm form) throws CODEException, GravityException
    {
        OSurveyForm oforms = new OSurveyForm();
        ArrayList<OSurveyAttribute> oattr = new ArrayList<>();
        oforms.setCode(form.getCode());
        oforms.setId(form.getId());
        oforms.setPublished(form.getIsPublished());
        ArrayList<SurveyAttribute> survattributes = _tctx.getDB().Select(new SurveyAttributeQuery().filterBySurveyForm(form.getId()));
        for (SurveyAttribute survattribute : survattributes)
        {
            OSurveyAttribute osurvattr = new OSurveyAttribute();
            osurvattr.setId(survattribute.getId());
            osurvattr.setCode(survattribute.getCode());
            osurvattr.setDataType(survattribute.getDataType());
            osurvattr.setValidation(survattribute.getValidation());
            oattr.add(osurvattr);
        }
        oforms.setSurveyAttribute(oattr);
        return oforms;
    }

}
