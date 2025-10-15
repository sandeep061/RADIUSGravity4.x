package ois.cc.gravity.services.oi;

import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.OIAlertConfigQuery;
import ois.cc.gravity.framework.events.oi.EventOIAlertConfigFetch;
import ois.cc.gravity.objects.OAlertConfig;
import ois.cc.gravity.objects.OUser;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.XAlertID;
import ois.radius.cc.entities.tenant.oi.OIAlertConfig;

import java.util.ArrayList;

public class RequestOIAlertConfigFetchService extends ARequestEntityService {
    public RequestOIAlertConfigFetchService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {

        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        try
        {
            OIAlertConfigQuery enQry = new OIAlertConfigQuery();
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
            
            ArrayList<OAlertConfig> oialertconfig = new ArrayList<>();
            ArrayList<OIAlertConfig> alertconfigs = _tctx.getDB().Select(getClass(), ctq);
            for (OIAlertConfig conf : alertconfigs)
            {
                oialertconfig.add(BuildOOIAlertConfig(conf));
            }

            EventOIAlertConfigFetch event = new EventOIAlertConfigFetch(reqFetch);
            event.setOIAlertConfig(oialertconfig);
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

    private OAlertConfig BuildOOIAlertConfig(OIAlertConfig config){
        OAlertConfig oconfig=new OAlertConfig();
        ArrayList<XAlertID> alert=new ArrayList<>(config.getXAlertIDs());
        oconfig.setXAlertIDs(alert);
        oconfig.setInApp(config.getInApp());
        oconfig.setUsers(BuildOUser(new ArrayList<>(config.getUsers())));
        oconfig.setId(config.getId());
        return oconfig;
    }

    private ArrayList<OUser> BuildOUser(ArrayList<User> users) {
        ArrayList<OUser>ousers=new ArrayList<>();
        for (User user : users) {
            OUser ouser = new OUser();
            ouser.setName(user.getName());
            ouser.setUserId(user.getUserId());
            ouser.setLoginId(user.getLoginId());
            ouser.setId(user.getId());
            ousers.add(ouser);
        }
        return ousers;
    }
}
