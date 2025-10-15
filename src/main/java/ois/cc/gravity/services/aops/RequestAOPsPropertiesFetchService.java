package ois.cc.gravity.services.aops;

import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.ua.UAClient;
import ois.cc.gravity.framework.events.aops.EventCampaignPropertiesFetched;
import ois.cc.gravity.framework.requests.aops.RequestAOPsPropertiesFetch;
import ois.cc.gravity.services.ARequestEntityService;
import java.util.ArrayList;
import java.util.HashMap;
import ois.cc.gravity.db.queries.AOPsPropertiesQuery;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.AOPsProperties;

public class RequestAOPsPropertiesFetchService extends ARequestEntityService
{

    public RequestAOPsPropertiesFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestAOPsPropertiesFetch reqFetch = (RequestAOPsPropertiesFetch) request;

        AOPs aops = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), reqFetch.getAOPsId());

        AOPsPropertiesQuery enQry = new AOPsPropertiesQuery();
        enQry.filterByAOPs(aops.getId());
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
        HashMap<AOPsProperties.Keys, String> hmCampProps = new HashMap<>();

        ArrayList<AOPsProperties> aopsProps = _tctx.getDB().Select(getClass(), ctq);
       for (AOPsProperties e : aopsProps)
        {
            AOPsProperties.Keys key = AOPsProperties.Keys.valueOf(e.getConfKey().name());
            String value = e.getConfValue();
            hmCampProps.put(key, value);
        }
        EventCampaignPropertiesFetched ev = new EventCampaignPropertiesFetched(reqFetch);
        ev.setAOPsProperties(hmCampProps);
        ev.setRecordCount(recCount);
        
        return ev;

    }

}
