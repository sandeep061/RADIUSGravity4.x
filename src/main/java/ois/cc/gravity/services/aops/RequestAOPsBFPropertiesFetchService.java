package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsBFPropertiesQuery;
import ois.cc.gravity.framework.requests.aops.RequestAOPsBFPropertiesFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsBF;
import ois.radius.cc.entities.tenant.cc.AOPsBFProperties;

import java.util.ArrayList;
import java.util.HashMap;
import ois.cc.gravity.framework.events.aops.EventAOPsBFPropertiesFetched;

public class RequestAOPsBFPropertiesFetchService extends ARequestEntityService {


    public RequestAOPsBFPropertiesFetchService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestAOPsBFPropertiesFetch reqFetch = (RequestAOPsBFPropertiesFetch) request;
        //validate AOPsBF
        AOPsBF aopsbf = _tctx.getDB().FindAssert(EN.AOPsBF.getEntityClass(),reqFetch.getAOPsBFId());
        if (aopsbf == null)
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, EN.AOPsBF);
            ev.setCondition("UserId", OPRelational.Eq, reqFetch.getAOPsBFId());
            return ev;
        }
        AOPsBFPropertiesQuery enQry = new AOPsBFPropertiesQuery();
        enQry.filterByAOPsBF(reqFetch.getAOPsBFId());


        if (reqFetch != null & reqFetch.getFilters() != null)
        {
            enQry.doApplyFilters(reqFetch.getFilters());
        }

        Integer recCount = null;

        enQry.ApplyOrderBy(reqFetch.getOrderBy());

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

        HashMap<String, String> hmAOPsBFConfProps = new HashMap<>();
        ArrayList<AOPsBFProperties> aopsbfprops = _tctx.getDB().Select(getClass(), ctq);
        for (AOPsBFProperties e : aopsbfprops)
        {
            String key = e.getConfKey();
            String value = e.getConfValue();
            hmAOPsBFConfProps.put(key, value);
        }

        EventAOPsBFPropertiesFetched ev=new EventAOPsBFPropertiesFetched(request);
        ev.setAOPsBFProperties(hmAOPsBFConfProps);
        ev.setRecordCount(recCount);
        return ev;
    }
}
