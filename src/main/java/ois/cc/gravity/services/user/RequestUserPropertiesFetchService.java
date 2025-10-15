package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserPropertiesQuery;
import ois.cc.gravity.framework.events.user.EventUserPropertiesFetched;
import ois.cc.gravity.framework.requests.aops.RequestUserPropertiesFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserProperties;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestUserPropertiesFetchService extends ARequestEntityService
{
    public RequestUserPropertiesFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestUserPropertiesFetch reqFetch = (RequestUserPropertiesFetch) request;
        //validate User
        User user = _tctx.getNucleusCtx().GetUserById(_tctx.getTenant().getCode(), reqFetch.getUser(), UserRole.Agent.name());
        if (user == null)
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, EN.User);
            ev.setCondition("UserId", OPRelational.Eq, reqFetch.getUser());
            return ev;
        }
        UserPropertiesQuery enQry = new UserPropertiesQuery();
        enQry.filterByUserId(user.getUserId());


        if (reqFetch != null & reqFetch.getFilters() != null)
        {
            enQry.doApplyFilters(reqFetch.getFilters());
        }

        enQry.ApplyOrderBy(reqFetch.getOrderBy());

        if (reqFetch.getLimit() != null)
        {
            enQry.setLimit(reqFetch.getLimit());
        }
        if (reqFetch.getOffset() != null)
        {
            enQry.setOffset(reqFetch.getOffset());
        }

        JPAQuery ctq = enQry.toSelect();

        HashMap<UserProperties.Keys, String> hmUserProps = new HashMap<>();
        ArrayList<UserProperties> userProps = _tctx.getDB().Select(getClass(), ctq);
        for (UserProperties e : userProps)
        {
            UserProperties.Keys key = UserProperties.Keys.valueOf(e.getConfKey());
            String value = e.getConfValue();
            hmUserProps.put(key, value);
        }

        EventUserPropertiesFetched ev = new EventUserPropertiesFetched(reqFetch);
        ev.setUserProperties(hmUserProps);

        return ev;
    }

}
