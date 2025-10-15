package ois.cc.gravity.services.aops;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCallerIdAddressQuery;
import ois.cc.gravity.db.queries.AOPsCallerIdQuery;
import ois.cc.gravity.framework.events.aops.EventAOPsCallerIdFetch;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OAOPsCallerId;
import ois.cc.gravity.objects.OAOPsCallerIdAddress;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCallerId;
import ois.radius.cc.entities.tenant.cc.AOPsCallerIdAddress;

import java.util.ArrayList;

public class RequestAOPsCallerIdFetchService extends ARequestEntityService
{
    public RequestAOPsCallerIdFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

//        CoreDB coreDB = _cctx.getCoreDB();
        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        try
        {
            AOPsCallerIdQuery enQry = new AOPsCallerIdQuery();
            enQry.doApplyFilters(reqFetch.getFilters());
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
            ArrayList<OAOPsCallerId> oaopsclId=new ArrayList<>();
            ArrayList<AOPsCallerId> aopsclids = _tctx.getDB().Select(getClass(), ctq);
            for (AOPsCallerId aopclid:aopsclids){
                OAOPsCallerId ocdn= buildOAOPsCDN(aopclid);
                oaopsclId.add(ocdn);
            }

            EventAOPsCallerIdFetch event=new EventAOPsCallerIdFetch(reqFetch);
            event.setAOPsCallerIds(oaopsclId);

            return event;
        }
        catch (GravityException rex)
        {
            return BuildExceptionEvents(reqFetch, rex);
        }
    }

    protected Event BuildExceptionEvents(RequestEntityFetch reqfetch, GravityException rex) throws GravityNoSuchFieldException, GravityException
    {
        if (rex instanceof GravityNoSuchFieldException)
        {
            GravityNoSuchFieldException fex = (GravityNoSuchFieldException) rex;
            throw fex;
        }

        else
        {
            throw rex;
        }
    }

    private OAOPsCallerId buildOAOPsCDN(AOPsCallerId aopclid) throws CODEException, GravityException
    {
        OAOPsCallerId oclid = new OAOPsCallerId();
        ArrayList<OAOPsCallerIdAddress> oadd = new ArrayList<>();
        oclid.setAOPs(aopclid.getAOPs());
        oclid.setChannel(aopclid.getChannel());
        oclid.setCode(aopclid.getCode());
        oclid.setId(aopclid.getId());
        ArrayList<AOPsCallerIdAddress> address = _tctx.getDB().Select(new AOPsCallerIdAddressQuery().filterByAOPsCallerId(aopclid.getId()));
        for (AOPsCallerIdAddress clid : address)
        {
            OAOPsCallerIdAddress ocdnclid = new OAOPsCallerIdAddress();
            ocdnclid.setAddress(clid.getAddress());
            ocdnclid.setChannel(clid.getChannel());
            ocdnclid.setDefault(clid.getIsDefault());
            ocdnclid.setXPlatformID(clid.getXPlatformID());
            ocdnclid.setXPlatformSID(clid.getXPlatformSID());
            ocdnclid.setXPlatformUA(clid.getXPlatformUA());
            ocdnclid.setAOPsCallerId(clid.getAOPsCallerId());
            ocdnclid.setId(clid.getId());
            oadd.add(ocdnclid);
        }
        oclid.setAOPsCallerIdAddress(oadd);
        return oclid;
    }
}
