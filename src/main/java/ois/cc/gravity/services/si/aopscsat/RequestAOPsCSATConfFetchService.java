package ois.cc.gravity.services.si.aopscsat;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCSATConfQuery;
import ois.cc.gravity.db.queries.XAlertIDQuery;
import ois.cc.gravity.framework.events.aops.EventAOPsCSATConfFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OAOPsCSATConf;
import ois.cc.gravity.objects.OXAlertID;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCSATConf;
import ois.radius.cc.entities.tenant.cc.XAlertID;

import java.util.ArrayList;

public class RequestAOPsCSATConfFetchService extends ARequestEntityService
{

    public RequestAOPsCSATConfFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        try
        {
            AOPsCSATConfQuery enQry = new AOPsCSATConfQuery();
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

            ArrayList<OAOPsCSATConf> oaoPsCSATConfs = new ArrayList<>();
            ArrayList<AOPsCSATConf> cstconfs = _tctx.getDB().Select(getClass(), ctq);
            for (AOPsCSATConf conf : cstconfs)
            {
                oaoPsCSATConfs.add(buildOAOPsCSATConf(conf));
            }

            EventAOPsCSATConfFetched event = new EventAOPsCSATConfFetched(reqFetch);
            event.setOAOPsCSATConfs(oaoPsCSATConfs);
            event.setRecordCount(recCount);
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

    private OAOPsCSATConf buildOAOPsCSATConf(AOPsCSATConf csatconf) throws CODEException, GravityException
    {
        OAOPsCSATConf oaoPsCSATConf = new OAOPsCSATConf();
        ArrayList<XAlertID> xalerts = _tctx.getDB().Select(new XAlertIDQuery().filterByAOPsCSATConf(csatconf.getId()));
        oaoPsCSATConf.setAOPs(csatconf.getAOPs());
        oaoPsCSATConf.setChannel(csatconf.getChannel());
        oaoPsCSATConf.setXAlerts(buildXalert(xalerts));
        oaoPsCSATConf.setId(csatconf.getId());
        oaoPsCSATConf.setAuto(csatconf.getIsAuto());
        oaoPsCSATConf.setEnable(csatconf.getIsEnable());
        oaoPsCSATConf.setXSessType(csatconf.getXSessType());
        oaoPsCSATConf.setDispositionCodes(csatconf.getDispositionCodes());
        return oaoPsCSATConf;
    }

    private ArrayList<OXAlertID> buildXalert(ArrayList<XAlertID> alertIDS)
    {
        ArrayList<OXAlertID> oxAlertIDS = new ArrayList<>();
        for (XAlertID id : alertIDS)
        {
            OXAlertID oid = new OXAlertID();
            oid.setChannel(id.getChannel());
            oid.setId(id.getId());
            oid.setTemplate(id.getTemplate());
            oid.setXPlatform(id.getXPlatform());
            oid.setXPlatformUA(id.getXPlatformUA());
            oxAlertIDS.add(oid);
        }
        return oxAlertIDS;
    }

}
