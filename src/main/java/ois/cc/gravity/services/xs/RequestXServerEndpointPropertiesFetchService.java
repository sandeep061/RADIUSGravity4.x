package ois.cc.gravity.services.xs;

import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.XServerEndpointPropertiesQuery;
import ois.cc.gravity.framework.events.xs.EventXServerEndpointPropertiesFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OXServer;
import ois.cc.gravity.objects.OXServerEndpointProperties;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.radius.cc.entities.tenant.cc.XServerEndpointProperties;

import java.util.ArrayList;

public class RequestXServerEndpointPropertiesFetchService extends ARequestEntityService
{

    public RequestXServerEndpointPropertiesFetchService(UAClient uac)
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
            XServerEndpointPropertiesQuery enQry = new XServerEndpointPropertiesQuery();
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

            ArrayList<OXServerEndpointProperties> oxendpointprops = new ArrayList<>();
            ArrayList<XServerEndpointProperties> xendpointprops = _tctx.getDB().Select(getClass(), ctq);

            for (XServerEndpointProperties props : xendpointprops)
            {
                oxendpointprops.add(BuildOXServerEndpointProperties(props));
            }

            EventXServerEndpointPropertiesFetched event = new EventXServerEndpointPropertiesFetched(reqFetch);
            event.setXServerEndpointProperties(oxendpointprops);
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

    private OXServer BuildOXserver(XServer xserver)
    {
        OXServer oserver = new OXServer();
        oserver.setChannel(xserver.getChannel());
        oserver.setCode(xserver.getCode());
        oserver.setName(xserver.getName());
        oserver.setId(xserver.getId());
        oserver.setDescription(xserver.getDescription());
        oserver.setEndPointTypeProps(xserver.GetAllEndpointProps());
        oserver.setProviderID(xserver.getProviderID());
        return oserver;
    }

    private OXServerEndpointProperties BuildOXServerEndpointProperties(XServerEndpointProperties props)
    {
        OXServerEndpointProperties properties = new OXServerEndpointProperties();
        properties.setEndpointType(props.getEndpointType());
        properties.setXServer(BuildOXserver(props.getXServer()));
        properties.setPropKey(props.getPropKey());
        properties.setId(props.getId());
        properties.setPropValue(props.getPropValue());
        return properties;
    }

}
