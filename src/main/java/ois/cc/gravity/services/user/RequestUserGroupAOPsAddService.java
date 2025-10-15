package ois.cc.gravity.services.user;

import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.ua.UAClient;

public class RequestUserGroupAOPsAddService extends RequestEntityAddService
{
    public RequestUserGroupAOPsAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {
        return super.DoProcessEntityRequest(request);
    }
}
