package ois.cc.gravity.services.aops;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCDNAddressQuery;
import ois.cc.gravity.db.queries.AOPsCDNQuery;
import ois.cc.gravity.framework.events.aops.EventAOPsCDNFetch;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OAOPsCDN;
import ois.cc.gravity.objects.OAOPsCDNAddress;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCDN;
import ois.radius.cc.entities.tenant.cc.AOPsCDNAddress;

import java.util.ArrayList;

public class RequestAOPsCDNFetchService extends ARequestEntityService
{
    public RequestAOPsCDNFetchService(UAClient uac)
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
            AOPsCDNQuery enQry = new AOPsCDNQuery();
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
            ArrayList<OAOPsCDN> oaopscdn=new ArrayList<>();
            ArrayList<AOPsCDN> aopscdns = _tctx.getDB().Select(EN.AOPsCDN.getEntityClass(), ctq);
               for (AOPsCDN aopcdn:aopscdns){
                  OAOPsCDN ocdn= buildOAOPsCDN(aopcdn);
                  oaopscdn.add(ocdn);
               }
            //
            EventAOPsCDNFetch event=new EventAOPsCDNFetch(reqFetch);
               event.setAOPsCDNs(oaopscdn);

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
//            EventAttributeInvalid ev = new EventAttributeInvalid(reqfetch, reqfetch.getEntityName().name(), fex.getFlieldName());
            throw fex;
        }

        else
        {
//            EventProcessFailed ev = new EventProcessFailed(reqfetch, rex);
            throw rex;
        }
    }

    private OAOPsCDN buildOAOPsCDN(AOPsCDN aopcdn) throws CODEException, GravityException
    {
        OAOPsCDN ocdn = new OAOPsCDN();
        ArrayList<OAOPsCDNAddress> oadd = new ArrayList<>();
        ocdn.setAOPs(aopcdn.getAOPs());
        ocdn.setChannel(aopcdn.getChannel());
        ocdn.setCode(aopcdn.getCode());
        ocdn.setWorkFlow(aopcdn.getWorkFlow());
        ocdn.setId(aopcdn.getId());

        ArrayList<AOPsCDNAddress> address = _tctx.getDB().Select(new AOPsCDNAddressQuery().filterByAOPsCDN(aopcdn.getId()));
        for (AOPsCDNAddress add : address)
        {
            OAOPsCDNAddress ocdnadd = new OAOPsCDNAddress();
            ocdnadd.setAddress(add.getAddress());
            ocdnadd.setId(add.getId());
            ocdnadd.setChannel(add.getChannel());
            ocdnadd.setWeb(add.getIsWeb());
            ocdnadd.setXPlatformID(add.getXPlatformID());
            ocdnadd.setXPlatformSID(add.getXPlatformSID());
            ocdnadd.setXPlatformUA(add.getXPlatformUA());
            ocdnadd.setAOPsCDN(add.getAOPsCDN());
            oadd.add(ocdnadd);
        }
        ocdn.setAOPsCDNAddresses(oadd);
        return ocdn;
    }
}
